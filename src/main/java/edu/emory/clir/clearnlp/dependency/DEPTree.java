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
import java.util.Iterator;
import java.util.List;

import com.carrotsearch.hppc.cursors.IntCursor;
import com.google.common.collect.Lists;

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

	/** The artificial root node is inserted front automatically. */
	public DEPTree(int size)
	{
		init(size);
	}
	
	public DEPTree(List<DEPNode> nodes)
	{
		int i, size = nodes.size();
		init(size);
		
		for (i=0; i<size; i++)
			add(nodes.get(i));
	}
	
	/** @param string dummy parameter. */
	public DEPTree(List<String> tokens, int flag)
	{
		int i, size = tokens.size();
		init(size);
		
		for (i=0; i<size; i++)
			add(new DEPNode(i+1, tokens.get(i)));
	}
	
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
	
	private void init(int size)
	{
		d_tree = new DEPNode[size+1];
		DEPNode root = new DEPNode();
		root.initRoot();
		n_size = 0;
		add(root);
	}
	
//	====================================== Tree operations ======================================
	
	/** @return the dependency node with the specific ID if exists; otherwise, {@code null}. */
	public DEPNode get(int id)
	{
		return (0 <= id && id < n_size) ? d_tree[id] : null;
	}
	
	public void add(DEPNode node)
	{
		increaseSize();
		d_tree[n_size++] = node;
	}
	
	private void increaseSize()
	{
		if (n_size == d_tree.length)
		{
			DEPNode[] nTree = new DEPNode[n_size+5];
			System.arraycopy(d_tree, 0, nTree, 0, n_size);
			d_tree = nTree;
		}
	}
	
	public int size()
	{
		return n_size;
	}
	
	/** Removes the node with the specific id from this tree. */
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
	
	/** Inserts the specific node at the specific index of this tree. */
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
	
	public void resetNodeIDs()
	{
		resetNodeIDs(1);
	}
	
	/** @param beginID beginning ID (inclusive). */
	private void resetNodeIDs(int beginID)
	{
		int i, size = size();
		
		for (i=beginID; i<size; i++)
			get(i).setID(i);
	}
	
//	====================================== Initialization ======================================

	public void initSecondaryHeads()
	{
		for (DEPNode node : this)
			node.initSecondaryHeads();
	}
	
	public void initSemanticHeads()
	{
		for (DEPNode node : this)
			node.initSemanticHeads();
	}
	
//	====================================== Dependency ======================================
	
	/** @return a list of root nodes in this tree. */
	public List<DEPNode> getRoots()
	{
		List<DEPNode> roots = Lists.newArrayList();
		DEPNode root = get(DEPLib.ROOT_ID);
		
		for (DEPNode node : this)
		{
			if (node.isDependentOf(root))
				roots.add(node);
		}

		return roots;
	}
	
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
	
	/** @return [LAS, UAS]. */
	public int[] getScoreCounts(DEPArc[] goldHeads, boolean evalPunct)
	{
		int i, las = 0, uas = 0, size = size();
		DEPNode node;
		DEPArc g;
		
		for (i=1; i<size; i++)
		{
			node = get(i);
			
			if (!evalPunct && StringUtils.containsPunctuationOnly(node.getSimplifiedWordForm()))
				continue;
			
			g = goldHeads[i];
			
			if (node.isDependentOf(get(g.getNode().getID())))
			{
				uas++;
				if (node.isLabel(g.getLabel())) las++;
			}
		}
		
		return new int[]{las, uas};
	}
	
	public void projectivize(String left, String right)
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
			dir = (head.getID() < gHead.getID()) ? left : right;
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
	
	/** @return {@code true} if this tree contains a cycle. */
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
	
	/** @param beginID exclusive. */
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
	
	public boolean containsSemanticHead()
	{
		for (DEPNode node : this)
		{
			if (node.isSemanticHead())
				return true;
		}
		
		return false;
	}
	
	public List<List<SRLArc>> getArgumentList()
	{
		List<List<SRLArc>> list = Lists.newArrayList();
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
	 * @return a semantic tree representing a predicate-argument structure of the specific token if exists; otherwise, {@code null}.
	 * @param predicateID the node ID of a predicate.
	 */
	public SRLTree getSRLTree(int predicateID)
	{
		return getSRLTree(get(predicateID));
	}
	
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
	
	public String[] getPOSTags()
	{
		int i, size = size();
		String[] tags = new String[size];
		
		for (i=1; i<size; i++)
			tags[i] = get(i).getPOSTag();
		
		return tags;
	}
	
	public void setPOSTags(String[] tags)
	{
		int i, size = size();
		
		for (i=1; i<size; i++)
			get(i).setPOSTag(tags[i]);
	}
	
	public String[] getNamedEntityTags()
	{
		int i, size = size();
		String[] tags = new String[size];
		
		for (i=1; i<size; i++)
			tags[i] = get(i).getNamedEntityTag();
		
		return tags;
	}
	
	public void setNamedEntityTags(String[] tags)
	{
		int i, size = size();
		
		for (i=1; i<size; i++)
			get(i).setNamedEntityTag(tags[i]);
	}
	
	public DEPArc[] getHeads()
	{
		return getHeads(size());
	}
	
	/** @param endIndex the ending index (exclusive). */
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
	
	public void clearDependencies()
	{
		int i, size = size();
		
		for (i=0; i<size; i++)
			get(i).clearDependencies();
	}
	
	public String[] getFeatureTags(String key)
	{
		int i, size = size();
		String[] tags = new String[size];
		
		for (i=1; i<size; i++)
			tags[i] = get(i).getFeat(key);
		
		return tags;
	}
	
	public void setFeatureTags(String key, String[] tags)
	{
		int i, size = size();
		
		for (i=1; i<size; i++)
			get(i).putFeat(key, tags[i]);
	}
	
	public void clearFeatureTags(String key)
	{
		int i, size = size();
		
		for (i=1; i<size; i++)
			get(i).removeFeat(key);
	}

	public String[] getRolesetIDs()
	{
		int i, size = size();
		String[] rolesets = new String[size];
		
		for (i=1; i<size; i++)
			rolesets[i] = get(i).getRolesetID();
		
		return rolesets;
	}
	
	public void clearRolesetIDs()
	{
		int i, size = size();
		
		for (i=1; i<size; i++)
			get(i).clearRolesetID();
	}
	
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
	
	public void clearSemanticHeads()
	{
		int i, size = size();
		
		for (i=1; i<size; i++)
			get(i).clearSemanticHeads();
	}
	
	public List<DEPNode> getDepthFirstNodeList()
	{
		List<DEPNode> list = new ArrayList<>(size());
		traverseDepthFirst(list, get(DEPLib.ROOT_ID));
		return list;
	}
	
	private void traverseDepthFirst(List<DEPNode> list, DEPNode node)
	{
		for (DEPNode child : node.getDependentList())
			traverseDepthFirst(list, child);
		
		list.add(node);
	}
	
//	====================================== String ======================================
	
	public String toStringPOS()
	{
		StringBuilder build = new StringBuilder();
		
		for (DEPNode node : this)
		{
			build.append(StringConst.NEW_LINE);
			build.append(node.toStringPOS());
		}

		return build.substring(1);
	}
	
	public String toStringMorph()
	{
		StringBuilder build = new StringBuilder();
		
		for (DEPNode node : this)
		{
			build.append(StringConst.NEW_LINE);
			build.append(node.toStringMorph());
		}

		return build.substring(1);
	}
	
	public String toStringDEP()
	{
		StringBuilder build = new StringBuilder();
		
		for (DEPNode node : this)
		{
			build.append(StringConst.NEW_LINE);
			build.append(node.toStringDEP());
		}

		return build.substring(1);
	}
	
	public String toStringDAG()
	{
		StringBuilder build = new StringBuilder();
		
		for (DEPNode node : this)
		{
			build.append(StringConst.NEW_LINE);
			build.append(node.toStringDAG());
		}

		return build.substring(1);
	}
	
	public String toStringSRL()
	{
		StringBuilder build = new StringBuilder();
		
		for (DEPNode node : this)
		{
			build.append(StringConst.NEW_LINE);
			build.append(node.toStringSRL());
		}

		return build.substring(1);
	}
	
	public String toStringCoNLLX()
	{
		StringBuilder build = new StringBuilder();
		
		for (DEPNode node : this)
		{
			build.append(StringConst.NEW_LINE);
			build.append(node.toStringCoNLLX());
		}

		return build.substring(1);
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		for (DEPNode node : this)
		{
			build.append(StringConst.NEW_LINE);
			build.append(node.toString());
		}

		return build.substring(1);
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
}