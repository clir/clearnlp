/**
 * Copyright 2014, Emory University
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
package edu.emory.clir.clearnlp.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import edu.emory.clir.clearnlp.dependency.DEPFeat;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.arc.AbstractArc;
import edu.emory.clir.clearnlp.util.arc.DEPArc;
import edu.emory.clir.clearnlp.util.arc.SRLArc;
import edu.emory.clir.clearnlp.util.constant.PatternConst;
import edu.emory.clir.clearnlp.util.constant.StringConst;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class TSVReader extends AbstractReader<DEPTree>
{
	static public final String BLANK = StringConst.UNDERSCORE;
	/** The delimiter between columns. */
	static public final String DELIM_COLUMN = StringConst.TAB;
	/** The delimiter between arcs. */
	static public final String DELIM_ARCS = StringConst.SEMICOLON;

	private final Pattern P_COLUMN = PatternConst.TAB;
	private final Pattern P_ARCS   = PatternConst.SEMICOLON;
	
	protected int i_id;
	protected int i_form;
	protected int i_lemma;
	protected int i_posTag;
	protected int i_namedEntityTag;
	protected int i_feats;
	protected int i_headID;
	protected int i_deprel;
	protected int i_xheads;
	protected int i_sheads;
	
	public TSVReader(int iForm)
	{
		super(TReader.TSV);
		init(-1, iForm, -1, -1, -1, -1, -1, -1, -1, -1);
	}
	
	/**
	 * For part-of-speech tagging.
	 * @see #init(int, int, int, int, int, int, int, int, int, int)
	 */
	public TSVReader(int iForm, int iPOSTag)
	{
		super(TReader.TSV);
		init(-1, iForm, -1, iPOSTag, -1, -1, -1, -1, -1, -1);
	}
	
	/**
	 * For dependency parsing.
	 * @see #init(int, int, int, int, int, int, int, int, int, int)
	 */
	public TSVReader(int iID, int iForm, int iLemma, int iPOSTag, int iFeats, int iHeadID, int iDeprel)
	{
		super(TReader.TSV);
		init(iID, iForm, iLemma, iPOSTag, -1, iFeats, iHeadID, iDeprel, -1, -1);
	}
	
	/**
	 * For semantic role labeling.
	 * @see #init(int, int, int, int, int, int, int, int, int, int)
	 */
	public TSVReader(int iID, int iForm, int iLemma, int iPOSTag, int iFeats, int iHeadID, int iDeprel, int iSHeads)
	{
		super(TReader.TSV);
		init(iID, iForm, iLemma, iPOSTag, -1, iFeats, iHeadID, iDeprel, -1, iSHeads);
	}
	
	/**
	 * Including all.
	 */
	public TSVReader(int iID, int iForm, int iLemma, int iPOSTag, int iNamedEntityTag, int iFeats, int iHeadID, int iDeprel, int iXHeads, int iSHeads)
	{
		super(TReader.TSV);
		init(iID, iForm, iLemma, iPOSTag, iNamedEntityTag, iFeats, iHeadID, iDeprel, iXHeads, iSHeads);
	}
	
	/**
	 * Constructs a dependency reader.
	 * @param iID the column index of the node ID field.
	 * @param iForm the column index of the word-form field.
	 * @param iLemma the column index of the lemma field.
	 * @param iPOSTag the column index of the POS field.
	 * @param iNamedEntityTag the column index of the named entity tag field.
	 * @param iFeats the column index of the extra features field.
	 * @param iHeadID the column index of the head ID field.
	 * @param iDeprel the column index of the dependency label field.
	 * @param iXHeads the column index of the secondary dependency field.
	 * @param iSHeads the column index of the semantic head field.
	 */
	public void init(int iID, int iForm, int iLemma, int iPOSTag, int iNamedEntityTag, int iFeats, int iHeadID, int iDeprel, int iXHeads, int iSHeads)
	{
		i_id				= iID;
		i_form				= iForm;
		i_lemma				= iLemma;
		i_posTag			= iPOSTag;
		i_namedEntityTag	= iNamedEntityTag;
		i_feats				= iFeats;
		i_headID			= iHeadID;
		i_deprel			= iDeprel;
		i_xheads			= iXHeads;
		i_sheads			= iSHeads;
	}

	public DEPTree next()
	{
		DEPTree tree = null;
		
		try
		{
			List<String[]> lines = readLines();
			if (lines == null)	return null;
			
			tree = getDEPTree(lines);
		}
		catch (Exception e) {e.printStackTrace();}
		
		return tree;
	}
	
	/** Returns the next batch of lines. */
	protected List<String[]> readLines() throws Exception
	{
		// skip empty lines
		String line;
		
		while ((line = b_reader.readLine()) != null)
			if (!isSkip(line))	break;

		// the end of the line
		if (line == null)
		{	close();	return null;	}
		
		// add lines
		List<String[]> list = new ArrayList<String[]>();
		list.add(P_COLUMN.split(line));
		
		while ((line = b_reader.readLine()) != null)
		{
			if (isSkip(line))
				return list;
			else
				list.add(P_COLUMN.split(line));
		}

		return list;
	}
	
	/** Called by {@link AbstractColumnReader#readLines()}. */
	protected boolean isSkip(String line)
	{
		return line.trim().isEmpty();
	}

	protected DEPTree getDEPTree(List<String[]> lines)
	{
		List<DEPNode> nodes = new ArrayList<>();
		String form, lemma, pos, feats, nament;
		int id, i, size = lines.size();
		DEPNode node;
		String[] tmp;
		
		// add nodes
		for (i=0; i<size; i++)
		{
			tmp    = lines.get(i);
			form   = tmp[i_form];
			id     = (i_id     < 0) ? i+1   : Integer.parseInt(tmp[i_id]);
			lemma  = (i_lemma  < 0) ? null  : tmp[i_lemma];
			pos    = (i_posTag < 0) ? null  : tmp[i_posTag];
			feats  = (i_feats  < 0) ? BLANK : tmp[i_feats];
			nament = (i_namedEntityTag < 0 || tmp[i_namedEntityTag].equals(BLANK)) ? null : tmp[i_namedEntityTag];

			node = new DEPNode(id, form, lemma, pos, nament, new DEPFeat(feats));
			nodes.add(node);			
		}
		
		DEPTree tree = new DEPTree(nodes);

		// add heads
		for (i=0; i<size; i++)
		{
			node = tree.get(i+1);
			tmp  = lines.get(i);
			
			if (i_headID >= 0 && !tmp[i_headID].equals(BLANK))
				node.setHead(tree.get(Integer.parseInt(tmp[i_headID])), tmp[i_deprel]);
			
			if (i_xheads >= 0)
				node.setSecondaryHeads(getSecondaryHeadList(tree, tmp[i_xheads]));
			
			if (i_sheads >= 0)
				node.setSemanticHeads(getSemanticHeadList(tree, tmp[i_sheads]));
		}
		
		return tree;
	}
	
	private List<DEPArc> getSecondaryHeadList(DEPTree tree, String heads)
	{
		List<DEPArc> arcs = new ArrayList<>();
		if (heads.equals(BLANK)) return arcs;
		int headID, idx;
		String label;
		
		for (String head : P_ARCS.split(heads))
		{
			idx    = head.indexOf(AbstractArc.DELIM);
			headID = Integer.parseInt(head.substring(0, idx));
			label  = head.substring(idx+1);
			arcs.add(new DEPArc(tree.get(headID), label));
		}
		
		return arcs;
	}
	
	private List<SRLArc> getSemanticHeadList(DEPTree tree, String heads)
	{
		List<SRLArc> arcs = new ArrayList<>();
		if (heads.equals(BLANK)) return arcs;
		int headID, idx;
		String label;
		
		for (String head : P_ARCS.split(heads))
		{
			idx    = head.indexOf(AbstractArc.DELIM);
			headID = Integer.parseInt(head.substring(0, idx));
			label  = head.substring(idx+1);
			arcs.add(new SRLArc(tree.get(headID), label));
		}
		
		return arcs;
	}
	
	public boolean hasPOSTags()
	{
		return i_posTag >= 0;
	}
	
	public boolean hasLemmas()
	{
		return i_lemma >= 0;
	}
	
	public boolean hasNamedEntityTags()
	{
		return i_namedEntityTag >= 0;
	}
	
	public boolean hasDependencyHeads()
	{
		return i_headID >= 0;
	}
	
	public boolean hasSemanticHeads()
	{
		return i_sheads >= 0;
	}
}