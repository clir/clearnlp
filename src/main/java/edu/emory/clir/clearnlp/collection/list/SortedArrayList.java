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
package edu.emory.clir.clearnlp.collection.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SortedArrayList<T extends Comparable<T>> implements List<T>, Serializable, Iterable<T>
{
	private static final long serialVersionUID = 3219296829240273911L;
	private ArrayList<T> t_list;
	private boolean b_ascending;
	
	public SortedArrayList()
	{
		init(0, true);
	}
	
	public SortedArrayList(int initialCapacity)
	{
		init(initialCapacity, true);
	}
	
	public SortedArrayList(boolean ascending)
	{
		init(0, ascending);
	}
	
	public SortedArrayList(int initialCapacity, boolean ascending)
	{
		init(initialCapacity, ascending);
	}
	
	private void init(int initialCapacity, boolean ascending)
	{
		t_list = (initialCapacity > 0) ? new ArrayList<T>(initialCapacity) : new ArrayList<T>();
		b_ascending = ascending;
	}
	
	/**
	 * Adds the specific item to this list in a sorted order.
	 * @return {@code true}.
	 */
	public boolean add(T e)
	{
		int index = getInsertIndex(e);
		t_list.add(index, e);
		return true;
	}
	
	/** @return the index of the added item. */
	public int addItem(T e)
	{
		int index = getInsertIndex(e);
		t_list.add(index, e);
		return index;
	}
	
	/**
	 * Adds all items in the specific collection to this list in a sorted order.
	 * @return {@code true}.
	 */
	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		for (T t : c) add(t);
		return false;
	}
	
	/**
	 * Removes the first occurrence of the specific item from this list if exists.
	 * @return the index of the item's original position if exists; otherwise, return a negative number.
	 */
	public int remove(T item)
	{
		int index = indexOf(item);
		if (index >= 0) t_list.remove(index);
		return index;
	}
	
	/** @return {@code true} if the specific item is in the list. */
	public boolean contains(T item)
	{
		return indexOf(item) >= 0;
	}
	
	/** @return the index of the first occurrence of the specific item if exists; otherwise, a negative number. */
	public int indexOf(T item)
	{
		return b_ascending ? Collections.binarySearch(t_list, item) : Collections.binarySearch(t_list, item, Collections.reverseOrder());
	}
	
	/** @return the index of the specific item if it is added to this list. */
	public int getInsertIndex(T item)
	{
		int index = indexOf(item);
		return (index < 0) ? -(index+1) : index+1;
	}
	
	public void trimToSize()
	{
		t_list.trimToSize();
	}

	@Override
	public int size()
	{
		return t_list.size();
	}

	@Override
	public boolean isEmpty()
	{
		return t_list.isEmpty();
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return t_list.containsAll(c);
	}

	@Override
	public Iterator<T> iterator()
	{
		return t_list.iterator();
	}

	@Override
	public Object[] toArray()
	{
		return t_list.toArray();
	}

	@Override @SuppressWarnings("hiding")
	public <T>T[] toArray(T[] a)
	{
		return t_list.toArray(a);
	}

	@Override
	public T remove(int index)
	{
		return t_list.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		return t_list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		return t_list.retainAll(c);
	}

	@Override
	public void clear()
	{
		t_list.clear();
	}

	@Override
	public T get(int index)
	{
		return t_list.get(index);
	}
	
	@Override
	public ListIterator<T> listIterator()
	{
		return t_list.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index)
	{
		return t_list.listIterator(index);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex)
	{
		return t_list.subList(fromIndex, toIndex);
	}
	
	@Override
	public String toString()
	{
		return t_list.toString();
	}
	
	@Override @Deprecated
	public void add(int index, T element)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override @Deprecated
	public boolean addAll(int index, Collection<? extends T> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override @Deprecated
	public T set(int index, T element)
	{
		throw new UnsupportedOperationException();
	}
	
	/** @deprecated Use {@link #remove(Comparable)} instead. */
	@Override
	public boolean remove(Object o)
	{
		return t_list.remove(o);
	}
	
	/** @deprecated Use {@link #contains(Comparable)} instead. */
	@Override
	public boolean contains(Object o)
	{
		throw new UnsupportedOperationException();
	}
	
	/** @deprecated Use {@link #indexOf(Comparable)} instead. */
	@Override
	public int indexOf(Object o)
	{
		throw new UnsupportedOperationException();
	}
	
	/** @deprecated Use {@link #indexOf(Comparable)} instead. */
	@Override
	public int lastIndexOf(Object o)
	{
		throw new UnsupportedOperationException();
	}
}