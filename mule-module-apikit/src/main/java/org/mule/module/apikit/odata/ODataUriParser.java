/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata;

import org.mule.module.apikit.odata.processor.ODataApikitProcessor;
import org.mule.module.apikit.odata.processor.ODataMetadataProcessor;
import org.mule.module.apikit.odata.processor.ODataRequestProcessor;
import org.mule.module.apikit.odata.processor.ODataServiceDocumentProcessor;


public class ODataUriParser {

    private static final String ODATA_SVC_URI_PREFIX = "odata.svc/";
    
    /**
     * Parses the URI and returns the right processor to handle 
     * the request
     * @param event
     * @return
     */
    public static ODataRequestProcessor parse(String path, String query) {
	
	path = path.replace(ODATA_SVC_URI_PREFIX, "");
	
        //make nice
	if (path.contains("$metadata")) {
	    return new ODataMetadataProcessor();
	} else if (path.isEmpty()) {
	    return new ODataServiceDocumentProcessor();
	} else {
	    String apikitFriendlyUri = "/resource/1"; //complete parsing and transformation
	    return new ODataApikitProcessor(apikitFriendlyUri);
	}
    	    	
    }
    
}
