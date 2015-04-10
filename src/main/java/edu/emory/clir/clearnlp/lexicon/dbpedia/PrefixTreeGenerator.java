package edu.emory.clir.clearnlp.lexicon.dbpedia;

import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;

import edu.emory.clir.clearnlp.collection.tree.PrefixTree;
import edu.emory.clir.clearnlp.collection.triple.ObjectIntIntTriple;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.ner.NERInfo;
import edu.emory.clir.clearnlp.ner.NERInfoList;
import edu.emory.clir.clearnlp.ner.NERTag;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

public class PrefixTreeGenerator implements DBPediaXML
{
	private Map<DBPediaType,DBPediaType> super_type_map;
	private DBPediaTypeMap type_map;
	private DBPediaInfoMap info_map;

	public PrefixTreeGenerator(DBPediaTypeMap typeMap, DBPediaInfoMap infoMap, Set<DBPediaType> types)
	{
		type_map = typeMap;
		info_map = infoMap;
		super_type_map = getSuperTypeMap(types);
	}
	
	public Map<DBPediaType,DBPediaType> getSuperTypeMap(Set<DBPediaType> superTypes)
	{
		Map<DBPediaType,DBPediaType> map = new HashMap<>();
		
		for (DBPediaType superType : superTypes)
			map.put(superType, superType);
		
		for (DBPediaType type : type_map.keySet())
		{
			for (DBPediaType superType : superTypes)
			{
				if (type_map.isSuperType(type, superType))
				{
					map.put(type, superType);
					break;
				}
			}
		}
		
		return map;
	}
	
	public PrefixTree<String,NERInfoList> getPrefixTree(AbstractTokenizer tokenizer)
	{
		PrefixTree<String,NERInfoList> tree = new PrefixTree<>();
		NERInfoList list;
		DBPediaInfo info;
		
		for (Entry<String,DBPediaInfo> e : info_map.entrySet())
		{
			info = e.getValue();
			list = getNERInfoList(e.getKey(), info.getTypes());
			if (list != null) addAliases(tokenizer, tree, info.getAliases(), list);
		}
		
		return tree;
	}
	
	private NERInfoList getNERInfoList(String title, Set<DBPediaType> types)
	{
		Set<DBPediaType> set = new HashSet<>();
		DBPediaType superType;
		
		for (DBPediaType type : types)
		{
			if ((superType = super_type_map.get(type)) != null)
				set.add(superType);
		}
		
		if (set.isEmpty()) return null;
		NERInfoList list = new NERInfoList(title);
		for (DBPediaType type : set) list.add(new NERInfo(NERTag.fromDBPediaType(type)));
		return list;
	}
	
	private void addAliases(AbstractTokenizer tokenizer, PrefixTree<String,NERInfoList> tree, Set<String> aliases, NERInfoList list)
	{
		List<String> tokens;
		String[] t;
		
		for (String alias : aliases)
		{
			tokens = tokenizer.tokenize(alias);
			t = new String[tokens.size()];
			tokens.toArray(t);
			tree.set(t, list, s -> s);
		}
	}
	
	static public void main(String[] args) throws Exception
	{
		final String typeMapFile    = args[0];	// dbpedia.owl.json.xz
		final String infoMapFile    = args[1];	// instances_en.json.xz
		final String prefixTreeFile = args[2];	// prefix_tree.xz
		
		Gson gson = new Gson();
		DBPediaTypeMap typeMap = gson.fromJson(new InputStreamReader(IOUtils.createXZBufferedInputStream(typeMapFile)), DBPediaTypeMap.class);
		DBPediaInfoMap infoMap = gson.fromJson(new InputStreamReader(IOUtils.createXZBufferedInputStream(infoMapFile)), DBPediaInfoMap.class);
		AbstractTokenizer tokenizer = NLPUtils.getTokenizer(TLanguage.ENGLISH);
		PrefixTreeGenerator ptg = new PrefixTreeGenerator(typeMap, infoMap, DSUtils.toHashSet(DBPediaType.Person, DBPediaType.Place, DBPediaType.Organisation));
		PrefixTree<String,NERInfoList> prefixTree = ptg.getPrefixTree(tokenizer);
		ObjectOutputStream out = new ObjectOutputStream(IOUtils.createXZBufferedOutputStream(prefixTreeFile));
		out.writeObject(prefixTree);
		out.close();
		
		String[] array = "the Methodist Episcopal Church and was named in honor of Methodist bishop John Emory".split(" ");
		long st, et;
		
		st = System.currentTimeMillis();
		for (ObjectIntIntTriple<NERInfoList> t : prefixTree.getAll(array, 0, String::toString, true, true))
			System.out.println(Joiner.join(array, " ", t.i1, t.i2+1));
		et = System.currentTimeMillis();
		System.out.println(et-st);
		
//		String[] array = "The Chicago Bulls are an American professional basketball team . They are based in Chicago , Illinois , playing in the Central Division of the Eastern Conference in the National Basketball Association (NBA) . The team was founded on January 26 , 1966 . The Bulls play their home games at the United Center . The Bulls saw their greatest success during the 1990s . They are known for having one of the NBA 's greatest dynasties , winning six NBA championships between 1991 and 1998 with two three-peats . All six championship teams were led by Hall of Famers Michael Jordan , Scottie Pippen and coach Phil Jackson . The Bulls are the only NBA franchise to win multiple championships and never lose an NBA Finals in their history.".split(" ");
//		ObjectInputStream in = new ObjectInputStream(IOUtils.createXZBufferedInputStream(prefixTreeFile));
//		long st, et;
//		@SuppressWarnings("unchecked")
//		PrefixTree<String,NERInfoList> pre= (PrefixTree<String,NERInfoList>)in.readObject(); in.close();
//		ObjectIntPair<NERInfoList> p;
//		int i, len = array.length;
//		st = System.currentTimeMillis();
//		for (i=0; i<len; i++)
//		{
//			p = pre.getValue(array, i, false);
//			if (p != null) System.out.println(Joiner.join(array, " ", i, p.i+1)+" - "+p.o);
//		}
//		et = System.currentTimeMillis();
//		System.out.println(et-st);
	}
}
