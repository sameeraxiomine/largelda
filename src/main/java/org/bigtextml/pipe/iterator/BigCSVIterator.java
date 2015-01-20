/* Copyright (C) 2014 Sameer Wadkar.
This file is an adaptation to the  "MALLET" (MAchine Learning for LanguagE Toolkit)
It is adapted from the "MALLET" (MAchine Learning for LanguagE Toolkit) API  by, 
McCallum, Andrew Kachites-  "MALLET: A Machine Learning for Language Toolkit."
http://mallet.cs.umass.edu. 2002.
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */
package org.bigtextml.pipe.iterator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bigtextml.types.Instance;


public class BigCSVIterator implements Iterator<Instance>{
	private static int counter=0;
	LineNumberReader reader;
	Pattern lineRegex;
	int uriGroup, targetGroup, dataGroup;
	String currentLine;
	long millis = System.currentTimeMillis();
	
	public BigCSVIterator (Reader input, Pattern lineRegex, int dataGroup, int targetGroup, int uriGroup)
	{
		this.reader = new LineNumberReader (input);
		this.lineRegex = lineRegex;
		this.targetGroup = targetGroup;
		this.dataGroup = dataGroup;
		this.uriGroup = uriGroup;
		if (dataGroup <= 0)
			throw new IllegalStateException ("You must extract a data field.");
		try {
			this.currentLine = reader.readLine();
		} catch (IOException e) {
			throw new IllegalStateException ();
		}
	}

	public BigCSVIterator (Reader input, String lineRegex, int dataGroup, int targetGroup, int uriGroup)
	{
		this (input, Pattern.compile (lineRegex), dataGroup, targetGroup, uriGroup);
	}

	public BigCSVIterator (String filename, String lineRegex, int dataGroup, int targetGroup, int uriGroup)
		throws java.io.FileNotFoundException
	{
		this (new FileReader (new File(filename)),
					Pattern.compile (lineRegex), dataGroup, targetGroup, uriGroup);
	}
	
	// The PipeInputIterator interface

	public Instance next ()
	{
		
		String uriStr = null;
		String data = null;
		String target = null;
		Matcher matcher = lineRegex.matcher(currentLine);
		if (matcher.find()) {
			if (uriGroup > 0)
				uriStr = matcher.group(uriGroup);
			if (targetGroup > 0)
				target = matcher.group(targetGroup);
			if (dataGroup > 0)
				data = matcher.group(dataGroup);
			//int xxx=Integer.parseInt(uriStr);
			counter++;
			if(counter%10000==0){
				//System.out.println("URISTR=="+uriStr);
				System.out.println("Just Processed the Instance of count " + counter + " in " + (System.currentTimeMillis()-millis));
				millis=System.currentTimeMillis();
			}
			//System.out.println("TARGETGRP=="+target);
			//System.out.println("DATAGRP=="+data);
		} else {
			throw new IllegalStateException ("Line #"+reader.getLineNumber()+" does not match regex:\n" +
											 currentLine);
		}

		String uri;
		if (uriStr == null) {
			uri = "csvline:"+reader.getLineNumber();
		} else {
			uri = uriStr;
		}
		assert (data != null);
		//Add Stemming here
		//data=com.axiomine.bigtm.Utils.porterStemContent(data);

		Instance carrier = new Instance (data, target, uri, null);
	
		try {
			this.currentLine = reader.readLine();
		} catch (IOException e) {
			throw new IllegalStateException ();
		}

		
		
		return carrier;
	}

	public boolean hasNext ()	{	return currentLine != null;	}
	
	public void remove () {
		throw new IllegalStateException ("This Iterator<Instance> does not support remove().");
	}
}
