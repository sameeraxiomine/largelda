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
import cc.mallet.types.InstanceList;
import cc.mallet.util.*;
import cc.mallet.pipe.iterator.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.*;
import java.io.*;

import org.bigtextml.bigcollections.BigEntries;
import org.bigtextml.bigtm.Utils;
import org.bigtextml.management.ManagementServices;
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
import org.bigtextml.types.Instance;


public class ParallelTopicModelClientBak {
	private static java.util.Date dt =new Date(System.currentTimeMillis());
	//private String workingDir=System.getProperty("java.io.tmpdir");
	private IWorkingDirectory workingDirectory = null;
	private String stopListFilePath="";
	private String modelTrainingDataPath=null;
	private int threadCnt=5;
	private int threadPoolSize=20;
	private int iterationCnt=2000;
	
	private int topicCnt=20;
	private float alpha = 1.0f;
	private float beta = 0.1f;
	private int noOfIterations = 2000;	
	
	
	private int noOfWordsPerTopic=200;
	
	private int printEveryNIterations = 10;
	
	private double probThreshold = 0.01;
	
	private String topicModelPath = null;
	
	private String modelTestingDataPath = null;
	
	public String getTopicModelPath() {
        return topicModelPath;
    }


    public void setTopicModelPath(String topicModelPath) {
        this.topicModelPath = topicModelPath;
    }


    public IWorkingDirectory getWorkingDirectory() {
		return workingDirectory;
	}


	public void setWorkingDirectory(IWorkingDirectory workingDirectory) {
		this.workingDirectory = workingDirectory;
		
	}


	public double getProbThreshold() {
		return probThreshold;
	}




	public void setProbThreshold(double probThreshold) {
		this.probThreshold = probThreshold;
	}




	public int getPrintEveryNIterations() {
		return printEveryNIterations;
	}




	public void setPrintEveryNIterations(int printEveryNIterations) {
		this.printEveryNIterations = printEveryNIterations;
	}



	public String getModelTestingDataPath() {
        return modelTestingDataPath;
    }


    public void setModelTestingDataPath(String modelTestingDataPath) {
        this.modelTestingDataPath = modelTestingDataPath;
    }


    /*
	public String getWorkingDir() {
		return workingDir;
	}


	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		this.workingDir= this.workingDir+"/"+sdf.format(dt)+"/";
		File wDir = new File(this.workingDir);
		if(!wDir.exists()){
			boolean success = wDir.mkdirs();
			if(!success){				
				System.out.println("Cannot create working directory " + wDir.getAbsolutePath());
				throw new RuntimeException("Cannot Create Working Directory");
			}
		}
		System.out.println("Working Directory Created ====" + this.workingDir);
	}
*/
	public String getModelTrainingDataPath() {
		return modelTrainingDataPath;
	}


	public void setModelTrainingDataPath(String inputPath) {
		this.modelTrainingDataPath = inputPath;
	}


	public int getThreadCnt() {
		return threadCnt;
	}


	public void setThreadCnt(int threadCnt) {
		this.threadCnt = threadCnt;
	}




	public int getThreadPoolSize() {
		return threadPoolSize;
	}




	public void setThreadPoolSize(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}




	public int getIterationCnt() {
		return iterationCnt;
	}




	public void setIterationCnt(int iterationCnt) {
		this.iterationCnt = iterationCnt;
	}




	public int getTopicCnt() {
		return topicCnt;
	}




	public void setTopicCnt(int topicCnt) {
		this.topicCnt = topicCnt;
	}




	public float getAlpha() {
		return alpha;
	}




	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}




	public float getBeta() {
		return beta;
	}




	public void setBeta(float beta) {
		this.beta = beta;
	}




	public int getNoOfIterations() {
		return noOfIterations;
	}




	public void setNoOfIterations(int noOfIterations) {
		this.noOfIterations = noOfIterations;
	}




	public int getNoOfWordsPerTopic() {
		return noOfWordsPerTopic;
	}




	public void setNoOfWordsPerTopic(int noOfWordsPerTopic) {
		this.noOfWordsPerTopic = noOfWordsPerTopic;
	}




	public String getStopListFilePath() {
		return stopListFilePath;
	}




	public void setStopListFilePath(String stopListFilePath) {
		this.stopListFilePath = stopListFilePath;
	}




	public static void main(String[] args) throws Exception {
		
		ParallelTopicModelClientBak client = (ParallelTopicModelClientBak)ManagementServices.getBean("ParallelTopicModelClient");
		
		//System.out.println(client.getThreadCnt());	
		//System.out.println(client.getAlpha());	
		//System.out.println(client.getInputPath());
		//File workingDir = ManagementServices.generateWorkingDir(client.getWorkingDir());
		ParallelTopicModel model = new ParallelTopicModel(client.getTopicCnt(), client.getAlpha(), client.getBeta());
		model.setOutDir(new File(client.getWorkingDirectory().getDirectory()));
		model.setNumIterations(client.getIterationCnt());
		model.setNumThreads(client.getThreadCnt());	
		model.setThreadPool(client.getThreadPoolSize());
		model.setNoOfWordsPerTopic(client.getNoOfWordsPerTopic());
		model.setPrintEveryNIterations(client.getPrintEveryNIterations());
		model.setProbThreshold(client.getProbThreshold());
		
		ArrayList<BigPipe> pipeList = new ArrayList<BigPipe>();
		File f = new File(client.getModelTrainingDataPath());
	
		pipeList.add( new BigCharSequenceLowerCase() );
		pipeList.add( new BigCharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
		pipeList.add( new BigTokenSequenceRemoveStopwords(new File(client.getStopListFilePath()), "UTF-8", false, false, false) );
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
		
		BigAlphabet dataAlphabet = instances.getDataAlphabet();
		
		//BigFeatureSequence tokens = (BigFeatureSequence) ((TopicAssignment)(model.getData().get(0))).instance.getData();
		//BigLabelSequence topics = ((TopicAssignment)(model.getData().get(0))).topicSequence;
		
		BigFeatureSequence tokens = model.getData().getTokens(0);
		BigLabelSequence topics = model.getData().getTopicSequenceObj(0);
		
		Formatter out = new Formatter(new StringBuilder(), Locale.US);
		for (int position = 0; position < tokens.getLength(); position++) {
			out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
		}
		System.out.println(out);
		
		// Estimate the topic distribution of the first instance, 
		//  given the current Gibbs state.
		
		//BigInstanceList testing = new BigInstanceList(instances.getPipe());
		StringBuilder topicZeroText = new StringBuilder();
		//topicZeroText.append("The invention provides an improved collapsible tent and tent frame of the umbrella type. The frame includes a plurality of legs pivoted, at the upper ends of the legs, to an upper clevis member. Each leg has pivoted thereto a radial stay member spaced downwardly from the upper end of the leg. The stay member extends inwardly of the tent to be pivoted to a lower clevis. The upper clevis includes a central downwardly opening recess and the lower clevis has fixed thereto an upperwardly projection post or rod which is adapted to engage the upper clevis so as to stop movement of the lower clevis. Each leg comprises an elongated lower section which can be relatively stiff. The upper end of the lower section is pivoted to an upper section which is relatively flexible and resilient so that the upper portion of the legs can conform to the dome of the sheet material forming the tent cover or ceiling.");
		topicZeroText.append("Dittler Brothers called for investigations by the attorneys general of 24 states in connection with possible violation of state lottery laws by Bally Manufacturing Corp and its subsidiary, Scientific Games. Dittler Brothers said it requested the states' law enforcement chiefs to investigate the companies following last week's determination by a court-appointed auditor that Scientific furnished erroneous information to those 24 state lottery officials. The states named are spread throughout the country");
		System.out.println(model.getData().size());
		BigInstanceList testing = new BigInstanceList(instances.getPipe());
		testing.setTopicAlphabet(model.getTopicAlphabet());
		testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));
		//instances.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));
		System.out.println(model.getData().size());
		BigTopicInferencer inferencer = model.getInferencer();
		
		double[] testProbabilities = inferencer.getSampledDistribution(model.getData().getTokens(model.getData().size()-1), 500, 10, 50);
		System.out.print(client.getTopicCnt());
		for(int i=0;i<client.getTopicCnt();i++){
			System.out.print("\t" + testProbabilities[i]);
		}
		System.out.println();
		
		//double[] testProbabilities2 = inferencer.getSampledDistribution(testing.get(0), 500, 10, 50);
		double[] testProbabilities2 = inferencer.getSampledDistribution(testing.getLastTopicAssigment(), 500, 10, 50);
		System.out.print("0");
		for(int i=0;i<client.getTopicCnt();i++){
			System.out.print("\t" + testProbabilities2[i]);
		}
		System.out.println();
		
		
        try{
            //ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("c:/temtmp/pm.ser")));
            System.out.println("Saving model to " + client.getTopicModelPath());
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(client.getTopicModelPath())));
            oos.writeObject(model);
            oos.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        ParallelTopicModel serModel;
        try{
           
            //ObjectInputStream ois = new ObjectInputStream(new FileInputStream( new File("c:/tmp/pm.ser")));
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream( new File(client.getTopicModelPath())));
            serModel = (ParallelTopicModel) ois.readObject();
            ois.close();
               
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }
        BigTopicInferencer inferencer2 = serModel.getInferencer();
        double[] testProbabilities3 = inferencer2.getSampledDistribution(testing.getLastTopicAssigment(), 500, 10, 50);
        System.out.print("From Ser Model");
        for(int i=0;i<client.getTopicCnt();i++){
            System.out.print("\t" + testProbabilities3[i]);
        }
        System.out.println();
        
	}

}