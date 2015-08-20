/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.metadata.raml;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataFormatException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinition;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionProperty;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionSet;

/**
 * 
 * @author arielsegura
 */
public class RamlParserTest {
	
	RamlParser ramlParser;
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private EntityDefinitionSet mockEntitySet;

	@Before
	public void setUp() throws Exception {
		ramlParser = new RamlParser();
		mockEntitySet = mockEntitySet();
	}
	
	private EntityDefinitionSet mockEntitySet() {
		EntityDefinitionSet newEntitySet = new EntityDefinitionSet();
		EntityDefinition entityDefinition;

		entityDefinition = new EntityDefinition("gateways", "gateways");
		entityDefinition.setHasPrimaryKey(true);
		// String name, String sample, String type, boolean nullable, int length,
		// String description, boolean key
		entityDefinition.addProperty(new EntityDefinitionProperty("id", "12", "integer", false, 4, "bla", true));
		entityDefinition.addProperty(new EntityDefinitionProperty("name", "Ariel", "string", true, 45, "bla", false));
		entityDefinition.addProperty(new EntityDefinitionProperty("description", "Ariel", "string", true, 45, "bla", false));
		entityDefinition.addProperty(new EntityDefinitionProperty("status", "Ariel", "string", true, 255, "bla", false));
		entityDefinition.addProperty(new EntityDefinitionProperty("published", "Ariel", "boolean", true, 5, "bla", false));
		entityDefinition.addProperty(new EntityDefinitionProperty("draft", "Ariel", "boolean", true, 5, "bla", false));
		entityDefinition.addProperty(new EntityDefinitionProperty("ch_domain", "Ariel", "string", true, 5, "bla", false));
		entityDefinition.addProperty(new EntityDefinitionProperty("ch_full_domain", "Ariel", "string", true, 5, "bla", false));
		newEntitySet.addEntity(entityDefinition);

		entityDefinition = new EntityDefinition("users", "users");
		entityDefinition.setHasPrimaryKey(true);
		entityDefinition.addProperty(new EntityDefinitionProperty("id", "1", "integer", false, 4, "ID Field", true));
		entityDefinition.addProperty(new EntityDefinitionProperty("first_name", "Marty", "string", true, 45, "First Name", false));
		entityDefinition.addProperty(new EntityDefinitionProperty("last_name", "Mc Fly", "string", true, 45, "Last Name", false));
		entityDefinition.addProperty(new EntityDefinitionProperty("email", "Mc Fly", "string", true, 45, "Last Name", false));
		newEntitySet.addEntity(entityDefinition);

		return newEntitySet;
	}
	
	
	@Test
	public void incompleteSchemaTest() throws GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataFormatException{
	    thrown.expect(GatewayMetadataFieldsException.class);
	    thrown.expectMessage("JSONObject[\"nullable\"] not found.");
		ramlParser.getEntitiesFromRaml("src/test/resources/org/mule/module/apikit/odata/metadata/raml/incomplete-schema.raml");
	}
	
	@Test
	public void withSomeTraitsTest() throws GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataFormatException{
		thrown.expect(GatewayMetadataFormatException.class);
	    thrown.expectMessage("RAML is invalid. See log list.");
		ramlParser.getEntitiesFromRaml("src/test/resources/org/mule/module/apikit/odata/metadata/raml/with-some-traits.raml");
	}
	
	@Test
	public void withoutTraitsTest() throws GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataFormatException{
		thrown.expect(GatewayMetadataFormatException.class);
	    thrown.expectMessage("RAML is invalid. See log list.");
		ramlParser.getEntitiesFromRaml("src/test/resources/org/mule/module/apikit/odata/metadata/raml/without-traits.raml");
	}
	
	@Test
	public void withoutSchemas() throws GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataFormatException{
		thrown.expect(GatewayMetadataFormatException.class);
	    thrown.expectMessage("No schemas found.");
		ramlParser.getEntitiesFromRaml("src/test/resources/org/mule/module/apikit/odata/metadata/raml/without-schemas.raml");
	}
	
	@Test
	public void withSchemasKey() throws GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataFormatException{
		thrown.expect(GatewayMetadataFieldsException.class);
		ramlParser.getEntitiesFromRaml("src/test/resources/org/mule/module/apikit/odata/metadata/raml/with-schemas-key.raml");
	}
	
	@Test
	public void schemasMultipleKey() throws GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataFormatException{
		thrown.expect(GatewayMetadataFormatException.class);
		thrown.expectMessage("Duplicate key \"remoteName\"");
		ramlParser.getEntitiesFromRaml("src/test/resources/org/mule/module/apikit/odata/metadata/raml/schemas-multiple-keys.raml");
	}
	
	@Test
	public void schemasWithoutProperties() throws GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataFormatException{
		thrown.expect(GatewayMetadataFieldsException.class);
		thrown.expectMessage("JSONObject[\"properties\"] not found.");
		ramlParser.getEntitiesFromRaml("src/test/resources/org/mule/module/apikit/odata/metadata/raml/schema-without-properties.raml");
	}
	
	@Test
	public void invalidKeyValue() throws GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataFormatException{
		thrown.expect(GatewayMetadataFieldsException.class);
		thrown.expectMessage("JSONObject[\"key\"] is not a Boolean.");
		ramlParser.getEntitiesFromRaml("src/test/resources/org/mule/module/apikit/odata/metadata/raml/invalid-key-value.raml");
	}
	
	@Test
	public void invalidLengthValue() throws GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataFormatException{
		thrown.expect(GatewayMetadataFieldsException.class);
		thrown.expectMessage("JSONObject[\"maxLength\"] is not a number.");
		ramlParser.getEntitiesFromRaml("src/test/resources/org/mule/module/apikit/odata/metadata/raml/invalid-length-value.raml");
	}
	
	@Test
	public void invalidNullableValue() throws GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataFormatException{
		thrown.expect(GatewayMetadataFieldsException.class);
		thrown.expectMessage("JSONObject[\"nullable\"] is not a Boolean.");
		ramlParser.getEntitiesFromRaml("src/test/resources/org/mule/module/apikit/odata/metadata/raml/invalid-nullable-value.raml");
	}

	@Test
	public void testPositive() throws GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataFormatException {
		EntityDefinitionSet entitySet = ramlParser.getEntitiesFromRaml("datagateway-definition.raml");
		EntityDefinition entityDefinition = entitySet.toList().get(0);
		for (int i = 0; i < mockEntitySet.toList().get(0).getProperties().size(); i++) {
			EntityDefinitionProperty expectedProperty = mockEntitySet.toList().get(0).getProperties().get(i);
			Assert.assertEquals(expectedProperty, entityDefinition.getProperties().get(i));
		}
		Assert.assertEquals(mockEntitySet.toList().get(0), entityDefinition);
	}
	
	

}
