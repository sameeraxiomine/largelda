/* Copyright (C) 2014 Sameer Wadkar.
This file is an adaptation to the  "MALLET" (MAchine Learning for LanguagE Toolkit)
It is adapted from the "MALLET" (MAchine Learning for LanguagE Toolkit) API  by, 
McCallum, Andrew Kachites-  "MALLET: A Machine Learning for Language Toolkit."
http://mallet.cs.umass.edu. 2002.
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */
package org.bigtextml.topics;


import java.io.Serializable;

import org.bigtextml.types.BigLabelSequence;
import org.bigtextml.types.BigLabeling;
import org.bigtextml.types.Instance;


/** This class combines a sequence of observed features
 *   with a sequence of hidden "labels".
 */

public class TopicAssignment implements Serializable {
	public Instance instance;
	public BigLabelSequence topicSequence;
	public BigLabeling topicDistribution;
                
	public TopicAssignment (Instance instance, BigLabelSequence topicSequence) {
		this.instance = instance;
		this.topicSequence = topicSequence;
	}
}