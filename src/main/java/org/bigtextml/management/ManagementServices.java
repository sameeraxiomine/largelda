/* Copyright (C) 2014 Sameer Wadkar.
This file is an adaptation to the  "MALLET" (MAchine Learning for LanguagE Toolkit)
It is adapted from the "MALLET" (MAchine Learning for LanguagE Toolkit) API  by, 
McCallum, Andrew Kachites-  "MALLET: A Machine Learning for Language Toolkit."
http://mallet.cs.umass.edu. 2002.
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */
package org.bigtextml.management;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bigtextml.bigcollections.BigMap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ManagementServices {
	private static java.util.Date dt =new Date(System.currentTimeMillis());
	private static ApplicationContext context = 
			new ClassPathXmlApplicationContext(new String[] {"InMemoryDataMining.xml"});
	
	
	//private static File workingDir = null;
	
	/*
	public static File generateWorkingDir(String baseDir){
		
		if(ManagementServices.workingDir!=null){
			return ManagementServices.workingDir;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		
		
		System.out.println(dt);
		File workingDir = new File( baseDir+"/"+sdf.format(dt)+"/");
		if(!workingDir.exists()){
			boolean success = workingDir.mkdirs();
			if(!success){				
				System.out.println("Cannot create working directory " + workingDir.getAbsolutePath());
			}
		}
		ManagementServices.workingDir = workingDir;
		return ManagementServices.workingDir;

	}
	*/
	public static Map getBigMap(String bName){
		Map bm = context.getBean(bName,Map.class);
		
		return bm;
	}
	
	public static Object getBean(String bName){
		return  context.getBean(bName);
	}
	

}
