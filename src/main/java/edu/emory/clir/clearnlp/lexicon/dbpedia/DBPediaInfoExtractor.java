package edu.emory.clir.clearnlp.lexicon.dbpedia;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import edu.emory.clir.clearnlp.util.CharUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.PatternUtils;
import edu.emory.clir.clearnlp.util.StringUtils;
import edu.emory.clir.clearnlp.util.constant.PatternConst;
import edu.emory.clir.clearnlp.util.constant.StringConst;

public class DBPediaInfoExtractor implements DBPediaXML
{
	static final Pattern RESOURCE = Pattern.compile("<http://dbpedia.org/resource/(.+?)>");
	static final Pattern ONTOLOGY = Pattern.compile("<http://dbpedia.org/ontology/(.+?)>");
	
	public DBPediaInfoMap getInfoMap(DBPediaTypeMap typeMap, InputStream in) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		DBPediaInfoMap map = new DBPediaInfoMap();
		String line, title, type;
		DBPediaInfo info;
		Matcher m;
		
		while ((line = reader.readLine()) != null)
		{
			m = RESOURCE.matcher(line);
			if (!m.find()) continue;
			title = m.group(1);
			
			m = ONTOLOGY.matcher(line);
			if (!m.find()) continue;
			type = m.group(1);
			
			if (!type.startsWith("Wikidata"))
				map.computeIfAbsent(title, k -> new DBPediaInfo()).addType(DBPediaType.getType(type));
		}
		
		for (Entry<String,DBPediaInfo> e : map.entrySet())
		{
			info = e.getValue();
			trimInstanceTypes(typeMap, info.getTypes());
			info.addAlias(getAlias(e.getKey()));
		}
		
		return map;
	}
	
	private void trimInstanceTypes(DBPediaTypeMap typeMap, Set<DBPediaType> set)
	{
		List<DBPediaType>  list = new ArrayList<>(set);
		Set<DBPediaType> remove = new HashSet<>();
		int i, j, size = list.size();
		DBPediaType ti, tj;
		
		for (i=1; i<size; i++)
		{
			ti = list.get(i);
			
			for (j=0; j<i; j++)
			{
				tj = list.get(j);
				
				if (typeMap.isSuperType(ti, tj))
					remove.add(tj);
				else if (typeMap.isSuperType(tj, ti))
					remove.add(ti);
			}
		}

		set.removeAll(remove);
	}
	
	private String getAlias(String s)
	{
		if (StringUtils.containsPunctuation(s) || StringUtils.containsUpperCaseOnly(s))
			return PatternUtils.replaceAll(PatternConst.UNDERSCORE, s, StringConst.SPACE);
		
		StringBuilder build = new StringBuilder();
		char[] cs = s.toCharArray();
		int i, len = cs.length;
		
		for (i=0; i<len; i++)
		{
			if (0 < i&&i < len-1 && CharUtils.isLowerCase(cs[i-1]) && CharUtils.isUpperCase(cs[i]))
				build.append(StringConst.SPACE);
			
			build.append(cs[i]);
		}
		
		return build.toString();
	}
	
	public void addRedirects(Map<String,DBPediaInfo> infoMap, InputStream in) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		String line, redirect, title;
		DBPediaInfo info;
		Matcher m;
		
		while ((line = reader.readLine()) != null)
		{
			m = RESOURCE.matcher(line);
			if (!m.find()) continue;
			redirect = m.group(1);
			
			if (!m.find()) continue;
			title = m.group(1);
			
			if ((info = infoMap.get(title)) != null)
				info.addAlias(getAlias(redirect));
		}
	}
	
	static public void main(String[] args) throws Exception
	{
		DBPediaInfoExtractor ex = new DBPediaInfoExtractor();
		Gson gson = new Gson();
		
		DBPediaTypeMap typeMap = gson.fromJson(new InputStreamReader(IOUtils.createXZBufferedInputStream(args[0])), DBPediaTypeMap.class);	// dbpedia.owl.json.xz
		DBPediaInfoMap infoMap = ex.getInfoMap(typeMap, IOUtils.createXZBufferedInputStream(args[1]));	// instance_types_en.nt.xz
		ex.addRedirects(infoMap, IOUtils.createXZBufferedInputStream(args[2]));							// redirects_en.ttl.xz
		PrintStream out = new PrintStream(IOUtils.createXZBufferedOutputStream(args[3]));				// instances_en.json.xz
		out.print(gson.toJson(infoMap));
		out.close();
		System.out.println(infoMap.get("Abraham_Lincoln").getAliases());
	}
}
