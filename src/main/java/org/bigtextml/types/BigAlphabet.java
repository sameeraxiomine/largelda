/* Copyright (C) 2014 Sameer Wadkar.
This file is an adaptation to the  "MALLET" (MAchine Learning for LanguagE Toolkit)
It is adapted from the "MALLET" (MAchine Learning for LanguagE Toolkit) API  by, 
McCallum, Andrew Kachites-  "MALLET: A Machine Learning for Language Toolkit."
http://mallet.cs.umass.edu. 2002.
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */
package org.bigtextml.types;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.rmi.dgc.VMID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bigtextml.bigcollections.BigMap;
import org.bigtextml.management.ManagementServices;

import cc.mallet.types.Alphabet;
import cc.mallet.types.AlphabetCarrying;

public class BigAlphabet implements Serializable{
	//transient BigMap alphabetMap;
	//transient BigMap alphabetEntries;
	boolean growthStopped = false;
	Class entryClass = null;
	VMID instanceId = new VMID();  //used in readResolve to identify persitent instances
	private static int internalCounter=0;
	private static String  alphaLabel="Alphabet_";
	private static String  entriesLabel="Entries_";
	
	private String alphaId="";
	private String entriesId="";
	
	private List<String> mapList = new ArrayList<String>();

	public BigAlphabet (int capacity, Class entryClass)
	{
		this.entriesId=entriesLabel+Integer.toString(internalCounter);
		this.alphaId=alphaLabel+Integer.toString(internalCounter);
		internalCounter=internalCounter+1;
		//this.alphabetMap = new BigMap<String,Integer> ();
		//this.alphabetEntries = new BigMap<Integer,String> ();
		this.entryClass = entryClass;
		// someone could try to deserialize us into this image (e.g., by RMI).  Handle this.
		deserializedEntries.put (instanceId, this);
	}
	
	
	private Map getMap(){
		
		return (Map) ManagementServices.getBigMap(this.alphaId);
	}
	
	private Map getEntries(){
		return (Map) ManagementServices.getBigMap(this.entriesId);
	}
	
	public BigAlphabet (Class entryClass)
	{
		this (8, entryClass);
	}

	public BigAlphabet (int capacity)
	{
		this (capacity, null);
	}

	public BigAlphabet ()
	{
		this (8, null);
	}
	
	public BigAlphabet (Object[] entries) {
		this (entries.length);
		for (Object entry : entries)
			this.lookupIndex(entry);
	}
/*
	public Object clone ()
	{
		//try {
		// Wastes effort, because we over-write ivars we create
		BigAlphabet ret = new BigAlphabet ();
		ret.map = (BigMap)map.clone();
		ret.entries = (BigMap)entries.clone();
		ret.growthStopped = growthStopped;
		ret.entryClass = entryClass;
		return ret;
		//} catch (CloneNotSupportedException e) {
		//e.printStackTrace();
		//throw new IllegalStateException ("Couldn't clone InstanceList Vocabuary");
		//}
	}
*/
	/** Return -1 if entry isn't present. */
	public int lookupIndex (Object entry, boolean addIfNotPresent)
	{
		if (entry == null)
			throw new IllegalArgumentException ("Can't lookup \"null\" in an Alphabet.");
		if (entryClass == null)
			entryClass = entry.getClass();
		else
			// Insist that all entries in the Alphabet are of the same
			// class.  This may not be strictly necessary, but will catch a
			// bunch of easily-made errors.
			if (entry.getClass() != entryClass)
				throw new IllegalArgumentException ("Non-matching entry class, "+entry.getClass()+", was "+entryClass);
		
		Integer retIndex = null;
		long millis = System.currentTimeMillis();
		
		boolean contains = this.getMap().containsKey(entry);
		if(contains){
			retIndex = (Integer)this.getMap().get( entry );
		}
		
		if ( (retIndex==null || retIndex==-1) && !growthStopped && addIfNotPresent) {
			retIndex = this.getEntries().size();
			
			this.getMap().put(entry, retIndex);
			this.getEntries().put(retIndex,retIndex);
			//this.getEntries().put(retIndex,entry);
		}

		//System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXX===="+(System.currentTimeMillis()-millis));
		return retIndex;
	}

	public int lookupIndex (Object entry)
	{
		return lookupIndex (entry, true);
	}

	/*
	public Object lookupObject (int index)
	{
		Object obj = this.getEntries().get(index);;
		return obj;
	}
	*/

	public Object lookupObject (int index)
	{
		int index2 = (Integer)this.getEntries().get(index);;
		BigMap map = (BigMap) this.getMap();
		return map.getFromOffHeapKeyByIndex(index2);
	}

	public String[] toArray () {
		throw new UnsupportedOperationException();
		//return entries.toArray();
	}

	/**
	 * Returns an array containing all the entries in the Alphabet.
	 *  The runtime type of the returned array is the runtime type of in.
	 *  If in is large enough to hold everything in the alphabet, then it
	 *  it used.  The returned array is such that for all entries <tt>obj</tt>,
	 *  <tt>ret[lookupIndex(obj)] = obj</tt> .
	 */ 
	public String[] toArray (String[] in) {
		throw new UnsupportedOperationException();
		//return entries.toArray (in);
	}

	// xxx This should disable the iterator's remove method...
	public Iterator iterator () {
		throw new UnsupportedOperationException();
		//return entries.iterator();
	}

	public Object[] lookupObjects (int[] indices)
	{
		Object[] ret = new Object[indices.length];
		for (int i = 0; i < indices.length; i++)
			ret[i] = this.getEntries().get(indices[i]);
		return ret;
	}

	/**
	 * Returns an array of the objects corresponding to
	 * @param indices An array of indices to look up
	 * @param buf An array to store the returned objects in.
	 * @return An array of values from this Alphabet.  The runtime type of the array is the same as buf
	 */
	public Object[] lookupObjects (int[] indices, Object[] buf)
	{
		for (int i = 0; i < indices.length; i++)
			buf[i] = this.getEntries().get(indices[i]);
		return buf;
	}

	public int[] lookupIndices (String[] objects, boolean addIfNotPresent)
	{
		int[] ret = new int[objects.length];
		for (int i = 0; i < objects.length; i++)
			ret[i] = lookupIndex (objects[i], addIfNotPresent);
		return ret;
	}

	public boolean contains (Object entry)
	{
		throw new UnsupportedOperationException();
		//return map.contains (entry);
	}

	public int size ()
	{
		return this.getEntries().size();
	}

	public void stopGrowth ()
	{
		growthStopped = true;
	}

	public void startGrowth ()
	{
		growthStopped = false;
	}

	public boolean growthStopped ()
	{
		return growthStopped;
	}

	public Class entryClass ()
	{
		return entryClass;
	}

	/** Return String representation of all Alphabet entries, each
	separated by a newline. */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.getEntries().size(); i++) {
			sb.append (this.getEntries().get(i).toString());
			sb.append ('\n');
		}
		return sb.toString();
	}

	public void dump () { dump (System.out); }

	public void dump (PrintStream out)
	{
		dump (new PrintWriter (new OutputStreamWriter (out), true));
	}

	public void dump (PrintWriter out)
	{
		for (int i = 0; i < this.getEntries().size(); i++) {
			out.println (i+" => "+this.getEntries().get (i));
		}
	}

	/** Convenience method that can often implement alphabetsMatch in classes that implement the AlphabetsCarrying interface. */
	public static boolean alphabetsMatch (BigAlphabetCarrying object1, BigAlphabetCarrying object2) {
		BigAlphabet[] a1 = object1.getAlphabets();
		BigAlphabet[] a2 = object2.getAlphabets();
		if (a1.length != a2.length) return false;
		for (int i = 0; i < a1.length; i++) {
			if (a1[i] == a2[i]) continue;
			if (a1[i] == null || a2[i] == null) return false;  // One is null, but the other isn't
			if (! a1[i].equals(a2[i])) return false;
		}
		return true;
	}

	public VMID getInstanceId() { return instanceId;} // for debugging
	public void setInstanceId(VMID id) { this.instanceId = id; }
	// Serialization

	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 1;

	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt (CURRENT_SERIAL_VERSION);
		/*
		out.writeInt (this.getEntries().size());
		for (int i = 0; i < this.getEntries().size(); i++)
			out.writeObject (this.getEntries().get(i));
		*/
		out.writeObject(this.alphaId);
		out.writeObject(this.entriesId);
		out.writeBoolean (growthStopped);
		out.writeObject (entryClass);
		out.writeObject(instanceId);
	}

	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		//throw new UnsupportedOperationException();
		
		int version = in.readInt ();
		this.alphaId = (String)in.readObject();
		this.entriesId = (String)in.readObject();
		//this.alphabetEntries = this.getEntries();
		//this.alphabetMap = this.getMap();
		/*
		for (int i = 0; i < size; i++) {
			Object o = in.readObject();
			map.put (o, i);
			entries. add (o);
		}
		*/
		growthStopped = in.readBoolean();
		entryClass = (Class) in.readObject();
		if (version >0 ){ // instanced id added in version 1S
			instanceId = (VMID) in.readObject();
		}
		
	}

	private transient static HashMap deserializedEntries = new HashMap();
	/**
	 * This gets called after readObject; it lets the object decide whether
	 * to return itself or return a previously read in version.
	 * We use a hashMap of instanceIds to determine if we have already read
	 * in this object.
	 * @return
	 * @throws ObjectStreamException
	 */

	public Object readResolve() throws ObjectStreamException {
		Object previous = deserializedEntries.get(instanceId);
		if (previous != null){
			//System.out.println(" ***Alphabet ReadResolve:Resolving to previous instance. instance id= " + instanceId);
			return previous;
		}
		if (instanceId != null){
			deserializedEntries.put(instanceId, this);
		}
		//System.out.println(" *** Alphabet ReadResolve: new instance. instance id= " + instanceId);
		return this;
	}
}
