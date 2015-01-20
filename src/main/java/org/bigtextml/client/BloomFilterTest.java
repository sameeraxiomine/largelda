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
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
public class BloomFilterTest {
	public static Funnel<String> stringFunnel = new Funnel<String>() {
		public void funnel(String str, PrimitiveSink into) {
		    into
		      .putInt(str.hashCode());
		  }
		};
	public static void main(String[] args) {
	
		BloomFilter<String> strsBloom = BloomFilter.create(stringFunnel, Integer.MAX_VALUE, 0.5);
		int cnt=0;
		for(long i=0;i<4000001;i++){
			cnt=cnt+1;
			strsBloom.put(Long.toString(i));
		}
		System.out.println(cnt);
		System.out.println(strsBloom.mightContain("5000000"));

	}
	

}
