/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.metadata.raml;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataMissingFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;

import com.google.gson.JsonSyntaxException;

/**
 * 
 * @author arielsegura
 */
public class RamlParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws JsonSyntaxException, FileNotFoundException, IOException, GatewayMetadataMissingFieldsException, GatewayMetadataResourceNotFound, JSONException {
		new RamlParser().getEntitiesFromRaml("datagateway-definition.raml");
	}

}
