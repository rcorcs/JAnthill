package dcc.ufmg.anthill.stream.hdfs;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 30 July 2013
 */
//TODO MAKE IT WORK WITH TYPES OTHER THAN STRING (use Java Generics)

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import java.lang.reflect.Type;


import java.io.OutputStreamWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.RandomStringUtils;

import java.util.AbstractMap.SimpleEntry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class KeyValueWriter extends Stream< SimpleEntry<String,String> > {
	private Type dataType;
	private Gson gson;

	private FSDataOutputStream []writers;
	//private JsonWriter []writers;
	private FileSystem fileSystem;
	private int divisor;

	public KeyValueWriter(){
		dataType = new TypeToken< SimpleEntry<String,String> >() {}.getType();
		gson = new Gson();

		writers = null;
		fileSystem = null;
		divisor = 0;
	}

	public void start(String hostName, int taskId){
		try{
			fileSystem = FileSystem.get(new Configuration());
		}catch(IOException e){
			e.printStackTrace();
			return;
		}

		//check for possible errors
		if(getStreamInfo().getAttribute("divisor")==null || getStreamInfo().getAttribute("path")==null) return;

		divisor = Integer.parseInt(getStreamInfo().getAttribute("divisor"));
		String folder = 	getStreamInfo().getAttribute("path");
		String nameNodeAddr = "localhost"+":"+Settings.getHostInfo(hostName).getHDFSInfo().getPort();
		writers = new FSDataOutputStream[divisor];
		//writers = new JsonWriter[divisor];
		for(int i = 0; i<divisor; i++){
			String fileName = RandomStringUtils.randomAlphanumeric(20);
			Path path = new Path("hdfs://"+nameNodeAddr+folder+("keyset"+(i+1))+"/"+fileName);
			try{
				if(fileSystem.exists(path)) {
					//DEBUG LOG
					Logger.severe("Output file "+(folder+("keyset"+(i+1))+"/"+fileName)+" already exists");
					return;
				}
				writers[i] = fileSystem.create(path);
				//writers[i] = new JsonWriter(new OutputStreamWriter(fileSystem.create(path)));
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	public void write(SimpleEntry<String,String> data) throws StreamNotWritable, IOException {
		if(writers!=null && divisor>0){
			String jsonStr = gson.toJson(data, dataType);
			byte[] bytes = (jsonStr.replace('\n', ' ')+"\n").getBytes();
			int writerIndex = (Math.abs(data.getKey().hashCode()))%divisor;
			writers[writerIndex].write(bytes, 0, bytes.length);
			//gson.toJson(data, dataType, writers[writerIndex]);
		}else throw new IOException();
	}

	public SimpleEntry<String,String> read() throws StreamNotReadable, IOException {
		throw new StreamNotReadable();
	}

	public void finish(){
		try{
			if(writers!=null){
				for(int i = 0; i<divisor; i++){
					writers[i].close();	
				}
			}
			fileSystem.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
