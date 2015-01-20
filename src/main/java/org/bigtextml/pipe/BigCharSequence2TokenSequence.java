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

import java.io.*;
import java.net.URI;
import java.util.regex.Pattern;

import org.bigtextml.types.BigSingleInstanceIterator;
import org.bigtextml.types.Instance;

import cc.mallet.extract.StringSpan;
import cc.mallet.extract.StringTokenization;
import cc.mallet.types.TokenSequence;
import cc.mallet.util.CharSequenceLexer;
import cc.mallet.util.Lexer;

/**
*  Pipe that tokenizes a character sequence.  Expects a CharSequence
*   in the Instance data, and converts the sequence into a token
*   sequence using the given regex or CharSequenceLexer.  
*   (The regex / lexer should specify what counts as a token.)
*/
public class BigCharSequence2TokenSequence extends BigPipe implements Serializable
{
	CharSequenceLexer lexer;
	
	public BigCharSequence2TokenSequence (CharSequenceLexer lexer)
	{
		this.lexer = lexer;
	}

	public BigCharSequence2TokenSequence (String regex)
	{
		this.lexer = new CharSequenceLexer (regex);
	}

	public BigCharSequence2TokenSequence (Pattern regex)
	{
		this.lexer = new CharSequenceLexer (regex);
	}

	public BigCharSequence2TokenSequence ()
	{
		this (new CharSequenceLexer());
	}

	public Instance pipe (Instance carrier)
	{
		long millis=System.currentTimeMillis();
		CharSequence string = (CharSequence) carrier.getData();
		lexer.setCharSequence (string);
		TokenSequence ts = new StringTokenization (string);
		while (lexer.hasNext()) {
			lexer.next();
			ts.add (new StringSpan (string, lexer.getStartOffset (), lexer.getEndOffset ()));
		}
		
		carrier.setData(ts);
		//System.out.println(this.getClass().getCanonicalName() + "----" +(System.currentTimeMillis()-millis));
		return carrier;
	}

	public static void main (String[] args)
	{
		try {
			for (int i = 0; i < args.length; i++) {
				Instance carrier = new Instance (new File(args[i]), null, null, null);
				BigSerialPipes p = new BigSerialPipes (new BigPipe[] {
					new BigInput2CharSequence (),
					new BigCharSequence2TokenSequence(new CharSequenceLexer())});
				carrier = p.newIteratorFrom (new BigSingleInstanceIterator(carrier)).next();
				TokenSequence ts = (TokenSequence) carrier.getData();

			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	// Serialization 
	
	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;
	
	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt(CURRENT_SERIAL_VERSION);
		out.writeObject(lexer);
	}
	
	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt ();
		lexer = (CharSequenceLexer) in.readObject();
	}


	
}
