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


import java.util.Iterator;


public class BigSingleInstanceIterator implements Iterator<Instance> {
	
	Instance nextInstance;
	boolean doesHaveNext;
	
	public BigSingleInstanceIterator (Instance inst) {
		nextInstance = inst;
		doesHaveNext = true;
	}

	public boolean hasNext() {
		return doesHaveNext;
	}

	public Instance next() {
		doesHaveNext = false;
		return nextInstance;
	}
	
	public void remove () { throw new IllegalStateException ("This iterator does not support remove().");	}

}
