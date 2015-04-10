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
package edu.emory.clir.clearnlp.ner;

import edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface NERTag
{
	String PERSON       = "PER";
	String ORGANIZATION = "ORG";
	String LOCATION     = "LOC";
	
	static BILOU toBILOU(String tag)
	{
		return BILOU.valueOf(tag.substring(0,1));
	}
	
	static String toBILOUTag(BILOU bilou, String tag)
	{
		return bilou+"-"+tag;
	}
	
	static String toNamedEntity(String tag)
	{
		return tag.substring(2);
	}
	
	static String fromDBPediaType(DBPediaType type)
	{
		switch (type)
		{
		case Person: return PERSON;
		case Place: return LOCATION;
		case Organisation: return ORGANIZATION;
		default: return type.toString();
		}
	}
}
