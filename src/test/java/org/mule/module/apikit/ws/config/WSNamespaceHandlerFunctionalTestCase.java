/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.apikit.ws.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.mule.module.apikit.api.WebService;
import org.mule.module.apikit.ws.WSWebService;
import org.mule.module.apikit.ws.WSWebServiceInterface;
import org.mule.tck.junit4.FunctionalTestCase;

import org.junit.Test;

public class WSNamespaceHandlerFunctionalTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "org/mule/module/wsapi/ws/config/namespace-handler-config.xml, org/mule/module/wsapi/test-flows-config.xml";
    }

    @Test
    public void testInterfaceCreation() throws Exception
    {
        assertNotNull(muleContext.getRegistry().lookupObject("myInterface"));
        assertEquals(WSWebServiceInterface.class, muleContext.getRegistry()
            .lookupObject("myInterface")
            .getClass());
    }

    @Test
    public void testServiceCreation() throws Exception
    {
        assertNotNull(muleContext.getRegistry().lookupObject("myService"));
        assertEquals(WSWebService.class, muleContext.getRegistry().lookupObject("myService").getClass());
    }

    @Test
    public void testServiceInterfaceReference() throws Exception
    {
        assertEquals(muleContext.getRegistry().lookupObject("myInterface"),
            ((WebService) muleContext.getRegistry().lookupObject("myService")).getInterface());
    }

}