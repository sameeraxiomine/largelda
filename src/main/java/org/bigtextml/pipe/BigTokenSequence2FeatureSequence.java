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

public class BigTokenSequence2FeatureSequence extends BigPipe{

	public BigTokenSequence2FeatureSequence (BigAlphabet dataDict)
	{
		super (dataDict, null);
	}

	public BigTokenSequence2FeatureSequence ()
	{
		super(new BigAlphabet(), null);
	}
	
	public Instance pipe (Instance carrier)
	{
		
		TokenSequence ts = (TokenSequence) carrier.getData();
		

		BigFeatureSequence ret =
			new BigFeatureSequence ((BigAlphabet)getDataAlphabet(), ts.size());
		long millis = System.currentTimeMillis();
		for (int i = 0; i < ts.size(); i++) {
			ret.add (ts.get(i).getText());
		}
		//System.out.println(this.getClass().getCanonicalName() + "x===x" + (System.currentTimeMillis()-millis));
		carrier.setData(ret);
		//carrier.lock();
		return carrier;
	}
}
