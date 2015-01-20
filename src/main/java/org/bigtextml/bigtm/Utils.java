/* Copyright (C) 2014 Sameer Wadkar.
This file is an adaptation to the  "MALLET" (MAchine Learning for LanguagE Toolkit)
It is adapted from the "MALLET" (MAchine Learning for LanguagE Toolkit) API  by, 
McCallum, Andrew Kachites-  "MALLET: A Machine Learning for Language Toolkit."
http://mallet.cs.umass.edu. 2002.
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */
package org.bigtextml.bigtm;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bigtextml.types.BigAlphabet;

import cc.mallet.types.IDSorter;

//import org.tartarus.snowball.SnowballStemmer;


public class Utils {
	

	public static double compareTopics(Map<Integer,Double> topic1,Map<Integer,Double> topic2){
		Set<Integer> wordsT1 = topic1.keySet();
		double sum=0;
		for(int w:wordsT1){
			double d1=topic1.get(w);
			if(topic2.containsKey(w)){
				double d2=topic2.get(w);
				sum= Math.abs(Math.log(d1)-Math.log(d2));
				//sum=Math.abs(d1)-Math.abs(d2));
			}
		}
		
		return sum;
	}
	
	public static void printTopicWords(boolean addTab,int topicId,ArrayList<TreeSet<IDSorter>> topicSortedWords,BigAlphabet dataAlphabet,double score,double thresholdSim){
		Iterator<IDSorter> iterator = topicSortedWords.get(topicId).iterator();		
		Formatter out = new Formatter(new StringBuilder(), Locale.US);
		out = new Formatter(new StringBuilder(), Locale.US);		
		int rank = 0;
		while (iterator.hasNext() && rank < 10) {
			IDSorter idCountPair = iterator.next();
			out.format("%s ", dataAlphabet.lookupObject(idCountPair.getID()));
			rank++;
		}
		if(addTab){
			if(score<=thresholdSim)
				System.out.println("\t TopicId=" +topicId+ ",Score="+ score+ "-"+out);
		}
		else
			System.out.println("MainTopicId=" +topicId+ "       "+ out);
	}
	
	public static Map<Integer,SortedMap<Integer,Double>> compareTopics(ArrayList<TreeSet<IDSorter>> topicSortedWords){
		
		Map<Integer,Double> normalizingSumByTopic = new HashMap<Integer,Double>();
		Map<Integer,Map<Integer,Double>> wordsByTopic = new HashMap<Integer,Map<Integer,Double>>();
		int noOfTopics=topicSortedWords.size();
		for (int topic = 0; topic < noOfTopics; topic++) {
			Map<Integer,Double> wByTopic = new HashMap<Integer,Double>();
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();	
			
			double sum=0.0;
			int rank = 0;
			while (iterator.hasNext() && rank < 100) {
				IDSorter idCountPair = iterator.next();
				if(idCountPair.getWeight()>0)
					sum= sum+(Math.log(idCountPair.getWeight())*Math.log(idCountPair.getWeight()));
				rank++;
			}
			
			normalizingSumByTopic.put(topic, sum);
			iterator = topicSortedWords.get(topic).iterator();			
			rank = 0;
			while (iterator.hasNext() && rank < 20) {
				IDSorter idCountPair = iterator.next();
				if(idCountPair.getWeight()>0)
					//wByTopic.put(idCountPair.getID(),Math.log(idCountPair.getWeight()));
					wByTopic.put(idCountPair.getID(),idCountPair.getWeight());
				rank++;
			}
			
			wordsByTopic.put(topic, wByTopic);
		}
		//Normalized weights are caculated here
		
		Map<Integer,SortedMap<Integer,Double>> simScores = new HashMap<Integer,SortedMap<Integer,Double>>();
		
		for (int topic = 0; topic < noOfTopics; topic++) {	
			SortedMap<Integer,Double> scoresForTopic = new TreeMap<Integer,Double>();
			simScores.put(topic, scoresForTopic);
			for(int topicInner=0;topicInner<noOfTopics;topicInner++){				
				double score=compareTopics(wordsByTopic.get(topic),wordsByTopic.get(topicInner));
				scoresForTopic.put(topicInner,score);
			}
		}
		return simScores;
	}
	
	public static void printSimilarTopics(Map<Integer,SortedMap<Integer,Double>> sims,ArrayList<TreeSet<IDSorter>> topicSortedWords,BigAlphabet dataAlphabet,double thresholdSim){
		Set<Integer> keys = sims.keySet();
		for(int topic:keys){
			printTopicWords(false,topic,topicSortedWords,dataAlphabet,2.0,0);
			TreeMap<Integer,Double> relTopics =(TreeMap<Integer,Double>) sims.get(topic);
			NavigableSet<Integer> keysForTopic= relTopics.descendingKeySet();
			for(int tp1:keysForTopic){
				if(relTopics.get(tp1)>0)
					printTopicWords(true,tp1,topicSortedWords,dataAlphabet,relTopics.get(tp1),thresholdSim);
			 }
		}
	}
}
