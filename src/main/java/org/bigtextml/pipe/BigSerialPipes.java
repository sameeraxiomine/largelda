/* Copyright (C) 2014 Sameer Wadkar.
This file is an adaptation to the  "MALLET" (MAchine Learning for LanguagE Toolkit)
It is adapted from the "MALLET" (MAchine Learning for LanguagE Toolkit) API  by, 
McCallum, Andrew Kachites-  "MALLET: A Machine Learning for Language Toolkit."
http://mallet.cs.umass.edu. 2002.
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */
package org.bigtextml.pipe;

/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.io.*;

import org.bigtextml.pipe.iterator.EmptyInstanceIterator;
import org.bigtextml.types.BigAlphabet;
import org.bigtextml.types.Instance;

/**
* Convert an instance through a sequence of pipes.
@author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
*/

public class BigSerialPipes extends BigPipe implements Serializable
{
	ArrayList<BigPipe> pipes;

	public BigSerialPipes ()
	{
		this.pipes = new ArrayList<BigPipe> ();
	}
	
	public BigSerialPipes (BigPipe[] pipes)
	{
		this.pipes = new ArrayList<BigPipe> (pipes.length);
		for (int i = 0; i < pipes.length; i++)
			this.pipes.add (pipes[i]);
		resolveAlphabets();
	}

	public BigSerialPipes (Collection<BigPipe> pipeList)
	{
		pipes = new ArrayList<BigPipe> (pipeList);
		resolveAlphabets();
	}
	
	public abstract class Predicate {
		public abstract boolean predicate (BigPipe p);
	}
	
	public BigSerialPipes newSerialPipesFromSuffix (Predicate testForStartingNewPipes) {
		int i = 0;
		while (i < pipes.size())
			if (testForStartingNewPipes.predicate(pipes.get(i))) {
				return new BigSerialPipes(pipes.subList(i, pipes.size()-1));
			}
		throw new IllegalArgumentException ("No pipes in this SerialPipe satisfied starting predicate.");
	}
	
	public BigSerialPipes newSerialPipesFromRange (int start, int end) {
		return new BigSerialPipes(pipes.subList(start, end));
	}
	
	private void resolveAlphabets ()
	{
		BigAlphabet da = null, ta = null;
		for (BigPipe p : pipes) {
			p.preceedingPipeDataAlphabetNotification(da);
			da = p.getDataAlphabet();
			p.preceedingPipeTargetAlphabetNotification(ta);
			ta = p.getTargetAlphabet();
		}
		dataAlphabet = da;
		targetAlphabet = ta;
	}

	// protected void add (Pipe pipe)
	// protected void remove (int i)
	// This method removed because pipes should be immutable to be safe.
	// If you need an augmented pipe, you can make a new SerialPipes containing this one.

	public void setTargetProcessing (boolean lookForAndProcessTarget)
	{
		super.setTargetProcessing (lookForAndProcessTarget);
		for (BigPipe p : pipes)
			p.setTargetProcessing (lookForAndProcessTarget);
	}
	
	public Iterator<Instance> newIteratorFrom (Iterator<Instance> source)
	{
		if (pipes.size() == 0)
			return new EmptyInstanceIterator();
		Iterator<Instance> ret = pipes.get(0).newIteratorFrom(source);
		for (int i = 1; i < pipes.size(); i++)
			ret = pipes.get(i).newIteratorFrom(ret);
		return ret;
	}
	
	public int size()
	{
		return pipes.size();
	}

	public BigPipe getPipe (int index) {
		BigPipe retPipe = null;
		try {
			retPipe = pipes.get(index);
		}
		catch (Exception e) {
			System.err.println("Error getting pipe. Index = " + index + ".  " + e.getMessage());
		}
		return retPipe;
	}
	
	/** Allows access to the underlying collection of Pipes.  Use with caution. */
	public ArrayList<BigPipe> pipes() {
		return pipes;
	}
	
	public String toString ()
	{
		StringBuffer sb = new StringBuffer();
		for (BigPipe p : pipes)
			sb.append (p.toString()+",");
		return sb.toString();
	}

	// Serialization 
	
	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;
	
	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt (CURRENT_SERIAL_VERSION);
		out.writeObject(pipes);
	}
	
	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt ();
		pipes = (ArrayList) in.readObject();
		resolveAlphabets();
	}

}
