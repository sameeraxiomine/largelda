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




/** A distribution over possible labels for an instance. */

public interface BigLabeling extends BigAlphabetCarrying
{
	public BigLabelAlphabet getLabelAlphabet ();
	
	public BigLabel getBestLabel ();
	public double getBestValue ();
	public int getBestIndex ();

	public double value (BigLabel label);
	public double value (int labelIndex);

	// Zero-based
	public int getRank (BigLabel label);
	public int getRank (int labelIndex);
	public BigLabel getLabelAtRank (int rank);
	public double getValueAtRank (int rank);

	public void addTo (double[] values);
	public void addTo (double[] values, double scale);

	// The number of non-zero-weight Labels in this Labeling, not total
	// number in the Alphabet
	public int numLocations ();
	// xxx Use "get..."? 
	public int indexAtLocation (int pos);
	public BigLabel labelAtLocation (int pos);
	public double valueAtLocation (int pos);

	public BigLabelVector toLabelVector();
	
}
