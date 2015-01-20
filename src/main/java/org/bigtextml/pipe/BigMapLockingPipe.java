/* Copyright (C) 2014 Sameer Wadkar.
This file is an adaptation to the  "MALLET" (MAchine Learning for LanguagE Toolkit)
It is adapted from the "MALLET" (MAchine Learning for LanguagE Toolkit) API  by, 
McCallum, Andrew Kachites-  "MALLET: A Machine Learning for Language Toolkit."
http://mallet.cs.umass.edu. 2002.
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */
package org.bigtextml.pipe;


import org.bigtextml.types.BigAlphabet;
import org.bigtextml.types.BigFeatureSequence;
import org.bigtextml.types.Instance;

import cc.mallet.pipe.Pipe;


import cc.mallet.types.TokenSequence;

public class BigMapLockingPipe extends BigPipe{

	public BigMapLockingPipe (BigAlphabet dataDict)
	{
		super (dataDict, null);
	}

	public BigMapLockingPipe ()
	{
		super(new BigAlphabet(), null);
	}
	
	public Instance pipe (Instance carrier)
	{
		long millis = System.currentTimeMillis();
		carrier.lock();
		//System.out.println(this.getClass().getCanonicalName() + "===" + (System.currentTimeMillis()-millis));
		return carrier;
	}
}
