/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata;

import org.mule.api.MuleEvent;
import org.mule.module.apikit.odata.processor.formatters.ODataPayloadFormatter;
import org.mule.module.apikit.odata.processor.formatters.ODataPayloadFormatter.Format;
import org.raml.model.Raml;

public class ODataResponseTransformer {

    public static MuleEvent transform(Raml raml, MuleEvent event, ODataPayload payload)
	    throws Exception {
		if (payload.getContent() != null) {
			event.getMessage().setPayload(payload.getContent());
		} else {

			Format format = Format.Atom;
			if (event.getMessage().getOutboundProperty("Content-Type")
					.equals("application/json")) {
				format = Format.Json;
			}

			StringBuffer result = new StringBuffer();

			ODataPayloadFormatter formatter = payload.getFormatter();
			result.append(formatter.format(format));
			event.getMessage().setPayload(result.toString());
		}

		if (event.getMessage().getOutboundProperty("http.status") == null) {
			event.getMessage().setOutboundProperty("http.status", 200);
		}

		return event;
    }
}
