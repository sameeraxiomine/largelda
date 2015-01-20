/* Copyright (C) 2014 Sameer Wadkar.
This file is an adaptation to the  "MALLET" (MAchine Learning for LanguagE Toolkit)
It is adapted from the "MALLET" (MAchine Learning for LanguagE Toolkit) API  by, 
McCallum, Andrew Kachites-  "MALLET: A Machine Learning for Language Toolkit."
http://mallet.cs.umass.edu. 2002.
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */
package org.bigtextml.bigcollections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.higherfrequencytrading.chronicle.impl.IndexedChronicle;

public class BigList<E> implements List<E> {
	private BigMap<Integer,E> map1 = new BigMap<Integer,E>();
	private BigMap<E,List<Integer>> map2 = new BigMap<E,List<Integer>>();
	
	public int size() {
		// TODO Auto-generated method stub
		return map1.size();
	}

	
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return map1.size()==0;
	}

	
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return map2.containsKey(o);
	}

	
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}

	
	public Object[] toArray() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		//return null;
	}

	
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		//return null;
	}

	
	public boolean add(E e) {
		// TODO Auto-generated method stub
		int index = map1.size();
		this.map1.put(index, e);
		List<Integer> lInt = this.map2.get(e);
		if(lInt==null){
			lInt=new ArrayList<Integer>();
		}
		lInt.add(index);
		this.map2.put(e, lInt);
		return true;
	}

	
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		List<Integer> indexes = this.map2.remove(o);
		for(int i:indexes) {
			this.map1.remove(i);
		}
		return true;
	}

	
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}

	
	public boolean addAll(Collection<? extends E> c) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}

	
	public boolean addAll(int index, Collection<? extends E> c) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}

	
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
		
		
	}

	
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void clear() {
		// TODO Auto-generated method stub
		this.map1.clear();
		this.map2.clear();

		
	}

	
	public E get(int index) {
		// TODO Auto-generated method stub
		return this.map1.get(index);
	}

	
	public E set(int index, E element) {
		// TODO Auto-generated method stub
		this.remove(index);
		this.add(index,element);
		return element;
	}

	
	public void add(int index, E element) {
		// TODO Auto-generated method stub
		if(this.contains(index)){
			this.remove(index);
		}
		this.add(index,element);
		
	}

	
	public E remove(int index) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		/*
		E obj = this.map1.remove(index);
		List<Integer> lInts = this.map2.remove(obj);
		lInts.remove(0);
		if(lInts.size()>0){
			this.map2.put(obj, lInts);
		}
		return obj;
		*/
	}

	
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		List<Integer> lInts= this.map2.get(o);
		if(lInts!=null && lInts.size()>0)
			return lInts.get(0);
		return -1;
	}

	
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		List<Integer> lInts= this.map2.get(o);
		if(lInts!=null && lInts.size()>0)
			return lInts.get(lInts.size()-1);
		return -1;
	}

	
	public ListIterator<E> listIterator() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	
	public ListIterator<E> listIterator(int index) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	
	public List<E> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	protected void finalize() throws Throwable {
		this.map1.clear();
		this.map2.clear();
	
	}
	public static void main(String[] args){
		System.setProperty("BigMapCachePath", "C:/tmp");
		BigList bl = new BigList();	
		try{
			for(int i=0;i<100000;i++){
				bl.add(i);
			}
			Object obj = new Integer(15000);
			bl.remove(obj);
			System.out.println((Object)bl.get(15001));
			
		}
		finally{
			bl.clear();
		}


	}
}
