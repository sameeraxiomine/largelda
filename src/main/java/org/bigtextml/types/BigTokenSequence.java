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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import cc.mallet.types.Sequence;
import cc.mallet.types.Token;
import cc.mallet.util.PropertyList;

/**
 * A representation of a piece of text, usually a single word, to which we can attach properties.
 */

public class BigTokenSequence extends ArrayList<Token> implements Sequence, Serializable {
	//ArrayList tokens;
	PropertyList properties = null;				// for arbitrary properties

	public BigTokenSequence (Collection<Token> tokens) {
		super(tokens);
	}

	public BigTokenSequence () {
		super();
	}

	public BigTokenSequence (int capacity) {
		super (capacity);
	}

	public BigTokenSequence (Token[] tokens) {
		this (tokens.length);
		for (int i = 0; i < tokens.length; i++)
			this.add( tokens[i] );
	}

	public BigTokenSequence (Object[] tokens) {
		this( tokens.length );
		for (int i = 0; i < tokens.length; i++)
			this.add (new Token( tokens[i].toString()));
	}
	
	//public Token get (int i) {return this.get(i);	}

	public String toString () {
		StringBuffer sb = new StringBuffer();
		sb.append( "TokenSequence " + super.toString() + "\n" );
		for (int i = 0; i < this.size(); i++) {
			String tt = get(i).toString();
			sb.append( "Token#" + i + ":" );
			sb.append( tt );
			if (!tt.endsWith( "\n" ))
				sb.append( "\n" );
		}
		return sb.toString();
	}

	public String toStringShort () {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.size(); i++) {
			String tt = get(i).toString();
			tt.replaceAll("\n","");
			if (i > 0){
				sb.append(" ");
			}
			sb.append(tt);
		}
		return sb.toString();
	}

	// gdruck
	// This method causes a compiler error in Eclipse Helios.
	// Removed support for adding Objects other than String.
	/*
	public void add (Object o) {
		if (o instanceof Token)
			add( (Token)o );
		else if (o instanceof TokenSequence)
			add( (TokenSequence)o );
		else
			add( new Token( o.toString() ) );
	}
	*/
	
	public void add(String string) {
		add(new Token(string));
	}

	// added by Fuchun Peng, Oct. 24, 2003
	public Object removeLast () {
		if (this.size() > 0) 
			return this.remove (this.size() - 1);
		else
			return null;
	}


	public void addAll (Object[] objects) {
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] instanceof Token)
				add( (Token)objects[i] );
			else
				add( new Token( objects[i].toString() ) );
		}
	}

	public BigFeatureSequence toFeatureSequence (BigAlphabet dict) {
		BigFeatureSequence fs = new BigFeatureSequence( dict, this.size() );
		for (int i = 0; i < this.size(); i++)
			fs.add (dict.lookupIndex( (this.get(i)).getText()));
		return fs;
	}

	public BigFeatureVector toFeatureVector (BigAlphabet dict) {
		return new BigFeatureVector( toFeatureSequence( dict ) );
	}

	public void setNumericProperty (String key, double value) {
		properties = PropertyList.add( key, value, properties );
	}

	public void setProperty (String key, Object value) {
		properties = PropertyList.add( key, value, properties );
	}

	public double getNumericProperty (String key) {
		return properties.lookupNumber( key );
	}

	public Object getProperty (String key) {
		return properties.lookupObject( key );
	}

	public boolean hasProperty (String key) {
		return properties.hasProperty( key );
	}

	// added gmann 8/30/2006
	public PropertyList getProperties () {
		return properties;
	}


	// Serialization

	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt( CURRENT_SERIAL_VERSION );
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		@SuppressWarnings("unused")
		int version = in.readInt();
		in.defaultReadObject();
	}


}
