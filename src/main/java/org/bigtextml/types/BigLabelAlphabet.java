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


import java.util.ArrayList;

import java.io.*;


/**
		A mapping from arbitrary objects (usually String's) to integers
		(and corresponding Label objects) and back.

@author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
*/
public class BigLabelAlphabet extends BigAlphabet implements Serializable
{
	ArrayList labels;
		
	public BigLabelAlphabet ()
	{
		super();
		this.labels = new ArrayList ();
	}

	public int lookupIndex (Object entry, boolean addIfNotPresent)
	{
		int index = super.lookupIndex (entry, addIfNotPresent);
		if (index >= labels.size() && addIfNotPresent)
			labels.add (new BigLabel(entry, this, index));
		return index;
	}

	public BigLabel lookupLabel (Object entry, boolean addIfNotPresent)
	{
		int index = lookupIndex (entry, addIfNotPresent);
		if (index >= 0)
			return (BigLabel) labels.get(index);
		else
			return null;
	}
		
	public BigLabel lookupLabel (Object entry)
	{
		return this.lookupLabel (entry, true);
	}

	public BigLabel lookupLabel (int labelIndex)
	{
		return (BigLabel) labels.get(labelIndex);
	}
		
}
