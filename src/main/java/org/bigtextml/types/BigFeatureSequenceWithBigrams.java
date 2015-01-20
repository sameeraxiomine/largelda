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


import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


import cc.mallet.types.Token;

/** A FeatureSequence with a parallel record of bigrams, kept in a separate dictionary
 *  @author <a href="mailto:mccallum@cs.umass.edu">Andrew McCallum</a>
 */

public class BigFeatureSequenceWithBigrams extends BigFeatureSequence
{
	public final static String deletionMark = "NextTokenDeleted";
	BigAlphabet biDictionary;
	int[] biFeatures;

	public BigFeatureSequenceWithBigrams (BigAlphabet dict, BigAlphabet bigramDictionary, BigTokenSequence ts)
	{
		super (dict, ts.size());
		int len = ts.size();
		this.biDictionary = bigramDictionary;
		this.biFeatures = new int[len];
		Token t, pt = null;
		for (int i = 0; i < len; i++) {
			t = ts.get(i);
			super.add(t.getText());
			if (pt != null && pt.getProperty(deletionMark) == null)
				biFeatures[i] = biDictionary == null ? 0 : biDictionary.lookupIndex(pt.getText()+"_"+t.getText(), true);
			else
				biFeatures[i] = -1;
			pt = t;
		}
	}

	public BigAlphabet getBiAlphabet ()	{	return biDictionary; }

	public final int getBiIndexAtPosition (int pos)
	{
		return biFeatures[pos];
	}

	public Object getObjectAtPosition (int pos)
	{
		return biFeatures[pos] == -1 ? null : (biDictionary == null ? null : biDictionary.lookupObject (biFeatures[pos]));
	}

	// Serialization

	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;
	private static final int NULL_INTEGER = -1;

	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt (CURRENT_SERIAL_VERSION);
		out.writeObject (biDictionary);
		out.writeInt (biFeatures.length);
		for (int i = 0; i < biFeatures.length; i++)
			out.writeInt (biFeatures[i]);
	}

	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt ();
		biDictionary = (BigAlphabet) in.readObject ();
		int featuresLength = in.readInt();
		biFeatures = new int[featuresLength];
		for (int i = 0; i < featuresLength; i++)
			biFeatures[i] = in.readInt ();
	}


}
