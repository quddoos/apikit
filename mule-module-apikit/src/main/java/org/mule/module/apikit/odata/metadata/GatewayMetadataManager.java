package org.mule.module.apikit.odata.metadata;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataEntityNotFoundException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataFileNotFoundException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataMissingFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.metadata.exception.WrongYamlFormatException;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinition;
import org.mule.module.apikit.odata.metadata.model.entities.EntitySet;

import com.google.gson.JsonSyntaxException;

/**
 * 
 * @author arielsegura
 */
public class GatewayMetadataManager {

	private EntitySet entitySet;
	private JsonSchemaManager schemaManager;

	public GatewayMetadataManager() throws WrongYamlFormatException, GatewayMetadataFileNotFoundException, GatewayMetadataResourceNotFound, GatewayMetadataMissingFieldsException, JsonSyntaxException, FileNotFoundException, IOException, JSONException {
		super();
		schemaManager = new JsonSchemaManager();
		refreshMetadata();
	}

	private void refreshMetadata() throws JsonSyntaxException, FileNotFoundException, IOException, GatewayMetadataMissingFieldsException, GatewayMetadataResourceNotFound, JSONException {
		entitySet = schemaManager.getEntitiesFromSchema();
	}

	public EntitySet getEntitySet() throws WrongYamlFormatException, GatewayMetadataFileNotFoundException, GatewayMetadataResourceNotFound, GatewayMetadataMissingFieldsException, JsonSyntaxException, FileNotFoundException, IOException, JSONException {
		if (entitySet == null) {
			refreshMetadata();
		}
		return entitySet;
	}

	public EntityDefinition getEntityByName(String entityName) throws GatewayMetadataEntityNotFoundException {
		for (EntityDefinition EntityDefinition : entitySet.toList()) {
			if (EntityDefinition.getName().equalsIgnoreCase(entityName)) {
				return EntityDefinition;
			}
		}
		throw new GatewayMetadataEntityNotFoundException("EntityDefinition " + entityName + " not found.");
	}


}
