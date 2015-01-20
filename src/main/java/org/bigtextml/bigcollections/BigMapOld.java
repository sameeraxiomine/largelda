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
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.higherfrequencytrading.chronicle.Excerpt;
import com.higherfrequencytrading.chronicle.impl.IndexedChronicle;
import com.higherfrequencytrading.chronicle.tools.ChronicleTools;

public class BigMapOld<K,V> implements Map<K,V>{
	//private static volatile int fcounter = 1;
	private static Random rnd = new Random();
    private volatile String basePath = System.getProperty("java.io.tmpdir") + File.separator;
    private volatile String cacheName = "test";


    
    //private final List<Long> deleteIndexes = new ArrayList<Long>();
   
    private final int[] consolidates = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};	
	
	
	//private Map<K,Integer> offHeapKeyIndex = new HashMap<K,Integer>();
	private volatile int maxInMemoryCapacity = 10000;
	private int offHeapIndex = 0;
	
	
	//private Map<K,V> mostRecentlyUsed = new HashMap<K,V>();
	public  int mruQueueSize = 10000;
	public int keySplits = 29;
	private int maxSizeOfSplit=10000;
	
	public final Map<K,V> mostRecentlyUsedMap = new HashMap<K,V>();
	public final Queue<K> mostRecentlyUsed = new ConcurrentLinkedQueue();
	
	//public List<IndexedChronicle> keyChronicle = new ArrayList<IndexedChronicle>(2000);
	
	

	private volatile List<IndexedChronicle> chronicleIndexes;
	private volatile List<Excerpt> excerptIndexes;
	private volatile Map<Integer,Map<K,Integer>> mapChronicleIndexes;
	private volatile Map<Integer,Integer> countOfChronicles;
    private IndexedChronicle currentChronicle;
    private Excerpt currentExcerpt;
    
    private IndexedChronicle deleteChronicle;
    private Excerpt deleteExcerpt;
	
	private final Set<K> deleteKeys = new HashSet<K>();
	
	private boolean deleteOnExit = true;
	

 

	public BigMapOld(boolean deleteOnExit,String cacheName){
		this.deleteOnExit=deleteOnExit;
		this.cacheName=cacheName;
		if(System.getProperty("InvokedFromSpring")==null){
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
						this.cacheName=BigMapOld.getTmpCacheName();
					}
				}
			}	
		}		
	}

	private void createNextChronicle(int index) throws Exception{
		IndexedChronicle chronicle = new IndexedChronicle(this.getFilePath(index));
		if(this.deleteOnExit)
			ChronicleTools.deleteOnExit(this.getFilePath(index));
		Excerpt excerpt = chronicle.createExcerpt();
		this.chronicleIndexes.add(chronicle);
		this.excerptIndexes.add(excerpt);
	}
	
	private void createDeleteChronicle() throws Exception{
		this.deleteChronicle = new IndexedChronicle(this.getDeleteFilePath());
		if(this.deleteOnExit)
			ChronicleTools.deleteOnExit(this.getDeleteFilePath());
		this.deleteExcerpt = deleteChronicle.createExcerpt();

	}
	public void initialize(){
		try{			

			this.createEnvironment();
			this.createDeleteChronicle();
			//this.fileName = this.cacheName;			
			this.chronicleIndexes = new ArrayList<IndexedChronicle>(this.keySplits);
			this.excerptIndexes = new ArrayList<Excerpt>(this.keySplits);
			this.mapChronicleIndexes = new HashMap<Integer,Map<K,Integer>>();
			this.countOfChronicles = new HashMap<Integer,Integer>();
			for(int i=0;i<this.keySplits;i++){
				this.countOfChronicles.put(i,1);
				this.mapChronicleIndexes.put(i, new HashMap<K,Integer>());
				try{
					this.createNextChronicle(i);

				}
				catch(Exception e){
					throw new RuntimeException(e);
				}
			}	
			
			File f = new File(this.getFilePath());
			if(f.exists()){
				f.delete();
			}
			this.currentChronicle = new IndexedChronicle(this.getFilePath());
			ChronicleTools.deleteOnExit(this.getFilePath());
			this.currentExcerpt = this.currentChronicle.createExcerpt();
	        this.initializeKeyChronicles();
		}
		catch(Exception e){
			throw new RuntimeException(e.getCause());
		}		
	}
	
	public void prepareIndexes(){
		
	}
	
	public void initializeKeyChronicles(){
		
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
    
	public String getDeleteFilePath(){
		return this.basePath+this.cacheName+"_delete";
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
		return (offHeapIndex-this.deleteKeys.size());
	}

	
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return this.size()==0;
	}

	
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}

	
	
	
	public V get(Object key) {
		K key2 = (K) key;
		
		MyEntry<K,V> val = null;

			
			
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
			val= getFromOffHeapByIndex((K)key);

			if(val!=null){
				this.mostRecentlyUsedMap.put(key2, val.getValue());
				this.mostRecentlyUsed.add(key2);
									
			}
		}

		if(val!=null){
			return val.getValue();
		}
		return null;
		
	}
	
	private void addToExcerpt(Object val,Excerpt ex){
		
		byte[] b=serialize(val);
		float multiplier = 1.1f;
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
				e.printStackTrace();
				//Do nothing until
				if(multiplier<=3){
				
					multiplier=multiplier+0.5f;
				}
				else{
					throw new RuntimeException("Too much size required for the object"+val);
				}
			}			
		}
	}

	private void addToIndex(K key){
        int offset = Math.abs(key.hashCode()%this.keySplits);
		Map<K,Integer> cMap = this.mapChronicleIndexes.get(offset);
		if(cMap==null){
			cMap=new HashMap<K,Integer>();
			this.mapChronicleIndexes.put(offset,new HashMap<K,Integer>());
		}
		else{
			if(cMap.size()>=this.maxInMemoryCapacity){
				
				//Save the chronicle
				this.addToExcerpt(cMap, this.excerptIndexes.get(offset));
				cMap=new HashMap<K,Integer>();
				this.mapChronicleIndexes.put(offset,cMap);
				int noOfOffsets=this.countOfChronicles.get(offset)+1;
				
				this.countOfChronicles.put(offset, noOfOffsets);
				
			}
		}
		cMap.put(key, offHeapIndex);
		
		
        offHeapIndex++;
        
        
	}
	
	public V put(K key, V value) {

		MyEntry<K,V> me = new MyEntry(key,value);
		this.addToExcerpt(me, this.currentExcerpt);
		this.addToIndex(key);

		return value;
	}
	
	private MyEntry<K,V> getFromOffHeapByIndex(K key){
		int index=-1;
		int hashCode = key.hashCode()%this.keySplits;
		if(this.mapChronicleIndexes.get(hashCode).get(key)!=null){
			index=this.mapChronicleIndexes.get(hashCode).get(key);
		}
		else{
			int noOfChronicles = this.countOfChronicles.get(hashCode);
			Excerpt ex = this.excerptIndexes.get(hashCode);
			for(int i=0;i<noOfChronicles;i++){
				boolean  b= ex.index(i);
				if(b){
					Map<K,Integer> mp = (Map<K,Integer>)ex.readObject();
					if(mp.containsKey(key)){
						index=mp.get(key);
						break;
					}
				}

			}
			
		}

		if(index>=0){
			boolean  b= this.currentExcerpt.index(index);
			if(!b){
				throw new RuntimeException("Error reading for key " + index + " from offheap");
			}
			else{
		        MyEntry<K,V> val=(MyEntry<K,V>)currentExcerpt.readObject();
		       return val;

			}		
		}
		return null;
	
	}

	

	
	public V remove(Object key) {
		V val  = null;
		val = this.get(key);
		if(val!=null) {
			deleteKeys.add((K)key);
			this.mostRecentlyUsed.remove((K)key);
			this.mostRecentlyUsedMap.remove((K)key);
		}


		return val;
	}

	
	public void putAll(Map<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	
	public void clear() {
		// TODO Auto-generated method stub
		this.currentChronicle.close();
		for(IndexedChronicle ic:chronicleIndexes){
			ic.close();
		}
		File bd = new File(this.basePath);
		bd.delete();
	}
	protected void finalize() throws Throwable {
		this.currentChronicle.close();
		for(IndexedChronicle ic:chronicleIndexes){
			ic.close();
		}		
	}

	
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		//This will need an implemntation of Set as well to ensure Memory Management
		throw new UnsupportedOperationException();

		//return null;
	}

	
	public Collection<V> values() {
		// TODO Auto-generated method stub
		//This will need an implemntation of Set as well to ensure Memory Management
		throw new UnsupportedOperationException();

		//return null;
	}

	
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		//This will need an implemntation of Set as well to ensure Memory Management
		throw new UnsupportedOperationException();
	}
	public static void main(String[] args){
		int max = 1000000;
		System.setProperty("BigMapCachePath", "C:/tmp");
		Map<String,List<Integer>> bm = new BigMapOld<String,List<Integer>>(false,"MyCache");
		//bm.setMaxCapacity(1000);

		for(int i=0;i<max;i++){
			
			
			List<Integer> x = new ArrayList<Integer>();
			for(int j=0;j<5;j++){
				x.add(j);
			}
			x.add(i);
			bm.put(Integer.toString(i), x);
		}
		String key = Integer.toString(max-1);
		//bm.remove(key);
		for(int i=0;i<3;i++){
			long millis = System.currentTimeMillis();
			System.out.println( bm.get(Integer.toString(500000)));
			System.out.println(System.currentTimeMillis()-millis);
		}
		bm.clear();
		//bm.cleanup();
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



	private int getKeyChronicleIndex(K key, int index){
		int remainder = key.hashCode()%1000;
		return remainder;
	}
	

	private static String getTmpCacheName(){
		return "tmpcache"+Integer.toString(rnd.nextInt());
	}
	public BigMapOld(){
		
		this.cacheName= BigMapOld.getTmpCacheName();
		if(System.getProperty("InvokedFromSpring")==null){
			this.initialize();
		}
	}
	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
}
