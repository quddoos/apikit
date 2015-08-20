/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.processor;

import org.mule.api.MuleEvent;
import org.mule.module.apikit.AbstractRouter;
import org.mule.module.apikit.odata.ODataPayload;
import org.mule.module.apikit.odata.processor.formatters.ServiceDocumentPayloadFormatter;
import org.raml.model.Raml;

public class ODataServiceDocumentProcessor extends ODataRequestProcessor
{

	public ODataServiceDocumentProcessor(Raml raml)
	{
		super(raml);
	}

	@Override
	public ODataPayload process(MuleEvent event, AbstractRouter router) throws Exception
	{
		final String url = "http://localhost";
		ODataPayload result = new ODataPayload();
		result.setFormatter(new ServiceDocumentPayloadFormatter(getMetadataManager(), url));
		return result;
	}

}
