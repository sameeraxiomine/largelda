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

import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.*;
import java.io.*;

import org.bigtextml.bigtm.Utils;



public class TopicModelTest {

	public static void main(String[] args) throws Exception {
		/*
		System.setErr(new PrintStream(new OutputStream() {
		    public void write(int b) {
		    }
		}));
		*/

			
			//String fName="C:/Users/Sameer/Documents/workspace-sts-3.0.0.RELEASE/Mallet/sample-data/sample-data/ap.txt";
			//String fName="C:/MyData/patentabstracts3.txt";
			// fName=args[0].trim();
			String outDir=null;
			int noOfTopics=500;
			int noOfThreads=2;
			int noOfThreadsInPool=2;
			int noOfIterations=50;
			int noOfWordsPerTopic=5;
			int weightThreshold;
			

			
			if(System.getProperty("NoOfTopics")!=null){
				noOfTopics=Integer.parseInt(System.getProperty("NoOfTopics").trim());
				System.out.println("No of Topics == "+ noOfTopics);
				
			}
			ParallelTopicModel model = new ParallelTopicModel(noOfTopics, 1.0, 0.01);
			
			model.setNumThreads(5);
			//long id = System.currentTimeMillis();
			
			
			// Use two parallel samplers, which each look at one half the corpus and combine
			//  statistics after every iteration.

			
			
			



			// Run the model for 50 iterations and stop (this is for testing only, 
			//  for real applications, use 1000 to 2000 iterations)
			System.out.println("No of Iterations :" + noOfIterations);
			model.setNumIterations(noOfIterations);


			if(System.getProperty("NoOfIterations")!=null){
				noOfIterations=Integer.parseInt(System.getProperty("NoOfIterations").trim());
				model.setNumIterations(noOfIterations);
				System.out.println("No of Iterations == "+ noOfIterations);
			}
			
			// Begin by importing documents from text to feature sequences
			ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
			File f = new File("C:/Code/mallet/aignore/data/ap.txt");
			
			System.out.println(f.exists());
			System.out.println(f.getAbsolutePath());
			// Pipes: lowercase, tokenize, remove stopwords, map to features
			pipeList.add( new CharSequenceLowercase() );
			pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
			pipeList.add( new TokenSequenceRemoveStopwords(new File("C:/Code/mallet/stoplists/en.txt"), "UTF-8", false, false, false) );
			pipeList.add( new TokenSequence2FeatureSequence() );

			InstanceList instances = new InstanceList (new SerialPipes(pipeList));

			Reader fileReader = new InputStreamReader(new FileInputStream(f), "UTF-8");
			instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
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
			
	}	
	

}