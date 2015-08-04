package org.mule.module.apikit.odata.metadata.model.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author arielsegura
 */
public class EntitySet {

	private List<EntityDefinition> entities;

	public EntitySet() {
		entities = new ArrayList<EntityDefinition>();
	}

	public void addEntity(EntityDefinition EntityDefinition) {
		entities.add(EntityDefinition);
	}

	public String toString() {
		return toJsonString();
	}

	public List<EntityDefinition> toList() {
		return entities;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entities == null) ? 0 : entities.hashCode());
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
		EntitySet other = (EntitySet) obj;
		if (entities == null) {
			if (other.entities != null)
				return false;
		} else if (!entities.equals(other.entities))
			return false;
		return true;
	}

	public String toJsonString() {
		StringBuilder ret = new StringBuilder("{");
		ret.append("\"entitySet\":[");
		String delim = "";
		for (EntityDefinition EntityDefinition : entities) {
			ret.append(delim + EntityDefinition.toJsonString());
			delim = ",";
		}
		ret.append("]}");
		return ret.toString();
	}
}
