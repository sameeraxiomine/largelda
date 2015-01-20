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


public class BigLabel implements BigLabeling, Serializable, BigAlphabetCarrying
{
	Object entry;
	BigLabelAlphabet dictionary;
	int index;
	
	protected BigLabel ()
	{
		throw new IllegalStateException
			("Label objects can only be created by their Alphabet.");
	}

	/** You should never call this directly.  New Label objects are
			created on-demand by calling LabelAlphabet.lookupIndex(obj). */
	BigLabel (Object entry, BigLabelAlphabet dict, int index)
	{
		this.entry = entry;
		this.dictionary = dict;
		assert (dict.lookupIndex (entry, false) == index);
		this.index = index;
	}

	public BigLabelAlphabet getLabelAlphabet ()
	{
		return (BigLabelAlphabet) dictionary;
	}

	public int getIndex () { return index; }

	public BigAlphabet getAlphabet () { return dictionary; }
	
	public BigAlphabet[] getAlphabets () { return new BigAlphabet[] { dictionary }; }
	
	public Object getEntry () { return entry; }

	public String toString () { return entry.toString(); }


	// Comparable interface

	public int compareTo (Object o)
	{
		BigLabel os = (BigLabel)o;
		if (this.index < os.index)
			return -1;
		else if (this.index == os.index)
			return 0;
		else
			return 1;
	}
	

	// Labeling interface

	public BigLabel getBestLabel ()
	{
		return this;
	}

	public int getBestIndex ()
	{
		return index;
	}

	static final double weightOfLabel = 1.0;

	public double getBestValue ()
	{
		return weightOfLabel;
	}

	public double value (BigLabel label)
	{
		assert (label.dictionary.equals(this.dictionary));
		return weightOfLabel;
	}

	public double value (int labelIndex)
	{
		return labelIndex == this.index ? weightOfLabel : 0;
	}

	public int getRank (BigLabel label)
	{
		assert (label.dictionary.equals(this.dictionary));
		return label == this ? 0 : -1;
	}

	public int getRank (int labelIndex)
	{
		return labelIndex == this.index ? 0 : -1;
	}

	public BigLabel getLabelAtRank (int rank)
	{
		assert (rank == 0);
		return this;
	}

	public double getValueAtRank (int rank)
	{
		assert (rank == 0);
		return weightOfLabel;
	}

	public void addTo (double[] weights)
	{
		weights[this.index] += weightOfLabel;
	}
	
	public void addTo (double[] weights, double scale)
	{
		weights[this.index] += weightOfLabel * scale;
	}


	// The number of non-zero-weight Labels in this Labeling, not total
	// number in the Alphabet
	public int numLocations ()
	{
		return 1;
	}

	public BigLabel labelAtLocation (int loc)
	{
		assert (loc == 0);
		return this;
	}

	public double valueAtLocation (int loc)
	{
		assert (loc == 0);
		return weightOfLabel;
	}

	public int indexAtLocation (int loc)
	{
		assert (loc == 0);
		return index;
	}

	public BigLabelVector toLabelVector ()
	{
		return new BigLabelVector ((BigLabelAlphabet)dictionary,
														new int[] {index}, new double[] {weightOfLabel});
	}

	public boolean equals (Object l) {
		if (l instanceof BigLabel) {
			return ((BigLabel)l).compareTo(this) == 0;
		}
		else throw new IllegalArgumentException ("Cannot compare a Label object with a " + l.getClass().getName() + " object.");
	}

	// Serialization 

	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;

	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt (CURRENT_SERIAL_VERSION);
		out.writeObject (dictionary);
		out.writeInt (index);
	}

	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt ();
		dictionary = (BigLabelAlphabet) in.readObject ();
		index = in.readInt ();
		entry = dictionary.lookupObject (index);
	}


}
