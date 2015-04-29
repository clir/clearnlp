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

import edu.emory.clir.clearnlp.collection.tree.PrefixNode;
import edu.emory.clir.clearnlp.collection.tree.PrefixTree;
import edu.emory.clir.clearnlp.collection.triple.ObjectIntIntTriple;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.ner.NERInfoSet;
import edu.emory.clir.clearnlp.ner.NERTag;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.StringUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;
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
	
	/** Note this list is an object. */
	public PrefixTree<String,NERInfoSet> getPrefixTree(AbstractTokenizer tokenizer)
	{
		PrefixTree<String,NERInfoSet> tree = new PrefixTree<>();
		NERInfoSet list;
		DBPediaInfo info;
		
		for (Entry<String,DBPediaInfo> e : info_map.entrySet())
		{
			info = e.getValue();
			list = getNERInfoSet(e.getKey(), info.getTypes());
			if (list != null) addAliases(tokenizer, tree, info.getAliases(), list);
		}
		
		return tree;
	}
	
	private NERInfoSet getNERInfoSet(String title, Set<DBPediaType> types)
	{
		Set<DBPediaType> set = new HashSet<>();
		DBPediaType superType;
			
		for (DBPediaType type : types)
		{
			if ((superType = super_type_map.get(type)) != null)
				set.add(superType);
		}
		
		if (set.isEmpty()) return null;
		NERInfoSet list = new NERInfoSet();
		for (DBPediaType type : set) list.addCategory(NERTag.fromDBPediaTypeCoNLL03(type));
		return list;
	}
	
	private void addAliases(AbstractTokenizer tokenizer, PrefixTree<String,NERInfoSet> tree, Set<String> aliases, NERInfoSet list)
	{
		PrefixNode<String,NERInfoSet> node;
		List<String> tokens;
		String[] t;
		
		for (String alias : aliases)
		{
			tokens = tokenizer.tokenize(alias);
			t = trimTokens(tokens);
			
			if (t.length > 0)
			{
				node = tree.add(t, 0, t.length, String::toString);
				if (node.hasValue()) node.getValue().addCategories(list.getCategorySet());
				else node.setValue(list);
			}
		}
	}
	
	private String[] trimTokens(List<String> tokens)
	{
		int i, size, bIdx = -1;
		
		for (i=tokens.size()-1; i>=0; i--)
		{
			if (StringUtils.containsDigitOnly(tokens.get(i)))
				tokens.remove(i);
			else
				break;
		}
		
		size = tokens.size();
		
		for (i=0; i<size; i++)
		{
			if (tokens.get(i).equals(StringConst.LRB))
				bIdx = i;
			else if (tokens.get(i).equals(StringConst.RRB) && bIdx >= 0)
			{
				tokens.subList(bIdx, i+1).clear();
				break;
			}
		}
		
		
		if (tokens.size() == 1 && StringUtils.containsDigitOnly(tokens.get(0)))
			tokens.clear();
		
		int len = tokens.size();
		String[] t = new String[len];
		for (i=0; i<len; i++) t[i] = tokens.get(i);
		return t;
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
		PrefixTreeGenerator ptg = new PrefixTreeGenerator(typeMap, infoMap, NERTag.DBPediaTypeSet);
		PrefixTree<String,NERInfoSet> prefixTree = ptg.getPrefixTree(tokenizer);
		ObjectOutputStream out = new ObjectOutputStream(IOUtils.createXZBufferedOutputStream(prefixTreeFile));
		out.writeObject(prefixTree);
		out.close();
		
		String[] array = "John Emory Democratic Party London Bridge Emory University South Korea Rocky Mountains M16 New Years Eve The Catcher in the Rye Korean Ming Dynasty Euro".split(" ");
		
		for (ObjectIntIntTriple<NERInfoSet> t : prefixTree.getAll(array, 0, String::toString, true, true))
			System.out.println(t.o+" "+Joiner.join(array, " ", t.i1, t.i2+1));
		
//		String[] array = "The Chicago Bulls are an American professional basketball team . They are based in Chicago , Illinois , playing in the Central Division of the Eastern Conference in the National Basketball Association (NBA) . The team was founded on January 26 , 1966 . The Bulls play their home games at the United Center . The Bulls saw their greatest success during the 1990s . They are known for having one of the NBA 's greatest dynasties , winning six NBA championships between 1991 and 1998 with two three-peats . All six championship teams were led by Hall of Famers Michael Jordan , Scottie Pippen and coach Phil Jackson . The Bulls are the only NBA franchise to win multiple championships and never lose an NBA Finals in their history.".split(" ");
//		ObjectInputStream in = new ObjectInputStream(IOUtils.createXZBufferedInputStream(prefixTreeFile));
//		long st, et;
//		@SuppressWarnings("unchecked")
//		PrefixTree<String,NERInfoSet> pre= (PrefixTree<String,NERInfoSet>)in.readObject(); in.close();
//		ObjectIntPair<NERInfoSet> p;
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
