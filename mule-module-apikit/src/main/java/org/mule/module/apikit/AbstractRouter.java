/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.mule.DefaultMuleEvent;
import org.mule.NonBlockingVoidMuleEvent;
import org.mule.OptimizedRequestContext;
import org.mule.VoidMuleEvent;
import org.mule.api.DefaultMuleException;
import org.mule.api.MessagingException;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.MuleRuntimeException;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.lifecycle.StartException;
import org.mule.api.transport.ReplyToHandler;
import org.mule.construct.Flow;
import org.mule.module.apikit.exception.ApikitRuntimeException;
import org.mule.module.apikit.exception.InvalidUriParameterException;
import org.mule.module.apikit.exception.MethodNotAllowedException;
import org.mule.module.apikit.exception.MuleRestException;
import org.mule.module.apikit.odata.ODataPayload;
import org.mule.module.apikit.odata.ODataResponseTransformer;
import org.mule.module.apikit.odata.ODataUriParser;
import org.mule.module.apikit.odata.error.ODataErrorHandler;
import org.mule.module.apikit.odata.processor.ODataRequestProcessor;
import org.mule.module.apikit.uri.ResolvedVariables;
import org.mule.module.apikit.uri.URIPattern;
import org.mule.module.apikit.uri.URIResolver;
import org.mule.processor.AbstractRequestResponseMessageProcessor;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.parameter.UriParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.LoadingCache;

public abstract class AbstractRouter extends
	AbstractRequestResponseMessageProcessor implements ApiRouter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected FlowConstruct flowConstruct;
    protected AbstractConfiguration config;
    protected RamlDescriptorHandler ramlHandler;

    private static final String ODATA_SVC_URI_PREFIX = "/odata.svc";

    @Override
    public void start() throws MuleException {
	startConfiguration();
	ramlHandler = new RamlDescriptorHandler(config);
	config.publishConsoleUrls(muleContext.getConfiguration()
		.getWorkingDirectory());
    }

    protected abstract void startConfiguration() throws StartException;

    protected Raml getApi() {
	return config.getApi();
    }

    @Override
    public void setFlowConstruct(FlowConstruct flowConstruct) {
	this.flowConstruct = flowConstruct;
    }

    @Override
    protected MuleEvent processBlocking(MuleEvent event) throws MuleException {

	if (isODataRequest(event)) {
	    return processODataRequest(event);
	} else {
	    RouterRequest result = processRouterRequest(event);
	    event = result.getEvent();
	    if (result.getFlow() != null) {
		event = result.getFlow().process(event);
	    }
	    return processRouterResponse(event, result.getSuccessStatus());
	}
    }

    @Override
    protected MuleEvent processNonBlocking(MuleEvent event)
	    throws MuleException {
	final RouterRequest result = processRouterRequest(event);

	event = result.getEvent();

	final ReplyToHandler originalReplyToHandler = event.getReplyToHandler();
	event = new DefaultMuleEvent(event, new ReplyToHandler() {
	    @Override
	    public void processReplyTo(MuleEvent event,
		    MuleMessage returnMessage, Object replyTo)
		    throws MuleException {
		MuleEvent response = processRouterResponse(
			new DefaultMuleEvent(event, originalReplyToHandler),
			result.getSuccessStatus());
		// Update RequestContext ThreadLocal for backwards compatibility
		OptimizedRequestContext.unsafeSetEvent(response);
		if (!NonBlockingVoidMuleEvent.getInstance().equals(response)) {
		    originalReplyToHandler.processReplyTo(response, null, null);
		}
		processFinally(event, null);
	    }

	    @Override
	    public void processExceptionReplyTo(MessagingException exception,
		    Object replyTo) {
		originalReplyToHandler.processExceptionReplyTo(exception,
			replyTo);
		processFinally(exception.getEvent(), exception);
	    }
	});
	// Update RequestContext ThreadLocal for backwards compatibility
	OptimizedRequestContext.unsafeSetEvent(event);

	if (result.getFlow() != null) {
	    event = result.getFlow().process(event);
	}
	if (!(event instanceof NonBlockingVoidMuleEvent)) {
	    return processRouterResponse(event, result.getSuccessStatus());
	}
	return event;
    }

    protected RouterRequest processRouterRequest(MuleEvent event)
	    throws MuleException {

	HttpRestRequest request = getHttpRestRequest(event);

	String path = request.getResourcePath();

	MuleEvent handled = handleEvent(event, path);
	if (handled != null) {
	    return new RouterRequest(handled);
	}

	// check for raml descriptor request
	if (ramlHandler.handles(request)) {
	    return new RouterRequest(ramlHandler.processRouterRequest(event));
	}

	URIPattern uriPattern;
	URIResolver uriResolver;
	path = path.isEmpty() ? "/" : path;
	try {
	    uriPattern = getUriPatternCache().get(path);
	    uriResolver = getUriResolverCache().get(path);
	} catch (ExecutionException e) {
	    if (e.getCause() instanceof MuleRestException) {
		throw (MuleRestException) e.getCause();
	    }
	    throw new DefaultMuleException(e);
	}

	Resource resource = getRoutingTable().get(uriPattern);
	if (resource.getAction(request.getMethod()) == null) {
	    throw new MethodNotAllowedException(resource.getUri(),
		    request.getMethod());
	}

	ResolvedVariables resolvedVariables = uriResolver.resolve(uriPattern);

	processUriParameters(resolvedVariables, resource, event);

	Flow flow = getFlow(resource, request);
	if (flow == null) {
	    throw new ApikitRuntimeException("Flow not found for resource: "
		    + resource);
	}

	MuleEvent validatedEvent = request.validate(resource.getAction(request
		.getMethod()));

	return new RouterRequest(validatedEvent, flow,
		request.getSuccessStatus());
    }

    private MuleEvent processRouterResponse(MuleEvent event,
	    Integer successStatus) {
	if (event == null || VoidMuleEvent.getInstance().equals(event)) {
	    return event;
	}
	return doProcessRouterResponse(event, successStatus);
    }

    protected abstract MuleEvent doProcessRouterResponse(MuleEvent event,
	    Integer successStatus);

    @Override
    protected MuleEvent processRequest(MuleEvent event) throws MuleException {
	throw new UnsupportedOperationException();
    }

    @Override
    protected MuleEvent processNext(MuleEvent event) throws MuleException {
	throw new UnsupportedOperationException();
    }

    @Override
    protected MuleEvent processResponse(MuleEvent event) throws MuleException {
	throw new UnsupportedOperationException();
    }

    private Map<URIPattern, Resource> getRoutingTable() {
	return config.routingTable;
    }

    private LoadingCache<String, URIResolver> getUriResolverCache() {
	return config.uriResolverCache;
    }

    private LoadingCache<String, URIPattern> getUriPatternCache() {
	return config.uriPatternCache;
    }

    protected abstract MuleEvent handleEvent(MuleEvent event, String path)
	    throws MuleException;

    private HttpRestRequest getHttpRestRequest(MuleEvent event) {
	return config.getHttpRestRequest(event);
    }

    private void processUriParameters(ResolvedVariables resolvedVariables,
	    Resource resource, MuleEvent event)
	    throws InvalidUriParameterException {
	if (logger.isDebugEnabled()) {
	    for (String name : resolvedVariables.names()) {
		logger.debug("        uri parameter: " + name + "="
			+ resolvedVariables.get(name));
	    }
	}

	if (!config.isDisableValidations()) {
	    for (Map.Entry<String, UriParameter> entry : resource
		    .getResolvedUriParameters().entrySet()) {
		String value = (String) resolvedVariables.get(entry.getKey());
		UriParameter uriParameter = entry.getValue();
		if (!uriParameter.validate(value)) {
		    String msg = String.format(
			    "Invalid value '%s' for uri parameter %s. %s",
			    value, entry.getKey(), uriParameter.message(value));
		    throw new InvalidUriParameterException(msg);
		}

	    }
	}
	for (String name : resolvedVariables.names()) {
	    event.getMessage().setInvocationProperty(name,
		    resolvedVariables.get(name));
	}
    }

    protected abstract Flow getFlow(Resource resource, HttpRestRequest request);

    private static class RouterRequest {

	private MuleEvent event;
	private Flow flow;
	private Integer successStatus;

	public RouterRequest(MuleEvent event) {
	    this(event, null, null);
	}

	public RouterRequest(MuleEvent event, Flow flow, Integer successStatus) {
	    this.event = event;
	    this.flow = flow;
	    this.successStatus = successStatus;
	}

	public MuleEvent getEvent() {
	    return event;
	}

	public Flow getFlow() {
	    return flow;
	}

	public Integer getSuccessStatus() {
	    return successStatus;
	}
    }

    /**
     * Returns true if the path of the HTTP request starts with the OData prefix
     * 
     * @param event
     * @return
     */
    protected boolean isODataRequest(MuleEvent event) {
	HttpRestRequest request = getHttpRestRequest(event);
	String path = request.getResourcePath().toLowerCase();

	return path.startsWith(ODATA_SVC_URI_PREFIX);
    }

    protected MuleEvent processODataRequest(MuleEvent event)
	    throws MuleException {

	HttpRestRequest request = getHttpRestRequest(event);
	String path = request.getResourcePath();
	String query = event.getMessage().getInboundProperty("http.query.string");

	try {
	    // OData URI Parser returns processor for Metadata, Service Document
	    // or APIkit request
	    ODataRequestProcessor odataRequestProcessor = ODataUriParser.parse(path, query);

	    ODataPayload odataPayload = odataRequestProcessor.process(event, this);

	    return ODataResponseTransformer.transform(event, odataPayload);

	} catch (Exception ex) {
	    event.getMessage().setOutboundProperty("Content-Type", "application/xml");
	    ODataErrorHandler.handle(ex);
	    throw new MuleRuntimeException(ex);
	}
    }

}
