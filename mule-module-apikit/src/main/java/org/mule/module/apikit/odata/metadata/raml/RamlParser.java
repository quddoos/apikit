package org.mule.module.apikit.odata.metadata.raml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataMissingFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinition;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionProperty;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionSet;
import org.raml.model.Raml;
import org.raml.parser.visitor.RamlDocumentBuilder;

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

	public EntityDefinitionSet getEntitiesFromRaml(String path) throws GatewayMetadataMissingFieldsException, GatewayMetadataResourceNotFound, JSONException {
		return getEntitiesFromRaml(new RamlDocumentBuilder().build(path));
	}
	
	public EntityDefinitionSet getEntitiesFromRaml(InputStream inputStream) throws GatewayMetadataMissingFieldsException, GatewayMetadataResourceNotFound, JSONException {
		return getEntitiesFromRaml(new RamlDocumentBuilder().build(inputStream));
	}

	public EntityDefinitionSet getEntitiesFromRaml(Raml raml) throws GatewayMetadataMissingFieldsException, GatewayMetadataResourceNotFound, JSONException  {

		EntityDefinitionSet entitySet = new EntityDefinitionSet();

		List<Map<String, String>> schemas = raml.getSchemas();

		for (int i = 0; i < schemas.size(); i++) {
			Map<String, String> schema = schemas.get(i);
			String entityName = (String) schema.keySet().toArray()[0];
			JSONObject jsonSchema = new JSONObject(schema.get(entityName));
			String remoteName = jsonSchema.getString("remoteName");
			checkFieldNotNull("Remote Name", remoteName);

			EntityDefinition entity = new EntityDefinition(entityName, remoteName);

			JSONObject properties = jsonSchema.getJSONObject("properties");
			if (properties == null) {
				throw new GatewayMetadataResourceNotFound("Properties not found in entity " + entityName + ".");
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

	private List<EntityDefinitionProperty> parseEntityProperties(JSONObject properties) throws GatewayMetadataMissingFieldsException, JSONException {
		List<EntityDefinitionProperty> entityProperties = new ArrayList<EntityDefinitionProperty>();
		if (properties != null) {
			Iterator<String> keyIterator = properties.keys();
			while(keyIterator.hasNext()){
				String item = keyIterator.next();
				String propertyName = item;
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
				EntityDefinitionProperty newEntityProperty = new EntityDefinitionProperty(propertyName, sample, type, nullable, length, description, key);
				entityProperties.add(newEntityProperty);
			}
		}
		Collections.sort(entityProperties);
		return entityProperties;
	}

	private void checkFieldNotNull(String expected, Object actual) throws GatewayMetadataMissingFieldsException {
		if (actual == null) {
			throw new GatewayMetadataMissingFieldsException(expected + " not found.");
		}
	}

}
