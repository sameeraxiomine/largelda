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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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

import org.bigtextml.client.IWorkingDirectory;
import org.bigtextml.client.WorkingDirectory;
import org.bigtextml.management.ManagementServices;

import com.higherfrequencytrading.chronicle.Excerpt;
import com.higherfrequencytrading.chronicle.impl.IndexedChronicle;
import com.higherfrequencytrading.chronicle.tools.ChronicleTools;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
public class BigMap<K,V> implements Map<K,V>, Serializable{
	//private static volatile int fcounter = 1;
	protected IWorkingDirectory workingDirectory = null;
	protected boolean reclaimMemory = false;
	public static int CNT_TO_RECLAIM_MEMORY = 500000;
	protected  Funnel<K> myFunnel = new Funnel<K>() {
		public void funnel(K obj, PrimitiveSink into) {
		    into.putInt(Math.abs(obj.hashCode()));
		      
		  }
		};	
	protected transient BloomFilter<K> mapKeyBloom = BloomFilter.create(myFunnel, 6000000, 0.01);
	protected static Random rnd = new Random();
	//protected volatile String rootPath = System.getProperty("java.io.tmpdir") + File.separator;
    protected volatile String basePath = "";
    protected volatile String cacheName = "test";

    protected transient Excerpt excerpt;
    
    //private final List<Long> deleteIndexes = new ArrayList<Long>();
    protected transient IndexedChronicle chronicle;
    protected final int[] consolidates = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};	
	
	//private Map<K,Integer> offHeapKeyIndex = new HashMap<K,Integer>();
    protected volatile int maxInMemoryCapacity = 10000;
	protected int offHeapIndex = 0;
	
	
	//private Map<K,V> mostRecentlyUsed = new HashMap<K,V>();
	protected  int mruQueueSize = 10000;
	protected int keySplits = 29;
	protected int maxSizeOfSplit=10000;
	
	protected Map<K,V> mostRecentlyUsedMap = new HashMap<K,V>();
	protected Queue<K> mostRecentlyUsed = new ConcurrentLinkedQueue();
	//public List<IndexedChronicle> keyChronicle = new ArrayList<IndexedChronicle>(2000);
	
	

	protected transient volatile List<IndexedChronicle> chronicleIndexes;
	protected transient volatile List<Excerpt> excerptIndexes;
	protected transient volatile Map<Integer,Map<K,Integer>> mapChronicleIndexes;
	protected transient volatile Map<Integer,Integer> countOfChronicles;
	
	protected final Set<K> deleteKeys = new HashSet<K>();
	
	protected boolean deleteOnExit = false;
	protected boolean useTmpCache = true;
	

	public BigMap(boolean deleteOnExit,String cacheName){
		this.setDeleteOnExit(deleteOnExit);
		this.setCacheName(cacheName);
		if(System.getProperty("InvokedFromSpring")==null){
			this.initialize();
		}
	}

	public BigMap(){
		if(System.getProperty("InvokedFromSpring")==null){
			this.cacheName= BigMap.getTmpCacheName();
			this.initialize();
		}
	}

	public boolean isReclaimMemory() {
		return reclaimMemory;
	}

	public void setReclaimMemory(boolean reclaimMemory) {
		this.reclaimMemory = reclaimMemory;
	}

	public IWorkingDirectory getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(IWorkingDirectory workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	protected void createEnvironment(){

		//System.out.println(this.rootPath + "=----" + ManagementServices.generateWorkingDir(this.rootPath).getAbsolutePath());
		//this.setRootPath(ManagementServices.generateWorkingDir(this.rootPath).getAbsolutePath());
		System.out.println("Working Directory ----" + this.workingDirectory.getDirectory());
		//this.setRootPath(this.workingDirectory.getWorkingDirectory());
		
		File f = new File(this.workingDirectory.getDirectory());
		if(f.exists() && !f.isDirectory()){
			throw new RuntimeException(this.workingDirectory.getDirectory() + " must be a directory");
		}
		
		if(!f.exists()){
			f.mkdir();
		}
		
		while(true){			
			if(!this.useTmpCache){
					this.basePath=this.workingDirectory.getDirectory() + File.separator+this.cacheName +File.separator;
					File dir = new File(basePath);
					if(!dir.exists()){
						dir.mkdir();						
					}		
					break;
			}
			else{
				this.cacheName=BigMap.getTmpCacheName();
				File dir = new File(basePath);
				if(!dir.exists()){
					dir.mkdir();
					break;
				}				
			}			
		}


	}
	
	protected void recreateIndexesFromFile() throws Exception{
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
		        MyEntry<K,V> val=(MyEntry<K,V>)excerpt.readObject();
		      
		       this.addToIndex(val.getKey());

			}	
		}
	}
	
	
	public void initialize(){
		try{
			
			this.createEnvironment();
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
			this.recreateIndexesFromFile();

		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.getCause());
		}		
	}

	protected void createNextChronicle(int index) throws Exception{

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

 
	public V get(Object key) {
		K key2 = (K) key;
		MyEntry<K,V> val = null;
		if(this.deleteKeys.contains(key)) {
				return null;
		}
		else if(this.mostRecentlyUsedMap.containsKey(key)){
			return this.mostRecentlyUsedMap.get(key);
			/*
			int index= this.mostRecentlyUsedMap.get(key);
			return this.getFromOffHeapValueByIndex(index);
			*/
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
	
	protected void addToExcerpt(Object val,Excerpt ex){
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

	protected void addToIndex(K key){
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
				cMap=null;
				cMap=new HashMap<K,Integer>();
				this.mapChronicleIndexes.put(offset,cMap);
				int noOfOffsets=this.countOfChronicles.get(offset)+1;
				this.countOfChronicles.put(offset, noOfOffsets);
			}
		}
		cMap.put(key, offHeapIndex);
		
		mapKeyBloom.put(key);
        offHeapIndex++;
        
        if(offHeapIndex%CNT_TO_RECLAIM_MEMORY==0 && this.reclaimMemory){
        	//this.reclaimMemory();
        	//this.emptyAndStoreIndexes();;
        }
        
        
	}
	
	public V put(K key, V value) {
		MyEntry<K,V> me = new MyEntry<K,V>(key,value);
		this.addToExcerpt(me, this.excerpt);
		this.addToIndex(key);
		return value;
	}
	
	protected int getIndex(Object key2){
		K key = (K) key2;
		int index=-1;
		int hashCode = Math.abs(key.hashCode()%this.keySplits);
		
		
		
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
		return index;
	}
	
	protected MyEntry<K,V> getFromOffHeapByIndex(K key){
		int index = this.getIndex(key);

		if(index>=0){
			boolean  b= excerpt.index(index);
			if(!b){
				throw new RuntimeException("Error reading for key " + index + " from offheap");
			}
			else{
				//this.mostRecentlyUsedMap.put(key, index);
				//this.mostRecentlyUsed.add(key);				
				MyEntry<K,V> val=(MyEntry<K,V>)excerpt.readObject();
				return val;

			}		
		}
		return null;
	
	}
	public K getFromOffHeapKeyByIndex(long index){
		

		if(index>=0){
			boolean  b= excerpt.index(index);
			if(!b){
				throw new RuntimeException("Error reading for key " + index + " from offheap");
			}
			else{
				MyEntry<K,V> val=(MyEntry<K,V>)excerpt.readObject();
				return val.getKey();
			}		
		}
		return null;
	
	}
	
	public V getFromOffHeapValueByIndex(long index){
		

		if(index>=0){
			boolean  b= excerpt.index(index);
			if(!b){
				throw new RuntimeException("Error reading for key " + index + " from offheap");
			}
			else{
				MyEntry<K,V> val=(MyEntry<K,V>)excerpt.readObject();
				return val.getValue();
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
		/*}*/

		return val;
	}

	
	public void putAll(Map<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	
	public void clear() {
		// TODO Auto-generated method stub
		chronicle.close();
		for(IndexedChronicle ic:chronicleIndexes){
			ic.close();
		}
		if(this.deleteOnExit){
			File bd = new File(this.basePath);
			bd.delete();
		}
	}
	protected void finalize() throws Throwable {
		chronicle.close();
		for(IndexedChronicle ic:chronicleIndexes){
			ic.close();
		}		
	}

	
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		//This will need an implemntation of Set as well to ensure Memory Management
		throw new UnsupportedOperationException();
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



	protected int getKeyChronicleIndex(K key, int index){
		int remainder = key.hashCode()%1000;
		return remainder;
	}
	

	protected static String getTmpCacheName(){
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
		return (offHeapIndex-this.deleteKeys.size());
	}

		
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return this.size()==0;
	}

		
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		boolean contains = this.mapKeyBloom.mightContain((K)key);
		if(!contains){
			return false;
		}
		contains = this.mostRecentlyUsedMap.get(key)!=null;
		if(contains){
			return true;
		}
		else{
			return this.getIndex(key)>=0;
		}
		
	}
	
	public boolean mightContain(Object key) {
		// TODO Auto-generated method stub
		boolean contains = this.mapKeyBloom.mightContain((K)key);
		return contains;
	}
	
	protected int getKeyIndex(Object key) {
		// TODO Auto-generated method stub
		boolean contains = this.mapKeyBloom.mightContain((K)key);
		if(!contains){
			return -1;
		}
		int index = this.getIndex(key);
		return index;
		
	}

		
	public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
			
	}

    protected  static byte[] serialize(Object obj) {
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
    
    protected  Object deserialize(byte[] bytes)  {
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
		Map<String,List<Integer>> bm = new BigMap<String,List<Integer>>(false,"MyCacheTest");
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
    }
    
    public static void readMap(){
		int max = 1000000;
		System.setProperty("BigMapCachePath", "C:/tmp");
		Map<String,List<Integer>> bm = new BigMap<String,List<Integer>>(false,"MyCacheTest");

		String key = Integer.toString(max-1);
		//bm.remove(key);
		for(int i=0;i<3;i++){
			long millis = System.currentTimeMillis();
			System.out.println( bm.get(Integer.toString(500000)));
			System.out.println(System.currentTimeMillis()-millis);
		}
		//bm.clear();
		//bm.cleanup();
    }
    

/*
	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
*/
	public static void main(String[] args){
		createNewMap();
		//readMap();
	}
	
	protected void reclaimMemory() {
		System.out.println("About to reclaim memory for " + this.cacheName);
		//this.emptyAndStoreIndexes();
		try{
			this.chronicle.close();
			/*
			for(IndexedChronicle index:this.chronicleIndexes){
				index.close();
			}
			*/
			System.gc();
			Thread.currentThread().sleep(2000);	
		}
		catch(Exception e){
			System.out.println("Looks like trouble. Cannot pause to wait for memory to be reclaimed");
		}
		try{
			this.chronicle= new IndexedChronicle(this.getFilePath());
			this.excerpt = this.chronicle.createExcerpt(); 
			/*
			this.chronicleIndexes.clear();
			this.excerptIndexes.clear();
			for(int i=0;i<this.keySplits;i++){
				this.createNextChronicle(i);
			}
			*/
		}
		catch(Exception e){
			throw new RuntimeException("Trouble recreating chronicles again after gc");
		}
		
	}
	
	protected void emptyAndStoreIndexes(){
        for(int offset=0;offset<this.keySplits;offset++){
        	Map<K,Integer> cMap = this.mapChronicleIndexes.get(offset);
        	if(cMap!=null){
        		this.addToExcerpt(cMap, this.excerptIndexes.get(offset));
        		cMap=null;
        		cMap=new HashMap<K,Integer>();
        		this.mapChronicleIndexes.put(offset, cMap);
				int noOfOffsets=this.countOfChronicles.get(offset)+1;
				this.countOfChronicles.put(offset, noOfOffsets);        		
        	}
        }  
        this.mostRecentlyUsed.clear();
        this.mostRecentlyUsedMap.clear();
        this.mostRecentlyUsedMap =null;
        this.mostRecentlyUsed=null;
        this.mostRecentlyUsedMap = new HashMap<K,V>();
		this.mostRecentlyUsed= new ConcurrentLinkedQueue();
    	
    	
	}
	
	private void writeObject(ObjectOutputStream oos)
	        throws IOException {
	            // default serialization 
	            oos.defaultWriteObject();
	            // write the object

	            //oos.writeObject(loc);
	}

	private void readObject(ObjectInputStream ois)
	        throws ClassNotFoundException, IOException {
	    
	    System.setProperty("InvokedFromSpring","true");
	    ois.defaultReadObject();
	    this.initialize();
	}
    
}
