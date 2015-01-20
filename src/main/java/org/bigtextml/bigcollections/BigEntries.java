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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

import org.bigtextml.client.BaseWorkingDirectory;

import com.higherfrequencytrading.chronicle.Excerpt;
import com.higherfrequencytrading.chronicle.impl.IndexedChronicle;
import com.higherfrequencytrading.chronicle.tools.ChronicleTools;

public class BigEntries extends BigMap<Integer,Integer>{

	public BigEntries(boolean deleteOnExit,String cacheName){
		/*
		this.setDeleteOnExit(deleteOnExit);
		this.setCacheName(cacheName);
		if(System.getProperty("InvokedFromSpring")==null){			
			this.initialize();
		}
		*/
		super(deleteOnExit,cacheName);
	}

	public BigEntries(){
		super();
		/*
		if(System.getProperty("InvokedFromSpring")==null){
			this.cacheName= BigEntries.getTmpCacheName();
			this.initialize();
		}
		*/
	}


	/*
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
							this.cacheName=BigEntries.getTmpCacheName();
						else{
							//Do nothing. Recover from the existing files
							break;
						}
					}
				}
			}	
		}		
	}
	*/
	
	@Override
	protected void recreateIndexesFromFile() throws Exception{
		if(this.deleteOnExit)
			ChronicleTools.deleteOnExit(this.getFilePath());
		
		this.chronicle= new IndexedChronicle(this.getFilePath());
		
		//There is an edge condition when size is 1. Needs to be fixed.
		if(this.chronicle.size()>0){
			for(long i=0;i<this.chronicle.size()+1;i++){
				if(this.chronicle.getIndexData(i)>this.offHeapIndex){
					this.offHeapIndex=(int)i;
				}
			}
			//this.offHeapIndex = this.offHeapIndex-1;
		}
	}
	
	@Override
	public void initialize(){
		try{			
			this.createEnvironment();
			
			this.recreateIndexesFromFile();

		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.getCause());
		}		
	}




	public Integer get(Object key) {
		int key2 = (Integer) key;
		int val = (int)this.chronicle.getIndexData((key2+1));
		return val;
	}
	

	@Override
	public Integer put(Integer key, Integer value) {
		this.chronicle.setIndexData((key+1), value);
		//this.offHeapIndex=this.offHeapIndex+1;	
		if(!this.containsKey((key+1))){
			
			this.offHeapIndex=this.offHeapIndex+1;	
		}		
		return value;
	}
	


	
	public Integer remove(Object key) {
		throw new UnsupportedOperationException();
	}

	
	public void putAll(Map<? extends Integer, ? extends Integer> m) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	
	public void clear() {
		// TODO Auto-generated method stub
		chronicle.clear();
		
		
	}
	protected void finalize() throws Throwable {
		chronicle.close();

	}

	
	public Set<Integer> keySet() {
		// TODO Auto-generated method stub
		//This will need an implemntation of Set as well to ensure Memory Management
		throw new UnsupportedOperationException();
	}

	
	public Collection<Integer> values() {
		// TODO Auto-generated method stub
		//This will need an implemntation of Set as well to ensure Memory Management
		throw new UnsupportedOperationException();

		//return null;
	}

	
	public Set<java.util.Map.Entry<Integer, Integer>> entrySet() {
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



	

	public static String getTmpCacheName(){
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
			
		


		
	public int size() {
		// TODO Auto-generated method stub
		//return ((offHeapIndex-this.deleteKeys.size())+ (int)this.map.size());
		return this.offHeapIndex;
	}
	public int size2() {
		// TODO Auto-generated method stub
		//return ((offHeapIndex-this.deleteKeys.size())+ (int)this.map.size());
		return (int)this.chronicle.size();
	}
		
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return this.offHeapIndex==0;
	}

		
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		long myKey = ((Integer) key).longValue();
		return (myKey <= this.size());
		
	}

		
	public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			return ((Long)value < this.chronicle.size());
			
	}


    
    
    public static BigEntries createNewMap2(){
		int max = 300000;
		System.setProperty("InvokedFromSpring","true");
		//System.setProperty("BigMapCachePath", "C:/tmp");
		BigEntries bm = new BigEntries(false,"MyCacheEntriesTest");
		//bm.setRootPath("c:/tmp/");
		BaseWorkingDirectory wd = new BaseWorkingDirectory();
		wd.setDirectory("c:/tmp");
		bm.setWorkingDirectory(wd);
		bm.initialize();
		//bm.setMaxCapacity(1000);

		for(int i=0;i<max;i++){
			System.out.println(i);
			bm.put(i, i);
		}
		System.out.println(bm.size());
		saveObject(bm,new File("c:/tmp/x.ser"));
		return bm;
		
    }
    
    public static void readMap(){
		int max = 200;
		
		System.setProperty("BigMapCachePath", "C:/tmp");
		System.setProperty("InvokedFromSpring","true");
		BigEntries bm = new BigEntries(false,"MyCacheEntriesTest");
		BaseWorkingDirectory wd = new BaseWorkingDirectory();
        wd.setDirectory("c:/tmp");
        bm.setWorkingDirectory(wd);
        bm.initialize();
		String key = Integer.toString(max-1);
		System.out.println( bm.get(499));
		System.out.println( bm.size());
		

    }
    
    public static void readMap2(){
        int max = 200;
        BigEntries bm = readObject(new File("c:/tmp/x.ser"));
        System.out.println(bm.getWorkingDirectory().toString());
        //bm.initialize();
        //System.setProperty("BigMapCachePath", "C:/tmp");
        
        //BigEntries bm = new BigEntries(false,"MyCacheEntriesTest");
        //BaseWorkingDirectory wd = new BaseWorkingDirectory();
        //wd.setWorkingDirectory("c:/tmp");
        //bm.setWorkingDirectory(wd);
        //bm.initialize();
        String key = Integer.toString(max-1);
        System.out.println( bm.get(499));
        System.out.println( bm.size());
        

    }
    public static void saveObject(BigMap model,
            File serializedFile)  {

        // The standard method for saving classifiers in
        // Mallet is through Java serialization. Here we
        // write the classifier object to the specified file.
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                    serializedFile));
            oos.writeObject(model);
            oos.close();
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }

    }
 
    public static BigEntries readObject(File serializedFile) {
        try{
            BigEntries model;
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializedFile));
            model = (BigEntries) ois.readObject();
            ois.close();
            return model;   
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }


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
 /*   
    protected void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        System.out.println("DDDD");
        System.setProperty("InvokedFromSpring","true");
        ois.defaultReadObject();
        
    }
    */
	public static void main(String[] args){
		//createNewMap2();		
		readMap2();
	}
    
}
