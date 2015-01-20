/* Copyright (C) 2014 Sameer Wadkar.
This file is an adaptation to the  "MALLET" (MAchine Learning for LanguagE Toolkit)
It is adapted from the "MALLET" (MAchine Learning for LanguagE Toolkit) API  by, 
McCallum, Andrew Kachites-  "MALLET: A Machine Learning for Language Toolkit."
http://mallet.cs.umass.edu. 2002.
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */
package org.bigtextml.bigcollections;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.lang.Long;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.higherfrequencytrading.chronicle.Excerpt;
import com.higherfrequencytrading.chronicle.impl.IndexedChronicle;
import com.higherfrequencytrading.chronicle.tools.ChronicleTools;

public class BigMapEntries implements Map<Integer,String>{
	//private static volatile int fcounter = 1;
	private static Random rnd = new Random();
    private volatile String basePath = System.getProperty("java.io.tmpdir") + File.separator;
    private volatile String cacheName = "test";

    private Excerpt excerpt;
    
    //private final List<Long> deleteIndexes = new ArrayList<Long>();
    private IndexedChronicle chronicle;
    private final int[] consolidates = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};	
	
	//private Map<K,Integer> offHeapKeyIndex = new HashMap<K,Integer>();
	private volatile int maxInMemoryCapacity = 10000;
	private int offHeapIndex = 0;
	
	
	//private Map<K,V> mostRecentlyUsed = new HashMap<K,V>();
	public  int mruQueueSize = 10000;
	public int keySplits = 29;
	private int maxSizeOfSplit=10000;
	
	public final Map<Integer,String> mostRecentlyUsedMap = new HashMap<Integer,String>();
	public final Queue<Integer> mostRecentlyUsed = new ConcurrentLinkedQueue<Integer>();
	
	//public List<IndexedChronicle> keyChronicle = new ArrayList<IndexedChronicle>(2000);
	
	

	private volatile List<IndexedChronicle> chronicleIndexes;
	private volatile List<Excerpt> excerptIndexes;
	private volatile Map<Integer,Map<Long,Integer>> mapChronicleIndexes;
	private volatile Map<Integer,Integer> countOfChronicles;
	
	private final Set<Long> deleteKeys = new HashSet<Long>();
	
	private boolean deleteOnExit = true;
	private boolean useTmpCache = true;
	

	public BigMapEntries(boolean deleteOnExit,String cacheName){
		this.setDeleteOnExit(deleteOnExit);
		this.setCacheName(cacheName);
		if(System.getProperty("InvokedFromSpring")==null){
			
			this.initialize();
		}
	}

	public BigMapEntries(){
		if(System.getProperty("InvokedFromSpring")==null){
			this.cacheName= BigMapEntries.getTmpCacheName();
			this.initialize();
		}
	}



	private void createEnvironment(){
		String cachePath = System.getProperty("BigMapCachePath");
		while(true){
			if(cachePath!=null && cachePath.trim().length()>0){
				File f = new File(cachePath);
				if(f.exists() && f.isDirectory()){
					this.basePath=cachePath + File.separator+this.cacheName +File.separator;
					File dir = new File(basePath);
					if(!dir.exists()){
						dir.mkdir();
						break;
					}
					else{
						if(this.useTmpCache)
							this.cacheName=BigMapEntries.getTmpCacheName();
						else{
							//Do nothing. Recover from the existing files
							break;
						}
					}
				}
			}	
		}		
	}
	
	private void recreateIndexesFromFile() throws Exception{
		if(this.deleteOnExit)
			ChronicleTools.deleteOnExit(this.getFilePath());
		
		this.chronicle= new IndexedChronicle(this.getFilePath());
		this.excerpt = this.chronicle.createExcerpt();
		long size = this.chronicle.size();
		for(long i=0;i<size;i++){
			boolean  b= excerpt.index(i);
			if(!b){
				throw new RuntimeException("Error reading for key " + i + " from offheap");
			}
			else{
		        String val=(String)excerpt.readObject();
		    

			}	
		}
	}
	
	
	public void initialize(){
		try{			
			this.createEnvironment();
			
	
			this.recreateIndexesFromFile();
			/*
			File f = new File(this.getFilePath());
			if(f.exists()){
				f.delete();
			}
			
			this.chronicle = new IndexedChronicle(this.getFilePath());
			if(this.deleteOnExit)
				ChronicleTools.deleteOnExit(this.getFilePath());
			this.excerpt = chronicle.createExcerpt();
			*/
	        //this.initializeKeyChronicles();
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.getCause());
		}		
	}

	private void createNextChronicle(int index) throws Exception{
		IndexedChronicle chronicle = new IndexedChronicle(this.getFilePath(index));
		
		/*Consider removing this. This should be recreated everytime we start*/
		if(this.deleteOnExit)
			ChronicleTools.deleteOnExit(this.getFilePath(index));
		Excerpt excerpt = chronicle.createExcerpt();
		this.chronicleIndexes.add(chronicle);
		this.excerptIndexes.add(excerpt);
	}

	
	public void prepareIndexes(){
		
	}
	
	public void initializeKeyChronicles(){
		
	}


	public String get(Object key) {
		int key2 = (Integer) key;
		
		String val = null;
		if(this.deleteKeys.contains(key)) {
				return null;
		}
		else if(this.mostRecentlyUsedMap.containsKey(key)){
			return this.mostRecentlyUsedMap.get(key);
		}
		else {
			if(this.mostRecentlyUsed.size()>=this.mruQueueSize){
				this.mostRecentlyUsed.remove();
			}
			boolean  b= excerpt.index(key2);
			if(!b){
				throw new RuntimeException("Error reading for key " + key2 + " from offheap");
			}
			else{
				val=(String)excerpt.readObject();
				

			}	

			if(val!=null){
				this.mostRecentlyUsedMap.put(key2, val);
				this.mostRecentlyUsed.add(key2);
									
			}
		}

		return val;
	}
	
	private void addToExcerpt(Object val,Excerpt ex){
		byte[] b=serialize(val);
		float multiplier = 2.1f;
		while(multiplier<4){
			try{
				ex.startExcerpt(8 + 4 +  (int) (multiplier*b.length));				
				ex.writeObject(val);				
	            for (final int consolidate : consolidates) {
	            	ex.writeStopBit(consolidate);
	            }
	            ex.finish();
	            break;
			}
			catch(Exception e){
				//e.printStackTrace();
				//Do nothing until
				if(multiplier<=3){				
					multiplier=multiplier+0.5f;
					//System.out.println(multiplier);
				}
				else{
					throw new RuntimeException("Too much size required for the object"+val);
				}
			}			
		}
	}

	
	
	public String put(Integer key, String value) {
		this.addToExcerpt(value, this.excerpt);		
		return value;
	}
	

	
	
	

	
	public String remove(Object key) {
		String val  = null;
		val = this.get(key);
		if(val!=null) {
			deleteKeys.add((Long)key);
			this.mostRecentlyUsed.remove((Long)key);
			this.mostRecentlyUsedMap.remove((Long)key);
		}
		/*}*/

		return val;
	}

	
	public void putAll(Map<? extends Integer, ? extends String> m) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	
	public void clear() {
		// TODO Auto-generated method stub
		chronicle.close();
		for(IndexedChronicle ic:chronicleIndexes){
			ic.close();
		}
		File bd = new File(this.basePath);
		bd.delete();
	}
	protected void finalize() throws Throwable {
		chronicle.close();
		for(IndexedChronicle ic:chronicleIndexes){
			ic.close();
		}		
	}

	
	public Set<Integer> keySet() {
		// TODO Auto-generated method stub
		//This will need an implemntation of Set as well to ensure Memory Management
		throw new UnsupportedOperationException();
	}

	
	public Collection<String> values() {
		// TODO Auto-generated method stub
		//This will need an implemntation of Set as well to ensure Memory Management
		throw new UnsupportedOperationException();

		//return null;
	}

	
	public Set<java.util.Map.Entry<Integer, String>> entrySet() {
		// TODO Auto-generated method stub
		//This will need an implemntation of Set as well to ensure Memory Management
		throw new UnsupportedOperationException();
	}


	
	
	public boolean isDeleteOnExit() {
		return deleteOnExit;
	}



	public void setDeleteOnExit(boolean deleteOnExit) {
		this.deleteOnExit = deleteOnExit;
	}



	public int getMruQueueSize() {
		return mruQueueSize;
	}



	public void setMruQueueSize(int mruQueueSize) {
		this.mruQueueSize = mruQueueSize;
	}



	public int getKeySplits() {
		return keySplits;
	}



	public void setKeySplits(int keySplits) {
		this.keySplits = keySplits;
	}



	public int getMaxSizeOfSplit() {
		return maxSizeOfSplit;
	}



	public void setMaxSizeOfSplit(int maxSizeOfSplit) {
		this.maxSizeOfSplit = maxSizeOfSplit;
	}



	private int getKeyChronicleIndex(Integer key, int index){
		int remainder = key.hashCode()%1000;
		return remainder;
	}
	

	private static String getTmpCacheName(){
		return "tmpcache"+Integer.toString(rnd.nextInt());
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
		this.useTmpCache=false;
	}
	   
	public String getFilePath(){
		return this.basePath+this.cacheName;
	}
		
	public String getFilePath(int index){
		return this.getFilePath()+"_"+Integer.toString(index);
	}
			
		
	public int getMaxInMemoryCapacity() {
		return maxInMemoryCapacity;
	}



	public void setMaxInMemoryCapacity(int maxCapacity) {
		this.maxInMemoryCapacity = maxInMemoryCapacity;
	}

		
	public int size() {
		// TODO Auto-generated method stub
		//return ((offHeapIndex-this.deleteKeys.size())+ (int)this.map.size());
		return (int)(this.excerpt.size()-this.deleteKeys.size());
	}

		
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return this.size()==0;
	}

		
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		long myKey = ((Long) key).longValue();
		return (myKey < this.size());
		
	}

		
	public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
			
	}

    private  static byte[] serialize(Object obj) {
    	try{
	        ByteArrayOutputStream b = new ByteArrayOutputStream();
	        ObjectOutputStream o = new ObjectOutputStream(b);
	        o.writeObject(obj);
	        return b.toByteArray();
    	}
    	catch(Exception ex){
    		throw new RuntimeException(ex);
    	}
    }
    
    private  Object deserialize(byte[] bytes)  {
    	try{
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    	}
    	catch(Exception ex){
    		throw new RuntimeException(ex);
    	}
    }
    
    
    public static void createNewMap(){
		int max = 1000000;
		System.setProperty("BigMapCachePath", "C:/tmp");
		Map bm = new BigMapEntries(false,"MyCacheEntriesTest");
		//bm.setMaxCapacity(1000);

		for(int i=0;i<max;i++){
			
			bm.put(i, Integer.toString(i));
		}
		String key = Integer.toString(max-1);
		//bm.remove(key);
		for(int i=0;i<3;i++){
			long millis = System.currentTimeMillis();
			System.out.println( bm.get(500000));
			System.out.println(System.currentTimeMillis()-millis);
		}
    }
    
    public static void readMap(){
		int max = 1000000;
		System.setProperty("BigMapCachePath", "C:/tmp");
		Map bm = new BigMapEntries(false,"MyCacheEntriesTest");

		String key = Integer.toString(max-1);
		//bm.remove(key);
		for(int i=0;i<3;i++){
			long millis = System.currentTimeMillis();
			System.out.println( bm.get(500000));
			System.out.println(System.currentTimeMillis()-millis);
		}
		//bm.clear();
		//bm.cleanup();
    }
    
	public static void main(String[] args){
		//createNewMap();
		readMap();
	}
    
}
