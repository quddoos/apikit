/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.processor;

import java.util.ArrayList;
import java.util.List;

import org.mule.api.MuleEvent;
import org.mule.module.apikit.AbstractRouter;
import org.mule.module.apikit.odata.ODataPayload;
import org.mule.module.apikit.odata.model.Entity;

public class ODataApikitProcessor extends ODataRequestProcessor {

    private String uri;

    public ODataApikitProcessor(String uri) {
	super();
	this.uri = uri;
    }

    @Override
    public ODataPayload process(MuleEvent event, AbstractRouter router)
	    throws Exception {
	return new ODataPayload(processEntityRequest(event, router));
    }

    public List<Entity> processEntityRequest(MuleEvent event, AbstractRouter router) {
	List<Entity> entities = new ArrayList<Entity>();
	
	try {
	    final StringBuffer result = new StringBuffer();
	    
	    MuleEvent response = router.process(event);
	    Object payload = response.getMessage().getPayload();

	    if (payload instanceof List) {
		entities = (List<Entity>) payload;
	    } 
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	
	return entities;
    }
}
