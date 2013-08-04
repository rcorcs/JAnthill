package dcc.ufmg.anthill.stream.hdfs;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 30 July 2013
 */
//TODO MAKE IT WORK WITH TYPES OTHER THAN STRING (use Java Generics)
//import com.google.gson.Gson;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.lang.reflect.Type;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;

import org.apache.commons.lang.RandomStringUtils;

import java.util.AbstractMap.SimpleEntry;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileStatus;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class SortedKeyValueReader extends Stream< SimpleEntry<String,String> > {
	private Type dataType;
	private Gson gson;

	private FileSystem fileSystem;
	private Deque<Path> pathsDeque;
	private BufferedReader reader;
	//private JsonReader reader;

	private String currentKey;

	private HashMap<String, Deque<String> > pairs;
	private SortedSet<String> keySet;
	private Iterator<String> keyIterator;

	public SortedKeyValueReader(){
		dataType = new TypeToken< SimpleEntry<String,String> >() {}.getType();
		gson = new Gson();

		fileSystem = null;
		pathsDeque = new ArrayDeque<Path>();
		reader = null;

		keyIterator = null;
		currentKey = null;

		pairs = new HashMap<String, Deque<String> >();
		keySet = new TreeSet<String>();
	}

	public void start(String hostName, int taskId){
		try{
			fileSystem = FileSystem.get(new Configuration());
		}catch(IOException e){
			e.printStackTrace();
			return;
		}

		//check for possible errors
		if(getStreamInfo().getAttribute("path")==null) return;

		String folder = 	getStreamInfo().getAttribute("path");
		
		String nameNodeAddr = "localhost"+":"+Settings.getHostInfo(hostName).getHDFSInfo().getPort();
		
		FileStatus[] status = null;
		try{
			status = fileSystem.listStatus(new Path("hdfs://"+nameNodeAddr+folder+("keyset"+(taskId))+"/"));
		}catch(IOException e){
			e.printStackTrace();
			return;
		}

		for(int i=0;i<status.length;i++){
			pathsDeque.add(status[i].getPath());
		}

		Path path = pathsDeque.removeFirst();
		try{
			//reader = new JsonReader(new BufferedReader(new InputStreamReader(fileSystem.open(path))));
			reader = new BufferedReader(new InputStreamReader(fileSystem.open(path)));
		}catch(IOException e){
			e.printStackTrace();
			return;
		}

		SimpleEntry<String,String> p;
		while( true ){
			try{
				p=readPair();
			}catch(IOException e){
				e.printStackTrace();
				break;
			}
			if(p==null)break;

			keySet.add(p.getKey());
			if(!pairs.containsKey(p.getKey())){
				pairs.put(p.getKey(), new ArrayDeque<String>());
			}
			pairs.get(p.getKey()).addLast(p.getValue());
		}
		
		try{
			if(reader!=null) reader.close();
			fileSystem.close();
		}catch(IOException e){
			e.printStackTrace();
		}

		keyIterator = keySet.iterator();
		if(keyIterator.hasNext()){
			currentKey = keyIterator.next();
		}
	}

	public void write(SimpleEntry<String,String> data) throws StreamNotWritable, IOException {
		throw new StreamNotWritable();
	}

	public SimpleEntry<String,String> readPair() throws IOException {
		if(reader==null) throw new IOException();
		else{
			String line = reader.readLine();
			if(line!=null){
				SimpleEntry<String,String> data = gson.fromJson(line, dataType);
				return data;
			}else{
				reader.close();
				if(pathsDeque.size()==0) return null;
				else{
					Path path = pathsDeque.removeFirst();
					reader = new BufferedReader(new InputStreamReader(fileSystem.open(path)));
					return readPair();
				}
			}
		}
	}

	public SimpleEntry<String,String> read() throws StreamNotReadable, IOException {
		if(currentKey==null) throw new IOException();
		else {
			if(pairs.get(currentKey).size()>0){
				String val = pairs.get(currentKey).removeFirst();
				return new SimpleEntry<String,String>(currentKey, val);
			}else{
				if(keyIterator.hasNext()){
					currentKey = keyIterator.next();
					return read();
				}else return null;
			}
		}
	}

	public void finish(){}
}
