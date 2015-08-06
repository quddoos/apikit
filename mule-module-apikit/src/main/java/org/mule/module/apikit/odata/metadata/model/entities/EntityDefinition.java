/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.metadata.model.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author arielsegura
 */
public class EntityDefinition implements Comparable<EntityDefinition>{

	private String name;
	private List<EntityDefinitionProperty> properties;
	private String remoteEntity;
	private boolean hasPrimaryKey = false;

	public EntityDefinition(String name, List<EntityDefinitionProperty> properties, String remoteEntity) {
		this.name = name;
		this.remoteEntity = remoteEntity;
		this.properties = properties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (hasPrimaryKey ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((remoteEntity == null) ? 0 : remoteEntity.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityDefinition other = (EntityDefinition) obj;
		if (hasPrimaryKey != other.hasPrimaryKey)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (remoteEntity == null) {
			if (other.remoteEntity != null)
				return false;
		} else if (!remoteEntity.equals(other.remoteEntity))
			return false;
		return true;
	}

	public EntityDefinition(String name, String remoteEntity) {
		this.name = name;
		this.remoteEntity = remoteEntity;
		this.properties = new ArrayList<EntityDefinitionProperty>();
	}

	public boolean hasPrimaryKey() {
		return hasPrimaryKey;
	}

	public void setHasPrimaryKey(boolean hasPrimaryKey) {
		this.hasPrimaryKey = hasPrimaryKey;
	}

	public void addProperty(EntityDefinitionProperty entityProperty) {
		this.properties.add(entityProperty);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EntityDefinitionProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<EntityDefinitionProperty> properties) {
		this.properties = properties;
	}

	public String getRemoteEntity() {
		return remoteEntity;
	}

	public void setRemoteEntity(String remoteEntity) {
		this.remoteEntity = remoteEntity;
	}

	public String toString() {
		return toJsonString();
	}

	public String toJsonString() {
		StringBuilder ret = new StringBuilder("{");
//		ret.append("\"name\":\"" + this.name + "\",");
		ret.append("\"remoteEntity\":\"" + this.remoteEntity + "\",");
		ret.append("\"hasPrimaryKey\":" + this.hasPrimaryKey + ",");
		ret.append("\"properties\":[");
		String delim = "";
		for (EntityDefinitionProperty property : properties) {
			ret.append(delim + property.toJsonString());
			delim = ",";
		}
		ret.append("]}");
		return ret.toString();
	}
	
	@Override
	public int compareTo(EntityDefinition o) {
		return this.name.compareTo(o.name);
	}
}
