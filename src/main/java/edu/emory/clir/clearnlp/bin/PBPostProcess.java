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
package edu.emory.clir.clearnlp.bin;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.kohsuke.args4j.Option;

import edu.emory.clir.clearnlp.collection.list.SortedArrayList;
import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.constituent.CTLibEn;
import edu.emory.clir.clearnlp.constituent.CTNode;
import edu.emory.clir.clearnlp.constituent.CTTagEn;
import edu.emory.clir.clearnlp.constituent.CTTree;
import edu.emory.clir.clearnlp.lexicon.propbank.PBArgument;
import edu.emory.clir.clearnlp.lexicon.propbank.PBInstance;
import edu.emory.clir.clearnlp.lexicon.propbank.PBLib;
import edu.emory.clir.clearnlp.lexicon.propbank.PBLocation;
import edu.emory.clir.clearnlp.lexicon.propbank.PBReader;
import edu.emory.clir.clearnlp.lexicon.propbank.PBTag;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBPostProcess
{
	static final public Pattern ILLEGAL_ROLESET = Pattern.compile(".*\\.(ER|NN|IE|YY)");

	/** The error code for mis-aligned arguments. */	
	static final public String ERR_ALIGN   = "A";
	/** The error code for cyclic relations. */	
	static final public String ERR_CYCLIC  = "C";
	/** The error code for overlapping arguments. */	
	static final public String ERR_OVERLAP = "O";
	/** The error code for no counterpart of light-verb. */	
	static final public String ERR_LV      = "L";
	
	final private Pattern P_ICH_RNR = Pattern.compile("\\*(ICH|RNR)\\*.*");
	
	@Option(name="-i", usage="the PropBank file to be post-processed (required)", required=true, metaVar="<filename>")
	private String s_propFile;
	@Option(name="-o", usage="the post-processed PropBank file (default: null)", required=false, metaVar="<filename>")
	private String s_postFile = null;
	@Option(name="-t", usage="the directory path to Treebank files (required)", required=true, metaVar="<dirpath>")
	private String s_treeDir;
	@Option(name="-n", usage="if set, normalize co-indices of constituent trees", required=false)
	private boolean b_norm;
	@Option(name="-l", usage="language (default: english)", required=false, metaVar="<language>")
	private String s_language = TLanguage.ENGLISH.toString();
	
	public PBPostProcess(String[] args)
	{
		BinUtils.initArgs(args, this);
		postProcess(s_propFile, s_postFile, s_treeDir, b_norm, TLanguage.getType(s_language));
	}
	
	@SuppressWarnings("incomplete-switch")
	public void postProcess(String propFile, String postFile, String treeDir, boolean norm, TLanguage language)
	{
		PBReader reader = new PBReader(IOUtils.createFileInputStream(propFile));
		List<PBInstance> instances = reader.getSortedInstanceList(treeDir, norm);
		List<PBInstance> remove = new ArrayList<>();
		mergeLightVerbs(instances);
		PBArgument aDSP;
		CTTree tree;
		
		for (PBInstance instance : instances)
		{
			System.out.println(instance.getKey());
			tree = instance.getTree();
			
			// LINK-SLC, LINK-PSV are found here
			switch (language)
			{
			case ENGLISH: CTLibEn.preprocess(tree); break;
			}
			
			// removes instances that do not align with the constituent tree
			if (isSkip(instance, tree))		// varies by languages
			{
				remove.add(instance);
				continue;
			}
			
			// sorts by arguments' terminal IDs
			instance.sortArguments();
			
			joinConcatenations(instance);
			fixCyclicLocations(instance);
			removeRedundantLocations(instance);
			
			// annotating NP(PRO) under S following the verb
			if (instance.isVerbPredicate())					// English only
				fixIllegalPROs(instance);
			
			aDSP = getArgumentDSP(instance);				// English only
			setLinks(instance);
			normalizeLinks(instance);						// varies by languages
			instance.sortArguments();
			removeRedundantLocations(instance);
			findOverlappingArguments(instance);
			addLinks(instance);
			raiseEmptyArguments(instance);					// English only
			if (aDSP != null) instance.addArgument(aDSP);	// English only
		}
		
		instances.removeAll(remove);
		
		if (postFile == null)
			printInstances(instances, treeDir);
		else
			PBLib.printInstances(instances, IOUtils.createFileOutputStream(postFile));
	}
	
	/**
	 * Returns {@code true} if the specific PropBank instance is valid.
	 * @param instance a PropBank instance
	 * @param tree a constituent tree associated with the PropBank instance.
	 * @return {@code true} if the specific PropBank instance is valid.
	 */
	boolean isSkip(PBInstance instance, CTTree tree)
	{
		if (ILLEGAL_ROLESET.matcher(instance.getRolesetID()).find())
			return true;
		
		if (findMisalignedArguments(instance))
			return true;
		
		if (instance.isVerbPredicate() && tree.getTerminal(instance.getPredicateID()).getParent().isConstituentTag(CTTagEn.C_PP))
			return true;	// according to
		
		return false;
	}
	
	// PRE : parse 25 10 gold make-v make.LV ----- 10:0-rel 1:1-ARG0 11:0-ARGM-PRR
	// PRE : parse 25 11 gold determination-n determination.01 ----- 10:0-ARGM-LVB 11:0-rel 12:2-ARGM-MNR
	// POST: parse 25 10 gold make-v make.LV ----- 10:0-rel 11:1-ARGM-PRR
	// POST: parse 25 11 gold determination-n determination.01 ----- 1:1-ARG0 10:0,11:0-rel 12:2-ARGM-MNR 
	void mergeLightVerbs(List<PBInstance> instances)
	{
		Map<String,PBInstance> mNouns   = new HashMap<>();
		List<PBInstance> lightVerbs     = new ArrayList<>();
		List<PBInstance> errorInstances = new ArrayList<>();
		PBInstance nounInstance;
		List<PBArgument> args;
		PBArgument rel;
		
		for (PBInstance instance : instances)
		{
			if (instance.isVerbPredicate())
			{
				if (instance.getRolesetID().endsWith("LV"))
					lightVerbs.add(instance);
			}
			else
				mNouns.put(instance.getKey(), instance);
		}
		
		for (PBInstance lightVerb : lightVerbs)
		{
			nounInstance = null;
			args  = new ArrayList<PBArgument>();
			
			for (PBArgument arg : lightVerb.getArgumentList())
			{
				if (arg.getLabel().endsWith("PRR"))
					nounInstance = mNouns.get(lightVerb.getKey(arg.getLocation(0).getTerminalID()));
				else if (arg.getLabel().startsWith("LINK") || arg.isLabel(PBTag.PB_ARG0))
					args.add(arg);
			}
			
			if (nounInstance == null)
			{
				StringBuilder build = new StringBuilder();
				
				build.append(ERR_LV);
				build.append(":");
				build.append(" ");
				build.append(lightVerb.toString());
				
				System.err.println(build.toString());
				errorInstances.add(lightVerb);
			}
			else
			{
				nounInstance.addArguments(args);
				rel = nounInstance.getFirstArgument(PBTag.PB_REL);
				rel.addLocation(new PBLocation(lightVerb.getPredicateID(), 0, ","));
				
				args.clear();
				
				for (PBArgument arg : lightVerb.getArgumentList())
				{
					if (!arg.isLabel(PBTag.PB_REL) && !arg.getLabel().endsWith("PRR"))
						args.add(arg);
				}
				
				lightVerb.removeArguments(args);
			}
		}
		
		instances.removeAll(errorInstances);
	}
	
	/** Returns {@code true} if the specific instance includes arguments misaligned to the constituent tree. */
	private boolean findMisalignedArguments(PBInstance instance)
	{
		CTTree tree  = instance.getTree();
		String label = null;
		
		if (!tree.isRange(instance.getPredicateID(), 0) ||
			(instance.isVerbPredicate() && !tree.getTerminal(instance.getPredicateID()).getConstituentTag().startsWith("VB")) ||
			(instance.isNounPredicate() && !tree.getTerminal(instance.getPredicateID()).getConstituentTag().startsWith("NN")))
		{
			label = PBTag.PB_REL;
		}
		else
		{
			outer: for (PBArgument arg : instance.getArgumentList())
			{
				for (PBLocation loc : arg.getLocationList())
				{
					if (!tree.isRange(loc))
					{
						label = arg.getLabel();
						break outer;
					}
					
					if (loc.isType("&"))
						loc.setType("*");
				}
			}
		}
		
		if (label != null)
		{
			StringBuilder build = new StringBuilder();
			
			build.append(ERR_ALIGN);
			build.append(":");
			build.append(label);
			build.append(" ");
			build.append(instance.toString());
			
			System.err.println(build.toString());
			return true;
		}
		
		return false;
	}
	
	/**
	 * Joins concatenated locations by replacing them with higher nodes.
	 * PRE: {@link PBInstance#sortArgs()} is called.
	 */
	private void joinConcatenations(PBInstance instance)
	{
		SortedArrayList<Integer> ids = new SortedArrayList<>();
		CTTree tree = instance.getTree();
		int terminalId, height;
		CTNode node, parent;
		List<PBLocation> lNew;
		
		for (PBArgument arg : instance.getArgumentList())
		{
			if (arg.isLabel(PBTag.PB_REL))	continue;
			ids.clear();
			
			for (PBLocation loc : arg.getLocationList())
			{
				if (!loc.isType("") && !loc.isType(","))	return;
				if (loc.getHeight() > 0)					return;
				ids.add(loc.getTerminalID());
			}
			
			lNew = new ArrayList<PBLocation>();
			
			while (!ids.isEmpty())
			{
				terminalId = ids.get(0);
				height     = 0;
				node       = tree.getNode(terminalId, height);
				
				while ((parent = node.getParent()) != null && !parent.isConstituentTag(CTTagEn.TOP) && isSubset(ids, parent.getTerminalIDSet()))
				{
					node = parent;
					height++;
				}
				
				lNew.add(new PBLocation(terminalId, height, ","));
				ids.removeAll(node.getTerminalIDSet());
			}
			
			if (lNew.size() < arg.getLocationSize())
			{
				lNew.get(0).setType("");
				arg.setLocations(lNew);
			}
		}
	}
	
	public boolean isSubset(SortedArrayList<Integer> s1, Set<Integer> s2)
	{
		for (Integer t : s2)
		{
			if (!s1.contains(t))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Fixes locations cyclic to its predicate.
	 * PRE: {@link PBInstance#sortArgs()} is called.
	 */
	private void fixCyclicLocations(PBInstance instance)
	{
		CTTree   tree    = instance.getTree();
		int    predId    = instance.getPredicateID();
		boolean isCyclic = false;
		CTNode  node, tmp;
		
		StringBuilder build = new StringBuilder();
		build.append(ERR_CYCLIC);
		
		for (PBArgument arg : instance.getArgumentList())
		{
			if (arg.isLabel(PBTag.PB_REL))	continue;
			
			for (PBLocation loc : arg.getLocationList())
			{
				if ((node = tree.getNode(loc)).getTerminalIDSet().contains(predId))
				{
					if (arg.isLabel(PBTag.PB_ARGM_MOD))
						loc.setHeight(0);
					else if (arg.isLabel(PBTag.PB_LINK_SLC) && node.isConstituentTag(CTLibEn.C_SBAR) && (tmp = node.getFirstChild(CTLibEn.M_WHx)) != null)
						loc.set(tmp.getPBLocation(), loc.getType());
					else if (node.isConstituentTag(CTLibEn.C_NP) && (tmp = node.getChild(0)).isConstituentTag(CTLibEn.C_NP) && !tmp.getTerminalIDSet().contains(predId))
						loc.setHeight(loc.getHeight()-1);
					else
					{
						build.append(":");
						build.append(arg.getLabel());
						isCyclic = true;
						break;
					}
				}
			}
		}
		
		if (isCyclic)
		{
			build.append(" ");
			build.append(instance.toString());
			System.err.println(build.toString());
		//	System.err.println(tree.toString(true,true));
		}
	}
	
	/**
	 * Removes redundant or overlapping locations of this argument.
	 * PRE: {@link PBInstance#sortArgs()} is called.
	 */
	private void removeRedundantLocations(PBInstance instance)
	{
		List<PBLocation> lDel = new ArrayList<PBLocation>();
		PBLocation curr, next;
		int i, size;
		
		for (PBArgument arg : instance.getArgumentList())
		{
			size = arg.getLocationSize() - 1;
			lDel.clear();
			
			for (i=0; i<size; i++)
			{
				curr = arg.getLocation(i);
				next = arg.getLocation(i+1);
				
				if (curr.getTerminalID() == next.getTerminalID())
					lDel.add(curr);
			}
			
			if (!lDel.isEmpty())
				arg.removeLocations(lDel);
		}
	}
	
	/** Fixes illegal PROs. */
	private void fixIllegalPROs(PBInstance instance)
	{
		CTTree tree = instance.getTree();
		CTNode node;
		
		for (PBArgument arg : instance.getArgumentList())
		{
			if (arg.isLabel(PBTag.PB_REL))	continue;
			
			for (PBLocation loc : arg.getLocationList())
			{
				if (loc.getTerminalID() > instance.getPredicateID())
				{
					node = tree.getNode(loc);
					
					if (node.isEmptyCategoryTerminal() && node.hasFunctionTag(CTLibEn.F_SBJ) && node.getParent().isConstituentTag(CTLibEn.C_S))
						loc.setHeight(loc.getHeight()+1);
				}
			}
		}
	}
	
	/**
	 * Adds antecedents from manual annotation of LINK-*.
	 * PRE: {@link PBInstance#sortArgs()} is called. 
	 */
	private void setLinks(PBInstance instance)
	{
		List<PBArgument> lLinks = new ArrayList<PBArgument>();
		CTTree tree = instance.getTree();
		CTNode node, link;
		PBLocation loc; int i;
		
		for (PBArgument arg : instance.getArgumentList())
		{
			if (arg.getLabel().startsWith("LINK"))
				lLinks.add(arg);
			
			for (i=arg.getLocationSize()-1; i>0; i--)
			{
				loc  = arg.getLocation(i);
				node = tree.getNode(loc);
				
				if (node.getConstituentTag().startsWith("WH"))
				{
					link = CTLibEn.getRelativizer(node);

					if (link.getAntecedent() == null)
					{
						link.setAntecedent(tree.getNode(arg.getLocation(i-1)));
						break;
					}
				}
				else if (CTLibEn.isRelativizer(node))
				{
					if (node.getAntecedent() == null)
					{
						node.setAntecedent(tree.getNode(arg.getLocation(i-1)));
						break;
					}
				}
				else if (node.isEmptyCategoryTerminal() && loc.isType("*"))
				{
					link = node.getFirstTerminal();
					
					if (link.getAntecedent() == null)
						link.setAntecedent(tree.getNode(arg.getLocation(i-1)));
				}
			}
		}
		
		if (!lLinks.isEmpty())
			instance.removeArguments(lLinks);
	}
	
	/**
	 * Normalizes links.
	 * PRE: {@link CTTree#setPBLocs()} and {@link C} needs to be called before.
	 */
	private void normalizeLinks(PBInstance instance)
	{
		List<PBLocation> lDel = new ArrayList<>();
		CTTree tree = instance.getTree();
		CTNode curr, node, ante;
		PBLocation cLoc; int i;
		List<CTNode> list;
		CTNode pred = tree.getTerminal(instance.getPredicateID());
		
		for (PBArgument arg : instance.getArgumentList())
		{
			if (arg.isLabel(PBTag.PB_REL))	continue;
			lDel.clear();
			
			for (i=0; i<arg.getLocationSize(); i++)	// size() gets changed dynamically
			{
				cLoc = arg.getLocation(i);
				curr = tree.getNode(cLoc);
				
				if (CTLibEn.isRelativizer(curr))
				{
					if ((ante = curr.getAntecedent()) != null)
						arg.addLocation(new PBLocation(ante.getPBLocation(), "*"));
					
					if ((node = getCoIndexedWHNode(curr)) != null && !node.getChildrenList().contains(tree.getTerminal(instance.getPredicateID())))
					{
						cLoc.set(node.getPBLocation(), "*");
					}
				}
				else if (curr.getConstituentTag().startsWith("WH"))
				{
					if ((node = CTLibEn.getRelativizer(curr)) != null && (ante = node.getAntecedent()) != null)
						arg.addLocation(new PBLocation(ante.getPBLocation(), "*"));
				}
				else if (curr.isEmptyCategoryTerminal())		// *T*, *
				{
					cLoc.setHeight(0);
					node = tree.getTerminal(cLoc.getTerminalID());
					
					if ((ante = node.getAntecedent()) != null)
						arg.addLocation(new PBLocation(ante.getPBLocation(), "*"));
				}
				else if (!(list = curr.getEmptyCategoryListInSubtree(P_ICH_RNR)).isEmpty())
				{
					for (CTNode ec : list)
					{
						lDel.add(new PBLocation(ec.getPBLocation(), ""));
						
						if ((ante = ec.getAntecedent()) != null)
						{
							if (ante.isDescendantOf(curr) || pred.isDescendantOf(ante))
								lDel.add(new PBLocation(ante.getPBLocation(), ""));
							else
								arg.addLocation(new PBLocation(ante.getPBLocation(), ";"));
						}
					}
				}
				else if (curr.isConstituentTag(CTTagEn.C_S) && (node = curr.getFirstChild(CTLibEn.M_SBJ)) != null && node.isEmptyCategoryTerminal() && curr.containsChild(CTLibEn.M_VP))
				{
					node = node.getFirstTerminal();
					
					if (CTLibEn.P_PASSIVE_NULL.matcher(node.getWordForm()).find() && (ante = node.getAntecedent()) != null && ante.hasFunctionTag(CTTagEn.F_SBJ) && !ante.isEmptyCategoryTerminal() && !existsLocation(instance, ante.getPBLocation()))
						arg.addLocation(new PBLocation(ante.getPBLocation(), "*"));
				}
			}
			
			// removes errorneous arguments
			for (PBLocation rLoc : lDel)
				arg.removeLocation(rLoc.getTerminalID(), rLoc.getHeight());
		}
	}
	
	/** Called by {@link PBLibEn#normalizeLinks(CTTree, PBArg, int)}. */
	private CTNode getCoIndexedWHNode(CTNode node)
	{
		CTNode parent = node.getParent();
		
		while (parent != null)
		{
			if (!parent.getConstituentTag().startsWith("WH"))
				break;
			
			if (parent.getEmptyCategoryIndex() != -1)
				return parent;
			
			parent = parent.getParent();
		}
		
		return null;
	}
	
	private boolean existsLocation(PBInstance instance, PBLocation loc)
	{
		for (PBArgument arg : instance.getArgumentList())
		{
			for (PBLocation l : arg.getLocationList())
			{
				if (l.matches(loc.getTerminalID(), loc.getHeight()))
					return true;
			}
		}
		
		return false;
	}
	
	private boolean findOverlappingArguments(PBInstance instance)
	{
		CTTree  tree = instance.getTree();
		Set<Integer> si, sj;
		PBArgument ai, aj;
		int i, j, size = instance.getArgumentSize(), ni, nj;
		List<PBArgument> lDel = new ArrayList<PBArgument>();
		
		for (i=0; i<size; i++)
		{
			ai = instance.getArgument(i);
			si = getTerminalIDSet(ai, tree);
			ni = si.size();
			
			for (j=i+1; j<size; j++)
			{
				aj = instance.getArgument(j);
				sj = getTerminalIDSet(aj, tree);
				nj = sj.size();
				
				if (DSUtils.isSubset(si, sj) && ni != nj)
				{
					if (!aj.isLabel("rel")) lDel.add(aj);
				}
				else if (DSUtils.isSubset(sj, si) && ni != nj)
				{
					if (!ai.isLabel("rel")) lDel.add(ai);
				}
				else if (DSUtils.hasIntersection(si, sj))
				{
					StringBuilder build = new StringBuilder();
					
					build.append(ERR_OVERLAP);
					build.append(":");
					build.append(ai.getLabel());
					build.append(":");
					build.append(aj.getLabel());
					build.append(" ");
					build.append(instance.toString());
					
					System.err.println(build.toString());
				//	System.err.println(tree.toString(true,true));
					return true;
				}
			}
		}
		
		instance.removeArguments(lDel);
		return false;
	}
	
	/** Returns the set of terminal IDs associated with this argument. */
	private Set<Integer> getTerminalIDSet(PBArgument arg, CTTree tree)
	{
		Set<Integer> set = new HashSet<Integer>();
		
		for (PBLocation loc : arg.getLocationList())
		{
			if (!loc.isType(";"))
				set.addAll(tree.getNode(loc).getTerminalIDSet());
		}
		
		return set;
	}
	
	private void addLinks(PBInstance instance)
	{
		CTTree tree = instance.getTree();
		CTNode node, comp, ante = null;
		String label;
		List<PBArgument> lAdd = new ArrayList<>();
		PBArgument nArg;
		
		for (PBArgument arg : instance.getArgumentList())
		{
			for (PBLocation loc : arg.getLocationList())
			{
				node  = tree.getNode(loc);
				label = null;
				
				if (node.getConstituentTag().startsWith("WH"))
				{
					if ((comp = CTLibEn.getRelativizer(node)) != null && (ante = comp.getAntecedent()) != null)
						label = PBTag.PB_LINK_SLC;
				}
				else if (node.isEmptyCategory())
				{
					if ((ante = node.getAntecedent()) != null)
					{
						if (node.isWordForm(CTLibEn.E_NULL))
							label = PBTag.PB_LINK_PSV;
						else if (node.isWordForm(CTLibEn.E_PRO))
							label = PBTag.PB_LINK_PRO;
					}
				}
				
				if (label != null)
				{
					nArg = new PBArgument();
					nArg.setLabel(label);
					nArg.addLocation(new PBLocation(ante.getPBLocation(), ""));
					nArg.addLocation(new PBLocation(node.getPBLocation(), "*"));
					
					lAdd.add(nArg);
				}
			}
		}
		
		instance.addArguments(lAdd);
	}
	
	private void raiseEmptyArguments(PBInstance instance)
	{
		CTTree tree = instance.getTree();
		CTNode node, parent;
		PBLocation loc;
		int i, size;
		
		for (PBArgument arg : instance.getArgumentList())
		{
			if (arg.isLabel(PBTag.PB_REL))	continue;
			size = arg.getLocationSize();
			
			for (i=0; i<size; i++)
			{
				loc  = arg.getLocation(i);
				node = tree.getNode(loc);
				parent = node.getParent();
				
				if (parent != null && !parent.isConstituentTag(CTLibEn.TOP) && parent.getChildrenSize() == 1)
					node = parent;
				
				loc.set(node.getPBLocation(), loc.getType());
			}
		}
	}
	
	private void printInstances(List<PBInstance> instances, String treeDir)
	{
		String treePath = "", propPath;
		PrintStream fout = null;
		
		for (PBInstance instance : instances)
		{
			if (!treePath.equals(instance.getTreePath()))
			{
				if (fout != null)	fout.close();
				treePath = instance.getTreePath();
				propPath = treePath.substring(0, treePath.lastIndexOf(".")) + ".prop";
				
				if (new File(propPath).exists())
					System.err.println("Warning: '"+propPath+"' already exists");
				
				fout = IOUtils.createBufferedPrintStream(treeDir+File.separator+propPath);
			}
			
			fout.println(instance.toString());
		}
		
		if (fout != null)	fout.close();
	}
	
	private PBArgument getArgumentDSP(PBInstance instance)
	{
		CTTree tree = instance.getTree();
		CTNode pred = tree.getTerminal(instance.getPredicateID());
		Pair<CTNode,CTNode> pair = getESMPair(pred);
		if (pair == null)	return null;
		
		Pair<PBArgument,Set<Integer>> max = new Pair<PBArgument,Set<Integer>>(null, new HashSet<>());
		CTNode prn = pair.o1;
		CTNode esm = pair.o2;
		Set<Integer> set;
		
		for (PBArgument arg : instance.getArgumentList())
		{
			if (!PBLib.isNumberedArgument(arg.getLabel()) || arg.isLabel(PBTag.PB_ARG0))
				continue;
			
			set = arg.getTerminalIDSet(tree);
			
			if (set.contains(esm.getTerminalID()))
			{
				max.set(arg, set);
				break;
			}
			
			if (arg.containsOperator(",") && max.o2.size() < set.size())
				max.set(arg, set);
		}
		
		if (max.o1 == null)	return null;
		CTNode dsp = esm.getAntecedent();
		if (dsp == null)	dsp = prn.getNearestAncestor(CTLibEn.M_Sx);
		
		if (dsp != null)
		{
			PBArgument arg = new PBArgument();
			arg.addLocation(dsp.getPBLocation());
			arg.setLabel(max.o1.getLabel()+"-"+PBTag.PB_DSP);
			instance.removeArguments(max.o1.getLabel());
			
			return arg;
		}
		
		return null;
	}
	
	private Pair<CTNode,CTNode> getESMPair(CTNode pred)
	{
		CTNode s = pred.getNearestAncestor(CTLibEn.M_Sx);
		
		if (s != null && s.getParent().isConstituentTag(CTLibEn.C_PRN))
		{
			CTNode next = pred.getRightNearestSibling(CTLibEn.M_S_SBAR);
			
			if (next != null)
			{
				CTNode ec = getESM(next);
				if (ec != null)	return new Pair<CTNode,CTNode>(s.getParent(), ec);
			}
		}
		
		return null;
	}
	
	private CTNode getESM(CTNode node)
	{
		if (node.isConstituentTag(CTTagEn.C_S))
			return getESMAux(node);
		else if (node.isConstituentTag(CTTagEn.C_SBAR))
		{
			if (node.getChildrenSize() == 2)
			{
				CTNode fst = node.getChild(0);
				CTNode snd = node.getChild(1);
				
				if (fst.isEmptyCategory() && fst.getWordForm().equals(CTTagEn.E_ZERO))
					return getESMAux(snd);
			}
		}
		
		return null;
	}
	
	private CTNode getESMAux(CTNode node)
	{
		if (node.isEmptyCategoryTerminal())
		{
			CTNode ec = node.getFirstTerminal();
			
			if (ec != null && (ec.getWordForm().startsWith(CTTagEn.E_TRACE) || ec.getWordForm().startsWith(CTLibEn.E_ESM)))
				return ec;
		}
		
		return null;
	}
	
	static public void main(String[] args)
	{
		new PBPostProcess(args);
	//	new PBPostProcess().postProcess(propFile, postFile, treeDir, norm);
	}
}