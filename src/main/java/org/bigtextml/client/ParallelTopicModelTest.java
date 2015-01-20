/* Copyright (C) 2014 Sameer Wadkar.
This file is an adaptation to the  "MALLET" (MAchine Learning for LanguagE Toolkit)
It is adapted from the "MALLET" (MAchine Learning for LanguagE Toolkit) API  by, 
McCallum, Andrew Kachites-  "MALLET: A Machine Learning for Language Toolkit."
http://mallet.cs.umass.edu. 2002.
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */
package org.bigtextml.client;


import cc.mallet.types.IDSorter;
import cc.mallet.util.*;
import cc.mallet.pipe.iterator.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.*;
import java.io.*;

import org.bigtextml.bigtm.Utils;
import org.bigtextml.pipe.BigCharSequence2TokenSequence;
import org.bigtextml.pipe.BigCharSequenceLowerCase;
import org.bigtextml.pipe.BigMapLockingPipe;
import org.bigtextml.pipe.BigPipe;
import org.bigtextml.pipe.BigSerialPipes;
import org.bigtextml.pipe.BigTokenSequence2FeatureSequence;
import org.bigtextml.pipe.BigTokenSequenceRemoveStopwords;
import org.bigtextml.pipe.iterator.BigCSVIterator;
import org.bigtextml.topics.BigTopicInferencer;
import org.bigtextml.topics.ParallelTopicModel;
import org.bigtextml.topics.TopicAssignment;
import org.bigtextml.types.BigAlphabet;
import org.bigtextml.types.BigFeatureSequence;
import org.bigtextml.types.BigInstanceList;
import org.bigtextml.types.BigLabelSequence;


public class ParallelTopicModelTest {
	private int noOfTopics=20;
	private float alpha = 1.0f;
	private float beta = 0.1f;
	private int noOfIterations = 2000;
	private String outDir="/tmp/";
	private int threadPool=20;
	private int noOfWordsPerTopic=20;
	private int weightThreshold=100;
	
	public static void main(String[] args) throws Exception {

		
		try{
			System.setProperty("BigMapCachePath","c:/tmp/");
			String fName="C:/Code/mallet/aignore/data/ap.txt";
			
			
			//String fName="C:/MyData/patentabstracts3.txt";
			
			String outDir="C:/tmp5/";
			int noOfTopics=25;
			int noOfThreads=5;
			int noOfThreadsInPool=5;
			int noOfIterations=500;
			int noOfWordsPerTopic=5;
			int weightThreshold;
			System.out.println(fName);

			/*
			if(System.getProperty("NoOfTopics")!=null){
				noOfTopics=Integer.parseInt(System.getProperty("NoOfTopics").trim());
				System.out.println("No of Topics == "+ noOfTopics);
				
			}
			*/
			ParallelTopicModel model = new ParallelTopicModel(noOfTopics, 1.0, 0.01);
			model.setOutDir(new File("C:/tmp/"));
			
			
			//long id = System.currentTimeMillis();
			
			
			// Use two parallel samplers, which each look at one half the corpus and combine
			//  statistics after every iteration.

			
			
			



			// Run the model for 50 iterations and stop (this is for testing only, 
			//  for real applications, use 1000 to 2000 iterations)
			System.out.println("No of Iterations :" + noOfIterations);
			model.setNumIterations(noOfIterations);
			model.setNumThreads(5);	
			model.setThreadPool(5);
			/*
			if(args.length>1){
				outDir = args[1].trim();
				model.setOutDir(outDir);
			}

			
			if(System.getProperty("NoOfThreads")!=null) {	
				noOfThreads =Integer.parseInt(System.getProperty("NoOfThreads").trim());				
				model.setNumThreads(noOfThreads);			
				System.out.println("No of Threads == "+ noOfThreads);
			}
			
			if(System.getProperty("ThreadPoolSize")!=null) {
				noOfThreadsInPool=Integer.parseInt(System.getProperty("ThreadPoolSize").trim());
				model.setThreadPool(noOfThreadsInPool);
				System.out.println("No of Threads == "+ noOfThreadsInPool);
			}
			if(System.getProperty("NoOfIterations")!=null){
				noOfIterations=Integer.parseInt(System.getProperty("NoOfIterations").trim());
				model.setNumIterations(noOfIterations);
				System.out.println("No of Iterations == "+ noOfIterations);
			}
			
			
			if(System.getProperty("NoOfWordsPerTopic")!=null){
				noOfWordsPerTopic = Integer.parseInt(System.getProperty("NoOfWordsPerTopic").trim());
				model.setNoOfWordsPerTopic(noOfWordsPerTopic);
				System.out.println("Max no of Words Per Topic == "+ noOfWordsPerTopic);
			}
			
			if(System.getProperty("WordWeightThreshold")!=null){
				weightThreshold = Integer.parseInt(System.getProperty("WordWeightThreshold").trim());
				model.setWeightThreshold(weightThreshold);
				System.out.println("Word Weight threshold for printing == "+ weightThreshold);
			}
			*/
			// Begin by importing documents from text to feature sequences
			ArrayList<BigPipe> pipeList = new ArrayList<BigPipe>();
			File f = new File(fName.trim());
			
			System.out.println(f.exists());
			System.out.println(f.getAbsolutePath());
			// Pipes: lowercase, tokenize, remove stopwords, map to features
			pipeList.add( new BigCharSequenceLowerCase() );
			pipeList.add( new BigCharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
			pipeList.add( new BigTokenSequenceRemoveStopwords(new File("C:/Users/Sameer/git/Axiomine/AxioInsights2/src/main/resources/stoplists/en.txt"), "UTF-8", false, false, false) );
			pipeList.add( new BigTokenSequence2FeatureSequence() );
			//pipeList.add( new BigMapLockingPipe() );

			BigInstanceList instances = new BigInstanceList (model.getTopicAlphabet(),new BigSerialPipes(pipeList));

			Reader fileReader = new InputStreamReader(new FileInputStream(f), "UTF-8");
			instances.addThruPipe(new BigCSVIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
												   3, 2, 1)); // data, label, name fields

			
			// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
			//  Note that the first parameter is passed as the sum over topics, while
			//  the second is 
			
			System.out.println("Created Instances...");
			model.addInstances(instances);
			System.out.println("Starting to estimate...");
			model.estimate();

			// Show the words and topics in the first instance

			// The data alphabet maps word IDs to strings
			BigAlphabet dataAlphabet = instances.getDataAlphabet();
			
			BigFeatureSequence tokens = (BigFeatureSequence) ((TopicAssignment)(model.getData().get(0))).instance.getData();
			BigLabelSequence topics = ((TopicAssignment)(model.getData().get(0))).topicSequence;
			
			Formatter out = new Formatter(new StringBuilder(), Locale.US);
			for (int position = 0; position < tokens.getLength(); position++) {
				out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
			}
			System.out.println(out);
			
			// Estimate the topic distribution of the first instance, 
			//  given the current Gibbs state.
			double[] topicDistribution = model.getTopicProbabilities(0);

			// Get an array of sorted sets of word ID/count pairs
			ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
			
			// Show top 5 words in topics with proportions for the first document
			for (int topic = 0; topic < noOfTopics; topic++) {
				Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
				
				out = new Formatter(new StringBuilder(), Locale.US);
				out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
				int rank = 0;
				while (iterator.hasNext() && rank < 5) {
					IDSorter idCountPair = iterator.next();
					out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
					rank++;
				}
				System.out.println(out);
			}
			

			

			Map<Integer,SortedMap<Integer,Double>> sims  = Utils.compareTopics(model.getSortedWords());
			
			double thresholdSim=0.15;
			Utils.printSimilarTopics(sims,model.getSortedWords(),dataAlphabet,thresholdSim);
		
			// Create a new instance with high probability of topic 0
			StringBuilder topicZeroText = new StringBuilder();
			Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

			int rank = 0;
			while (iterator.hasNext() && rank < 5) {
				IDSorter idCountPair = iterator.next();
				topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
				rank++;
			}

			// Create a new instance named "test instance" with empty target and source fields.
			/*
			BigInstanceList testing = new BigInstanceList(instances.getPipe());
			BigInstanceList testing2 = new BigInstanceList(topicZeroText.toString(), null, "test instance", null)
			testing.addThruPipe(testing2);

			BigTopicInferencer inferencer = model.getInferencer();
			double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
			System.out.println("0\t" + testProbabilities[0]);
			*/
			//model.printTopicRelatedFile(noOfIterations+1);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
			//CacheManagementServices.cacheManager.shutdown();
		}

		
	}

}