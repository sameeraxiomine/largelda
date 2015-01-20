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

public class ParallelTopicModel2 {
	private String outDir="/tmp/";
	private int noOfParallelProcessors=20;
	private int noOfMaxWordsPerTopic=20;
	private int weightThreshold=30;
	
	public ParallelTopicModel2(){
		
	}
	
	public String getOutDir() {
		return outDir;
	}
	public void setOutDir(String outDir) {
		this.outDir = outDir;
	}
	public int getNoOfParallelProcessors() {
		return noOfParallelProcessors;
	}
	public void setNoOfParallelProcessors(int noOfParallelProcessors) {
		this.noOfParallelProcessors = noOfParallelProcessors;
	}
	public int getNoOfMaxWordsPerTopic() {
		return noOfMaxWordsPerTopic;
	}
	public void setNoOfMaxWordsPerTopic(int noOfMaxWordsPerTopic) {
		this.noOfMaxWordsPerTopic = noOfMaxWordsPerTopic;
	}
	public int getWeightThreshold() {
		return weightThreshold;
	}
	public void setWeightThreshold(int weightThreshold) {
		this.weightThreshold = weightThreshold;
	}
	
	
	
}
