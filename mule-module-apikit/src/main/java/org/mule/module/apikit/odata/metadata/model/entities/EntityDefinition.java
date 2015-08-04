package org.mule.module.apikit.odata.metadata.model.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author arielsegura
 */
public class EntityDefinition {

	private String name;
	private List<EntityProperty> properties;
	private String remoteEntity;
	private boolean hasPrimaryKey = false;

	public EntityDefinition(String name, List<EntityProperty> properties, String remoteEntity) {
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
		this.properties = new ArrayList<EntityProperty>();
	}

	public boolean hasPrimaryKey() {
		return hasPrimaryKey;
	}

	public void setHasPrimaryKey(boolean hasPrimaryKey) {
		this.hasPrimaryKey = hasPrimaryKey;
	}

	public void addProperty(EntityProperty entityProperty) {
		this.properties.add(entityProperty);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EntityProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<EntityProperty> properties) {
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
		for (EntityProperty property : properties) {
			ret.append(delim + property.toJsonString());
			delim = ",";
		}
		ret.append("]}");
		return ret.toString();
	}
}
