/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.endtoend;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItems;

import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;

import com.jayway.restassured.RestAssured;

import org.junit.Rule;
import org.junit.Test;

public class ServiceDocumentTestCase extends FunctionalTestCase
{

    @Rule
    public DynamicPort serverPort = new DynamicPort("serverPort");

    @Override
    public int getTestTimeoutSecs()
    {
        return 6000;
    }

    @Override
    protected void doSetUp() throws Exception
    {
        RestAssured.port = serverPort.getNumber();
        super.doSetUp();
    }

    @Override
    protected String getConfigResources()
    {
	return "org/mule/module/apikit/odata/odata.xml";
    }

    
    @Test
    public void serviceSheetJsonPositive() throws Exception
    {
        given().header("Accept", "application/json")
            .expect()
                .response().body("value.name", hasItems("orders", "customers"))
                .header("Content-type", "application/json").statusCode(200)
            .when().get("/api/odata.svc?$format=json");
    }
    
    @Test
    public void serviceSheetXmlPositive() throws Exception
    {
        given().header("Accept", "application/xml")
            .expect()
                .response().body("service.workspace.collection", hasItems("orders"))
                .header("Content-type", "application/xml").statusCode(200)
            .when().get("/api/odata.svc");
    }

}
