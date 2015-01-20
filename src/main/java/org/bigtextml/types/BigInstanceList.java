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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;







import org.bigtextml.bigcollections.TopicAssignmentBigMap;
import org.bigtextml.management.ManagementServices;
import org.bigtextml.pipe.BigPipe;
import org.bigtextml.pipe.BigSerialPipes;
import org.bigtextml.pipe.Noop;
import org.bigtextml.topics.TopicAssignment;
import org.bigtextml.types.BigAlphabetCarrying;

import cc.mallet.pipe.FeatureSequence2FeatureVector;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.RandomTokenSequenceIterator;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Dirichlet;
import cc.mallet.types.MatrixOps;
import cc.mallet.util.MalletLogger;
import cc.mallet.util.Randoms;

/**
	 A list of machine learning instances, typically used for training
	 or testing of a machine learning algorithm.
   <p>
	 All of the instances in the list will have been passed through the
	 same {@link cc.mallet.pipe.Pipe}, and thus must also share the same data and target Alphabets.
   InstanceList keeps a reference to the pipe and the two alphabets.
   <p>
   The most common way of adding instances to an InstanceList is through
   the <code>add(PipeInputIterator)</code> method. PipeInputIterators are a way of mapping general
   data sources into instances suitable for processing through a pipe.
     As each {@link cc.mallet.types.Instance} is pulled from the PipeInputIterator, the InstanceList
     copies the instance and runs the copy through its pipe (with resultant
     destructive modifications) before saving the modified instance on its list.
     This is the  usual way in which instances are transformed by pipes.
     <p>
     InstanceList also contains methods for randomly generating lists of
     feature vectors; splitting lists into non-overlapping subsets (useful
     for test/train splits), and iterators for cross validation.

   @see Instance
   @see Pipe

   @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */

public class BigInstanceList extends ArrayList<Instance> implements Serializable, Iterable<Instance>, BigAlphabetCarrying
{
	private static Logger logger = MalletLogger.getLogger(BigInstanceList.class.getName());

	HashMap<Instance, Double> instWeights = null;
  // This should never be set by a ClassifierTrainer, it should be used in conjunction with a Classifier's FeatureSelection
	// Or perhaps it should be removed from here, and there should be a ClassifierTrainer.train(InstanceList, FeatureSelection) method.
	BigFeatureSelection featureSelection = null;  
	BigFeatureSelection[] perLabelFeatureSelection = null;
	BigPipe pipe;
	public static BigAlphabet dataAlphabet;
	public static BigAlphabet targetAlphabet;
	//private  Map<Integer,Integer> typeTotals =  (Map<Integer,Integer>) ManagementServices.getBigMap("TypeCounts");
	private  Map<Integer,Integer> typeTotals =  new HashMap<Integer,Integer>();
	TopicAssignmentBigMap topicAssignments = (TopicAssignmentBigMap)ManagementServices.getBigMap("TopicAssignment");
	Class dataClass = null;
	Class targetClass = null;
	private BigLabelAlphabet topicAlphabet =null;

	/**
	 * Construct an InstanceList having given capacity, with given default pipe.
	 * Typically Instances added to this InstanceList will have gone through the 
	 * pipe (for example using instanceList.addThruPipe); but this is not required.
	 * This InstanaceList will obtain its dataAlphabet and targetAlphabet from the pipe.
	 * It is required that all Instances in this InstanceList share these Alphabets. 
	 * @param pipe The default pipe used to process instances added via the addThruPipe methods.
	 * @param capacity The initial capacity of the list; will grow further as necessary.
	 */
	// XXX not very useful, should perhaps be removed
	public BigInstanceList (BigPipe pipe, int capacity)
	{
		super(capacity);
		this.pipe = pipe;
	}

	/**
	 * Construct an InstanceList with initial capacity of 10, with given default pipe.
	 * Typically Instances added to this InstanceList will have gone through the 
	 * pipe (for example using instanceList.addThruPipe); but this is not required.
	 * This InstanaceList will obtain its dataAlphabet and targetAlphabet from the pipe.
	 * It is required that all Instances in this InstanceList share these Alphabets. 
	 * @param pipe The default pipe used to process instances added via the addThruPipe methods.
	 */
	public BigInstanceList (BigPipe pipe)
	{
		this (pipe, 10);
	}
	

	public BigInstanceList (BigLabelAlphabet topicAlphabet,BigPipe pipe)
	{
		this (pipe, 10);
		this.topicAlphabet=topicAlphabet;
	}

	/** 
	 * Construct an InstanceList with initial capacity of 10, with a Noop default pipe.
	 * Used in those infrequent circumstances when Instances typically would not have further
	 * processing,  and objects containing vocabularies are entered
	 * directly into the <code>InstanceList</code>; for example, the creation of a
	 * random <code>InstanceList</code> using <code>Dirichlet</code>s and
	 * <code>Multinomial</code>s.</p>
	 *
	 * @param dataAlphabet The vocabulary for added instances' data fields
	 * @param targetAlphabet The vocabulary for added instances' targets
	 */
	public BigInstanceList (BigAlphabet dataAlphabet, BigAlphabet targetAlphabet)
	{
		this (new Noop(dataAlphabet, targetAlphabet), 10);
		this.dataAlphabet = dataAlphabet;
		this.targetAlphabet = targetAlphabet;
	}

	private static class NotYetSetPipe extends BigPipe	{
		public Instance pipe (Instance carrier)	{
			throw new UnsupportedOperationException (
					"The InstanceList has yet to have its pipe set; "+
			"this could happen by calling InstanceList.add(InstanceList)");
		}
		public Object readResolve () throws ObjectStreamException	{
			return notYetSetPipe;
		}
		private static final long serialVersionUID = 1;
	}
	static final BigPipe notYetSetPipe = new NotYetSetPipe();

	/** Creates a list that will have its pipe set later when its first Instance is added. */
	@Deprecated // Pipe is never set if you use this constructor 
	public BigInstanceList ()
	{
		this (notYetSetPipe);
	}

	
	

	/** Adds to this list every instance generated by the iterator,
	 * passing each one through this InstanceList's pipe. */
	// TODO This method should be renamed addPiped(Iterator<Instance> ii)
	public void addThruPipe (Iterator<Instance> ii)
	{
		//for debug
		Iterator<Instance> pipedInstanceIterator = pipe.newIteratorFrom(ii);
		while (pipedInstanceIterator.hasNext())
		{	
			add (pipedInstanceIterator.next());
		    //System.out.println("Add instance " + pipedInstanceIterator.next().getName());
		}
	}
	
	// gsc: method to add one instance at a time 
	/** Adds the input instance to this list, after passing it through the
	 * InstanceList's pipe.
	 * <p>
	 * If several instances are to be added then accumulate them in a List\<Instance\>
	 * and use <tt>addThruPipe(Iterator<Instance>)</tt> instead.
	 */
	public void addThruPipe(Instance inst)
	{
	  addThruPipe(new BigSingleInstanceIterator(inst));
	}


	/** Appends the instance to this list without passing the instance through
	 * the InstanceList's pipe.  
	 * The alphabets of this Instance must match the alphabets of this InstanceList.
	 * @return <code>true</code>
	 */
	public boolean add (Instance instance)
	{
		if (dataAlphabet == null)
			dataAlphabet = instance.getDataAlphabet();
		if (targetAlphabet == null)
			targetAlphabet = instance.getTargetAlphabet();
		if (!BigAlphabet.alphabetsMatch(this, instance)) {
		      // gsc
		      BigAlphabet data_alphabet = instance.getDataAlphabet();
		      BigAlphabet target_alphabet = instance.getTargetAlphabet();
		      StringBuilder sb = new StringBuilder();
		      sb.append("Alphabets don't match: ");
		      sb.append("Instance: [" + (data_alphabet == null ? null : data_alphabet.size()) + ", " +
		          (target_alphabet == null ? null : target_alphabet.size()) + "], ");
		      data_alphabet = this.getDataAlphabet();
		      target_alphabet = this.getTargetAlphabet();
		      sb.append("InstanceList: [" + (data_alphabet == null ? null : data_alphabet.size()) + ", " +
		          (target_alphabet == null ? null : target_alphabet.size()) + "]\n");
		      //throw new IllegalArgumentException(sb.toString());
//			throw new IllegalArgumentException ("Alphabets don't match: Instance: "+
//					instance.getAlphabets()+" InstanceList: "+this.getAlphabets());
    }
		if (dataClass == null) {
			dataClass = instance.getData().getClass();
			if (pipe != null && pipe.isTargetProcessing())
				if (instance.target != null)
					targetClass = instance.target.getClass();
		}
		// Once it is added to an InstanceList, generally-speaking, the Instance shouldn't change.
		// There are exceptions, and for these you can instance.unlock(), then instance.lock() again.
		initializeTypeCountsForInstance(instance);
		initializeTopicAssignments(instance);
		instance.lock(); 
		return super.add (instance);
	}


	public void initializeTypeCountsForInstance(Instance instance){
		BigFeatureSequence tokens = (BigFeatureSequence) instance.getData();
		for (int position = 0; position < tokens.getLength(); position++) {
			int type = tokens.getIndexAtPosition(position);
			if(this.typeTotals.get(type)==null){
				this.typeTotals.put(type,0);
			}
			int cnt = this.typeTotals.get(type)+1;
			typeTotals.put(type, cnt);
		}
	}
	
	
	public BigLabelAlphabet getTopicAlphabet() {
		return topicAlphabet;
	}

	public void setTopicAlphabet(BigLabelAlphabet topicAlphabet) {
		this.topicAlphabet = topicAlphabet;
	}

	public void initializeTopicAssignments(Instance instance){
		if(topicAlphabet==null){
			throw new RuntimeException("Topic Alphabet not set");
		}
		
		BigFeatureSequence tokens = (BigFeatureSequence) instance.getData();
		BigLabelSequence topicSequence =
			new BigLabelSequence(topicAlphabet, new int[ tokens.size() ]);

		
		TopicAssignment t = new TopicAssignment (instance, topicSequence);
		int idx = this.topicAssignments.size();
		this.topicAssignments.put (new Integer(idx),t);
	}

	public BigFeatureSequence getTopicAssigment(int idx){
		return this.topicAssignments.getTokens(idx);
	}
	public BigFeatureSequence getLastTopicAssigment(){
		return this.topicAssignments.getTokens(this.topicAssignments.size()-1);
	}
	public Instance set (int index, Instance instance) {
		throw new IllegalStateException ("Not yet implemented.");
		//prepareToRemove(get(index));
		//return super.set (index, instance);
  }
	
  public void add (int index, Instance element) {
  	throw new IllegalStateException ("Not yet implemented.");
  }
  
  public Instance remove (int index) {
	throw new IllegalStateException ("Not yet implemented.");	  
//  prepareToRemove (get(index));
//  return super.remove(index);
  }
  
  public boolean remove (Instance instance) {
	throw new IllegalStateException ("Not yet implemented.");
  	//prepareToRemove (instance);
  	//return super.remove(instance);
  }
  
  public boolean addAll (Collection<? extends Instance> instances) {
  	for (Instance instance : instances)
  		this.add (instance);
  	return true;
  }
  
  public boolean addAll(int index, Collection <? extends Instance> c) {
  	throw new IllegalStateException ("addAll(int,Collection) not supported by BigInstanceList.n");
  }
  
  public void clear() {
	  throw new IllegalStateException ("clear() not supported by BigInstanceList.n");
  }

  

  


	/** Returns the <code>Alphabet</code> mapping features of the data to
	 * integers. */
	public BigAlphabet getDataAlphabet ()
	{
		if (dataAlphabet == null && pipe != null) {
			dataAlphabet = pipe.getDataAlphabet ();
		}
		BigAlphabet b = pipe.getAlphabet();
		assert (pipe == null
				|| pipe.getDataAlphabet () == null
				|| pipe.getAlphabet() == this.dataAlphabet);
		return dataAlphabet;
	}
	
	/** Returns the <code>Alphabet</code> mapping target output labels to
	 * integers. */
	public BigAlphabet getTargetAlphabet ()
	{
		if (targetAlphabet == null && pipe != null) {
			targetAlphabet = pipe.getTargetAlphabet();
		}
		assert (pipe == null
				|| pipe.getTargetAlphabet () == null
				|| pipe.getTargetAlphabet () == targetAlphabet);
		return targetAlphabet;
	}
	
	public BigAlphabet getAlphabet () {
		return getDataAlphabet();
	}
	
	public BigAlphabet[] getAlphabets () {
		return new BigAlphabet[] {getDataAlphabet(), getTargetAlphabet() };
	}
	

	public Map<Integer,Integer> getTypeTotals(){
		return this.typeTotals;
	}

	public Map getTopicAssignment(){
		return this.topicAssignments;
	}
	/** Returns the pipe through which each added <code>Instance</code> is passed,
	 * which may be <code>null</code>. */
	public BigPipe getPipe ()
	{
		return pipe;
	}
	
}
