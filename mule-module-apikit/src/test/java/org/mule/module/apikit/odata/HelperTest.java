package org.mule.module.apikit.odata;

import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataMissingFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataNotInitializedException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.util.Helper;

public class HelperTest {

	@Before
	public void setUp() throws Exception {
	}

	@Ignore
	@Test
	public void test() throws GatewayMetadataNotInitializedException, GatewayMetadataMissingFieldsException, GatewayMetadataResourceNotFound, JSONException, IOException {
		Assert.assertEquals(Helper.refreshMetadataManager("datagateway-definition.raml").getEntitySet(), Helper.refreshMetadataManager(new URL("http://localhost:8081/api/config")).getEntitySet());
	}
}
