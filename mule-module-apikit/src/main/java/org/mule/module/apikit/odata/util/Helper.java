package org.mule.module.apikit.odata.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONException;
import org.mule.module.apikit.odata.metadata.GatewayMetadataManager;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataMissingFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinition;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionProperty;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionSet;
import org.mule.module.apikit.odata.model.Entity;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmProperty.Builder;
import org.odata4j.edm.EdmSchema;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.edm.EdmType;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.producer.Responses;
import org.raml.model.Raml;
import org.raml.parser.visitor.RamlDocumentBuilder;

public class Helper {

	private static final GatewayMetadataManager gwMetadataManager = new GatewayMetadataManager();

	public static EntitiesResponse convertEntitiesToOEntities(
			List<Entity> outputEntities, String entitySetName,
			EntityDefinitionSet metadata1) {
		EdmEntitySet ees = Helper.createMetadata(metadata1).getEdmEntitySet(
				entitySetName);
		List<OEntity> entities = new ArrayList<OEntity>();
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		int id = 0;
		for (Entity outputEntity : outputEntities) {
			properties = new ArrayList<OProperty<?>>();

			for (Entry<String, Object> entry : outputEntity.getProperties()
					.entrySet()) {

				properties.add(OProperties.string(entry.getKey(),
						entry.getValue() + ""));
			}
			entities.add(OEntities.create(ees, OEntityKey.create(id + ""),
					properties, null));
			id++;
		}

		return Responses.entities(entities, ees, null, null);
	}

	public static EdmDataServices createMetadata(EntityDefinitionSet metadata) {
		try {

			String namespace = "entities";

			List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
			List<EdmEntityType.Builder> entityTypes = new ArrayList<EdmEntityType.Builder>();
			List<EdmEntitySet.Builder> entitySets = new ArrayList<EdmEntitySet.Builder>();

			for (EntityDefinition entityMetadata : metadata.toList()) {
				for (EntityDefinitionProperty propertyMetadata : entityMetadata
						.getProperties()) {
					Builder builder = EdmProperty.newBuilder(
							propertyMetadata.getName()).setType(
							convertType(propertyMetadata.getType()));
					builder.setNullable(propertyMetadata.isNullable());
					properties.add(builder);
				}

				String entityName = entityMetadata.getName();
				EdmEntityType.Builder type = EdmEntityType.newBuilder()
						.setNamespace(namespace).setName(entityName)
						.addKeys("id").addProperties(properties);
				entityTypes.add(type);

				entitySets.add(EdmEntitySet.newBuilder().setName(entityName)
						.setEntityType(type));
			}

			EdmEntityContainer.Builder container = EdmEntityContainer
					.newBuilder().setName(namespace + "Entities")
					.setIsDefault(true).addEntitySets(entitySets);
			EdmSchema.Builder modelSchema = EdmSchema.newBuilder()
					.setNamespace(namespace + "Model")
					.addEntityTypes(entityTypes);
			EdmSchema.Builder containerSchema = EdmSchema.newBuilder()
					.setNamespace(namespace + "Container")
					.addEntityContainers(container);

			return EdmDataServices.newBuilder()
					.addSchemas(containerSchema, modelSchema).build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static EdmType convertType(String type) {
		if (type.equals("String"))
			return EdmSimpleType.STRING;
		if (type.equals("int"))
			return EdmSimpleType.INT64;

		return EdmSimpleType.STRING;
	}

	public static GatewayMetadataManager refreshMetadataManager(Raml raml)
			throws GatewayMetadataMissingFieldsException,
			GatewayMetadataResourceNotFound, JSONException {
		gwMetadataManager.refreshMetadata(raml);
		return gwMetadataManager;
	}

	public static GatewayMetadataManager refreshMetadataManager(String path)
			throws GatewayMetadataMissingFieldsException,
			GatewayMetadataResourceNotFound, JSONException {
		return refreshMetadataManager(getRaml(path));
	}

	public static GatewayMetadataManager refreshMetadataManager(URL url)
			throws GatewayMetadataMissingFieldsException,
			GatewayMetadataResourceNotFound, JSONException, IOException {
		return refreshMetadataManager(getRaml(url));
	}

	public static GatewayMetadataManager getMetadataManager()
			throws GatewayMetadataMissingFieldsException,
			GatewayMetadataResourceNotFound, JSONException {

		return gwMetadataManager;
	}

	private static Raml getRaml(URL url) throws IOException {
		HttpURLConnection conn;
		Raml raml = null;
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		InputStreamReader input = new InputStreamReader(conn
				.getInputStream());
		raml = new RamlDocumentBuilder().build(input);

		return raml;
	}

	private static Raml getRaml(String path) {
		Raml raml = new RamlDocumentBuilder().build(path);
		return raml;
	}

	public String getPayload(URL url) {
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		StringBuilder result = new StringBuilder();
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

}
