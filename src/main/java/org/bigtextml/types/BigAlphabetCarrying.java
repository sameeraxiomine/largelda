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

import org.bigtextml.types.BigAlphabet;

/** An interface for objects that contain one or more Alphabets.
 * <p>  
 * The primary kind of type checking among MALLET objects such as Instances, InstanceLists, Classifiers, etc is
 * by checking that their Alphabets match. */
public interface BigAlphabetCarrying {
	BigAlphabet getAlphabet();
	BigAlphabet[] getAlphabets();
	//boolean alphabetsMatch (AlphabetCarrying object);  //Now you should simply call the static method Alphabet.alphabetsMatch().
}