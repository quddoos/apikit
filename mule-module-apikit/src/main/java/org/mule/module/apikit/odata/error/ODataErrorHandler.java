/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.error;

public class ODataErrorHandler {

	private static final String ERROR_ENVELOPE = "<?xml version=\"1.0\" encoding=\"utf-8\"?><m:error xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><m:code /><m:message xml:lang=\"en-US\">%ERRORMSG%</m:message></m:error>";
	private static final String ERROR_MSG_PLACEHOLDER = "%ERRORMSG%";

	public static String handle(Exception ex) {
		return ERROR_ENVELOPE.replace(ERROR_MSG_PLACEHOLDER, ex.getMessage());
	}

}
