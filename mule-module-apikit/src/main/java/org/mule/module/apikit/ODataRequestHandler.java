/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.module.apikit.odata.ODataPayload;
import org.mule.module.apikit.odata.ODataResponseTransformer;
import org.mule.module.apikit.odata.ODataUriParser;
import org.mule.module.apikit.odata.error.ODataErrorHandler;
import org.mule.module.apikit.odata.processor.ODataRequestProcessor;
import org.raml.model.Raml;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.visitor.RamlDocumentBuilder;

public class ODataRequestHandler {


	private static final String ODATA_SVC_URI_PREFIX = "/odata.svc";
	
	/**
	 * Returns true if the path of the HTTP request starts with the OData prefix
	 * 
	 * @param event
	 * @return
	 */
	protected static boolean isODataRequest(MuleEvent event, AbstractConfiguration config) {
		HttpRestRequest request = config.getHttpRestRequest(event);
		String path = request.getResourcePath().toLowerCase();

		return path.startsWith(ODATA_SVC_URI_PREFIX);
	}

	protected static MuleEvent processODataRequest(MuleEvent event, AbstractRouter abstractRouter) throws MuleException {

		AbstractConfiguration config = abstractRouter.config;
		
		HttpRestRequest request = config.getHttpRestRequest(event);
		String path = request.getResourcePath();
		String query = event.getMessage().getInboundProperty("http.query.string");

		try {
			ODataRequestProcessor odataRequestProcessor = ODataUriParser.parse(getRaml(config), path, query);

			ODataPayload odataPayload = odataRequestProcessor.process(event, abstractRouter);

			return ODataResponseTransformer.transform(getRaml(config), event, odataPayload);

		} catch (Exception ex) {
			//Remove this poor's man debuger!	
			ex.printStackTrace();
			
			event.getMessage().setOutboundProperty("Content-Type", "application/xml");
			event.getMessage().setPayload(ODataErrorHandler.handle(ex));
			return event;
		}
	}
	
    public static Raml getRaml(AbstractConfiguration config) {
        ResourceLoader loader = config.getRamlResourceLoader();
        config.validateRaml(loader);
        RamlDocumentBuilder builder = new RamlDocumentBuilder(loader);   	
    	return builder.build(config.raml);
    }
}
