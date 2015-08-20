package org.mule.module.apikit.odata.processor.formatters;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mule.module.apikit.odata.metadata.GatewayMetadataManager;
import org.mule.module.apikit.odata.metadata.model.entities.EntityDefinitionSet;
import org.mule.module.apikit.odata.model.Entity;
import org.mule.module.apikit.odata.util.Helper;
import org.mule.module.apikit.odata.util.UriInfoImpl;
import org.odata4j.format.FormatWriter;
import org.odata4j.format.FormatWriterFactory;
import org.odata4j.producer.EntitiesResponse;
import org.raml.model.Raml;

public class ODataApiKitFormatter implements ODataPayloadFormatter
{
	private Raml raml;
	private List<Entity> entities2;
	private String entityName;
	private String url;

	public ODataApiKitFormatter(Raml raml, List<Entity> entities2, String entityName, String url)
	{
		this.raml = raml;
		this.entities2 = entities2;
		this.entityName = entityName;
		this.url = url;
	}

	public String format(Format format) throws Exception
	{
		StringWriter sw = new StringWriter();
		FormatWriter<EntitiesResponse> fw = FormatWriterFactory.getFormatWriter(EntitiesResponse.class, Arrays.asList(MediaType.valueOf(MediaType.WILDCARD)), format.name(), null);

		GatewayMetadataManager gwMetadataManager = Helper.refreshMetadataManager(raml);
		EntityDefinitionSet entitySet = gwMetadataManager.getEntitySet();

		EntitiesResponse entitiesResponse = Helper.convertEntitiesToOEntities(entities2, entityName, entitySet);

		UriInfo uriInfo = new UriInfoImpl(url);
		fw.write(uriInfo, sw, entitiesResponse);
		return sw.toString();
	}
}