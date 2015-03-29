package edu.emory.clir.clearnlp.lexicon.dbpedia;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

public class PrefixTreeGenerator implements DBPediaXML
{
	static final Pattern RESOURCE = Pattern.compile("<http://dbpedia.org/resource/(.+?)>");
	static final Pattern ONTOLOGY = Pattern.compile("<http://dbpedia.org/ontology/(.+?)>");
	
//	public PrefixTree<String,DBPediaType> getPrefixTree(DBPediaInfoMap infoMap, AbstractTokenizer tokenizer, DBPediaType[] types)
//	{
//		PrefixTree<String,Set<DBPediaType>> tree = new PrefixTree<>();
//		
//		for (DBPediaInfo info : infoMap.values())
//		{
//			
//		}
//		
//		
//		
//		return tree;
//	}
//	
//	private Set<DBPediaType> getTypeSet(DBPediaTypeMap typeMap, DBPediaInfo info, DBPediaType[] targetTypes)
//	{
//		Set<DBPediaType> set = new HashSet<>();
//		
//		for (DBPediaType type : info.getTypes())
//		{
//		}
//	}
	
	static public void main(String[] args) throws Exception
	{
		Gson gson = new Gson();
		DBPediaTypeMap typeMap = gson.fromJson(new InputStreamReader(IOUtils.createXZBufferedInputStream(args[0])), DBPediaTypeMap.class);	// dbpedia.owl.json.xz
		DBPediaInfoMap infoMap = gson.fromJson(new InputStreamReader(IOUtils.createXZBufferedInputStream(args[0])), DBPediaInfoMap.class);	// instances_en.json.xz
		AbstractTokenizer tokenizer = NLPUtils.getTokenizer(TLanguage.ENGLISH);
	}
}
