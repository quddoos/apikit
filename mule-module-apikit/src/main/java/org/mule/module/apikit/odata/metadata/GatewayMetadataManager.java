package org.mule.module.apikit.odata.metadata;

import java.io.InputStream;

import org.json.JSONException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataEntityNotFoundException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataMissingFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataNotInitializedException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinition;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionSet;
import org.mule.module.apikit.odata.metadata.raml.RamlParser;
import org.raml.model.Raml;

/**
 * 
 * @author arielsegura
 */
public class GatewayMetadataManager {

	private EntityDefinitionSet entitySet;
	private RamlParser ramlParser;

	public GatewayMetadataManager() {
		super();
		ramlParser = new RamlParser();
	}

	public void refreshMetadata(String raml)
			throws GatewayMetadataMissingFieldsException,
			GatewayMetadataResourceNotFound, JSONException {
		entitySet = ramlParser.getEntitiesFromRaml(raml);
	}

	public void refreshMetadata(InputStream raml)
			throws GatewayMetadataMissingFieldsException,
			GatewayMetadataResourceNotFound, JSONException {
		entitySet = ramlParser.getEntitiesFromRaml(raml);
	}

	public void refreshMetadata(Raml raml)
			throws GatewayMetadataMissingFieldsException,
			GatewayMetadataResourceNotFound, JSONException {
		entitySet = ramlParser.getEntitiesFromRaml(raml);
	}

	public EntityDefinitionSet getEntitySet()
			throws GatewayMetadataNotInitializedException {
		if (entitySet == null) {
			throw new GatewayMetadataNotInitializedException();
		}
		return entitySet;
	}

	public EntityDefinition getEntityByName(String entityName)
			throws GatewayMetadataEntityNotFoundException {
		for (EntityDefinition EntityDefinition : entitySet.toList()) {
			if (EntityDefinition.getName().equalsIgnoreCase(entityName)) {
				return EntityDefinition;
			}
		}
		throw new GatewayMetadataEntityNotFoundException("EntityDefinition "
				+ entityName + " not found.");
	}

	public boolean isInitialized() {
		return entitySet != null;
	}

}
