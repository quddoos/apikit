/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.metadata.raml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataFormatException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinition;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionProperty;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionSet;
import org.raml.model.Raml;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.visitor.RamlValidationService;

/**
 * 
 * @author arielsegura
 */
public class RamlParser {

	private static final String SAMPLE_PROPERTY_TEXT = "sample";
	private static final String TYPE_PROPERTY_TEXT = "type";
	private static final String NULLABLE_PROPERTY_TEXT = "nullable";
	private static final String LENGTH_PROPERTY_TEXT = "maxLength";
	private static final String DESCRIPTION_PROPERTY_TEXT = "description";
	private static final String KEY_PROPERTY_TEXT = "key";

	public RamlParser() {
		// TODO Auto-generated constructor stub
	}
	
	private void validateResults(List<ValidationResult> results) throws GatewayMetadataFormatException{
		if (!results.isEmpty()) {
			for (ValidationResult result : results) {
				Logger.getLogger(RamlParser.class).error(result.toString());
			}
			throw new GatewayMetadataFormatException("RAML is invalid. See log list.");
		} else {
			Logger.getLogger(RamlParser.class).info("RAML Validation ok");
		}
	}

	private void validateRaml(String path)  throws GatewayMetadataFormatException{
		List<ValidationResult> results = RamlValidationService.createDefault()
				.validate(path);
		validateResults(results);
	}

	private void validateRaml(InputStream inputStream)   throws GatewayMetadataFormatException{
		List<ValidationResult> results = RamlValidationService.createDefault()
				.validate(inputStream);
		validateResults(results);
	}

	public EntityDefinitionSet getEntitiesFromRaml(String path)
			throws GatewayMetadataFieldsException,
			GatewayMetadataResourceNotFound, GatewayMetadataFormatException {
		validateRaml(path);
		return getEntitiesFromRaml(new RamlDocumentBuilder().build(path));
	}

	public EntityDefinitionSet getEntitiesFromRaml(InputStream inputStream)
			throws GatewayMetadataFieldsException,
			GatewayMetadataResourceNotFound, GatewayMetadataFormatException {
		validateRaml(inputStream);
		return getEntitiesFromRaml(new RamlDocumentBuilder().build(inputStream));
	}

	public EntityDefinitionSet getEntitiesFromRaml(Raml raml)
			throws GatewayMetadataFieldsException,
			GatewayMetadataResourceNotFound, GatewayMetadataFormatException {

		EntityDefinitionSet entitySet = new EntityDefinitionSet();

		List<Map<String, String>> schemas = raml.getSchemas();
		
		if(schemas.isEmpty()){
			throw new GatewayMetadataFormatException(
					"No schemas found. ");
		}

		for (int i = 0; i < schemas.size(); i++) {
			Map<String, String> schema = schemas.get(i);
			if (schema.keySet().size() != 1) {
				throw new GatewayMetadataFormatException(
						"A schema must contain only one key and it has "+schema.keySet().size());
			}
			String entityName = (String) schema.keySet().toArray()[0];

			JSONObject jsonSchema = null;
			try {
				jsonSchema = new JSONObject(schema.get(entityName));
			} catch (JSONException ex) {
				throw new GatewayMetadataFormatException(ex.getMessage());
			}

			String remoteName = getStringFromJson(jsonSchema, "remoteName");
			checkFieldNotNull("Remote Name", remoteName);

			EntityDefinition entity = new EntityDefinition(entityName,
					remoteName);

			JSONObject properties = getJsonObjectFromJson(jsonSchema,
					"properties");
			if (properties == null) {
				throw new GatewayMetadataResourceNotFound(
						"Properties not found in entity " + entityName + ".");
			}
			entity.setProperties(parseEntityProperties(properties));
			for (EntityDefinitionProperty property : entity.getProperties()) {
				if (property.isKey()) {
					entity.setHasPrimaryKey(true);
					break;
				}
			}
			entitySet.addEntity(entity);
		}

		return entitySet;
	}

	private String getStringFromJson(JSONObject json, String objectName)
			throws GatewayMetadataFieldsException {
		try {
			return json.getString(objectName);
		} catch (JSONException ex) {
			throw new GatewayMetadataFieldsException(ex.getMessage());
		}
	}

	private Object getFieldFromJson(JSONObject json, String objectName)
			throws GatewayMetadataFieldsException {
		try {
			return json.get(objectName);
		} catch (JSONException ex) {
			throw new GatewayMetadataFieldsException(ex.getMessage());
		}
	}
	
	private int getIntegerFromJson(JSONObject json, String objectName)
			throws GatewayMetadataFieldsException {
		try {
			return json.getInt(objectName);
		} catch (JSONException ex) {
			throw new GatewayMetadataFieldsException(ex.getMessage());
		}
	}
	
	private Boolean getBooleanFromJson(JSONObject json, String objectName)
			throws GatewayMetadataFieldsException {
		try {
			return json.getBoolean(objectName);
		} catch (JSONException ex) {
			throw new GatewayMetadataFieldsException(ex.getMessage());
		}
	}


	private JSONObject getJsonObjectFromJson(JSONObject json, String objectName)
			throws GatewayMetadataFieldsException {
		try {
			return json.getJSONObject(objectName);
		} catch (JSONException ex) {
			throw new GatewayMetadataFieldsException(ex.getMessage());
		}
	}

	private List<EntityDefinitionProperty> parseEntityProperties(
			JSONObject properties) throws GatewayMetadataFieldsException {
		List<EntityDefinitionProperty> entityProperties = new ArrayList<EntityDefinitionProperty>();
		if (properties != null) {
			Iterator<String> keyIterator = properties.keys();
			while (keyIterator.hasNext()) {
				String item = keyIterator.next();
				String propertyName = item;
				JSONObject property = getJsonObjectFromJson(properties,
						propertyName);
				String sample = String.valueOf(getFieldFromJson(property,
						SAMPLE_PROPERTY_TEXT));
				checkFieldNotNull("Sample", sample);
				String type = String.valueOf(getFieldFromJson(property,
						TYPE_PROPERTY_TEXT));
				checkFieldNotNull("Type", type);
				Boolean nullable = getBooleanFromJson(property,
								NULLABLE_PROPERTY_TEXT);
				checkFieldNotNull("Nullable", nullable);
				Integer length = getIntegerFromJson(property,
								LENGTH_PROPERTY_TEXT);
				checkFieldNotNull("Length", length);
				String description = String.valueOf(getFieldFromJson(property,
						DESCRIPTION_PROPERTY_TEXT));
				checkFieldNotNull("Description", description);
				Boolean key = false;
				if (property.has(KEY_PROPERTY_TEXT)) {
					key = getBooleanFromJson(property, KEY_PROPERTY_TEXT);
					checkFieldNotNull("Key", key);
				}
				EntityDefinitionProperty newEntityProperty = new EntityDefinitionProperty(
						propertyName, sample, type, nullable, length,
						description, key);
				entityProperties.add(newEntityProperty);
			}
		}
		Collections.sort(entityProperties);
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
