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


import java.io.*;




public class BigLabelSequence extends BigFeatureSequence implements BigAlphabetCarrying, Serializable
{
	public BigLabelSequence (BigLabelAlphabet dict, int[] features)
	{
		super (dict, features);
	}
	
	public BigLabelSequence (BigLabelAlphabet dict, int capacity)
	{
		super (dict, capacity);
	}

	private static int[] getFeaturesFromLabels (BigLabel[] labels)
	{
		int[] features = new int[labels.length];
		for (int i = 0; i < labels.length; i++)
			features[i] = labels[i].getIndex();
		return features;
	}
	
	public BigLabelSequence (BigLabel[] labels)
	{
		super (labels[0].getLabelAlphabet(), getFeaturesFromLabels (labels));
	}

	public BigLabelSequence (BigAlphabet dict)
	{
		super (dict);
	}
	
	public BigLabelAlphabet getLabelAlphabet ()	{	return (BigLabelAlphabet) dictionary; }

	public BigLabel getLabelAtPosition (int pos)
	{
		return ((BigLabelAlphabet)dictionary).lookupLabel (features[pos]);
	}

	public class Iterator implements java.util.Iterator {
		int pos;
		public Iterator () {
			pos = 0;
		}
		public Object next() {
			return getLabelAtPosition(pos++);
		}
		public int getIndex () {
			return pos;
		}
		public boolean hasNext() {
			return pos < features.length;
		}
		public void remove () {
			throw new UnsupportedOperationException ();
		}
	}
	
	public Iterator iterator ()
	{
		return new Iterator();
	}

	
	
	// ???
	//public Object get (int pos) {	return getLabelAtPosition (pos); }


	// Serialization 
	
	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;
	
	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt(CURRENT_SERIAL_VERSION);
	}
	
	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt ();
	}



	
}
