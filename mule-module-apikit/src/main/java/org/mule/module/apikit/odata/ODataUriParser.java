/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mule.module.apikit.odata.exception.ODataInvalidQueryRequestException;
import org.mule.module.apikit.odata.processor.ODataApikitProcessor;
import org.mule.module.apikit.odata.processor.ODataMetadataProcessor;
import org.mule.module.apikit.odata.processor.ODataRequestProcessor;
import org.mule.module.apikit.odata.processor.ODataServiceDocumentProcessor;

public class ODataUriParser {

    private static final String ODATA_SVC_URI_PREFIX = "/odata.svc";
    private static final String[] ODATA_VALID_PARAMS = {"orderby", "top", "skip", "filter", "expand", "format", "select", "inlinecount"};

    /**
     * Parses the URI and returns the right processor to handle the request
     * 
     * MAKE IT NICE AND SHINY! TEST IT!
     * 
     * @param event
     * @return
     * @throws ODataInvalidQueryRequestException
     */
    public static ODataRequestProcessor parse(String path, String query)
	    throws ODataInvalidQueryRequestException {

	path = URLDecoder.decode(path.replace(ODATA_SVC_URI_PREFIX, ""));

	// metadata
	if (path.contains("/$metadata")) {
	    if (path.equals("/$metadata") && query.isEmpty()) {
		return new ODataMetadataProcessor();
	    }
	    throw new ODataInvalidQueryRequestException();
	}

	// service document
	if (path.isEmpty() || path.equals("/") || path.startsWith("/?$format=")) {
	    if (query.isEmpty()) {
		return new ODataServiceDocumentProcessor();
	    }
	    throw new ODataInvalidQueryRequestException();
	}

	// entity(s) request
	if (path.matches("^/[a-zA-Z0-9]+\\(*[a-zA-Z0-9]*\\)*$")) {
	    String pattern = "^/([a-zA-Z0-9]+)\\(*([a-zA-Z0-9])\\)*$";
	    Pattern r = Pattern.compile(pattern);
	    Matcher m = r.matcher(path);

	    String id = "";
	    String querystring = "";

	    // handle id
	    if (m.groupCount() == 2) {
		id = "/" + m.group(1);
	    }

	    // handle query
	    if (!query.isEmpty()) {
		String[] queryParams = query.replace("?","").split("&");
		for (String queryParam : queryParams) {
		    String[] elems = queryParam.split("=");
		    if (elems.length == 2 || !validQueryParam(elems[0])) {
			throw new ODataInvalidQueryRequestException("The query parameter '$" + elems[0]  + "' begins with a system-reserved '$' character but is not recognized.");
		    }
		}
		querystring = query.replace("$", "");
	    }
	    	    
	    // build path
	    String apikitFriendlyUri = m.group(1) + id + querystring;

	    return new ODataApikitProcessor(apikitFriendlyUri);
	} else {
	    String pattern = "^/([a-zA-Z0-9]+)/";
	    Pattern r = Pattern.compile(pattern);
	    Matcher m = r.matcher(path);

	    String entity = "";

	    if (m.groupCount() == 1) {
		entity = m.group(0);
	    }

	    throw new ODataInvalidQueryRequestException("Resource not found for the segment '" + entity + "'.");
	}

    }
    
    private static boolean validQueryParam(String param) {
	return (Arrays.asList(ODATA_VALID_PARAMS).contains(param));
    }

}