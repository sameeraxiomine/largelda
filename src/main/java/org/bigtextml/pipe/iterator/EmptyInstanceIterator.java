/* Copyright (C) 2014 Sameer Wadkar.
This file is an adaptation to the  "MALLET" (MAchine Learning for LanguagE Toolkit)
It is adapted from the "MALLET" (MAchine Learning for LanguagE Toolkit) API  by, 
McCallum, Andrew Kachites-  "MALLET: A Machine Learning for Language Toolkit."
http://mallet.cs.umass.edu. 2002.
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */
package org.bigtextml.pipe.iterator;


import java.util.Iterator;

import org.bigtextml.types.Instance;

public class EmptyInstanceIterator implements Iterator<Instance> {

	public boolean hasNext() { return false; }
	public Instance next () { throw new IllegalStateException ("This iterator never has any instances.");	}
	public void remove () { throw new IllegalStateException ("This iterator does not support remove().");	}
}
