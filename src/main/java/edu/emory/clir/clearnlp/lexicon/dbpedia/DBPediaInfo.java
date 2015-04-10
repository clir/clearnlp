/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.clir.clearnlp.lexicon.dbpedia;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DBPediaInfo implements Serializable
{
	private static final long serialVersionUID = -2801491629112590441L;
	private Set<DBPediaType> types;
	private Set<String> aliases;
	
	public DBPediaInfo()
	{
		types   = new HashSet<>();
		aliases = new HashSet<>();
	}
	
	public Set<DBPediaType> getTypes()
	{
		return types;
	}
	
	public void setTypes(Set<DBPediaType> types)
	{
		this.types = types;
	}
	
	public void addType(DBPediaType type)
	{
		types.add(type);
	}

	public boolean isType(DBPediaType type)
	{
		return types.contains(type);
	}
	
	public Set<String> getAliases()
	{
		return aliases;
	}
	
	public void setAliases(Set<String> aliases)
	{
		this.aliases = aliases;
	}

	public void addAlias(String alias)
	{
		aliases.add(alias);
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		build.append("type: ");
		build.append(types.toString());
		build.append("\naliases: ");
		build.append(aliases.toString());
		return build.toString();
	}
}
