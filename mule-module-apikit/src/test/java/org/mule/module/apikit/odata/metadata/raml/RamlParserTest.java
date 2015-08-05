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
