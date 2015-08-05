/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.mule.api.MuleEvent;
import org.mule.module.apikit.odata.metadata.GatewayMetadataManager;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataMissingFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataNotInitializedException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionSet;
import org.mule.module.apikit.odata.model.Entity;
import org.mule.module.apikit.odata.util.Helper;
import org.mule.module.apikit.odata.util.UriInfoImpl;
import org.odata4j.format.FormatWriter;
import org.odata4j.format.FormatWriterFactory;
import org.odata4j.producer.EntitiesResponse;

import com.google.gson.JsonSyntaxException;

public class ODataResponseTransformer {

    public static MuleEvent transform(MuleEvent event, ODataPayload payload)
	    throws Exception {
	if (!payload.getContent().isEmpty()) {
	    event.getMessage().setPayload(payload.getContent());
	} else {
	    String url = "bla";
	    
	    String format = "xml";
	    if (event.getMessage().getOutboundProperty("Content-Type").equals("application/json")) {
		format = "json";
	    }
	    
	    String entityName = "bla";
	    String entityName2 = entityName.replaceAll("\\(.*\\)", "");
	    StringBuffer result = new StringBuffer();
	    result.append(writeOutput(payload.getEntities(), entityName2, url, format));
	    event.getMessage().setPayload(result.toString());
	}

	if (event.getMessage().getOutboundProperty("http.status") == null) {
	    event.getMessage().setOutboundProperty("http.status", 200);
	}

	return event;
    }

    private static String writeOutput(List<Entity> entities2,
	    String entityName, String url, String format)
	    throws JsonSyntaxException, FileNotFoundException, IOException, JSONException, GatewayMetadataMissingFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataNotInitializedException {
	StringWriter sw = new StringWriter();
	FormatWriter<EntitiesResponse> fw = FormatWriterFactory
		.getFormatWriter(EntitiesResponse.class,
			Arrays.asList(MediaType.valueOf(MediaType.WILDCARD)),
			format, null);

	GatewayMetadataManager gwMetadataManager = Helper.getMetadataManager();
	EntityDefinitionSet entitySet = gwMetadataManager.getEntitySet();
	EntitiesResponse entitiesResponse = Helper.convertEntitiesToOEntities(
		entities2, entityName, entitySet);

	UriInfo uriInfo = new UriInfoImpl(url);
	fw.write(uriInfo, sw, entitiesResponse);
	return sw.toString();
    }
}
