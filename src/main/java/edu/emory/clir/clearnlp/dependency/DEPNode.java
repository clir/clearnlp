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
package edu.emory.clir.clearnlp.dependency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import edu.emory.clir.clearnlp.collection.list.SortedArrayList;
import edu.emory.clir.clearnlp.collection.set.IntHashSet;
import edu.emory.clir.clearnlp.feature.type.DirectionType;
import edu.emory.clir.clearnlp.feature.type.FieldType;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.StringUtils;
import edu.emory.clir.clearnlp.util.arc.AbstractArc;
import edu.emory.clir.clearnlp.util.arc.DEPArc;
import edu.emory.clir.clearnlp.util.arc.SRLArc;
import edu.emory.clir.clearnlp.util.constant.StringConst;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPNode implements Comparable<DEPNode>, Serializable
{
	private static final long serialVersionUID = 3794720014142939766L;
	/** The ID of this node (default: {@link DEPLib#NULL_ID}). */
	private int		n_id;
	/** The word-form of this node. */
	private String	s_wordForm;
	/** The simplified word-form of this node. */
	private String	s_simplifiedWordForm;
	/** The lemma of the word-form. */
	private String	s_lemma;
	/** The part-of-speech tag of the word-form. */
	private String	s_posTag;
	/** The named entity tag of this node. */
	private String	s_namedEntityTag;
	/** The extra features of this node. */
	private DEPFeat	d_feats;
	/** The dependency label of this node. */
	private String	s_label;
	/** The dependency head of this node. */
	private DEPNode	d_head;
	/** The sorted list of all dependents of this node (default: empty). */
	private SortedArrayList<DEPNode> l_dependents;
	/** The ID of this node among its sibling (starting with 0). */
	private int n_siblingID;
	private String s_sequenceLabel = null;
	
	/** The list of secondary heads of this node (default: empty). */
	private List<DEPArc> x_heads;
	/** The list of semantic heads of this node (default: empty). */
	private List<SRLArc> s_heads;
	
//	====================================== Constructors ======================================
	
	public DEPNode() {}
	
	public DEPNode(int id, String form)
	{
		init(id, form, null, null, null, new DEPFeat());
	}
	
	public DEPNode(int id, String form, String posTag, DEPFeat feats)
	{
		init(id, form, null, posTag, null, feats);
	}
	
	public DEPNode(int id, String form, String lemma, String posTag, DEPFeat feats)
	{
		init(id, form, lemma, posTag, null, feats);
	}
	
	public DEPNode(int id, String form, String lemma, String posTag, String namedEntityTag, DEPFeat feats)
	{
		init(id, form, lemma, posTag, namedEntityTag, feats);
	}
	
	/**
	 * Copies the basic fields from the specific node to this node.
	 * 
	 */
	public DEPNode(DEPNode node)
	{
		init(node.n_id, node.s_wordForm, node.s_lemma, node.s_posTag, node.s_namedEntityTag, new DEPFeat(node.d_feats));
	}
	
//	====================================== Initialization ======================================
	
	public void init(int id, String form, String lemma, String posTag, String namedEntityTag, DEPFeat feats)
	{
		setID(id);
		setWordForm(form);
		setLemma(lemma);
		setPOSTag(posTag);
		setNamedEntityTag(namedEntityTag);
		setFeats(feats);
		setLabel(null);
		setHead(null);
		l_dependents = new SortedArrayList<>();
	}
	
	/** Initializes this node as an artificial root node. */
	public void initRoot()
	{
		init(DEPLib.ROOT_ID, DEPLib.ROOT_TAG, DEPLib.ROOT_TAG, DEPLib.ROOT_TAG, DEPLib.ROOT_TAG, new DEPFeat());
	}
	
	public void initSecondaryHeads()
	{
		x_heads = new ArrayList<>();
	}

	/** Initializes semantic heads of this node. */
	public void initSemanticHeads()
	{
		s_heads = new ArrayList<>();
	}
	
	void clearDependencies()
	{
		d_head  = null;
		s_label = null;
		n_siblingID = 0;
		l_dependents.clear();
	}
	
	public String getSequenceLabel()
	{
		return s_sequenceLabel;
	}
	
	public void setSequenceLabel(String label)
	{
		s_sequenceLabel = label;
	}
	
	public String clearSequenceLabel()
	{
		String t = s_sequenceLabel;
		setSequenceLabel(null);
		return t;
	}
	
	public boolean isSequenceLabel(String label)
	{
		return label.equals(s_sequenceLabel);
	}
	
//	====================================== Basic fields ======================================
	
	public int getID()
	{
		return n_id;
	}
	
	public String getWordForm()
	{
		return s_wordForm;
	}
	
	public String getSimplifiedWordForm()
	{
		return s_simplifiedWordForm;
	}
	
	public String getLowerSimplifiedWordForm()
	{
		return StringUtils.toLowerCase(s_simplifiedWordForm);
	}
	
	public String getWordShape(int maxRepetitions)
	{
		return StringUtils.getShape(s_simplifiedWordForm, maxRepetitions);
	}
	
	public String getLemma()
	{
		return s_lemma;
	}
	
	public String getPOSTag()
	{
		return s_posTag;
	}
	
	public String getNamedEntityTag()
	{
		return s_namedEntityTag;
	}
	
	public DEPFeat getFeats()
	{
		return d_feats;
	}
	
	/** @return the value of the specific feature if exists; otherwise, {@code null}. */
	public String getFeat(String key)
	{
		return d_feats.get(key);
	}
	
	public void setID(int id)
	{
		n_id = id;
	}
	
	public void setWordForm(String form)
	{
		s_wordForm = form;
		s_simplifiedWordForm = StringUtils.toSimplifiedForm(form);
//		b_punctuation = StringUtils.containsPunctuationOnly(s_simplifiedWordForm);
	}
	
	public void setLemma(String lemma)
	{
		s_lemma = lemma;
	}
	
	public void setPOSTag(String posTag)
	{
		s_posTag = posTag;
	}
	
	public void setNamedEntityTag(String namedEntityTag)
	{
		s_namedEntityTag = namedEntityTag;
	}
	
	public void setFeats(DEPFeat feats)
	{
		d_feats = feats;
	}
	
	/**
	 * Puts an extra feature to this node using the specific key and value.
	 * This method overwrites an existing value of the same key with the current value. 
	 */
	public void putFeat(String key, String value)
	{
		d_feats.put(key, value);
	}
	
	public String clearPOSTag()
	{
		String pos = s_posTag;
		setPOSTag(null);
		return pos;
	}
	
	/** Removes the feature with the specific key. */
	public String removeFeat(String key)
	{
		return d_feats.remove(key);
	}
	
//	====================================== Getters ======================================
	
	/** @return the dependency label of this node. */
	public String getLabel()
	{
		return s_label;
	}
	
	/** @return the dependency head of this node. */
	public DEPNode getHead()
	{
		return d_head;
	}

	/** @return the dependency grand-head of this node if exists; otherwise, {@code null}. */
	public DEPNode getGrandHead()
	{
		DEPNode head = getHead();
		return (head == null) ? null : head.getHead();
	}
	
	public DEPNode getLeftNearestSibling()
	{
		return getLeftNearestSibling(0);
	}
	
	public DEPNode getLeftNearestSibling(int order)
	{
		if (d_head != null)
		{
			order = n_siblingID - order - 1;
			if (order >= 0) return d_head.getDependent(order);
		}
		
		return null;
	}

	public DEPNode getRightNearestSibling()
	{
		return getRightNearestSibling(0);
	}
	
	public DEPNode getRightNearestSibling(int order)
	{
		if (d_head != null)
		{
			order = n_siblingID + order + 1;
			if (order < d_head.getDependentSize()) return d_head.getDependent(order);
		}
		
		return null;
	}
	
	/** Calls {@link #getLeftMostDependent(int)}, where {@code order=0}. */
	public DEPNode getLeftMostDependent()
	{
		return getLeftMostDependent(0);
	}
	
	/**
	 * @param order 0 - leftmost, 1 - 2nd left-most, etc.
	 * @return the leftmost dependent of this node if exists; otherwise, {@code null}.
	 * The leftmost dependent must be on the left-hand side of this node.
	 */
	public DEPNode getLeftMostDependent(int order)
	{
		if (DSUtils.isRange(l_dependents, order))
		{
			DEPNode dep = getDependent(order);
			if (dep.n_id < n_id) return dep;
		}

		return null;
	}
	
	/** Calls {@link #getRightMostDependent(int)}, where {@code order=0}. */
	public DEPNode getRightMostDependent()
	{
		return getRightMostDependent(0);
	}
	
	/**
	 * @param order 0 - rightmost, 1 - 2nd rightmost, etc.
	 * @return the rightmost dependent of this node if exists; otherwise, {@code null}.
	 * The rightmost dependent must be on the right-hand side of this node.
	 */
	public DEPNode getRightMostDependent(int order)
	{
		order = getDependentSize() - 1 - order;
		
		if (DSUtils.isRange(l_dependents, order))
		{
			DEPNode dep = getDependent(order);
			if (dep.n_id > n_id) return dep;
		}

		return null;
	}
	
	/** Calls {@link #getLeftNearestDependent(int)}, where {@code order=0}. */
	public DEPNode getLeftNearestDependent()
	{
		return getLeftNearestDependent(0);
	}
	
	/**
	 * @param order 0 - left-nearest, 1 - 2nd left-nearest, etc.
	 * @return the left-nearest dependent of this node if exists; otherwise, {@code null}.
	 * The left-nearest dependent must be on the left-hand side of this node.
	 */
	public DEPNode getLeftNearestDependent(int order)
	{
		int index = l_dependents.getInsertIndex(this) - order - 1;
		return (index >= 0) ? getDependent(index) : null;
	}
	
	/** Calls {@link #getRightNearestDependent(int)}, where {@code order=0}. */
	public DEPNode getRightNearestDependent()
	{
		return getRightNearestDependent(0);
	}
	
	/**
	 * @param order 0 - right-nearest, 1 - 2nd right-nearest, etc.
	 * @return the right-nearest dependent of this node if exists; otherwise, {@code null}.
	 * The right-nearest dependent must be on the right-hand side of this node.
	 */
	public DEPNode getRightNearestDependent(int order)
	{
		int index = l_dependents.getInsertIndex(this) + order;
		return (index < getDependentSize()) ? getDependent(index) : null;
	}
	
	public DEPNode getFirstDependentByLabel(String label)
	{
		for (DEPNode node : l_dependents)
		{
			if (node.isLabel(label))
				return node;
		}
		
		return null;
	}
	
	public DEPNode getFirstDependentByLabel(Pattern pattern)
	{
		for (DEPNode node : l_dependents)
		{
			if (node.isLabel(pattern))
				return node;
		}
		
		return null;
	}
	
	public List<DEPNode> getDependentList()
	{
		return l_dependents;
	}
	
	public List<DEPNode> getDependentListByLabel(String label)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : l_dependents)
		{
			if (node.isLabel(label))
				list.add(node);
		}
		
		return list;
	}
	
	public List<DEPNode> getDependentListByLabel(Set<String> labels)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : l_dependents)
		{
			if (labels.contains(node.getLabel()))
				list.add(node);
		}
		
		return list;
	}
	
	public List<DEPNode> getDependentListByLabel(Pattern pattern)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : l_dependents)
		{
			if (node.isLabel(pattern))
				list.add(node);
		}
		
		return list;
	}
	
	public List<DEPNode> getLeftDependentList()
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : l_dependents)
		{
			if (node.n_id > n_id) break;
			list.add(node);
		}
		
		return list;
	}
	
	public List<DEPNode> getLeftDependentListByLabel(Pattern pattern)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : l_dependents)
		{
			if (node.n_id > n_id) break;
			if (node.isLabel(pattern)) list.add(node);
		}
		
		return list;
	}
	
	public List<DEPNode> getRightDependentList()
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : l_dependents)
		{
			if (node.n_id < n_id) continue;
			list.add(node);
		}
		
		return list;
	}
	
	public List<DEPNode> getRightDependentListByLabel(Pattern pattern)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : l_dependents)
		{
			if (node.n_id < n_id) continue;
			if (node.isLabel(pattern)) list.add(node);
		}
		
		return list;
	}
	
	/** @return an unsorted list of grand-dependants. */
	public List<DEPNode> getGrandDependentList()
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : l_dependents)
			list.addAll(node.getDependentList());
	
		return list;
	}
	
	/**
	 * @return an unsorted list of descendants.
	 * If {@code height == 1}, return {@link #getDependentList()}
	 * If {@code height > 1} , return all descendants within the depth.
	 * If {@code height < 1} , return an empty list.
	 */
	public List<DEPNode> getDescendantList(int height)
	{
		List<DEPNode> list = new ArrayList<>();
	
		if (height > 0)
			getDescendantListAux(this, list, height-1);
		
		return list;
	}
	
	private void getDescendantListAux(DEPNode node, List<DEPNode> list, int height)
	{
		list.addAll(node.getDependentList());
		
		if (height > 0)
		{
			for (DEPNode dep : node.getDependentList())
				getDescendantListAux(dep, list, height-1);
		}
	}
	
	public DEPNode getAnyDescendantByPOSTag(String tag)
	{
		return getAnyDescendantByPOSTagAux(this, tag);
	}
	
	private DEPNode getAnyDescendantByPOSTagAux(DEPNode node, String tag)
	{
		for (DEPNode dep : node.getDependentList())
		{
			if (dep.isPOSTag(tag)) return dep;
			
			dep = getAnyDescendantByPOSTagAux(dep, tag);
			if (dep != null) return dep;
		}
		
		return null;
	}

	/** @return a sorted list of nodes in the subtree of this node (inclusive). */
	public List<DEPNode> getSubNodeList()
	{
		List<DEPNode> list = new ArrayList<>();
		getSubNodeCollectionAux(list, this);
		Collections.sort(list);
		return list;
	}
	
	/** @return a set of nodes in the subtree of this node (inclusive). */
	public Set<DEPNode> getSubNodeSet()
	{
		Set<DEPNode> set = new HashSet<>();
		getSubNodeCollectionAux(set, this);
		return set;
	}
	
	private void getSubNodeCollectionAux(Collection<DEPNode> col, DEPNode node)
	{
		col.add(node);
		
		for (DEPNode dep : node.getDependentList())
			getSubNodeCollectionAux(col, dep);
	}
	
	public IntHashSet getSubNodeIDSet()
	{
		IntHashSet set = new IntHashSet();
		getSubNodeIDSetAux(set, this);
		return set;
	}

	private void getSubNodeIDSetAux(IntHashSet set, DEPNode node)
	{
		set.add(node.n_id);
		
		for (DEPNode dep : node.getDependentList())
			getSubNodeIDSetAux(set, dep);
	}
	
	/** @return a sorted array of IDs from the subtree of this node, including the ID of this node. */
	public int[] getSubNodeIDSortedArray()
	{
		IntHashSet set = getSubNodeIDSet();
		int[] list = set.toArray();
		Arrays.sort(list);
		return list;
	}
	
	/**
	 * @return the dependency of this node with the specific index if exists; otherwise, {@code null}.
	 * @throws IndexOutOfBoundsException
	 */
	public DEPNode getDependent(int index)
	{
		return l_dependents.get(index);
	}
	
	/**
	 * @return the index of the dependent node among other siblings (starting with 0).
	 * If the specific node is not a dependent of this node, returns a negative number.
	 */
	public int getDependentIndex(DEPNode node)
	{
		return l_dependents.indexOf(node);
	}
	
	/** @return the number of dependents of this node. */
	public int getDependentSize()
	{
		return l_dependents.size();
	}
	
	/** @return "0" - no dependents, "<" - left dependents, ">" - right dependents, "<>" - left and right dependents. */
	public String getValency(DirectionType direction)
	{
		switch (direction)
		{
		case  l: return getLeftValency();
		case  r: return getRightValency();
		case  a: return getLeftValency()+"-"+getRightValency();
		default: return null;
		}
	}
	
	public String getLeftValency()
	{
		StringBuilder build = new StringBuilder();
		
		if (getLeftMostDependent() != null)
		{
			build.append(StringConst.LESS_THAN);
			
			if (getLeftMostDependent(1) != null)
				build.append(StringConst.LESS_THAN);
		}
		
		return build.toString();
	}
	
	public String getRightValency()
	{
		StringBuilder build = new StringBuilder();
		
		if (getRightMostDependent() != null)
		{
			build.append(StringConst.GREATER_THAN);
			
			if (getRightMostDependent(1) != null)
				build.append(StringConst.GREATER_THAN);
		}
		
		return build.toString();
	}
	
	public String getSubcategorization(DirectionType direction, FieldType field)
	{
		switch (direction)
		{
		case l: return getLeftSubcategorization (field);
		case r: return getRightSubcategorization(field);
		case a:
			String left = getLeftSubcategorization(field);
			if (left == null) return getRightSubcategorization(field);
			String right = getRightSubcategorization(field);
			return  (right == null) ? left : left+right;
		default: return null; 
		}
	}
	
	/** @return null if not exist. */
	public String getLeftSubcategorization(FieldType field)
	{
		StringBuilder build = new StringBuilder();
		int i, size = getDependentSize();
		DEPNode node;
		
		for (i=0; i<size; i++)
		{
			node = getDependent(i);
			if (node.getID() > n_id) break;
			build.append(StringConst.LESS_THAN);
			build.append(getTagFeature(field));
		}
		
		return build.length() > 0 ? build.toString() : null;
	}
	
	/** @return null if not exist. */
	public String getRightSubcategorization(FieldType field)
	{
		StringBuilder build = new StringBuilder();
		int i, size = getDependentSize();
		DEPNode node;
		
		for (i=size-1; i>=0; i++)
		{
			node = getDependent(i);
			if (node.getID() < n_id) break;
			build.append(StringConst.GREATER_THAN);
			build.append(getTagFeature(field));
		}
		
		return build.length() > 0 ? build.toString() : null;
	}
	
	public String getPath(DEPNode node, FieldType field)
	{
		DEPNode lca = getLowestCommonAncestor(node);
		return (lca != null) ? getPath(node, lca, field) : null;
	}
	
	public String getPath(DEPNode node, DEPNode lca, FieldType field)
	{
		if (node == lca)
			return getPathAux(lca, this, field, "^", true);
		
		if (this == lca)
			return getPathAux(lca, node, field, "|", true);
		
		return getPathAux(lca, this, field, "^", true) + getPathAux(lca, node, field, "|", false);
	}
	
	private String getPathAux(DEPNode top, DEPNode bottom, FieldType field, String delim, boolean includeTop)
	{
		StringBuilder build = new StringBuilder();
		DEPNode node = bottom;
		int dist = 0;
		String s;
		
		do
		{
			s = node.getTagFeature(field);
			
			if (s != null)
			{
				build.append(delim);
				build.append(s);
			}
			else
			{
				dist++;
			}
		
			node = node.getHead();
		}
		while (node != top && node != null);
		
		if (field == FieldType.t)
		{
			build.append(delim);
			build.append(dist);
		}
		else if (field == FieldType.p && includeTop)
		{
			build.append(delim);
			build.append(top.getPOSTag());
		}
		
		return build.length() == 0 ? null : build.toString();
	}
	
	public Set<DEPNode> getAncestorSet()
	{
		Set<DEPNode> set = new HashSet<>();
		DEPNode node = getHead();
		
		while (node != null)
		{
			set.add(node);
			node = node.getHead();
		}
		
		return set;
	}
	
	public DEPNode getLowestCommonAncestor(DEPNode node)
	{
		Set<DEPNode> set = getAncestorSet();
		set.add(this);
		
		while (node != null)
		{
			if (set.contains(node)) return node;
			node = node.getHead();
		}
		
		return null;
	}
	
	public String getTagFeature(FieldType field)
	{
		switch (field)
		{
		case m : return getLemma();
		case p : return getPOSTag();
		case n : return getNamedEntityTag();
		case d : return getLabel();
		default: return null;
		}
	}
	
//	====================================== Setters ======================================

	/** Sets the dependency label of this node with the specific label. */
	public void setLabel(String label)
	{
		s_label = label;
	}
	
	/** Sets the dependency head of this node with the specific node. */
	public void setHead(DEPNode node)
	{
		if (hasHead())
			d_head.l_dependents.remove(this);
		
		if (node != null)
			n_siblingID = node.l_dependents.addItem(this);
		
		d_head = node;
	}
	
	/** Sets the dependency head of this node with the specific node and the label. */
	public void setHead(DEPNode node, String label)
	{
		setHead (node);
		setLabel(label);
	}
	
	public void addDependent(DEPNode node)
	{
		node.setHead(this);
	}
	
	public void addDependent(DEPNode node, String label)
	{
		node.setHead(this, label);
	}
	
//	====================================== Booleans ======================================
	
	/** @return {@code true} if this node has the dependency head; otherwise, {@code null}. */
	public boolean hasHead()
	{
		return d_head != null;
	}
	
	public boolean containsDependent(DEPNode node)
	{
		return l_dependents.contains(node);
	}
	
	public boolean containsDependent(String label)
	{
		return getFirstDependentByLabel(label) != null;
	}
	
	public boolean containsDependent(Pattern pattern)
	{
		return getFirstDependentByLabel(pattern) != null;
	}
	
	public boolean isWordForm(String form)
	{
		return form.equals(s_wordForm);
	}
	
	public boolean isSimplifiedForm(String form)
	{
		return form.equals(s_simplifiedWordForm);
	}
	
	public boolean isLemma(String lemma)
	{
		return lemma.equals(s_lemma);
	}
	
	/** @return {@code true} if the part-of-speech tag of this node equals to the specific tag. */
	public boolean isPOSTag(String tag)
	{
		return tag.equals(s_posTag);
	}
	
	/** @return {@code true} if the part-of-speech tag of this node matches the specific pattern. */
	public boolean isPOSTag(Pattern pattern)
	{
		return pattern.matcher(s_posTag).find();
	}
	
	/** @return {@code true} if the named entity tag of this node equals to the specific tag. */
	public boolean isNamedEntityTag(String tag)
	{
		return tag.equals(s_namedEntityTag);
	}
	
	/** @return {@code true} if the dependency label of this node equals to the specific label. */
	public boolean isLabel(String label)
	{
		return label.equals(s_label);
	}
	
	public boolean isLabelAny(String... labels)
	{
		for (String label : labels)
		{
			if (label.equals(s_label))
				return true;
		}
		
		return false;
	}
	
	/** @return {@code true} if the dependency label of this node matches the specific pattern. */
	public boolean isLabel(Pattern pattern)
	{
		return pattern.matcher(s_label).find();
	}
	
	/** @return {@code true} if this node is a dependent of the specific node. */
	public boolean isDependentOf(DEPNode node)
	{
		return d_head == node;
	}
	
	public boolean isDependentOf(DEPNode node, String label)
	{
		return isDependentOf(node) && isLabel(label);
	}
	
	/** @return {@code true} if this node is a descendant of the specific node. */
	public boolean isDescendantOf(DEPNode node)
	{
		DEPNode head = getHead();
		
		while (head != null)
		{
			if (head == node)	return true;
			head = head.getHead();
		}
		
		return false;
	}
	
	public boolean isSiblingOf(DEPNode node)
	{
		return hasHead() && node.isDependentOf(d_head);
	}
	
//	====================================== Secondary ======================================
	
	public void addSecondaryHead(DEPArc arc)
	{
		x_heads.add(arc);
	}
	
	public void addSecondaryHead(DEPNode head, String label)
	{
		addSecondaryHead(new DEPArc(head, label));
	}
	
	public List<DEPArc> getSecondaryHeadArcList()
	{
		return x_heads;
	}
	
	public List<DEPArc> getSecondaryHeadArcList(String label)
	{
		List<DEPArc> list = new ArrayList<>();
		
		for (DEPArc arc : x_heads)
		{
			if (arc.isLabel(label))
				list.add(arc);
		}
		
		return list;
	}
	
	public void setSecondaryHeads(List<DEPArc> arcs)
	{
		x_heads = arcs;
	}
	
//	====================================== Semantics ======================================
	
	/** @return the PropBank roleset ID of this node if exists; otherwise, {@code null}. */
	public String getRolesetID()
	{
		return d_feats.get(DEPLib.FEAT_PB);
	}
	
	public String setRolesetID(String rolesetID)
	{
		return d_feats.put(DEPLib.FEAT_PB, rolesetID);
	}
	
	public void clearRolesetID()
	{
		d_feats.remove(DEPLib.FEAT_PB);
	}
	
	public boolean isSemanticHead()
	{
		return d_feats.containsKey(DEPLib.FEAT_PB);
	}
	
	public Set<DEPNode> getSemanticHeadSet(String label)
	{
		Set<DEPNode> set = new HashSet<>();
		
		for (SRLArc arc : s_heads)
		{
			if (arc.isLabel(label))
				set.add(arc.getNode());
		}
		
		return set;
	}
	
	public Set<DEPNode> getSemanticHeadSet(Pattern pattern)
	{
		Set<DEPNode> set = new HashSet<>();
		
		for (SRLArc arc : s_heads)
		{
			if (arc.isLabel(pattern))
				set.add(arc.getNode());
		}
		
		return set;
	}
	
	public List<SRLArc> getSemanticHeadArcList()
	{
		return s_heads;
	}
	
	public List<SRLArc> getSemanticHeadArcList(String label)
	{
		List<SRLArc> list = new ArrayList<>();
		
		for (SRLArc arc : s_heads)
		{
			if (arc.isLabel(label))
				list.add(arc);
		}
		
		return list;
	}
	
	public SRLArc getSemanticHeadArc(DEPNode node)
	{
		for (SRLArc arc : s_heads)
		{
			if (arc.isNode(node))
				return arc;
		}
		
		return null;
	}
	
	public SRLArc getSemanticHeadArc(DEPNode node, String label)
	{
		for (SRLArc arc : s_heads)
		{
			if (arc.equals(node, label))
				return arc;
		}
		
		return null;
	}
	
	public SRLArc getSemanticHeadArc(DEPNode node, Pattern pattern)
	{
		for (SRLArc arc : s_heads)
		{
			if (arc.equals(node, pattern))
				return arc;
		}
		
		return null;
	}
	
	public String getSemanticLabel(DEPNode node)
	{
		for (SRLArc arc : s_heads)
		{
			if (arc.isNode(node))
				return arc.getLabel();
		}
		
		return null;
	}
	
	public DEPNode getFirstSemanticHead(String label)
	{
		for (SRLArc arc : s_heads)
		{
			if (arc.isLabel(label))
				return arc.getNode();
		}
		
		return null;
	}
	
	public DEPNode getFirstSemanticHead(Pattern pattern)
	{
		for (SRLArc arc : s_heads)
		{
			if (arc.isLabel(pattern))
				return arc.getNode();
		}
		
		return null;
	}
	
	public void addSemanticHeads(Collection<SRLArc> arcs)
	{
		s_heads.addAll(arcs);
	}
	
	public void addSemanticHead(DEPNode head, String label)
	{
		addSemanticHead(new SRLArc(head, label));
	}
	
	public void addSemanticHead(SRLArc arc)
	{
		s_heads.add(arc);
	}
	
	public void setSemanticHeads(List<SRLArc> arcs)
	{
		s_heads = arcs;
	}
	
	public boolean removeSemanticHead(DEPNode node)
	{
		for (SRLArc arc : s_heads)
		{
			if (arc.isNode(node))
				return s_heads.remove(arc);
		}
		
		return false;
	}
	
	public void removeSemanticHead(SRLArc arc)
	{
		s_heads.remove(arc);
	}
	
	public void removeSemanticHeads(Collection<SRLArc> arcs)
	{
		s_heads.removeAll(arcs);
	}
	
	public void removeSemanticHeads(String label)
	{
		s_heads.removeAll(getSemanticHeadArcList(label));
	}
	
	public void clearSemanticHeads()
	{
		s_heads.clear();
	}
	
	public boolean isArgumentOf(DEPNode node)
	{
		return getSemanticHeadArc(node) != null;
	}
	
	public boolean isArgumentOf(String label)
	{
		return getFirstSemanticHead(label) != null;
	}
	
	public boolean isArgumentOf(Pattern pattern)
	{
		return getFirstSemanticHead(pattern) != null;
	}
	
	public boolean isArgumentOf(DEPNode node, String label)
	{
		return getSemanticHeadArc(node, label) != null;
	}
	
	public boolean isArgumentOf(DEPNode node, Pattern pattern)
	{
		return getSemanticHeadArc(node, pattern) != null;
	}
	
	public Set<DEPNode> getArgumentCandidateSet(int depth, boolean includeSelf)
	{
		Set<DEPNode> set = new HashSet<>(getDescendantList(depth));
		DEPNode head = getHead();
		
		while (head != null)
		{
			set.add(head);
			set.addAll(head.getDependentList());
			head = head.getHead();
		}
		
		if (includeSelf)	set.add   (this);
		else				set.remove(this);
		
		return set;
	}
	
//	====================================== String ======================================
	
	public String toStringPOS()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(s_wordForm);	build.append(TSVReader.DELIM_COLUMN);
		build.append(s_posTag);	build.append(TSVReader.DELIM_COLUMN);
		build.append(d_feats.toString());
		
		return build.toString();
	}
	
	public String toStringMorph()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(s_wordForm);	build.append(TSVReader.DELIM_COLUMN);
		build.append(s_lemma);	build.append(TSVReader.DELIM_COLUMN);
		build.append(s_posTag);	build.append(TSVReader.DELIM_COLUMN);
		build.append(d_feats.toString());
		
		return build.toString();
	}
	
	public String toStringDEP()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(n_id);					build.append(TSVReader.DELIM_COLUMN);
		build.append(s_wordForm);				build.append(TSVReader.DELIM_COLUMN);
		build.append(s_lemma);				build.append(TSVReader.DELIM_COLUMN);
		build.append(s_posTag);				build.append(TSVReader.DELIM_COLUMN);
		build.append(d_feats.toString());	build.append(TSVReader.DELIM_COLUMN);
		build.append(toStringHead());
		
		return build.toString();
	}
	
	public String toStringDAG()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(n_id);					build.append(TSVReader.DELIM_COLUMN);
		build.append(s_wordForm);				build.append(TSVReader.DELIM_COLUMN);
		build.append(s_lemma);				build.append(TSVReader.DELIM_COLUMN);
		build.append(s_posTag);				build.append(TSVReader.DELIM_COLUMN);
		build.append(d_feats.toString());	build.append(TSVReader.DELIM_COLUMN);
		build.append(toStringHead());		build.append(TSVReader.DELIM_COLUMN);
		build.append(toString(x_heads));
		
		return build.toString();
	}
	
	public String toStringSRL()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(n_id);					build.append(TSVReader.DELIM_COLUMN);
		build.append(s_wordForm);				build.append(TSVReader.DELIM_COLUMN);
		build.append(s_lemma);				build.append(TSVReader.DELIM_COLUMN);
		build.append(s_posTag);				build.append(TSVReader.DELIM_COLUMN);
		build.append(d_feats.toString());	build.append(TSVReader.DELIM_COLUMN);
		build.append(toStringHead());		build.append(TSVReader.DELIM_COLUMN);
		build.append(toString(s_heads));
		
		return build.toString();
	}
	
	public String toStringCoNLLX()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(n_id);					build.append(TSVReader.DELIM_COLUMN);
		build.append(s_wordForm);				build.append(TSVReader.DELIM_COLUMN);
		build.append(s_lemma);				build.append(TSVReader.DELIM_COLUMN);
		build.append(s_posTag);				build.append(TSVReader.DELIM_COLUMN);
		build.append(s_posTag);				build.append(TSVReader.DELIM_COLUMN);
		build.append(d_feats.toString());	build.append(TSVReader.DELIM_COLUMN);
		build.append(toStringHead());		build.append(TSVReader.DELIM_COLUMN);
		
		return build.toString();
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(n_id);					build.append(TSVReader.DELIM_COLUMN);
		build.append(s_wordForm);				build.append(TSVReader.DELIM_COLUMN);
		build.append(s_lemma);				build.append(TSVReader.DELIM_COLUMN);
		build.append(s_posTag);				build.append(TSVReader.DELIM_COLUMN);
		build.append(s_namedEntityTag);		build.append(TSVReader.DELIM_COLUMN);
		build.append(d_feats.toString());	build.append(TSVReader.DELIM_COLUMN);
		build.append(toStringHead());		build.append(TSVReader.DELIM_COLUMN);
		build.append(toString(x_heads));	build.append(TSVReader.DELIM_COLUMN);
		build.append(toString(s_heads));
		
		return build.toString();
	}
	
	private String toStringHead()
	{
		StringBuilder build = new StringBuilder();
		
		if (hasHead())
		{
			build.append(d_head.n_id);
			build.append(TSVReader.DELIM_COLUMN);
			build.append(s_label);
		}
		else
		{
			build.append(TSVReader.BLANK);
			build.append(TSVReader.DELIM_COLUMN);
			build.append(TSVReader.BLANK);
		}
		
		return build.toString();
	}
	
	private <T extends AbstractArc<DEPNode>>String toString(List<T> arcs)
	{
		if (arcs == null || arcs.isEmpty())
			return TSVReader.BLANK;
		
		StringBuilder build = new StringBuilder();
		Collections.sort(arcs);
		
		for (T arc : arcs)
		{
			build.append(TSVReader.DELIM_ARCS);
			build.append(arc.toString());
		}
		
		return build.substring(TSVReader.DELIM_ARCS.length());
	}
		
	@Override
	public int compareTo(DEPNode node)
	{
		return n_id - node.n_id;
	}
}