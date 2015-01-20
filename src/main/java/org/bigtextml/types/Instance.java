
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

import java.util.Map;
import java.util.logging.*;
import java.io.*;

import org.bigtextml.bigcollections.BigMap;
import org.bigtextml.management.ManagementServices;

import cc.mallet.util.MalletLogger;
import cc.mallet.util.PropertyList;


public class Instance implements Serializable, BigAlphabetCarrying, Cloneable
{
	private static boolean DO_NOT_STORE=true;
	private static Logger logger = MalletLogger.getLogger(Instance.class.getName());
	final static Map cache =ManagementServices.getBigMap("Instances");
	protected Object data;				// The input data in digested form, e.g. a FeatureVector
	protected Object target;			// The output data in digested form, e.g. a Label
	protected Object name;				// A readable name of the source, e.g. for ML error analysis
	protected Object source;			/* The input in a reproducable form, e.g. enabling re-print of
									     * String w/POS tags, usually without target information,
								         * e.g. an unannotated RegionList. 
								         */
	PropertyList properties = null;
	boolean locked = false;
	boolean overwrite=false;

	/*
	 * In certain unusual circumstances, you might want to create an Instance without sending it through a pipe.
	 */
	public Instance (Object data, Object target, Object name, Object source)
	{
	    this.data = data;
		this.target = target;
		this.name = name;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public Object getData () { 
		if(!this.locked){
			return data;
		}
		else{
			return cache.get(name);
		}
	}

	public Object getTarget () { return target; }
	
	public Object getName () { return name; }
	
	public Object getSource () { return source; }
	
	public BigAlphabet getDataAlphabet() {
		if (data instanceof BigAlphabetCarrying)
			return ((BigAlphabetCarrying)data).getAlphabet();
		else
			return null;
	}

	public BigAlphabet getTargetAlphabet() {
		if (target instanceof BigAlphabetCarrying)
			return ((BigAlphabetCarrying)target).getAlphabet();
		else
			return null;
	}
	
	public BigAlphabet getAlphabet () {
		return getDataAlphabet();
	}
	
	public BigAlphabet[] getAlphabets()
	{
		return new BigAlphabet[] {getDataAlphabet(), getTargetAlphabet()};
	}
	
	public boolean alphabetsMatch (BigAlphabetCarrying object)
	{
		BigAlphabet[] oas = object.getAlphabets();
		return  oas.length == 2 && oas[0].equals(getDataAlphabet()) && oas[1].equals(getDataAlphabet());
	}


	public boolean isLocked () { return locked; }
	public void lock() { 
		
		locked = true; 
		if(data!=null){
			//boolean mightContain = Instance.cache.mightContain(name);
			boolean mightContain = false;
			boolean addInstance = false;
			if(!mightContain)
				addInstance=true;
			//System.out.println("Now checking Instance == " + this.name + "=="+mightContain);
			if(mightContain ){
				if(this.isOverwrite()) {
					addInstance=true;
				}
				else{
					//Do the more expensive check
					
					if(!Instance.cache.containsKey(name)){
						addInstance=true;
					}
				}
			}
			if(addInstance & !DO_NOT_STORE){
				Instance.cache.put(name,data);
			}
			this.data=null;
			this.source=null;
			
		}
		
		//System.out.println("Added " + name + " in millis " + (System.currentTimeMillis()-millis));
		//System.out.println("Putting " + name + "=" + data);

		
		
	}
	public void unLock() { locked = false; }

	public BigLabeling getLabeling ()
	{
		if (target == null || target instanceof BigLabeling)
			return (BigLabeling)target;
		throw new IllegalStateException ("Target is not a Labeling; it is a "+target.getClass().getName());
	}

	public void setData (Object d) {
		if (!locked){
			
			data = d;
		}
		else throw new IllegalStateException ("Instance is locked.");
	}
	public void setTarget (Object t) {
		if (!locked) target = t;
		else throw new IllegalStateException ("Instance is locked.");
	}
	public void setLabeling (BigLabeling l) {
		// This test isn't strictly necessary, but might catch some typos.
		assert (target == null || target instanceof BigLabeling);
		if (!locked) target = l;
		else throw new IllegalStateException ("Instance is locked.");
	}
	public void setName (Object n) {
		if (!locked) name = n;
		else throw new IllegalStateException ("Instance is locked.");
	}
	public void setSource (Object s) {
		if (!locked) source = s;
		else throw new IllegalStateException ("Instance is locked.");
	}
	public void clearSource () {
		source = null;
	}

	public Instance shallowCopy ()
	{
		Instance ret = new Instance (data, target, name, source);
		ret.locked = locked;
		ret.properties = properties;
		return ret;
	}
	
	public Object clone ()
	{
		return shallowCopy();
	}

	
	// Setting and getting properties
	
	public void setProperty (String key, Object value)
	{
		properties = PropertyList.add (key, value, properties);
	}

	public void setNumericProperty (String key, double value)
	{
		properties = PropertyList.add (key, value, properties);
	}

	@Deprecated
	public PropertyList getProperties ()
	{
		return properties;
	}

	@Deprecated
	public void setPropertyList (PropertyList p) 
	{
		if (!locked) properties = p;
		else throw new IllegalStateException ("Instance is locked.");
	}

	public Object getProperty (String key)
	{
		return properties == null ? null : properties.lookupObject (key);
	}

	public double getNumericProperty (String key)
	{
		return (properties == null ? 0.0 : properties.lookupNumber (key));
	}

	public boolean hasProperty (String key)
	{
		return (properties == null ? false : properties.hasProperty (key));
	}


	// Serialization of Instance

	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;
	
	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt (CURRENT_SERIAL_VERSION);
		out.writeObject(data);
		out.writeObject(target);
		out.writeObject(name);
		out.writeObject(source);
		out.writeObject(properties);
		out.writeBoolean(locked);
	}
	
	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt ();
		data = in.readObject();
		target = in.readObject();
		name = in.readObject();
		source = in.readObject();
		properties = (PropertyList) in.readObject();
		locked = in.readBoolean();
	}

}
