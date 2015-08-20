package org.mule.module.apikit.odata.processor.formatters;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.mule.module.apikit.odata.metadata.GatewayMetadataManager;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataMissingFieldsException;
import org.mule.module.apikit.odata.metadata.exception.GatewayMetadataResourceNotFound;
import org.mule.module.apikit.odata.util.Helper;
import org.mule.module.apikit.odata.util.UriInfoImpl;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.FormatWriter;
import org.odata4j.format.FormatWriterFactory;

public class ServiceDocumentPayloadFormatter implements ODataPayloadFormatter
{
	private final String url;
	private GatewayMetadataManager gatewayMetadataManager;

	public ServiceDocumentPayloadFormatter(GatewayMetadataManager gatewayMetadataManager, String url)
	{
		this.gatewayMetadataManager = gatewayMetadataManager;
		this.url = url;
	}

	public String format(Format format) throws Exception
	{
			FormatWriter<EdmDataServices> fw = FormatWriterFactory.getFormatWriter(EdmDataServices.class, Arrays.asList(MediaType.valueOf(MediaType.WILDCARD)), format.name(), null);
			EdmDataServices ees = Helper.createMetadata(gatewayMetadataManager.getEntitySet());
			UriInfo uriInfo = new UriInfoImpl(url);
			Writer w = new StringWriter();

			fw.write(uriInfo, w, ees);

			return w.toString();
	}
}