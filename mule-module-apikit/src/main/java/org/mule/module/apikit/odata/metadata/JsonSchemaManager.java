/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.metadata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinition;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionProperty;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionSet;
import org.mule.module.apikit.odata.util.FileUtils;

import com.google.gson.JsonSyntaxException;

/**
 * 
 * @author arielsegura
 */
public class JsonSchemaManager {

    private static final String SAMPLE_PROPERTY_TEXT = "sample";
    private static final String TYPE_PROPERTY_TEXT = "type";
    private static final String NULLABLE_PROPERTY_TEXT = "nullable";
    private static final String LENGTH_PROPERTY_TEXT = "length";
    private static final String DESCRIPTION_PROPERTY_TEXT = "description";
    private static final String KEY_PROPERTY_TEXT = "key";

    public JsonSchemaManager() {

    }

    public EntityDefinitionSet getEntitiesFromSchema() throws JsonSyntaxException,
	    FileNotFoundException, IOException,
	    GatewayMetadataFieldsException,
	    GatewayMetadataResourceNotFound, JSONException {

	EntityDefinitionSet entitySet = new EntityDefinitionSet();
	JSONObject obj = new JSONObject(
		FileUtils.readFromFile("json-schema.json"));

	JSONArray schemas = obj.getJSONArray("schemas");
	for (int i = 0; i < schemas.length(); i++) {
	    if (schemas.getJSONObject(i).length() != 1) {
		// wrong format?
	    }

	    String entityName = (String) schemas.getJSONObject(i).keys().next();
	    JSONObject schema = schemas.getJSONObject(i).getJSONObject(
		    entityName);
	    String remoteEntity = schema.getString("remoteEntity");
	    checkFieldNotNull("Remote Entity", remoteEntity);

	    EntityDefinition entity = new EntityDefinition(entityName,
		    remoteEntity);

	    JSONObject properties = schema.getJSONObject("properties");
	    if (properties == null) {
		throw new GatewayMetadataResourceNotFound(
			"Properties not found in entity " + entityName + ".");
	    }
	    entity.setProperties(parseEntityProperties(properties));
	    entitySet.addEntity(entity);
	}

	return entitySet;
    }

    private List<EntityDefinitionProperty> parseEntityProperties(JSONObject properties)
	    throws GatewayMetadataFieldsException, JSONException {
	List<EntityDefinitionProperty> entityProperties = new ArrayList<EntityDefinitionProperty>();
	if (properties != null) {
	    Iterator it = properties.keys();
	    while (it.hasNext()) {
		String propertyName = (String) it.next();
		JSONObject property = properties.getJSONObject(propertyName);
		String sample = String.valueOf(property
			.get(SAMPLE_PROPERTY_TEXT));
		checkFieldNotNull("Sample", sample);
		String type = String.valueOf(property.get(TYPE_PROPERTY_TEXT));
		checkFieldNotNull("Type", type);
		Boolean nullable = Boolean.valueOf(String.valueOf(property
			.get(NULLABLE_PROPERTY_TEXT)));
		checkFieldNotNull("Nullable", nullable);
		Integer length = Integer.valueOf(String.valueOf(property
			.get(LENGTH_PROPERTY_TEXT)));
		checkFieldNotNull("Length", length);
		String description = String.valueOf(property
			.get(DESCRIPTION_PROPERTY_TEXT));
		checkFieldNotNull("Description", description);
		Boolean key = false;
		if (property.has(KEY_PROPERTY_TEXT)) {
		    key = Boolean.valueOf(String.valueOf(property
			    .get(KEY_PROPERTY_TEXT)));
		    checkFieldNotNull("Key", key);
		}
		EntityDefinitionProperty newEntityProperty = new EntityDefinitionProperty(
			propertyName, sample, type, nullable, length,
			description, key);
		entityProperties.add(newEntityProperty);
	    }
	}
	return entityProperties;
    }

    private void checkFieldNotNull(String expected, Object actual)
	    throws GatewayMetadataFieldsException {
	if (actual == null) {
	    throw new GatewayMetadataFieldsException(expected
		    + " not found.");
	}
    }
}
