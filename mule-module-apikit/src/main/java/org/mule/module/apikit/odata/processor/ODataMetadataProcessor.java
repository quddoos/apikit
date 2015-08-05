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
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataFileNotFoundException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataMissingFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.metadata.exception.WrongYamlFormatException;
import org.mule.module.apikit.odata.util.Helper;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.xml.EdmxFormatWriter;

import com.google.gson.JsonSyntaxException;

public class ODataMetadataProcessor extends ODataRequestProcessor {

    @Override
    public ODataPayload process(MuleEvent event, AbstractRouter router) throws Exception {
	event.getMessage().setOutboundProperty("Content-Type", "application/xml");
	return new ODataPayload(createMetadataOutput());
    }

    private String createMetadataOutput() throws URISyntaxException,
	    WrongYamlFormatException, GatewayMetadataFileNotFoundException,
	    GatewayMetadataResourceNotFound,
	    GatewayMetadataMissingFieldsException, JsonSyntaxException,
	    FileNotFoundException, IOException, JSONException {
	Writer w = new StringWriter();
	GatewayMetadataManager gwMetadataManager = Helper.getMetadataManager();
	EdmDataServices ees = Helper.createMetadata(gwMetadataManager
		.getEntitySet());
	EdmxFormatWriter.write(ees, w);
	return w.toString();
    }
}
