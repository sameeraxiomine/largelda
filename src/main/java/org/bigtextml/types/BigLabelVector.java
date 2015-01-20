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




public class BigLabelVector extends BigRankedFeatureVector implements BigLabeling
{
	public BigLabelVector (BigLabelAlphabet dict,
											int[] features,
											double[] values)
	{
		super (dict, features, values);
	}

	private static int[] indicesForLabels (BigLabel[] labels)
	{
		int[] indices = new int[labels.length];
		for (int i = 0; i < labels.length; i++)
			indices[i] = labels[i].getIndex();
		return indices;
	}

	public BigLabelVector (BigLabel[] labels,
											double[] values)
	{
		super (labels[0].dictionary, indicesForLabels(labels), values);
	}

	public BigLabelVector (BigLabelAlphabet dict, double[] values)
	{
		super (dict, values);
	}

	public final BigLabel labelAtLocation (int loc)
	{
		return ((BigLabelAlphabet)dictionary).lookupLabel(indexAtLocation (loc));
	}

	public BigLabelAlphabet getLabelAlphabet ()
	{
		return (BigLabelAlphabet) dictionary;
	}


	// Labeling interface

	// xxx Change these names to better match RankedFeatureVector?

	public int getBestIndex ()
	{
		if (rankOrder == null)
			setRankOrder ();
		return rankOrder[0];
	}

	public BigLabel getBestLabel ()
	{
		return ((BigLabelAlphabet)dictionary).lookupLabel (getBestIndex());
	}

	public double getBestValue ()
	{
		if (rankOrder == null)
			setRankOrder ();
		return values[rankOrder[0]];
	}

	public double value (BigLabel label)
	{
		assert (label.dictionary  == this.dictionary);
		return values[this.location (label.toString ())];
	}

	public int getRank (BigLabel label)
	{

		//throw new UnsupportedOperationException ();
     // CPAL - Implemented this
     
     if (rankOrder == null)
         setRankOrder();

     int ii=-1;
     int tmpIndex = ((BigLabelAlphabet)dictionary).lookupIndex(label.entry);
     // Now find this index in the ordered list with a linear search
     for(ii=0; ii<rankOrder.length ; ii++) {
         if (rankOrder[ii] == tmpIndex)
            break;
     }

     // CPAL if ii == -1 we have a problem
     
     return ii;
	}

	public int getRank (int labelIndex)
	{
		return getRank(((BigLabelAlphabet)dictionary).lookupLabel(labelIndex));
	}

	public BigLabel getLabelAtRank (int rank)
	{
		if (rankOrder == null)
			setRankOrder ();
		return ((BigLabelAlphabet)dictionary).lookupLabel (rankOrder[rank]);
	}

	public double getValueAtRank (int rank)
	{
		if (rankOrder == null)
			setRankOrder ();
		return values[rankOrder[rank]];
	}

	public BigLabelVector toLabelVector ()
	{
		return this;
	}


	// Inherited from FeatureVector or SparseVector
	// public void addTo (double[] values)
	// public void addTo (double[] values, double scale)
	// public int numLocations ();
	// public double valueAtLocation (int loc)

	
}
