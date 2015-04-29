package edu.emory.clir.clearnlp.component.mode.ner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;

public class DocumentFeatureExtractor
{
	public Map<String, String> accumulateDocumentFeatures(List<DEPTree> trees)
	{
		Map<String, String> features = new HashMap<>();
		Map<String, List<DEPNode>> occurrences = new HashMap<>();
		for (DEPTree tree : trees)
		{
			for (DEPNode node : tree)
			{
				if (!node.getNamedEntityTag().equals("O"))
				{
					occurrences.computeIfAbsent(node.getSimplifiedWordForm(), k -> new ArrayList<>()).add(node);
					mergeSubstringKeys(occurrences);
					if (node.getHead() != null)
						features.put("node word to head label relation", node.getSimplifiedWordForm() + " - " + node.getHead().getLabel());
										
					features.put(node.getSimplifiedWordForm() + " next word", tree.get(node.getID()+1).getSimplifiedWordForm());
					features.put(node.getSimplifiedWordForm() + " next word lemma", tree.get(node.getID()+1).getLemma());

					features.put(node.getSimplifiedWordForm() + " previous Word", tree.get(node.getID()+1).getSimplifiedWordForm());
					features.put(node.getSimplifiedWordForm() + " previous Word lemma", tree.get(node.getID()+1).getLemma());
				}
			}			
		}
		List<List<DEPNode>> occurrencesList = new ArrayList<>(occurrences.values());
		Collections.sort(occurrencesList,(a,b) -> a.size()-(b.size()));
		int i = 0;
		for (List<DEPNode> oi : occurrencesList)
		{
			for (DEPNode d : oi)
			{
				features.put(i + "thMostCommonNE", d.getSimplifiedWordForm());
				List<DEPNode> siblings = getSiblings(d);
				int j = siblings.size();
				for (DEPNode sibling : siblings)
				{
					features.put(i + "thMostCommonNE" + j + "thSibling label", sibling.getLabel());
					features.put(i + "thMostCommonNE" + j + "thSibling word form", sibling.getSimplifiedWordForm());
					features.put(i + "thMostCommonNE" + j + "thSibling word shape", sibling.getWordShape(2));
					features.put(i + "thMostCommonNE" + j + "thSibling lemma", sibling.getLemma());
					j--;
				}
			}
			i++;
		}
		return features;
	}
	
	private void mergeSubstringKeys(Map<String, List<DEPNode>> occurrences) {
		Map<String, List<DEPNode>> temp = new HashMap<>();
		for (Entry<String, List<DEPNode>> small: occurrences.entrySet())
		{
			for (Entry<String, List<DEPNode>> large: occurrences.entrySet())
			{
				if (large.getKey().toLowerCase().contains(small.getKey().toLowerCase()))
				{
					temp.put(small.getKey(), large.getValue());
					temp.put(large.getKey(), null);
				}
			}
		}
		for (Entry<String, List<DEPNode>> e: temp.entrySet())
		{
			if (e.getValue() == null)
			{
				occurrences.remove(e.getKey());
			}
			else {
				occurrences.get(e.getKey()).addAll(e.getValue());
			}
		}
	}

	public static List<DEPNode> getSiblings(DEPNode node)
	{
		List<DEPNode> siblings = new ArrayList<>();
//		if (node.getLeftNearestSibling() != null)
			siblings.add(node.getLeftNearestSibling());
//		if(node.getLeftNearestSibling(1) != null)
			siblings.add(node.getLeftNearestSibling(1));
//		if(node.getRightNearestSibling() != null)
			siblings.add(node.getRightNearestSibling());
//		if(node.getRightNearestSibling(1) != null)
			siblings.add(node.getRightNearestSibling(1));
		return siblings;
	}
	
	public static DEPNode getPreviousNamedEntity(DEPNode node)
	{
		List<DEPNode> subNodes= node.getSubNodeList();
		for (int i = subNodes.size()-1; i>=0;i--)
		{
			if (!subNodes.get(i).getNamedEntityTag().equals("O"))
			{
				return subNodes.get(i);
			}
		}
		return null;
	}

	public static int getNumberOfCapitals(String s)
	{
		int count = 0;
		char[] chars = s.toCharArray();
		for (int i=0;i<s.length();i++)
			if (Character.isUpperCase(chars[i]))
			{
				count++;
			}
		return count;
	}
}
