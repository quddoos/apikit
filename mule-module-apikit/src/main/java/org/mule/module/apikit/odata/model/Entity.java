/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Entity
{
	private Map<String, Object> properties = new HashMap<String, Object>();

	public void addProperty(String name, Object value)
	{
		getProperties().put(name, value);
	}

	public Map<String, Object> getProperties()
	{
		return properties;
	}

	public void setProperties(Map<String, Object> properties)
	{
		this.properties = properties;
	}

	public Object getProperty(String name)
	{
		return properties.get(name);
	}
	
	public static List<Entity> convertMapToEntityList(List<Map<String, String>> query)
	{
		List<Entity> entities = new ArrayList<Entity>();

		for (Map<String, String> map : query)
		{
			Entity outputEntity = new Entity();
			for (Entry<String, String> entry : map.entrySet())
			{
				outputEntity.addProperty(entry.getKey(), entry.getValue());
			}

			entities.add(outputEntity);
		}

		return entities;
	}
}
