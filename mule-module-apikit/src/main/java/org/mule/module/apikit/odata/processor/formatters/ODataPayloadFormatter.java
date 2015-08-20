package org.mule.module.apikit.odata.processor.formatters;

public interface ODataPayloadFormatter
{
	public enum Format
	{
		Json, Atom
	}

	String format(Format format) throws Exception;
}
