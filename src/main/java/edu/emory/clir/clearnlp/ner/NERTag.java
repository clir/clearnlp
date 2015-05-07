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

import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Aircraft;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.ArchitecturalStructure;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Artwork;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Automobile;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Broadcaster;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Cartoon;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Cemetery;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.CollectionOfValuables;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Company;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Competition;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.ConcentrationCamp;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Continent;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Country;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Currency;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Device;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Document;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Drug;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.EducationalInstitution;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.EmployersOrganisation;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.EthnicGroup;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Film;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Food;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Garden;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.GeopoliticalOrganisation;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.GovernmentAgency;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.HistoricPlace;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.HistoricalRegion;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Island;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Language;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Legislature;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Locomotive;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Mayor;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.MilitaryVehicle;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Mine;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Monument;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Motorcycle;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Musical;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.MusicalWork;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Name;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.NaturalEvent;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.NaturalPlace;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.NaturalRegion;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.NonProfitOrganisation;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Organisation;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Park;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Parliament;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Person;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.PersonFunction;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Place;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.PoliticalParty;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.ProtectedArea;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Region;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.ReligiousOrganisation;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Rocket;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.SambaSchool;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Settlement;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Ship;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.SkiArea;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.SkiResort;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.SocietalEvent;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.SpaceShuttle;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Spacecraft;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.SportFacility;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.SportsLeague;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.SportsTeam;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.State;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Street;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.TelevisionShow;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Territory;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.TimePeriod;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Train;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.Website;
import static edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType.WrittenWork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.emory.clir.clearnlp.lexicon.dbpedia.DBPediaType;
import edu.emory.clir.clearnlp.util.DSUtils;


/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface NERTag
{
	@SuppressWarnings("serial")
	static public List<Set<DBPediaType>> DBPediaTypeList = new ArrayList<Set<DBPediaType>>() {{
		add(DSUtils.toHashSet(Person, PersonFunction, Mayor, Name)); // PERSON
		add(DSUtils.toHashSet(GeopoliticalOrganisation, Legislature, Parliament, PoliticalParty, ReligiousOrganisation, EthnicGroup)); // NORP
		add(DSUtils.toHashSet(ArchitecturalStructure, Cemetery, ConcentrationCamp, Garden, HistoricPlace, Mine, Monument, SkiResort, SportFacility, Park, Street)); // FACILITY
		add(DSUtils.toHashSet(GovernmentAgency, Broadcaster, Company, EducationalInstitution, EmployersOrganisation, NonProfitOrganisation, SambaSchool, SportsLeague, SportsTeam, Website)); // ORGANIZATION
		add(DSUtils.toHashSet(Country, Settlement, State)); // GPE
		add(DSUtils.toHashSet(Region, NaturalRegion, HistoricalRegion, Street, Territory, ProtectedArea, SkiArea, Island, NaturalPlace, Continent)); // LOCATION
		add(DSUtils.toHashSet(Aircraft, Automobile, Locomotive, MilitaryVehicle, Motorcycle, Rocket, Ship, SpaceShuttle, Spacecraft, Train, Device, Drug, Food)); // PRODUCT
		add(DSUtils.toHashSet(NaturalEvent, Competition, SocietalEvent)); // EVENT
		add(DSUtils.toHashSet(Artwork, Cartoon, CollectionOfValuables, Document, Film, Musical, MusicalWork, WrittenWork, TelevisionShow)); // WORK_OF_ART
		add(DSUtils.toHashSet(Language)); // LANGUAGE
		add(DSUtils.toHashSet(TimePeriod)); // DATE
		add(DSUtils.toHashSet(Currency)); // MONEY
	}};
	
	@SuppressWarnings("serial")
	static public Set<DBPediaType> DBPediaTypeSet = new HashSet<DBPediaType>() {{
		for (Set<DBPediaType> p : DBPediaTypeList) addAll(p);
	}};
	
	@SuppressWarnings("serial")
	static public Map<DBPediaType,String> DBPediaTypeMap = new HashMap<DBPediaType,String>() {{
		int i, size = DBPediaTypeList.size();
		Set<DBPediaType> p;
		
		for (i=0; i<size; i++)
		{
			p = DBPediaTypeList.get(i);
			for (DBPediaType type : p)
				put(type, Integer.toString(i));
		}
	}};
	
	public static String fromDBPediaType(DBPediaType type)
	{
		return DBPediaTypeMap.getOrDefault(type, type.toString());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	static public Set<DBPediaType> DBPediaTypesCoNLL03 = DSUtils.toHashSet(Person, Mayor, PersonFunction, Name, Place, Organisation, Website, Competition, SocietalEvent, Artwork, Film, MusicalWork, WrittenWork, TelevisionShow);
	
	public static String fromDBPediaTypeCoNLL03(DBPediaType type)
	{
		switch (type)
		{
		case Mayor			: return "PER";
		case Person			: return "PER";
		case PersonFunction	: return "PER";
		case Name			: return "PER";
		case Place			: return "LOC";
		case Organisation	: return "ORG";
		case Website		: return "ORG";
		case Artwork        : return "WORK";
		case MusicalWork    : return "WORK";
		case WrittenWork    : return "WORK";
		case TelevisionShow : return "WORK";
		case Film           : return "WORK";
		case Competition    : return "EVENT";
		case SocietalEvent  : return "EVENT";
		
		default: return type.toString();
		}
	}
}
