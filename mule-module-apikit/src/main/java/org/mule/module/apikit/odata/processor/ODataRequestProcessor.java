/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.processor;

import org.json.JSONException;
import org.mule.api.MuleEvent;
import org.mule.module.apikit.AbstractRouter;
import org.mule.module.apikit.odata.ODataPayload;
import org.mule.module.apikit.odata.metadata.GatewayMetadataManager;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataMissingFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.util.Helper;
import org.raml.model.Raml;

public abstract class ODataRequestProcessor {

	private Raml raml;
	
	public ODataRequestProcessor(Raml raml) {
		this.raml = raml;
	}
	
    public abstract ODataPayload process (MuleEvent event, AbstractRouter router) throws Exception;
    
    protected GatewayMetadataManager  getMetadataManager() throws GatewayMetadataMissingFieldsException, GatewayMetadataResourceNotFound, JSONException {
    	return Helper.refreshMetadataManager(raml);
    }
}
