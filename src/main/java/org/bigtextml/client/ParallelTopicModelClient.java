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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import org.bigtextml.management.ManagementServices;
import org.bigtextml.pipe.BigCharSequence2TokenSequence;
import org.bigtextml.pipe.BigCharSequenceLowerCase;
import org.bigtextml.pipe.BigPipe;
import org.bigtextml.pipe.BigSerialPipes;
import org.bigtextml.pipe.BigTokenSequence2FeatureSequence;
import org.bigtextml.pipe.BigTokenSequenceRemoveStopwords;
import org.bigtextml.pipe.iterator.BigCSVIterator;
import org.bigtextml.topics.BigTopicInferencer;
import org.bigtextml.topics.ParallelTopicModel;
import org.bigtextml.types.BigInstanceList;
import org.bigtextml.types.Instance;


public class ParallelTopicModelClient {
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
	
	private String modelTestingOutPath = null;
	
	
	
	public String getModelTestingOutPath() {
        return modelTestingOutPath;
    }


    public void setModelTestingOutPath(String modelTestingOutPath) {
        this.modelTestingOutPath = modelTestingOutPath;
    }


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
		String action = args[0];
        ParallelTopicModelClient client = (ParallelTopicModelClient)ManagementServices.getBean("ParallelTopicModelClient");
        
        ParallelTopicModel model = new ParallelTopicModel(client.getTopicCnt(), client.getAlpha(), client.getBeta());
        model.setOutDir(new File(client.getWorkingDirectory().getDirectory()));
        model.setNumIterations(client.getIterationCnt());
        model.setNumThreads(client.getThreadCnt()); 
        model.setThreadPool(client.getThreadPoolSize());
        model.setNoOfWordsPerTopic(client.getNoOfWordsPerTopic());
        model.setPrintEveryNIterations(client.getPrintEveryNIterations());
        model.setProbThreshold(client.getProbThreshold());
        ArrayList<BigPipe> pipeList = new ArrayList<BigPipe>(); pipeList.add( new BigCharSequenceLowerCase() );
        pipeList.add( new BigCharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new BigTokenSequenceRemoveStopwords(new File(client.getStopListFilePath()), "UTF-8", false, false, false) );
        pipeList.add( new BigTokenSequence2FeatureSequence() );
		
		if(action.equalsIgnoreCase("createmodel")){
	        BigInstanceList instances = new BigInstanceList (model.getTopicAlphabet(),new BigSerialPipes(pipeList));
		    File f = new File(client.getModelTrainingDataPath());
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
	        try{
	            System.out.println("Saving model to " + client.getTopicModelPath());
	            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(client.getTopicModelPath())));
	            oos.writeObject(model);
	            oos.close();
	        }
	        catch(Exception ex){
	            ex.printStackTrace();
	            throw new RuntimeException(ex);
	        }
		}
		else if(action.equalsIgnoreCase("usemodel")){
	        ParallelTopicModel serModel;
	        BigInstanceList testingInstances=null;
	        try{
	            ObjectInputStream ois = new ObjectInputStream(new FileInputStream( new File(client.getTopicModelPath())));
	            serModel = (ParallelTopicModel) ois.readObject();
	            testingInstances= new BigInstanceList(new BigSerialPipes(pipeList));
	            testingInstances.setTopicAlphabet(serModel.getTopicAlphabet());
	            List<String> lines = org.apache.commons.io.FileUtils.readLines(new File(client.getModelTestingDataPath()));
	            for(String l:lines){
	                l = l.trim();
	                if(l.length()>0){
	                    String[] comps = l.split("\\t");
	                    testingInstances.addThruPipe(new Instance(comps[1], null, comps[0], null));
	                }
	            }            
	            ois.close();
	        }
	        catch(Exception ex){
	            ex.printStackTrace();
	            throw new RuntimeException(ex);
	        }
	        
	        BigTopicInferencer testingInferencer = serModel.getInferencer();
	        int noOfTestingInstances = testingInstances.size();
	        List<String> outLines = new ArrayList<String>();
	        for(int i=0;i<noOfTestingInstances;i++){
	            Object name = testingInstances.get(i).getName();
	            StringBuilder out=new StringBuilder(name.toString());
	            double[] testProbs = testingInferencer.getSampledDistribution(testingInstances.getTopicAssigment(i), 500, 10, 50);
	            for(int j=0;j<client.getTopicCnt();j++){
	                if(testProbs[j]>=client.getProbThreshold()){
	                    out.append(",").append(j).append(",").append(testProbs[j]);
	                }                
	                //System.out.print("\t" + testProbs[j]);
	            }
	            outLines.add(out.toString());
	            //System.out.println();
	        }
	        org.apache.commons.io.FileUtils.writeLines(new File(client.getModelTestingOutPath()), outLines);
		}
	}
}
