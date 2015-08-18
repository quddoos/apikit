/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata;

import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataFormatException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataNotInitializedException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.util.Helper;

public class HelperTest {

	@Before
	public void setUp() throws Exception {
	}

	@Ignore
	@Test
	public void test() throws GatewayMetadataNotInitializedException, GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, JSONException, IOException, GatewayMetadataFormatException {
		Assert.assertEquals(Helper.refreshMetadataManager("datagateway-definition.raml").getEntitySet(), Helper.refreshMetadataManager(new URL("http://localhost:8081/api/config")).getEntitySet());
	}
}
