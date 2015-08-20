package org.mule.module.apikit.odata.processor.formatters;

import java.io.StringWriter;
import java.io.Writer;

import org.mule.module.apikit.odata.metadata.GatewayMetadataManager;
import org.mule.module.apikit.odata.util.Helper;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.xml.EdmxFormatWriter;

public class ODataPayloadMetadataFormatter implements ODataPayloadFormatter
{
	private GatewayMetadataManager gatewayMetadataManager;

	public ODataPayloadMetadataFormatter(GatewayMetadataManager gatewayMetadataManager)
	{
		this.gatewayMetadataManager = gatewayMetadataManager;
	}

	public String format(Format format) throws Exception
	{
		Writer w = new StringWriter();
		EdmDataServices ees = Helper.createMetadata(gatewayMetadataManager.getEntitySet());
		EdmxFormatWriter.write(ees, w);
		return w.toString();
	}
}