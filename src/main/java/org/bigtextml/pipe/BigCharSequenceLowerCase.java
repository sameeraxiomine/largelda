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

import org.bigtextml.types.Instance;


/**
* Replace the data string with a lowercased version. 
*  This can improve performance over TokenSequenceLowercase.
*/

public class BigCharSequenceLowerCase extends BigPipe implements Serializable {
	
	public Instance pipe (Instance carrier) {
		long millis=System.currentTimeMillis();
		if (carrier.getData() instanceof String) {
			String data = (String) carrier.getData();
			carrier.setData(data.toLowerCase());
		}
		else {
			throw new IllegalArgumentException("CharSequenceLowercase expects a String, found a " + carrier.getData().getClass());
		}
		//System.out.println(this.getClass().getCanonicalName() + "===" + (System.currentTimeMillis()-millis));
		return carrier;
	}

	// Serialization 
	
	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;
	
	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt (CURRENT_SERIAL_VERSION);
	}
	
	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt ();
	}

}
