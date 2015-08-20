/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.metadata;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataEntityNotFoundException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataFormatException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataNotInitializedException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinition;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionProperty;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionSet;

public class GatewayMetadataTestCase {

	GatewayMetadataManager metadataManager;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private EntityDefinitionSet mockEntitySet;

	@Before
	public void setUp() throws Exception {
		metadataManager = new GatewayMetadataManager();
		mockEntitySet = mockEntitySet();
	}

	@Test
	public void transformResponse() {
		fail("Not yet implemented");
	}
	
	@Test
	public void retrieveEntityPositive() throws GatewayMetadataEntityNotFoundException, GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataFormatException, GatewayMetadataNotInitializedException{
	    metadataManager.refreshMetadata("datagateway-definition.raml");
	    Assert.assertEquals(metadataManager.getEntityByName("gateways"), mockEntitySet.toList().get(0));
	}
	
	@Test
	public void notInitializedTest() throws GatewayMetadataEntityNotFoundException, GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataFormatException, GatewayMetadataNotInitializedException{
		thrown.expect(GatewayMetadataNotInitializedException.class);
		Assert.assertEquals(metadataManager.getEntityByName("gateways"), mockEntitySet.toList().get(0));
	}
	
	@Test
	public void retrieveEntityNegative() throws GatewayMetadataFieldsException, GatewayMetadataResourceNotFound, GatewayMetadataFormatException, GatewayMetadataEntityNotFoundException, GatewayMetadataNotInitializedException{
		thrown.expect(GatewayMetadataEntityNotFoundException.class);
	    thrown.expectMessage("Entity bla not found.");
	    metadataManager.refreshMetadata("datagateway-definition.raml");
	    metadataManager.getEntityByName("bla");
	}

	private EntityDefinitionSet mockEntitySet() {
		EntityDefinitionSet newEntitySet = new EntityDefinitionSet();
		EntityDefinition entityDefinition;

		entityDefinition = new EntityDefinition("gateways", "gateways");
		entityDefinition.setHasPrimaryKey(true);
		// String name, String sample, String type, boolean nullable, int
		// length,
		// String description, boolean key
		entityDefinition.addProperty(new EntityDefinitionProperty("id", "12",
				"integer", false, 4, "bla", true));
		entityDefinition.addProperty(new EntityDefinitionProperty("name",
				"Ariel", "string", true, 45, "bla", false));
		entityDefinition.addProperty(new EntityDefinitionProperty(
				"description", "Ariel", "string", true, 45, "bla", false));
		entityDefinition.addProperty(new EntityDefinitionProperty("status",
				"Ariel", "string", true, 255, "bla", false));
		entityDefinition.addProperty(new EntityDefinitionProperty("published",
				"Ariel", "boolean", true, 5, "bla", false));
		entityDefinition.addProperty(new EntityDefinitionProperty("draft",
				"Ariel", "boolean", true, 5, "bla", false));
		entityDefinition.addProperty(new EntityDefinitionProperty("ch_domain",
				"Ariel", "string", true, 5, "bla", false));
		entityDefinition.addProperty(new EntityDefinitionProperty(
				"ch_full_domain", "Ariel", "string", true, 5, "bla", false));
		newEntitySet.addEntity(entityDefinition);

		entityDefinition = new EntityDefinition("users", "users");
		entityDefinition.setHasPrimaryKey(true);
		entityDefinition.addProperty(new EntityDefinitionProperty("id", "1",
				"integer", false, 4, "ID Field", true));
		entityDefinition.addProperty(new EntityDefinitionProperty("first_name",
				"Marty", "string", true, 45, "First Name", false));
		entityDefinition.addProperty(new EntityDefinitionProperty("last_name",
				"Mc Fly", "string", true, 45, "Last Name", false));
		entityDefinition.addProperty(new EntityDefinitionProperty("email",
				"Mc Fly", "string", true, 45, "Last Name", false));
		newEntitySet.addEntity(entityDefinition);

		return newEntitySet;
	}

}
