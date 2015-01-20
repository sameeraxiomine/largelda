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
import java.text.SimpleDateFormat;

public class WorkingDirectory implements IWorkingDirectory{
	private String workingDirectory = System.getProperty("java.io.tmpdir");;
	private java.util.Date dt = null;
	public String getDirectory() {
		return workingDirectory;
	}

	public void setDirectory(String workingDir) {
		this.dt = new java.util.Date(System.currentTimeMillis());
		this.workingDirectory = workingDir;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		this.workingDirectory= this.workingDirectory+"/"+sdf.format(dt)+"/";
		File wDir = new File(this.workingDirectory);
		if(!wDir.exists()){
			boolean success = wDir.mkdirs();
			if(!success){				
				System.out.println("Cannot create working directory " + wDir.getAbsolutePath());
				throw new RuntimeException("Cannot Create Working Directory");
			}
		}
		System.out.println("Working Directory Created ====" + this.workingDirectory);
	}
	
}
