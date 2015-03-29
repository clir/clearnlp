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
	/** The label of the node in a sequence */
	private String s_sequenceLabel = null;
	/** The list of secondary heads of this node (default: empty). */
	private List<DEPArc> x_heads;
	/** The list of semantic heads of this node (default: empty). */
	private List<SRLArc> s_heads;
	
//	====================================== Constructors ======================================
	
	/**
	 * Construct an empty DEPNode.
	 */
	public DEPNode() {}
	
	/**
	 * Construct DEPNode with id and word-from.
	 * @param id id of the node
	 * @param form word-from of the node
	 */
	public DEPNode(int id, String form)
	{
		init(id, form, null, null, null, new DEPFeat());
	}
	
	/**
	 * Construct DEPNode with id, word-from, POS tag, and extra features.
	 * @param id id of the node
	 * @param form word-from of the node
	 * @param posTag POS tag of the node
	 * @param feats extra features of the node
	 */
	public DEPNode(int id, String form, String posTag, DEPFeat feats)
	{
		init(id, form, null, posTag, null, feats);
	}
	
	/**
	 * Construct DEPNode with id, word-from, word-form lemma, POS tag, and extra features.
	 * @param id id of the node
	 * @param form word-from of the node
	 * @param lemma word-form lemma of the node
	 * @param posTag POS tag of the node
	 * @param feats extra features of the node
	 */
	public DEPNode(int id, String form, String lemma, String posTag, DEPFeat feats)
	{
		init(id, form, lemma, posTag, null, feats);
	}
	
	/**
	 * Construct DEPNode with id, word-from, word-form lemma, POS tag, name-entity tag, and extra features.
	 * @param id id of the node
	 * @param form word-from of the node
	 * @param lemma word-form lemma of the node
	 * @param posTag POS tag of the node
	 * @param namedEntityTag name-entity tag of the node
	 * @param feats extra features of the node
	 */
	public DEPNode(int id, String form, String lemma, String posTag, String namedEntityTag, DEPFeat feats)
	{
		init(id, form, lemma, posTag, namedEntityTag, feats);
	}
	
	/**
	 * Copy constuctor that copies the basic fields from the specific node to this node.
	 * @param node another DEPNode you wish to copy
	 */
	public DEPNode(DEPNode node)
	{
		init(node.n_id, node.s_wordForm, node.s_lemma, node.s_posTag, node.s_namedEntityTag, new DEPFeat(node.d_feats));
	}
	
//	====================================== Initialization ======================================
	
	/**
	 * Initializes all fields of a DEPNode.
	 * @param id id of the node
	 * @param form word-from of the node
	 * @param lemma word-form lemma of the node
	 * @param posTag POS tag of the node
	 * @param namedEntityTag name-entity tag of the node
	 * @param feats extra features of the node
	 */
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
	
	/** Initializes the secondary dependency heads of a node. */
	public void initSecondaryHeads()
	{
		x_heads = new ArrayList<>();
	}

	/** Initializes semantic heads of this node. */
	public void initSemanticHeads()
	{
		s_heads = new ArrayList<>();
	}
	
	/** Clear all dependencies(head, label, and sibling relations) of the node. */
	void clearDependencies()
	{
		d_head  = null;
		s_label = null;
		n_siblingID = 0;
		l_dependents.clear();
	}
	
	/** 
	 * Get the sequence label of the node.
	 * @return the sequence label of the node 
	 */
	public String getSequenceLabel()
	{
		return s_sequenceLabel;
	}
	
	/**
	 * Set the sequence label to whatever input string.
	 * @param label sequence label
	 */
	public void setSequenceLabel(String label)
	{
		s_sequenceLabel = label;
	}
	
	/**
	 * Clear the sequence label of the node to {@code null} and return the label.
	 * @return the removed sequence label of the node
	 */
	public String clearSequenceLabel()
	{
		String t = s_sequenceLabel;
		setSequenceLabel(null);
		return t;
	}
	
	/**
	 * Check if the node has the sequence label of your input.
	 * @param label sequence label
	 * @return {@code true} if the node has matching label as the input label
	 */
	public boolean isSequenceLabel(String label)
	{
		return label.equals(s_sequenceLabel);
	}
	
//	====================================== Basic fields ======================================
	
	/**
	 * Get the ID of the node.
	 * @return ID of the node
	 */
	public int getID()
	{
		return n_id;
	}
	
	/** 
	 * Get the word-form of the node.
	 * @return word-form of the node 
	 */
	public String getWordForm()
	{
		return s_wordForm;
	}
	
	/** 
	 * Get the simplified word-from of the node.
	 * @return simplified word-form of the node 
	 */
	public String getSimplifiedWordForm()
	{
		return s_simplifiedWordForm;
	}
	
	/** 
	 * Get the simplified word-form of the node in all lower-case characters. 
	 * @return simplified word-from of the node in all lower-case characters 
	 */
	public String getLowerSimplifiedWordForm()
	{
		return StringUtils.toLowerCase(s_simplifiedWordForm);
	}
	
	/**
	 * Get the word shape of the simplified word-form of the node.
	 * @param maxRepetitions the max count of repetition of a word shape in sequence
	 * @return the word shape of a node's simplified word-form
	 */
	public String getWordShape(int maxRepetitions)
	{
		return StringUtils.getShape(s_simplifiedWordForm, maxRepetitions);
	}
	
	/** 
	 * Get the lemma of the word-form of the node.
	 * @return lemma of the word-form of the node 
	 */
	public String getLemma()
	{
		return s_lemma;
	}
	
	/**
	 * Get the POS tag the node.
	 * @return POS tag the node 
	 */
	public String getPOSTag()
	{
		return s_posTag;
	}
	
	/** 
	 * Get the name-entity tag of the node.
	 * @return name-entity tag of the node 
	 */
	public String getNamedEntityTag()
	{
		return s_namedEntityTag;
	}
	
	/** 
	 * Get the extra features {@code DEDFeat} of the node.
	 * @return extra features of the node 
	 */
	public DEPFeat getFeats()
	{
		return d_feats;
	}
	
	/**
	 * Get a specific feature of the extra features of the node.
	 * @param key feature label of the extra feature
	 * @return the value of the specific feature if exists; otherwise, {@code null}
	 */
	public String getFeat(String key)
	{
		return d_feats.get(key);
	}
	
	/**
	 * Set the ID of the node.
	 * @param id ID of the node
	 */
	public void setID(int id)
	{
		n_id = id;
	}
	
	/**
	 * Set the simplified word-form of the node.
	 * @param form simplified word-form of the node
	 */
	public void setWordForm(String form)
	{
		s_wordForm = form;
		s_simplifiedWordForm = StringUtils.toSimplifiedForm(form);
//		b_punctuation = StringUtils.containsPunctuationOnly(s_simplifiedWordForm);
	}
	
	/**
	 * Set the lemma of the word-form of the node.
	 * @param lemma lemma of the word-form  of the node
	 */
	public void setLemma(String lemma)
	{
		s_lemma = lemma;
	}
	
	/**
	 * Set the POS tag of the node.
	 * @param posTag POS tag of the node
	 */
	public void setPOSTag(String posTag)
	{
		s_posTag = posTag;
	}
	
	/**
	 * Set the name-entity tag of the node.
	 * @param namedEntityTag name-entity tag of the node
	 */
	public void setNamedEntityTag(String namedEntityTag)
	{
		s_namedEntityTag = namedEntityTag;
	}
	
	/**
	 * Set the extra features {@code DEPFeat} of the node.
	 * @param feats extra features of the node
	 */
	public void setFeats(DEPFeat feats)
	{
		d_feats = feats;
	}
	
	/**
	 * Puts an extra feature to this node using the specific key and value.
	 * This method overwrites an existing value of the same key with the current value. 
	 * @param key key of the extra feature
	 * @param value value of the extra feature
	 */
	public void putFeat(String key, String value)
	{
		d_feats.put(key, value);
	}
	
	/**
	 * Clear the POS tag of the node to {@code null} and return the POS tag of the node.
	 * @return the removed POS tag of the node
	 */
	public String clearPOSTag()
	{
		String pos = s_posTag;
		setPOSTag(null);
		return pos;
	}
	
	/**
	 * Removes the extra feature with the specific key.
	 * @param key key of the extra feature
	 * @return value of the removed extra feature
	 */
	public String removeFeat(String key)
	{
		return d_feats.remove(key);
	}
	
//	====================================== Getters ======================================
	
	/**
	 * Get the dependency label of the node.
	 * @return the dependency label of the node
	 */
	public String getLabel()
	{
		return s_label;
	}
	
	/** 
	 * Get the dependency head of this node.
	 * @return the dependency head of this node
	 */
	public DEPNode getHead()
	{
		return d_head;
	}

	/**
	 * Get the  dependency grand-head of the node.
	 * @return the dependency grand-head of the node if exists; otherwise, {@code null}. 
	 */
	public DEPNode getGrandHead()
	{
		DEPNode head = getHead();
		return (head == null) ? null : head.getHead();
	}
	
	/**
	 * Get the left nearest sibling node of the node.
	 * Calls {@link #getLeftNearestSibling(int)}, where {@code order=0}
	 * @return the left nearest sibling node
	 */
	public DEPNode getLeftNearestSibling()
	{
		return getLeftNearestSibling(0);
	}
	
	/**
	 * Get the left sibling node with input displacement (0 - leftmost, 1 - second leftmost, etc.).
	 * @param order left displacement
	 * @return the left sibling node with input displacement
	 */
	public DEPNode getLeftNearestSibling(int order)
	{
		if (d_head != null)
		{
			order = n_siblingID - order - 1;
			if (order >= 0) return d_head.getDependent(order);
		}
		
		return null;
	}

	/**
	 * Get the right nearest sibling node of the node.
	 * Calls {@link #getRightNearestSibling(int)}, where {@code order=0}.
	 * @return the right nearest sibling node
	 */
	public DEPNode getRightNearestSibling()
	{
		return getRightNearestSibling(0);
	}
	
	/**
	 * Get the right sibling node with input displacement (0 - rightmost, 1 - second rightmost, etc.).
	 * @param order right displacement
	 * @return the right sibling node with input displacement
	 */
	public DEPNode getRightNearestSibling(int order)
	{
		if (d_head != null)
		{
			order = n_siblingID + order + 1;
			if (order < d_head.getDependentSize()) return d_head.getDependent(order);
		}
		
		return null;
	}
	
	/**
	 * Get the left most dependency node of the node.
	 * Calls {@link #getLeftMostDependent(int)}, where {@code order=0}
	 * @return the left most dependency node of the node
	 */
	public DEPNode getLeftMostDependent()
	{
		return getLeftMostDependent(0);
	}
	
	/**
	 * Get the left dependency node with input displacement (0 - leftmost, 1 - second leftmost, etc.).
	 * The leftmost dependent must be on the left-hand side of this node.
	 * @param order left displacement
	 * @return the leftmost dependent of this node if exists; otherwise, {@code null}
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
	
	/** 
	 * Get the right most dependency node of the node.
	 * Calls {@link #getRightMostDependent(int)}, where {@code order=0}. 
	 * @return the right most dependency node of the node
	 */
	public DEPNode getRightMostDependent()
	{
		return getRightMostDependent(0);
	}
	
	/**
	 * Get the right dependency node with input displacement (0 - rightmost, 1 - second rightmost, etc.).
	 * The rightmost dependent must be on the right-hand side of this node.
	 * @param order right displacement
	 * @return the rightmost dependent of this node if exists; otherwise, {@code null}
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
	
	/** 
	 * Get the left nearest dependency node.
	 * Calls {@link #getLeftNearestDependent(int)}, where {@code order=0}.
	 * @return the left nearest dependency node
	 */
	public DEPNode getLeftNearestDependent()
	{
		return getLeftNearestDependent(0);
	}
	
	/**
	 * Get the left nearest dependency node with input displacement (0 - left-nearest, 1 - second left-nearest, etc.).
	 * The left nearest dependent must be on the left-hand side of this node.
	 * @param order left displacement
	 * @return the left-nearest dependent of this node if exists; otherwise, {@code null}
	 */
	public DEPNode getLeftNearestDependent(int order)
	{
		int index = l_dependents.getInsertIndex(this) - order - 1;
		return (index >= 0) ? getDependent(index) : null;
	}
	
	/**
	 * Get the right nearest dependency node.
	 * Calls {@link #getRightNearestDependent(int)}, where {@code order=0}. 
	 * @return the right nearest dependency node
	 */
	public DEPNode getRightNearestDependent()
	{
		return getRightNearestDependent(0);
	}
	
	/**
	 * Get the right nearest dependency node with input displacement (0 - right-nearest, 1 - second right-nearest, etc.).
	 * The right-nearest dependent must be on the right-hand side of this node.
	 * @param order right displacement
	 * @return the right-nearest dependent of this node if exists; otherwise, {@code null}
	 */
	public DEPNode getRightNearestDependent(int order)
	{
		int index = l_dependents.getInsertIndex(this) + order;
		return (index < getDependentSize()) ? getDependent(index) : null;
	}
	
	/**
	 * Get the first dependency node of the node by label.
	 * @param label string label of the first-dependency node
	 * @return the first-dependency node of the specific label
	 */
	public DEPNode getFirstDependentByLabel(String label)
	{
		for (DEPNode node : l_dependents)
		{
			if (node.isLabel(label))
				return node;
		}
		
		return null;
	}
	
	/**
	 * Get the first dependency node of the node by label.
	 * @param pattern pattern label of the first-dependency node
	 * @return the first-dependency node of the specific label
	 */
	public DEPNode getFirstDependentByLabel(Pattern pattern)
	{
		for (DEPNode node : l_dependents)
		{
			if (node.isLabel(pattern))
				return node;
		}
		
		return null;
	}
	
	/**
	 * Get the list of all the dependency nodes of the node.
	 * @return list of all the dependency nodes of the node
	 */
	public List<DEPNode> getDependentList()
	{
		return l_dependents;
	}
	
	/**
	 * Get the list of all the dependency nodes of the node by label.
	 * @param label string label
	 * @return list of all the dependency nodes of the node by label
	 */
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
	
	/**
	 * Get the list of all the dependency nodes of the node by labels set.
	 * @param label labels set
	 * @return list of all the dependency nodes of the node by labels set
	 */
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
	
	/**
	 * Get the list of all the dependency nodes of the node by label pattern.
	 * @param label label pattern
	 * @return list of all the dependency nodes of the node by label pattern
	 */
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
	
	/**
	 * Get the list of all the left dependency nodes of the node.
	 * @return list of all the left dependency nodes of the node
	 */
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
	
	/**
	 * Get the list of all the left dependency nodes of the node by label pattern.
	 * @param label label pattern
	 * @return list of all the left dependency nodes of the node by label pattern
	 */
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
	
	/**
	 * Get the list of all the right dependency nodes of the node.
	 * @return list of all the right dependency nodes of the node
	 */
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
	
	/**
	 * Get the list of all the right dependency nodes of the node by label pattern.
	 * @param label label pattern
	 * @return list of all the right dependency nodes of the node by label pattern
	 */
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
	
	/**
	 * Get the list of all grand-dependents of the node. 
	 * @return an unsorted list of grand-dependents of the node
	 */
	public List<DEPNode> getGrandDependentList()
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : l_dependents)
			list.addAll(node.getDependentList());
	
		return list;
	}
	
	/**
	 * Get the list of all descendant nodes of the node with specified height.
	 * If {@code height == 1}, return {@link #getDependentList()}.
	 * If {@code height > 1} , return all descendants within the depth.
	 * If {@code height < 1} , return an empty list.
	 * @param height height level of the descendant nodes
	 * @return an unsorted list of descendants.
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
	
	/**
	 * Get any descendant node with POS tag.
	 * @param tag POS tag
	 * @return s descendant node with the POS tag
	 */
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

	/**
	 * Get the sorted list of all the nodes in the subtree of the node.
	 * @return a sorted list of nodes in the subtree of this node (inclusive)
	  */
	public List<DEPNode> getSubNodeList()
	{
		List<DEPNode> list = new ArrayList<>();
		getSubNodeCollectionAux(list, this);
		Collections.sort(list);
		return list;
	}
	
	/**
	 * Get a set of all the nodes is the subtree of the node.
	 * @return a set of nodes in the subtree of this node (inclusive)
	 */
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
	
	/**
	 * Get the IntHashSet of all the nodes in the subtree (Node ID -> DEPNode).
	 * @return the ntHashSet of all the nodes in the subtree (inclusive)
	 */
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
	
	/** 
	 * Get a sorted array of IDs of all the nodes in the subtree of the node.
	 * @return a sorted array of IDs from the subtree of the node (inclusive) 
	 */
	public int[] getSubNodeIDSortedArray()
	{
		IntHashSet set = getSubNodeIDSet();
		int[] list = set.toArray();
		Arrays.sort(list);
		return list;
	}
	
	/**
	 * Get the dependency node with specific index.
	 * @return the dependency node of the node with the specific index if exists; otherwise, {@code null}.
	 * @throws IndexOutOfBoundsException
	 */
	public DEPNode getDependent(int index)
	{
		return l_dependents.get(index);
	}
	
	/**
	 * Get the index of the dependency node of a specified DEPNode.
	 * If the specific node is not a dependent of this node, returns a negative number.
	 * @return the index of the dependent node among other siblings (starting with 0).
	 */
	public int getDependentIndex(DEPNode node)
	{
		return l_dependents.indexOf(node);
	}
	
	/**
	 * Get the size of the dependents of the node.
	 * @return the number of dependents of the node 
	 */
	public int getDependentSize()
	{
		return l_dependents.size();
	}
	
	
	/**
	 * Get the the valency of the node.
	 * @param direction DirectionType of l, r, a 
	 * @return "0" - no dependents, "<" - left dependents, ">" - right dependents, "<>" - left and right dependents. 
	 */
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
	
	/**
	 * Get the left valency of the node.
	 * @return "<" - left dependents
	 */
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
	
	/**
	 * Get the right valency of the node.
	 * @return ">" - right dependents
	 */
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
	
	/**
	 * Get sub-categorization of the node.
	 * @param direction direction DirectionType of l, r, a
	 * @param field FieldType of tag feature
	 * @return "< {@code TagFeature}" for left sub-categorization, "> {@code TagFeature}" for right-categorization, and {@code null} if not exist
	 */
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
	
	/**
	 * Get left sub-categorization of the node.
	 * @param field FieldType of tag feature 
	 * @return "< {@code TagFeature}" for left sub-categorization, {@code null} if not exist. 
	 */
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
	
	/**
	 * Get right sub-categorization of the node.
	 * @param field FieldType of tag feature 
	 * @return "> {@code TagFeature}" for right sub-categorization, {@code null} if not exist. 
	 */
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
	
	
	/**
	 * Find the path of between this nodes and the input DEPNode.
	 * @param node the node that you want to find the path from this node
	 * @param field FieldType of the the node for search
	 * @return the path between the two nodes
	 */
	public String getPath(DEPNode node, FieldType field)
	{
		DEPNode lca = getLowestCommonAncestor(node);
		return (lca != null) ? getPath(node, lca, field) : null;
	}
	
	/**
	 * Find the path of between this nodes and the input DEPNode with the lowest common ancestor specified.
	 * @param node the node that you want to find the path from this node
	 * @param lca the lowest common ancestor DEPNode that you specified for the path
	 * @param field FieldType of the the node for search
	 * @return the path between the two nodes
	 */
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
	
	/**
	 * Get a set of all the ancestor nodes of the node (ie. Parent node, Grandparent node, etc.).
	 * @return set of all the ancestor nodes
	 */
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
	
	/**
	 * Get the first/lowest common ancestor of the two given nodes (this node and the input DEPNode).
	 * @param node the node that you want to find the lowest common ancestor with the node with
	 * @return the lowest common ancestor of the node and the specified node
	 */
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
	
	
	/**
	 * Get a specific tag feature of the node.
	 * @param field FieldType of the feature
	 * @return the value of the feature in the node
	 */
	public String getTagFeature(FieldType field)
	{
		switch (field)
		{
		case f : return getWordForm();
		case m : return getLemma();
		case p : return getPOSTag();
		case n : return getNamedEntityTag();
		case d : return getLabel();
		default: return null;
		}
	}
	
//	====================================== Setters ======================================

	/** 
	 * Sets the dependency label of this node with the specific label.
	 * @param label label of the node 
	 */
	public void setLabel(String label)
	{
		s_label = label;
	}
	
	/** 
	 * Sets the dependency head of this node with the specific node.
	 * @param node head node of the node 
	 */
	public void setHead(DEPNode node)
	{
		if (hasHead())
			d_head.l_dependents.remove(this);
		
		if (node != null)
			n_siblingID = node.l_dependents.addItem(this);
		
		d_head = node;
	}
	
	/** 
	 * Sets the dependency head of this node with the specific node and the label.
	 * @param node head node of the node
	 * @param label label of the node 
	 */
	public void setHead(DEPNode node, String label)
	{
		setHead (node);
		setLabel(label);
	}
	
	/**
	 * Add the node as a dependent to a specified node.
	 * @param node head node that you wish to add the node as a dependent to
	 */
	public void addDependent(DEPNode node)
	{
		node.setHead(this);
	}
	
	/**
	 * Add the node as a dependent to a specified node and set the label of the node.
	 * @param node head node that you wish to add the node as a dependent to
	 * @param label label of the node
	 */
	public void addDependent(DEPNode node, String label)
	{
		node.setHead(this, label);
	}
	
//	====================================== Booleans ======================================
	
	/**
	 * Check if the node has a head node.
	 * @return {@code true} if this node has the dependency head; otherwise {@code false} if head is {@code null}. 
	 */
	public boolean hasHead()
	{
		return d_head != null;
	}
	
	/**
	 * Check if the node contain another as dependent.
	 * @param node dependent code for check
	 * @return {@code true} if the node has the input DEPNode as a dependent
	 */
	public boolean containsDependent(DEPNode node)
	{
		return l_dependents.contains(node);
	}
	
	/**
	 * Check if the node has the label for its first dependent.
	 * @param label label of the node for check
	 * @return {@code true} if the node's first dependent has the input label
	 */
	public boolean containsDependent(String label)
	{
		return getFirstDependentByLabel(label) != null;
	}
	
	/**
	 * Check if the node has the pattern for its first dependent.
	 * @param pattern pattern of the node for check
	 * @return {@code true} if the node's first dependent has the input pattern
	 */
	public boolean containsDependent(Pattern pattern)
	{
		return getFirstDependentByLabel(pattern) != null;
	}
	
	/**
	 * Check if the node has word-form of as the input string.
	 * @param form word-form for check
	 * @return {@code true} if the node's word-form is equal to the input string
	 */
	public boolean isWordForm(String form)
	{
		return form.equals(s_wordForm);
	}
	
	/**
	 * Check if the node has simplified word-form as the input string.
	 * @param form simplified word-form for check
	 * @return {@code true} if the node's simplified word-form is equal to the input string
	 */
	public boolean isSimplifiedForm(String form)
	{
		return form.equals(s_simplifiedWordForm);
	}
	
	/**
	 * Check if the node has word-form lemma as the input string.
	 * @param lemma word-form lemma for check
	 * @return {@code true} if the node's word-form lemma is equal to the input string
	 */
	public boolean isLemma(String lemma)
	{
		return lemma.equals(s_lemma);
	}
	
	/**
	 * Check if the node has POS tag as the input string.
	 * @param tag POS tag string for check 
	 * @return {@code true} if the part-of-speech tag of this node equals to the specific tag 
	 */
	public boolean isPOSTag(String tag)
	{
		return tag.equals(s_posTag);
	}
	
	/** 
	 * Check if the node has POS tag as the input pattern.
	 * @param pattern POS tag pattern for check
	 * @return {@code true} if the part-of-speech tag of this node matches the specific pattern 
	 */
	public boolean isPOSTag(Pattern pattern)
	{
		return pattern.matcher(s_posTag).find();
	}
	
	/**
	 * Check if the node has the name-entity tag as the input string.
	 * @param tag name-entity tag string for check
	 * @return {@code true} if the named entity tag of this node equals to the specific tag 
	 */
	public boolean isNamedEntityTag(String tag)
	{
		return tag.equals(s_namedEntityTag);
	}
	
	/**
	 * Check if the node has the label as the input string.
	 * @param label label string for check
	 * @return {@code true} if the dependency label of this node equals to the specific label 
	 */
	public boolean isLabel(String label)
	{
		return label.equals(s_label);
	}
	
	/**
	 * Check if the node has the label as any label in the input strings array.
	 * @param labels label string array for check
	 * @return {@code true} if the dependency label of this node equals to any of the specific labels
	 */
	public boolean isLabelAny(String... labels)
	{
		for (String label : labels)
		{
			if (label.equals(s_label))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Check if the node has the label as the input label pattern.
	 * @param pattern label pattern for check
	 * @return {@code true} if the dependency label of this node matches the specific pattern
	 */
	public boolean isLabel(Pattern pattern)
	{
		return pattern.matcher(s_label).find();
	}
	
	/** 
	 * Check if the node has the input dependent node. 
	 * @param node dependent node for check
	 * @return {@code true} if this node is a dependent of the specific node 
	 */
	public boolean isDependentOf(DEPNode node)
	{
		return d_head == node;
	}
	
	/**
	 * Check if the node has the input dependent node and the input label string. 
	 * @param node dependent node for check
	 * @param label label string for check
	 * @return @return {@code true} if the node has the specific dependent node and the specific label string
	 */
	public boolean isDependentOf(DEPNode node, String label)
	{
		return isDependentOf(node) && isLabel(label);
	}
	
	/**
	 * Check if the node is the descendant of the input head node. 
	 * @param label label string for check
	 * @return {@code true} if the node is the dependent of the specific node
	 */
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
	
	/**
	 * Check if the node has the sibling node.
	 * @param node sibling node of the node for check
	 * @return {@code true} if the node has the sibling node
	 */
	public boolean isSiblingOf(DEPNode node)
	{
		return hasHead() && node.isDependentOf(d_head);
	}
	
//	====================================== Secondary ======================================
	
	/**
	 * Add a secondary head {@code DEPArc} to the node.
	 * @param arc secondary head arc for add 
	 */
	public void addSecondaryHead(DEPArc arc)
	{
		x_heads.add(arc);
	}
	
	/**
	 * Add a secondary head {@code DEPArc} with specified label to the node.
	 * @param arc secondary head arc for add 
	 * @param label label of the secondary head
	 */
	public void addSecondaryHead(DEPNode head, String label)
	{
		addSecondaryHead(new DEPArc(head, label));
	}
	
	/**
	 * Get a list of all secondary head arc {@code DEPArc} of the node.
	 * @return list of secondary head arc {@code DEPArc}
	 */
	public List<DEPArc> getSecondaryHeadArcList()
	{
		return x_heads;
	}
	
	/**
	 * Get a list of all secondary head arc {@code DEPArc} with the specific label of the node.
	 * @param label label string of the secondary head arc
	 * @return list of secondary head arc {@code DEPArc} with specific label
	 */
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
	
	/**
	 * Set list of secondary head arc {@code DEPArc} as the node's secondary heads 
	 * @param arcs list of secondary head arc {@code DEPArc}
	 */
	public void setSecondaryHeads(List<DEPArc> arcs)
	{
		x_heads = arcs;
	}
	
//	====================================== Semantics ======================================
	
	/**
	 * Get the PropBank roleset ID of the node(ie. verb.01).
	 * @return the PropBank roleset ID of the node if exists; otherwise, {@code null}. 
	 */
	public String getRolesetID()
	{
		return d_feats.get(DEPLib.FEAT_PB);
	}
	
	/**
	 * Set PropBank roleset ID of the node.
	 * @param rolesetID PropBank roleset ID
	 * @return the PropBank roleset ID of the node
	 */
	public String setRolesetID(String rolesetID)
	{
		return d_feats.put(DEPLib.FEAT_PB, rolesetID);
	}
	
	/**
	 * Remove the PropBank roleset ID of the node.
	 */
	public void clearRolesetID()
	{
		d_feats.remove(DEPLib.FEAT_PB);
	}
	
	
	/**
	 * Check if the node has a semantic head in its extra features.
	 * @return {@code true} if the node has a semantic head in its extra features
	 */
	public boolean isSemanticHead()
	{
		return d_feats.containsKey(DEPLib.FEAT_PB);
	}
	
	/**
	 * Get a set of semantic head of the node that contains the given label.
	 * @param label label string to search for in all semantic head of the node
	 * @return a set of semantic head of the node that contains the given label
	 */
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
	
	/**
	 * Get a set of semantic head of the node that contains the given pattern.
	 * @param pattern label pattern to search for in all semantic head of the node
	 * @return a set of semantic head of the node that contains the given pattern
	 */
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
	
	/**
	 * Get a list of all semantic head arc of the node.
	 * @return a list of all semantic head arc of the node
	 */
	public List<SRLArc> getSemanticHeadArcList()
	{
		return s_heads;
	}
	
	/**
	 * Get a list of all semantic head arc of the node with the given label.
	 * @param label String label for the semantic head arc search
	 * @return Get a list of all semantic head arc of the node with the given label
	 */
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
	
	/**
	 * Get the semantic arc relationship between the node and another given node.
	 * @param node DEPNode to find the semantic arc relationship with the node
	 * @return semantic arc relationship between the node and another given node
	 */
	public SRLArc getSemanticHeadArc(DEPNode node)
	{
		for (SRLArc arc : s_heads)
		{
			if (arc.isNode(node))
				return arc;
		}
		
		return null;
	}
	
	/**
	 * Get the semantic arc relationship between the node and another given node with a given label.
	 * @param node DEPNode to find the semantic arc relationship with the node
	 * @param label String label of the semantic head arc
	 * @return the semantic arc relationship between the node and another given node with a given label
	 */
	public SRLArc getSemanticHeadArc(DEPNode node, String label)
	{
		for (SRLArc arc : s_heads)
		{
			if (arc.equals(node, label))
				return arc;
		}
		
		return null;
	}
	
	/**
	 * Get the semantic arc relationship between the node and another given node with a given pattern.
	 * @param node DEPNode to find the semantic arc relationship with the node
	 * @param pattern label pattern of the semantic head arc
	 * @return the semantic arc relationship between the node and another given node with a given pattern
	 */
	public SRLArc getSemanticHeadArc(DEPNode node, Pattern pattern)
	{
		for (SRLArc arc : s_heads)
		{
			if (arc.equals(node, pattern))
				return arc;
		}
		
		return null;
	}
	
	/**
	 * Get the semantic label of the node that is related to the given node.
	 * @param node DEPNode that you want to check the semantic label relationship with
	 * @return the semantic label of the given in relation to the node
	 */
	public String getSemanticLabel(DEPNode node)
	{
		for (SRLArc arc : s_heads)
		{
			if (arc.isNode(node))
				return arc.getLabel();
		}
		
		return null;
	}
	
	/**
	 * Get the first node that is found to have the semantic head of the given label from the node.
	 * @param label String label of the semantic head
	 * @return the first node that is found to have the semantic head of the given label from the node
	 */
	public DEPNode getFirstSemanticHead(String label)
	{
		for (SRLArc arc : s_heads)
		{
			if (arc.isLabel(label))
				return arc.getNode();
		}
		
		return null;
	}
	
	/**
	 * Get the first node that is found to have the semantic head of the given pattern from the node.
	 * @param label label pattern of the semantic head
	 * @return the first node that is found to have the semantic head of the given pattern from the node
	 */
	public DEPNode getFirstSemanticHead(Pattern pattern)
	{
		for (SRLArc arc : s_heads)
		{
			if (arc.isLabel(pattern))
				return arc.getNode();
		}
		
		return null;
	}
	
	/**
	 * Add a collection of semantic heads to the node.
	 * @param arcs {@code Collection<SRLArc>} of the semantic heads
	 */
	public void addSemanticHeads(Collection<SRLArc> arcs)
	{
		s_heads.addAll(arcs);
	}
	
	/**
	 * Add a node a give the given semantic label to the node.
	 * @param head DEPNode the semantic head node for adding
	 * @param label String label of the semantic label
	 */
	public void addSemanticHead(DEPNode head, String label)
	{
		addSemanticHead(new SRLArc(head, label));
	}
	
	/**
	 * Add a semantic arc to the node.
	 * @param arc semantic arc for adding
	 */
	public void addSemanticHead(SRLArc arc)
	{
		s_heads.add(arc);
	}
	
	/**
	 * Set semantic heads of the node.
	 * @param arcs a list of semantic heads
	 */
	public void setSemanticHeads(List<SRLArc> arcs)
	{
		s_heads = arcs;
	}
	
	/**
	 * Remove all semantic heads of the node in relation to a given node.
	 * @param node DEPNode for semantic head removal in relation to the node 
	 * @return {@code true}, else {@code false} if nothing gets removed 
	 */
	public boolean removeSemanticHead(DEPNode node)
	{
		for (SRLArc arc : s_heads)
		{
			if (arc.isNode(node))
				return s_heads.remove(arc);
		}
		
		return false;
	}
	
	/**
	 * Remove a specific semantic head of the node.
	 * @param arc a semantic head for removal
	 */
	public void removeSemanticHead(SRLArc arc)
	{
		s_heads.remove(arc);
	}
	
	/**
	 * Remove a collection of specific semantic heads of the node.
	 * @param arcs a collection of semantic heads for removal
	 */
	public void removeSemanticHeads(Collection<SRLArc> arcs)
	{
		s_heads.removeAll(arcs);
	}
	
	/**
	 * Remove all semantic heads of the node that have the given label.
	 * @param label String label of the semantic head for removal
	 */
	public void removeSemanticHeads(String label)
	{
		s_heads.removeAll(getSemanticHeadArcList(label));
	}
	
	/**
	 * Remove all semantic heads of the node.
	 */
	public void clearSemanticHeads()
	{
		s_heads.clear();
	}
	
	/**
	 * Check if the node has a semantic role arc with a given node. 
	 * @param node DEPNode to check the SRL relation with
	 * @return {@code true}, else {@code false} if there is no SRLArc between the two nodes
	 */
	public boolean isArgumentOf(DEPNode node)
	{
		return getSemanticHeadArc(node) != null;
	}
	
	/**
	 * Check if the node has a semantic role arc of the label of a given label.
	 * @param label String label of a certain semantic role label
	 * @return {@code true}, else {@code false} if there is no SRLArc with the given label
	 */
	public boolean isArgumentOf(String label)
	{
		return getFirstSemanticHead(label) != null;
	}
	
	/**
	 * Check if the node has a semantic role arc of the label of a given pattern.
	 * @param pattern label pattern of a certain semantic role label
	 * @return {@code true}, else {@code false} if there is no SRLArc with the given pattern
	 */
	public boolean isArgumentOf(Pattern pattern)
	{
		return getFirstSemanticHead(pattern) != null;
	}
	
	/**
	 * Check if the node has a semantic role arc with a given node of a given label.
	 * @param node DEPNode to check the SRL relation with
	 * @param label String label of a certain semantic role label
	 * @return {@code true}, else {@code false} if there is no SRLArc with the given label between the two node
	 */
	public boolean isArgumentOf(DEPNode node, String label)
	{
		return getSemanticHeadArc(node, label) != null;
	}
	
	/**
	 * Check if the node has a semantic role arc with a given node of a given pattern.
	 * @param node DEPNode to check the SRL relation with
	 * @param pattern String label of a certain semantic role label
	 * @return {@code true}, else {@code false} if there is no SRLArc with the given pattern between the two node
	 */
	public boolean isArgumentOf(DEPNode node, Pattern pattern)
	{
		return getSemanticHeadArc(node, pattern) != null;
	}
	
	/**
	 * Get a list of all DEPNode nodes that are potential argument candidate of the node.
	 * @param depth the depth of how many level (going up) to search for candidates 
	 * @param includeSelf whether to include yourself as a candidate or not
	 * @return a list of all DEPNode nodes that are potential argument candidate of the node
	 */
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
	
	public String joinSubNodeFields(FieldType field, String delim)
	{
		StringBuilder build = new StringBuilder();
		
		for (DEPNode node : getSubNodeList())
		{
			build.append(delim);
			build.append(node.getTagFeature(field));
		}
		
		return build.substring(delim.length());
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