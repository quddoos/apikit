/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.processor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.mule.api.MuleEvent;
import org.mule.module.apikit.AbstractRouter;
import org.mule.module.apikit.odata.ODataPayload;
import org.mule.module.apikit.odata.metadata.GatewayMetadataManager;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataMissingFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataNotInitializedException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.util.Helper;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.xml.EdmxFormatWriter;
import org.raml.model.Raml;

import com.google.gson.JsonSyntaxException;

public class ODataMetadataProcessor extends ODataRequestProcessor {

	public ODataMetadataProcessor(Raml raml) {
		super(raml);
	}
	
    @Override
    public ODataPayload process(MuleEvent event, AbstractRouter router) throws Exception {
	event.getMessage().setOutboundProperty("Content-Type", "application/xml");
	return new ODataPayload(createMetadataOutput());
    }

    private String createMetadataOutput() throws URISyntaxException,
	    GatewayMetadataResourceNotFound,
	    GatewayMetadataMissingFieldsException, JsonSyntaxException,
	    FileNotFoundException, IOException, JSONException, GatewayMetadataNotInitializedException {
	Writer w = new StringWriter();
	GatewayMetadataManager gwMetadataManager = getMetadataManager();
	EdmDataServices ees = Helper.createMetadata(gwMetadataManager
		.getEntitySet());
	EdmxFormatWriter.write(ees, w);
	return w.toString();
    }
}
