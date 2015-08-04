package org.mule.module.apikit.odata.metadata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataMissingFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinition;
import org.mule.module.apikit.odata.metadata.model.entities.EntityProperty;
import org.mule.module.apikit.odata.metadata.model.entities.EntitySet;
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
    private static final String LENGTH_PROPERTY_TEXT = "maxLength";
    private static final String DESCRIPTION_PROPERTY_TEXT = "description";
    private static final String KEY_PROPERTY_TEXT = "key";

    public JsonSchemaManager() {

    }

    public EntitySet getEntitiesFromSchema() throws JsonSyntaxException,
	    FileNotFoundException, IOException,
	    GatewayMetadataMissingFieldsException,
	    GatewayMetadataResourceNotFound, JSONException {

	EntitySet entitySet = new EntitySet();
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
	    String remoteName = schema.getString("remoteName");
	    checkFieldNotNull("Remote Name", remoteName);

	    EntityDefinition entity = new EntityDefinition(entityName,
		    remoteName);

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

    private List<EntityProperty> parseEntityProperties(JSONObject properties)
	    throws GatewayMetadataMissingFieldsException, JSONException {
	List<EntityProperty> entityProperties = new ArrayList<EntityProperty>();
	if (properties != null) {
	    Iterator it = properties.keys();
	    while (it.hasNext()) {
		String propertyName = (String) it.next();
		JSONObject property = properties.getJSONObject(propertyName);
		String sample = String.valueOf(property.get(SAMPLE_PROPERTY_TEXT));
		checkFieldNotNull("Sample", sample);
		String type = String.valueOf(property.get(TYPE_PROPERTY_TEXT));
		checkFieldNotNull("Type", type);
		Boolean nullable = Boolean.valueOf(String.valueOf(property.get(NULLABLE_PROPERTY_TEXT)));
		checkFieldNotNull("Nullable", nullable);
		Integer length = Integer.valueOf(String.valueOf(property.get(LENGTH_PROPERTY_TEXT)));
		checkFieldNotNull("Length", length);
		String description = String.valueOf(property.get(DESCRIPTION_PROPERTY_TEXT));
		checkFieldNotNull("Description", description);
		Boolean key = false;
		if (property.has(KEY_PROPERTY_TEXT)) {
		    key = Boolean.valueOf(String.valueOf(property.get(KEY_PROPERTY_TEXT)));
		    checkFieldNotNull("Key", key);
		}
		EntityProperty newEntityProperty = new EntityProperty(
			propertyName, sample, type, nullable, length,
			description, key);
		entityProperties.add(newEntityProperty);
	    }
	}
	return entityProperties;
    }

    private void checkFieldNotNull(String expected, Object actual)
	    throws GatewayMetadataMissingFieldsException {
	if (actual == null) {
	    throw new GatewayMetadataMissingFieldsException(expected
		    + " not found.");
	}
    }
}
