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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;

import com.carrotsearch.hppc.cursors.IntCursor;

import edu.emory.clir.clearnlp.collection.set.IntHashSet;
import edu.emory.clir.clearnlp.srl.SRLTree;
import edu.emory.clir.clearnlp.util.StringUtils;
import edu.emory.clir.clearnlp.util.arc.DEPArc;
import edu.emory.clir.clearnlp.util.arc.SRLArc;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 1.0.0.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPTree implements Iterable<DEPNode>
{
	private DEPNode[] d_tree;
	private int n_size;
	
//	====================================== Constructors ======================================

	/**
	 * Create a new tree where the root is automatically added at the top
	 * @param size
	 */
	public DEPTree(int size)
	{
		init(size);
	}
	

	/**
	 * Create a DEPTree from a list of DEPNodes
	 * @param list
	 */
	public <T>DEPTree(List<T> list)
	{
		int i, size = list.size();
		init(size);
		T item;
		
		for (i=0; i<size; i++)
		{
			item = list.get(i);

			if (item instanceof DEPNode)
				add((DEPNode)list.get(i));
			else if (item instanceof String)
				add(new DEPNode(i+1, (String)item));
		}
	}

	/**
	 * Create a DEPTree from an old DEPTree
	 * @param oTree
	 */
	public DEPTree(DEPTree oTree)
	{
		DEPNode oNode, nNode, oHead, nHead;
		int i, size = oTree.size();
		init(size-1);
		
		for (i=1; i<size; i++)
		{
			oNode = oTree.get(i);
			nNode = new DEPNode(oNode);
			add(nNode);
			
			if (oNode.getSecondaryHeadArcList() != null)
				nNode.initSecondaryHeads();
			
			if (oNode.getSemanticHeadArcList() != null)
				nNode.initSemanticHeads();
		}
		
		for (i=1; i<size; i++)
		{
			oNode = oTree.get(i);
			oHead = oNode.getHead();
			nNode = get(i);
			nHead = get(oHead.getID());
			
			if (oNode.getSecondaryHeadArcList() != null)
			{
				for (DEPArc xHead : oNode.getSecondaryHeadArcList())
				{
					oHead = xHead.getNode();
					nNode.addSecondaryHead(new DEPArc(get(oHead.getID()), xHead.getLabel()));
				}				
			}
			
			if (oNode.getSemanticHeadArcList() != null)
			{
				for (SRLArc sHead : oNode.getSemanticHeadArcList())
				{
					oHead = sHead.getNode();
					nNode.addSemanticHead(new SRLArc(get(oHead.getID()), sHead.getLabel(), sHead.getNumberedArgumentTag()));
				}				
			}
			
			nNode.setHead(nHead, oNode.getLabel());
		}
	}
	

	/**
	 * Create a new DEPTree with root DEPNode
	 * @param size
	 */
	private void init(int size)
	{
		d_tree = new DEPNode[size+1];
		DEPNode root = new DEPNode();
		root.initRoot();
		n_size = 0;
		add(root);
	}
	
//	====================================== Tree operations ======================================
	
	/**
	 * Return the DEPNode of a specific ID if exists
	 * @param id
	 * @return 
	 */
	public DEPNode get(int id)
	{
		return (0 <= id && id < n_size) ? d_tree[id] : null;
	}
	
	/**
	 * Add a DEPNode to the DEPTree
	 * @param node
	 */
	public void add(DEPNode node)
	{
		increaseSize();
		d_tree[n_size++] = node;
	}
	
	/**
	 * Check if number of DEPNodes in the DEPTree has reached max size 
	 * if reached max size then increase DEPTree by 5
	 */
	private void increaseSize()
	{
		if (n_size == d_tree.length)
		{
			DEPNode[] nTree = new DEPNode[n_size+5];
			System.arraycopy(d_tree, 0, nTree, 0, n_size);
			d_tree = nTree;
		}
	}
	
	/**
	 * Return the number of DEPNodes in the DEPTree 
	 * @return
	 */
	public int size()
	{
		return n_size;
	}
	
	/**
	 * Remove the DEPNode with the specific ID
	 * @param id
	 */
	public void remove(int id)
	{
		if (id <= 0 || id >= n_size)
			throw new IndexOutOfBoundsException();
		
		try
		{
			d_tree[id].setHead(null, null);
			n_size--;
			
			for (int i=id; i<n_size; i++)
			{
				d_tree[i] = d_tree[i+1];
				d_tree[i].setID(i);
			}
		}
		catch (IndexOutOfBoundsException e) {e.printStackTrace();}
	}
	
	/**
	 * Inserts the specific node at the specific ID of this DEPTree.
	 * @param id
	 * @param node
	 */
	public void insert(int id, DEPNode node)
	{
		if (id <= 0 || id > n_size)
			throw new IndexOutOfBoundsException();
		
		try
		{
			increaseSize();
			
			for (int i=n_size; i>id; i--)
			{
				d_tree[i] = d_tree[i-1];
				d_tree[i].setID(i);
			}
				
			d_tree[id] = node;
			node.setID(id);
			n_size++;
		}
		catch (IndexOutOfBoundsException e) {e.printStackTrace();}
	}
	
	/**
	 * Reset all DEPNodes in DEPTree from beginning ID (inclusive) from ID = 1
	 */
	public void resetNodeIDs()
	{
		resetNodeIDs(1);
	}
	
	/**
	 * Starting from a given ID we reset all the IDs to be in ascending order
	 * @param beginID
	 */
	private void resetNodeIDs(int beginID)
	{
		int i, size = size();
		
		for (i=beginID; i<size; i++)
			get(i).setID(i);
	}
	
//	====================================== Initialization ======================================

	/**
	 * Initialize all secondary heads of this DEPTree
	 */
	public void initSecondaryHeads()
	{
		for (DEPNode node : this)
			node.initSecondaryHeads();
	}
	
	/**
	 * Initialize all semantic heads of this DEPTree
	 */
	public void initSemanticHeads()
	{
		for (DEPNode node : this)
			node.initSemanticHeads();
	}
	
//	====================================== Dependency ======================================
	
	/**
	 * Return a list of all the root DEPNodes in this DEPTree
	 * @return
	 */
	public List<DEPNode> getRoots()
	{
		List<DEPNode> roots = new ArrayList<>();
		DEPNode root = get(DEPLib.ROOT_ID);
		
		for (DEPNode node : this)
		{
			if (node.isDependentOf(root))
				roots.add(node);
		}

		return roots;
	}
	
	/**
	 * Return the first DEPNode of the DEPTree that is not null
	 * @return
	 */
	public DEPNode getFirstRoot()
	{
		DEPNode root = get(DEPLib.ROOT_ID);
		
		for (DEPNode node : this)
		{
			if (node.isDependentOf(root))
				return node;
		}
		
		return null;
	}
	
	/**
	 * Return Total Count, LAS, UAS scores in an array{3} 
	 * @param goldHeads
	 * @param evalPunct
	 * @return
	 */
	public int[] getScoreCounts(DEPArc[] goldHeads, boolean evalPunct)
	{
		int i, las = 0, uas = 0, total = 0, size = size();
		DEPNode node;
		DEPArc g;
		
		for (i=1; i<size; i++)
		{
			node = get(i);
			
			if (!evalPunct && StringUtils.containsPunctuationOnly(node.getSimplifiedWordForm()))
				continue;
			
			g = goldHeads[i];
			total++;
			
			if (node.isDependentOf(get(g.getNode().getID())))
			{
				uas++;
				if (node.isLabel(g.getLabel())) las++;
			}
		}
		
		return new int[]{total, las, uas};
	}
	
	/** Convert this DEPTree into a projective tree. */
	public void projectivize()
	{
		IntHashSet ids = new IntHashSet();
		DEPNode nonProj, head, gHead;
		int i, size = size();
		String dir;

		for (i=1; i<size; i++)
			ids.add(i);

		while ((nonProj = getSmallestNonProjectiveArc(ids)) != null)
		{
			head  = nonProj.getHead();
			gHead = head.getHead();
			dir = (head.getID() < gHead.getID()) ? DEPLib.NPROJ_LEFT: DEPLib.NPROJ_RIGHT;
			nonProj.setHead(gHead, nonProj.getLabel()+dir+head.getLabel());
		}
	}

	/** Called by {@link #projectivize(String)}. */
	private DEPNode getSmallestNonProjectiveArc(IntHashSet ids)
	{
		IntHashSet remove = new IntHashSet();
		DEPNode wk, nonProj = null;
		int id, np, max = 0;

		for (IntCursor cur : ids)
		{
			id = cur.value;
			wk = get(id);
			np = getNonProjectiveDistance(wk);
			
			if (np == 0)
			{
				remove.add(id);
			}
			else if (np > max)
			{
				nonProj = wk;
				max = np;
			}
		}
		
		ids.removeAll(remove);
		return nonProj;
	}

	/** @return > 0 if w_k is non-projective. */
	private int getNonProjectiveDistance(DEPNode node)
	{
		DEPNode head = node.getHead();
		if (head == null) return 0;
		DEPNode wj;

		int bId, eId, j;

		if (node.getID() < head.getID())
		{
			bId = node.getID();
			eId = head.getID();
		}
		else
		{
			bId = head.getID();
			eId = node.getID();
		}

		for (j=bId+1; j<eId; j++)
		{
			wj = get(j);

			if (!wj.isDescendantOf(head))
				return Math.abs(head.getID() - node.getID());
		}

		return 0;
	}
	
	/**
	 * Return true if it is non projective tree
	 * @return
	 */
	public boolean isNonProjective()
	{
		DEPNode head, wj, wh;
		int bId, eId, j;
		
		for (DEPNode node : this)
		{
			head = node.getHead();
			if (head == null) continue;
			
			if (node.getID() < head.getID())
			{
				bId = node.getID();
				eId = head.getID();
			}
			else
			{
				bId = head.getID();
				eId = node.getID();
			}

			for (j=bId+1; j<eId; j++)
			{
				wj = get(j);
				wh = wj.getHead();
				
				if (wh != null && (wh.getID() < bId || wh.getID() > eId))
					return true;
			}
		}

		return false;
	}
	
	/**
	 * Returns true if this DEPTree contains a cycle
	 * @return
	 */
	public boolean containsCycle()
	{
		for (DEPNode node : this)
		{
			if (node.getHead().isDescendantOf(node))
				return true;
		}
		
		return false;
	}
	
// --------------------------------- Semantics ---------------------------------
	
	/**
	 * Return the next Semantic Head of this ID
	 * @param beginID
	 * @return
	 */
	public DEPNode getNextSemanticHead(int beginID)
	{
		int i, size = size();
		DEPNode node;
		
		for (i=beginID+1; i<size; i++)
		{
			node = get(i);
			if (node.isSemanticHead()) return node;
		}
		
		return null;
	}
	
	/**
	 * Return true if this DEPTree contains a semantic head
	 * @return
	 */
	public boolean containsSemanticHead()
	{
		for (DEPNode node : this)
		{
			if (node.isSemanticHead())
				return true;
		}
		
		return false;
	}
	
	/**
	 * Return List of Semantic Role Label Arc
	 * @return
	 */
	public List<List<SRLArc>> getArgumentList()
	{
		List<List<SRLArc>> list = new ArrayList<>();
		int i, size = size();
		List<SRLArc> args;
		
		for (i=0; i<size; i++)
			list.add(new ArrayList<SRLArc>());
		
		for (DEPNode node : this)
		{
			for (SRLArc arc : node.getSemanticHeadArcList())
			{
				args = list.get(arc.getNode().getID());
				args.add(new SRLArc(node, arc.getLabel(), arc.getNumberedArgumentTag()));
			}
		}
		
		return list;
	}
	
	/**
	 * @return A semantic tree representing a predicate-argument structure of the specific token if exists; otherwise, {@code null}.
	 * @param predicateID the node ID of a predicate.
	 */
	public SRLTree getSRLTree(int predicateID)
	{
		return getSRLTree(get(predicateID));
	}
	
	/**
	 * Return all predicate argument structures of this DEPTree
	 * @param predicate
	 * @return
	 */
	public SRLTree getSRLTree(DEPNode predicate)
	{
		if (!predicate.isSemanticHead())
			return null;
		
		SRLTree tree = new SRLTree(predicate);
		SRLArc arc;
		
		for (DEPNode node : this)
		{
			arc  = node.getSemanticHeadArc(predicate);
			
			if (arc != null)
				tree.addArgument(new SRLArc(node, arc.getLabel(), arc.getNumberedArgumentTag()));
		}
		
		return tree;
	}
	
//	====================================== Gold tags ======================================
	
	/**
	 * Return all POS tags of this DEPTree
	 * @return
	 */
	public String[] getPOSTags()
	{
		int i, size = size();
		String[] tags = new String[size];
		
		for (i=1; i<size; i++)
			tags[i] = get(i).getPOSTag();
		
		return tags;
	}
	
	/**
	 * Set the POS tags of the DEPNode of this DEPTree to the given String[] of POS tags
	 * @param tags
	 */
	public void setPOSTags(String[] tags)
	{
		int i, size = size();
		
		for (i=1; i<size; i++)
			get(i).setPOSTag(tags[i]);
	}
	
	/**
	 * Return all named entity tags in this DEPTree
	 * @return
	 */
	public String[] getNamedEntityTags()
	{
		int i, size = size();
		String[] tags = new String[size];
		
		for (i=1; i<size; i++)
			tags[i] = get(i).getNamedEntityTag();
		
		return tags;
	}
	
	/**
	 *  Set the named entity tags of the DEPNode of this DEPTree to the given String[] of named entity tags
	 * @param tags
	 */
	public void setNamedEntityTags(String[] tags)
	{
		int i, size = size();
		
		for (i=1; i<size; i++)
			get(i).setNamedEntityTag(tags[i]);
	}
	
	/**
	 * Return an array of all dependency arcs in this DEPTree
	 * @return
	 */
	public DEPArc[] getHeads()
	{
		return getHeads(size());
	}
	
	/**
	 * Return an array of all dependency arcs in this DEPTree ending at this index
	 * @param endIndex (exclusive).
	 * @return
	 */
	public DEPArc[] getHeads(int endIndex)
	{
		DEPNode node, head;
		int i;
		
		DEPArc[] heads = new DEPArc[endIndex];
		heads[0] = new DEPArc(null, null);
		
		for (i=1; i<endIndex; i++)
		{
			node = get(i);
			head = node.getHead();
			heads[i] = (head != null) ? new DEPArc(head, node.getLabel()) : new DEPArc(null, null);
		}
		
		return heads;
	}
	
	/**
	 * Starting from top of DEPTree set the heads of the DEPNodes to the DEPArc given
	 * @param arcs
	 */
	public void setHeads(DEPArc[] arcs)
	{
		int i, len = arcs.length;
		DEPNode node;
		DEPArc  arc;
		
		clearDependencies();
		
		for (i=1; i<len; i++)
		{
			node = get(i);
			arc  = arcs[i];
			
			if (arc.getNode() != null)
				node.setHead(arc.getNode(), arc.getLabel());
		}
	}
	
	/**
	 * Remove all dependences of all DEPNodes in this DEPTree
	 */
	public void clearDependencies()
	{
		int i, size = size();
		
		for (i=0; i<size; i++)
			get(i).clearDependencies();
	}
	
	/**
	 * Return all POS tags of this given feature 
	 * @param key
	 * @return
	 */
	public String[] getFeatureTags(String key)
	{
		int i, size = size();
		String[] tags = new String[size];
		
		for (i=1; i<size; i++)
			tags[i] = get(i).getFeat(key);
		
		return tags;
	}
	
	/**
	 * Starting at the top of this DEPTree set this DEPNode with this feature label and this tag
	 * @param key
	 * @param tags
	 */
	public void setFeatureTags(String key, String[] tags)
	{
		int i, size = size();
		
		for (i=1; i<size; i++)
			get(i).putFeat(key, tags[i]);
	}
	
	/**
	 * Starting at the top of this DEPTree remove this feature with this specific key 
	 * @param key
	 */
	public void clearFeatureTags(String key)
	{
		int i, size = size();
		
		for (i=1; i<size; i++)
			get(i).removeFeat(key);
	}

	/**
	 * Return a String[] with all role set IDs
	 * @return
	 */
	public String[] getRolesetIDs()
	{
		int i, size = size();
		String[] rolesets = new String[size];
		
		for (i=1; i<size; i++)
			rolesets[i] = get(i).getRolesetID();
		
		return rolesets;
	}
	
	/**
	 * Starting at the top of this DEPTree clear all role set IDs
	 */
	public void clearRolesetIDs()
	{
		int i, size = size();
		
		for (i=1; i<size; i++)
			get(i).clearRolesetID();
	}
	
	/**
	 * 2-Dimension array 'i'th row is the DEPNode and 'j'ith column is the semantic head
	 * @return
	 */
	public SRLArc[][] getSemanticHeads()
	{
		int i, j, len, size = size();
		List<SRLArc> arcs;
		SRLArc[] heads;
		
		SRLArc[][] sHeads = new SRLArc[size][];
		sHeads[0] = new SRLArc[0];
		
		for (i=1; i<size; i++)
		{
			arcs  = get(i).getSemanticHeadArcList();
			len   = arcs.size();
			heads = new SRLArc[len];
			
			for (j=0; j<len; j++)
				heads[j] = new SRLArc(arcs.get(j));
			
			sHeads[i] = heads;
		}
		
		return sHeads;
	}
	
	public void setSemanticHeads(SRLArc[][] semanticArcs)
	{
		int i, len = semanticArcs.length;
		SRLArc[] arcs;
		DEPNode  node;
		
		clearSemanticHeads();
		
		for (i=1; i<len; i++)
		{
			arcs = semanticArcs[i];
			node = get(i);
			
			for (SRLArc arc : arcs)
				node.addSemanticHead(arc);
		}
	}
	
	/**
	 * Starting at the top of the DEPTree clear all the semantic heads
	 */
	public void clearSemanticHeads()
	{
		int i, size = size();
		
		for (i=1; i<size; i++)
			get(i).clearSemanticHeads();
	}
	
	/**
	 * Return a list of DEPNodes from a DFS traversal of this DEPTree
	 * @return
	 */
	public List<DEPNode> getDepthFirstNodeList()
	{
		List<DEPNode> list = new ArrayList<>(size());
		traverseDepthFirst(list, get(DEPLib.ROOT_ID));
		return list;
	}
	
	/**
	 * Recursive call that does a DFS traversal of this DEPNode's children
	 * @param list
	 * @param node
	 */
	private void traverseDepthFirst(List<DEPNode> list, DEPNode node)
	{
		for (DEPNode child : node.getDependentList())
			traverseDepthFirst(list, child);
		
		list.add(node);
	}
	
	public int countHeaded()
	{
		int c = 0;
		
		for (DEPNode node : this)
			if (node.hasHead()) c++;
		
		return c;
	}
	
//	====================================== String ======================================
	
	@Override
	public String toString()
	{
		return toString(DEPNode::toString);
	}
	
	public String toString(Function<DEPNode,String> f)
	{
		StringJoiner build = new StringJoiner(StringConst.NEW_LINE);
		
		for (DEPNode node : this)
			build.add(f.apply(node));

		return build.toString();
	}

	@Override
	public Iterator<DEPNode> iterator()
	{
		Iterator<DEPNode> it = new Iterator<DEPNode>()
		{
			private int current_index = 1;
			
			@Override
			public boolean hasNext()
			{
				return current_index < size();
			}
			
			@Override
			public DEPNode next()
			{
				return d_tree[current_index++];
			}
			
			@Override
			public void remove() {}
		};
		
		return it;
	}
	
	public DEPNode[] toNodeArray()
	{
		return d_tree;
	}
	
	/**
	 * @param beginIndex inclusive.
	 * @param endIndex exclusive.
	 */
	public String join(Function<DEPNode,String> f, String delim, int beginIndex, int endIndex)
	{
		StringJoiner joiner = new StringJoiner(delim);
		
		for (int i=beginIndex; i<endIndex; i++)
			joiner.add(f.apply(get(i)));
		
		return joiner.toString();
	}
	
//	====================================== String ======================================
	
	public Set<String> getNgrams(Function<DEPNode,String> f, String delim, int n)
	{
		Set<String> ngrams = new HashSet<>();
		int i, j, size = size();
		
		for (i=1; i<size; i++)
			for (j=0; j<n; j++)
				if (i-j > 0) ngrams.add(join(f, delim, i-j, i+1));
		
		return ngrams;
	}
	
	
	public Set<String> getNgrams(Function<DEPNode,String> f1, Function<DEPNode,String> f2, String delim, int n)
	{
		Set<String> ngrams = new HashSet<>();
		int i, j, k, l, c, size = size();
		StringJoiner joiner;
		
		for (i=1; i<size; i++)
			for (j=1; j<n; j++)
				if (i-j > 0)
					for (l=0; l<=j; l++)
					{
						joiner = new StringJoiner(delim);
						
						for (k=i-j,c=0; k<=i; k++,c++)
						{
							if (l == c)	joiner.add(f1.apply(get(k)));
							else		joiner.add(f2.apply(get(k)));
						}

						ngrams.add(joiner.toString());
					}
		
		return ngrams;
	}
	
//	public Set<String> getNgrams(Function<DEPNode,String> f, String delim, int n, boolean excludeSymbols)
//	{
//		List<String> list = new ArrayList<>();
//		String s;
//		
//		for (DEPNode node : this)
//		{
//			s = f.apply(node);
//			if (!excludeSymbols || !StringUtils.containsPunctuationOnly(s)) list.add(s);
//		}
//		
//		Set<String> ngrams = new HashSet<>(list);
//		int i, j, size = list.size();
//		
//		for (i=0; i<size; i++)
//			for (j=1; j<n; j++)
//				if (i-j >= 0) ngrams.add(Joiner.join(list, delim, i-j, i+1));
//		
//		return ngrams;
//	}
}