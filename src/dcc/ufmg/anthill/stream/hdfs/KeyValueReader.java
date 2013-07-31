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

public class KeyValueReader extends Stream< SimpleEntry<String,String> > {
	private Type dataType;
	private Gson gson;

	private FileSystem fileSystem;
	private Deque<Path> pathsDeque;
	private BufferedReader reader;
	//private JsonReader reader;

	private HashMap<String, String> keyFiles;
	private SortedSet<String> keySet;

	private BufferedReader keyReader;
	private String currentKey;

	public KeyValueReader(){
		dataType = new TypeToken< SimpleEntry<String,String> >() {}.getType();
		gson = new Gson();

		fileSystem = null;
		pathsDeque = new ArrayDeque<Path>();
		reader = null;
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
	}

	public void write(SimpleEntry<String,String> data) throws StreamNotWritable, IOException {
		throw new StreamNotWritable();
	}

	public SimpleEntry<String,String> read() throws StreamNotReadable, IOException {
		if(reader==null) throw new IOException();
		else {
			/*SimpleEntry<String,String> data = gson.fromJson(reader, dataType);
			if(data!=null){
				return data;
			}else{
				reader.close();
				if(pathsDeque.size()==0) return null;
				else {
					Path path = pathsDeque.removeFirst();
					reader = new JsonReader(new BufferedReader(new InputStreamReader(fileSystem.open(path))));
					return read();
				}
			}
			*/
			
			String line = reader.readLine();
			if(line != null){
				/*String []keyval = line.split("=");
				if(keyval.length==2 && keyval[0].length()>0 && keyval[1].length()>0){
					return new SimpleEntry<String,String>(keyval[0], keyval[1]);
				}else {
					return read();
				}*/
				SimpleEntry<String,String> data = gson.fromJson(line, dataType);
				return data;
			}else {
				reader.close();
				if(pathsDeque.size()==0) return null;
				else {
					Path path = pathsDeque.removeFirst();
					reader = new BufferedReader(new InputStreamReader(fileSystem.open(path)));
					return read();
				}
			}
			
		}
	}

	public void finish(){
		try{
			if(reader!=null) reader.close();
			fileSystem.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
