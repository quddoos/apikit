package org.mule.module.apikit.odata.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

import org.apache.commons.io.IOUtils;

/**
 * 
 * @author arielsegura
 */
public class FileUtils {

	public static String readFromFile(String filePath) throws FileNotFoundException, IOException {
		URL url = Thread.currentThread().getContextClassLoader().getResource(filePath);
		File file = new File(url.getPath());
		InputStream is = new FileInputStream(file);
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer);
		is.close();
		return writer.toString();
	}

}
