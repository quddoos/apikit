/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.processor;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mule.api.MuleEvent;
import org.mule.module.apikit.AbstractRouter;
import org.mule.module.apikit.odata.ODataPayload;
import org.mule.module.apikit.odata.metadata.GatewayMetadataManager;
import org.mule.module.apikit.odata.util.Helper;
import org.mule.module.apikit.odata.util.UriInfoImpl;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.FormatWriter;
import org.odata4j.format.FormatWriterFactory;

public class ODataServiceDocumentProcessor extends ODataRequestProcessor {

    @Override
    public ODataPayload process(MuleEvent event, AbstractRouter router) throws Exception {
	String format = "json";
	String url = "www.google.com";
	return new ODataPayload(createServiceDocumentOutput(format, url));
    }

    private String createServiceDocumentOutput(String format, String url)
	    throws Exception {

	FormatWriter<EdmDataServices> fw = FormatWriterFactory.getFormatWriter(
		EdmDataServices.class,
		Arrays.asList(MediaType.valueOf(MediaType.WILDCARD)), format,
		null);
	GatewayMetadataManager gwMetadataManager = Helper.getMetadataManager();
	EdmDataServices ees = Helper.createMetadata(gwMetadataManager
		.getEntitySet());
	UriInfo uriInfo = new UriInfoImpl(url);
	Writer w = new StringWriter();

	fw.write(uriInfo, w, ees);

	return w.toString();
    }
}
