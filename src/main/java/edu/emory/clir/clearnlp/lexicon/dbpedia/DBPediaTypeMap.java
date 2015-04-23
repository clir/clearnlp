package edu.emory.clir.clearnlp.lexicon.dbpedia;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.kohsuke.args4j.IllegalAnnotationError;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;

import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.XmlUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

public class DBPediaTypeMap extends HashMap<DBPediaType,Set<DBPediaType>> implements DBPediaXML
{
	private static final long serialVersionUID = -4500311827642203043L;
	
	public DBPediaTypeMap() {}
	
	public void readFromOWL(InputStream in) throws Exception
	{
		DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dFactory.newDocumentBuilder();
		Document doc = builder.parse(in);
		
		NodeList classes = doc.getElementsByTagName(OWL_CLASS);
		int i, j, len, size = classes.getLength();
		Set<DBPediaType> rdfResources;
		Element owlClass, subClass;
		DBPediaType rdfAbout;
		NodeList subClasses;
		String s;
		
		for (i=0; i<size; i++)
		{	
			owlClass = (Element)classes.item(i);
			subClasses = owlClass.getElementsByTagName(RDFS_SUBCLASS_OF);
			len = subClasses.getLength();

			rdfAbout = extractType(XmlUtils.getTrimmedAttribute(owlClass, RDF_ABOUT));
			rdfResources = new HashSet<>();
			
			for (j=0; j<len; j++)
			{
				subClass = (Element)subClasses.item(j);
				s = XmlUtils.getTrimmedAttribute(subClass, RDF_RESOURCE);
				if (s.startsWith(DBPEDIA_ORG_ONTOLOGY)) rdfResources.add(extractType(s));
			}
		
			put(rdfAbout, rdfResources);
		}
		
		get(DBPediaType.Mayor).add(DBPediaType.Politician);
	}
	
	private DBPediaType extractType(String url)
	{
		int idx = url.lastIndexOf(StringConst.FW_SLASH) + 1;
		if (idx >= url.length()) throw new IllegalAnnotationError(url);
		return DBPediaType.getType(url.substring(idx));
	}

	public boolean isSuperType(DBPediaType type, DBPediaType superType)
	{
		Set<DBPediaType> set = get(type);
		if (set == null) return false;
		if (set.contains(superType)) return true;
		
		for (DBPediaType s : set)
		{
			if (isSuperType(s, superType))
				return true;
		}
		
		return false;
	}
	
	/** Including self. */
	public Set<DBPediaType> getSubtypeSet(DBPediaType superType)
	{
		Set<DBPediaType> set = new HashSet<>();
		set.add(superType);
		
		for (DBPediaType type : keySet())
			if (isSuperType(type, superType))
				set.add(type);

		return set;
	}
	
	static public void main(String[] args) throws Exception
	{
		// args[0] = "http://mappings.dbpedia.org/server/ontology/dbpedia.owl";
		InputStream in = new BufferedInputStream(new FileInputStream(args[0]));
		DBPediaTypeMap map = new DBPediaTypeMap();
		Gson gson = new Gson();
		map.readFromOWL(in);
		in.close();
		
		PrintStream fout = new PrintStream(IOUtils.createXZBufferedOutputStream(args[1]));
		fout.print(gson.toJson(map));
		fout.close();
		
		map = gson.fromJson(new InputStreamReader(IOUtils.createXZBufferedInputStream(args[1])), DBPediaTypeMap.class);
		System.out.println(map.get(DBPediaType.Library));
	}
}