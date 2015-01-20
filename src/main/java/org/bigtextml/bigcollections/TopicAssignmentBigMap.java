/* Copyright (C) 2014 Sameer Wadkar.
This file is an adaptation to the  "MALLET" (MAchine Learning for LanguagE Toolkit)
It is adapted from the "MALLET" (MAchine Learning for LanguagE Toolkit) API  by, 
McCallum, Andrew Kachites-  "MALLET: A Machine Learning for Language Toolkit."
http://mallet.cs.umass.edu. 2002.
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */
package org.bigtextml.bigcollections;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.bigtextml.topics.TopicAssignment;
import org.bigtextml.types.BigFeatureSequence;
import org.bigtextml.types.BigLabelSequence;

import com.higherfrequencytrading.chronicle.Excerpt;
import com.higherfrequencytrading.chronicle.impl.IndexedChronicle;
import com.higherfrequencytrading.chronicle.tools.ChronicleTools;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
public class TopicAssignmentBigMap extends BigMap<Integer,TopicAssignment> implements Serializable{
	Map<Integer,BigFeatureSequence> tokensByKey = new HashMap<Integer,BigFeatureSequence>();
	Map<Integer,BigLabelSequence> topicSequences = new HashMap<Integer,BigLabelSequence>();
	
	public TopicAssignment put(Integer key, TopicAssignment value) {
		//TopicAssignment ta = super.put(key, value);
		BigFeatureSequence tokens = (BigFeatureSequence) value.instance.getData();
		BigLabelSequence topicSequence =  (BigLabelSequence) value.topicSequence;
		tokensByKey.put(key,tokens);
		topicSequences.put(key,topicSequence);
		
		//int[] topics = topicSequence.getFeatures();
		return value;
	}
	
	public int size(){
		return this.tokensByKey.size();
	}
	public BigFeatureSequence getTokens(Integer key){
		return this.tokensByKey.get(key);
	}
	
	public int[] getTopicSequence(Integer key){
		return this.topicSequences.get(key).getFeatures();
	}
    
	public BigLabelSequence getTopicSequenceObj(Integer key){
		return this.topicSequences.get(key);
	}
	
    private void writeObject(ObjectOutputStream oos)
            throws IOException {
                // default serialization 
                oos.defaultWriteObject();
                // write the object

                //oos.writeObject(loc);
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        
        System.setProperty("InvokedFromSpring","true");
        ois.defaultReadObject();
        super.initialize();
    }
    
}
