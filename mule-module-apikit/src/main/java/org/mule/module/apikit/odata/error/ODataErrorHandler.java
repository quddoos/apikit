package org.mule.module.apikit.odata.error;

public class ODataErrorHandler {
	
	private static final String ERROR_ENVELOPE = "<?xml version=\"1.0\" encoding=\"utf-8\"?><m:error xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><m:code /><m:message xml:lang=\"en-US\">%ERRORMSG%</m:message></m:error>";
	private static final String ERROR_MSG_PLACEHOLDER = "%ERRORMSG%";
	
	public static String handle(Exception ex) {	
		//TODO Bug: This method fails if ex.getMessage() returns null. 
		return ERROR_ENVELOPE.replace(ERROR_MSG_PLACEHOLDER, ex.getMessage());
	}
	
}
