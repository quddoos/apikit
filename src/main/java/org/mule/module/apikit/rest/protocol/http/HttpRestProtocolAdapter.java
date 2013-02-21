/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.apikit.rest.protocol.http;

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.module.apikit.UnauthorizedException;
import org.mule.module.apikit.rest.MediaTypeNotAcceptableException;
import org.mule.module.apikit.rest.RestException;
import org.mule.module.apikit.rest.RestRequest;
import org.mule.module.apikit.rest.UnsupportedMediaTypeException;
import org.mule.module.apikit.rest.action.ActionType;
import org.mule.module.apikit.rest.action.ActionTypeNotAllowedException;
import org.mule.module.apikit.rest.protocol.RestProtocolAdapter;
import org.mule.module.apikit.rest.resource.ResourceNotFoundException;
import org.mule.transport.NullPayload;
import org.mule.util.StringUtils;

import com.google.common.net.MediaType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpRestProtocolAdapter implements RestProtocolAdapter
{
    private URI baseURI;
    private URI resourceURI;
    private ActionType actionType;
    private List<MediaType> acceptableResponseMediaTypes = Collections.emptyList();;
    private MediaType requestMediaType;
    private Map<String, Object> queryParams;

    public HttpRestProtocolAdapter(MuleEvent event)
    {
        MuleMessage message = event.getMessage();
        this.baseURI = event.getMessageSourceURI();
        if (message.getInboundProperty("host") != null)
        {
            String hostHeader = message.getInboundProperty("host");
            if (hostHeader.indexOf(':') != -1)
            {
                String host = hostHeader.substring(0, hostHeader.indexOf(':'));
                int port = Integer.parseInt(hostHeader.substring(hostHeader.indexOf(':') + 1));
                try
                {
                    String requestPath;
                    requestPath = (String) message.getInboundProperty("http.request.path");
                    this.resourceURI = new URI("http", null, host, port, requestPath, null, null);
                }
                catch (URISyntaxException e)
                {
                    throw new IllegalArgumentException("Cannot parse URI", e);
                }
            }
            else
            {
                try
                {
                    String requestPath;
                    requestPath = (String) message.getInboundProperty("http.request.path");
                    this.resourceURI = new URI("http", null, (String) message.getInboundProperty("host"), 80,
                        requestPath, null, null);
                }
                catch (URISyntaxException e)
                {
                    throw new IllegalArgumentException("Cannot parse URI", e);
                }
            }
        }
        else
        {
            try
            {
                this.resourceURI = new URI("http", null, baseURI.getHost(), baseURI.getPort(),
                    (String) message.getInboundProperty("http.request.path"), null, null);
            }
            catch (URISyntaxException e)
            {
                throw new IllegalArgumentException("Cannot parse URI", e);
            }
        }
        String method = message.getInboundProperty("http.method");
        actionType = ActionType.fromHttpMethod(method);

        if (!StringUtils.isBlank((String) message.getInboundProperty("accept")))
        {
            this.acceptableResponseMediaTypes = parseAcceptHeader((String) message.getInboundProperty("accept"));
        }

        if (!StringUtils.isBlank((String) message.getInboundProperty("content-type")))
        {
            this.requestMediaType = MediaType.parse((String) message.getInboundProperty("content-type"));
        }
        if (this.requestMediaType == null
            && !StringUtils.isBlank((String) message.getOutboundProperty("content-type")))
        {
            this.requestMediaType = MediaType.parse((String) message.getOutboundProperty("content-type"));
        }

        this.queryParams = message.getInboundProperty("http.query.params");
    }

    @Override
    public ActionType getActionType()
    {
        return actionType;
    }

    @Override
    public URI getURI()
    {
        return resourceURI;
    }

    @Override
    public URI getBaseURI()
    {
        return baseURI;
    }

    @Override
    public Map<String, Object> getQueryParameters()
    {
        return queryParams;
    }

    private Set<String> actionTypesToHttpMethods(Set<ActionType> actionTypes)
    {
        Set<String> set = new HashSet<String>();
        for (ActionType type : actionTypes)
        {
            set.add(type.toHttpMethod());
        }
        return set;
    }

    @Override
    public void handleException(RestException re, RestRequest request)
    {
        MuleMessage message = request.getMuleEvent().getMessage();
        if (re instanceof ActionTypeNotAllowedException)
        {
            ActionTypeNotAllowedException anse = (ActionTypeNotAllowedException) re;
            message.setOutboundProperty("http.status",
                HttpStatusCode.CLIENT_ERROR_METHOD_NOT_ALLOWED.getCode());
            message.setOutboundProperty("Allow",
                StringUtils.join(actionTypesToHttpMethods(anse.getResource().getAllowedActionTypes()), " ,"));
            message.setPayload(NullPayload.getInstance());

        }
        else if (re instanceof ResourceNotFoundException)
        {
            message.setOutboundProperty("http.status", HttpStatusCode.CLIENT_ERROR_NOT_FOUND.getCode());
            message.setPayload(NullPayload.getInstance());
        }
        else if (re instanceof MediaTypeNotAcceptableException)
        {
            message.setOutboundProperty("http.status", HttpStatusCode.CLIENT_ERROR_NOT_ACCEPTABLE.getCode());
            message.setPayload(NullPayload.getInstance());
        }
        else if (re instanceof UnsupportedMediaTypeException)
        {
            message.setOutboundProperty("http.status",
                HttpStatusCode.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE.getCode());
            message.setPayload(NullPayload.getInstance());
        }
        else if (re instanceof UnauthorizedException)
        {
            message.setOutboundProperty("http.status", HttpStatusCode.CLIENT_ERROR_UNAUTHORIZED.getCode());
            message.setPayload(NullPayload.getInstance());
        }
    }

    @Override
    public List<MediaType> getAcceptableResponseMediaTypes()
    {
        return acceptableResponseMediaTypes;
    }

    @Override
    public MediaType getRequestMediaType()
    {
        return requestMediaType;
    }

    private List<MediaType> parseAcceptHeader(String acceptHeader)
    {
        List<MediaType> mediaTypes = new LinkedList<MediaType>();
        String[] types = StringUtils.split(acceptHeader, ',');
        if (types != null)
        {
            for (String mediaType : types)
            {
                mediaTypes.add(MediaType.parse(mediaType));
            }
        }
        return mediaTypes;
    }

}