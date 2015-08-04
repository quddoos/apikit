package org.mule.module.apikit.odata.metadata.model.entities;

/**
 * 
 * @author arielsegura
 */
public class EntityProperty {

	private String name;
	private String sample;
	private String type;
	private boolean nullable;
	private int length;
	private String description;
	private boolean key;

	public EntityProperty(String name, String sample, String type, boolean nullable, int length, String description, boolean key) {
		this.name = (name != null ? name : "");
		this.sample = (sample != null ? sample : "");
		this.type = (type != null ? type : "");
		this.nullable = nullable;
		this.length = length;
		this.description = description;
		this.key = key;
	}

	public String toString() {
		return toJsonString();
	}

	public String toJsonString() {
		StringBuilder ret = new StringBuilder("{");
		ret.append("\"name\":\"" + this.name + "\",");
		ret.append("\"fieldName\":\"" + this.sample + "\",");
		ret.append("\"type\":\"" + this.type + "\",");
		ret.append("\"length\":\"" + this.length + "\",");
		ret.append("\"description\":\"" + this.description + "\",");
		ret.append("\"key\":\"" + this.key + "\",");
		ret.append("\"nullable\":\"" + this.nullable + "\"");
		ret.append("}");
		return ret.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isKey() {
		return key;
	}

	public void setKey(boolean key) {
		this.key = key;
	}

	public String getSample() {
		return sample;
	}

	public void setSample(String sample) {
		this.sample = sample;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + (key ? 1231 : 1237);
		result = prime * result + length;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (nullable ? 1231 : 1237);
		result = prime * result + ((sample == null) ? 0 : sample.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		EntityProperty other = (EntityProperty) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (key != other.key)
			return false;
		if (length != other.length)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nullable != other.nullable)
			return false;
		if (sample == null) {
			if (other.sample != null)
				return false;
		} else if (!sample.equals(other.sample))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
