/* Copyright (C) 2014 Sameer Wadkar.
This file is an adaptation to the  "MALLET" (MAchine Learning for LanguagE Toolkit)
It is adapted from the "MALLET" (MAchine Learning for LanguagE Toolkit) API  by, 
McCallum, Andrew Kachites-  "MALLET: A Machine Learning for Language Toolkit."
http://mallet.cs.umass.edu. 2002.
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */
package org.bigtextml.drivers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bigtextml.bigcollections.BigMap;
import org.bigtextml.management.ManagementServices;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
public class BigMapReadTest {
	public static void main(String[] args){
		/*
		ApplicationContext context = 
				new ClassPathXmlApplicationContext(new String[] {"InMemoryDataMining.xml"});
		*/
		System.out.println(System.getProperty("InvokedFromSpring"));
		//Map<String,List<Integer>> bm = context.getBean("bigMapTest",BigMap.class);
		Map<String,List<Integer>> bm =  ManagementServices.getBigMap("tmCache");
		System.out.println(((BigMap)bm).getCacheName());
		int max = 1000000;
		

		String key = Integer.toString(max-1);
		//bm.remove(key);
		for(int i=0;i<3;i++){
			long millis = System.currentTimeMillis();
			System.out.println( bm.get(Integer.toString(500000)));
			System.out.println(System.currentTimeMillis()-millis);
		}
		bm.clear();		
	}
}
