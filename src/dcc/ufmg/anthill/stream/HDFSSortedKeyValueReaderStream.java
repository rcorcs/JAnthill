package dcc.ufmg.anthill.stream;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 30 July 2013
 */
//TODO BUG: Too many files opened during the "group by key" phase in the start() method.

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

public class HDFSSortedKeyValueReaderStream extends Stream< SimpleEntry<String,String> > {
	private FileSystem fileSystem;
	private HashMap<String, String> keyFiles;
	private SortedSet<String> keySet;

	private BufferedReader keyReader;
	private String currentKey;

	public HDFSSortedKeyValueReaderStream(){
		fileSystem = null;
		keySet = new TreeSet<String>();
		keyFiles = new HashMap<String, String>();
		currentKey = null;
		keyReader = null;
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

		HashMap<String, PrintWriter> keyWriters = new HashMap<String, PrintWriter>();
		String line;

		String dirPath = Settings.getHostInfo(hostName).getWorkspace()+AppSettings.getName()+"/"+RandomStringUtils.randomAlphanumeric(20)+"/";
		File dir = new File(dirPath);
		dir.mkdirs();

		for(int i=0;i<status.length;i++){
			BufferedReader br = null;
			try{
				br = new BufferedReader(new InputStreamReader(fileSystem.open(status[i].getPath())));
				line = br.readLine();
			}catch(IOException e){
				e.printStackTrace();
				return;
			}
			while (line != null){
				String []keyval = line.split("=");
				if(keyval.length==2 && keyval[0].length()>0 && keyval[1].length()>0){
					if(!keySet.contains(keyval[0])){
						keySet.add(keyval[0]);
						try{
							String writerPath = dirPath+RandomStringUtils.randomAlphanumeric(20);
							keyWriters.put(keyval[0], new PrintWriter(new BufferedWriter(new FileWriter(writerPath))));
							keyFiles.put(keyval[0], writerPath);
						}catch(IOException e){
							e.printStackTrace();
							return;
						}
						keyWriters.get(keyval[0]).println(keyval[1]);
					}
				}
				if(br==null) return;
				try{
					line = br.readLine();
				}catch(IOException e){
					e.printStackTrace();
					return;
				}
			}
		}
		
		for(String key : keySet){
			keyWriters.get(key).close();
		}

		if(keySet.size()>0){
			currentKey = keySet.first();
			keySet.remove(currentKey);
			try{
				keyReader = new BufferedReader(new FileReader(keyFiles.get(currentKey)));
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}
		}
	}

	public void write(SimpleEntry<String,String> data) throws StreamNotWritable, IOException {
		throw new StreamNotWritable();
	}

	public SimpleEntry<String,String> read() throws StreamNotReadable, IOException {
		if(keyReader==null) throw new IOException();
		else {
			String value = keyReader.readLine(); //return null if EOF
			if(value!=null){
				return new SimpleEntry<String,String>(currentKey, value);
			}else{
				if(keySet.size()>0){
					currentKey = keySet.first();
					keySet.remove(currentKey);
					try{
						keyReader.close();
						keyReader = new BufferedReader(new FileReader(keyFiles.get(currentKey)));
					}catch(FileNotFoundException e){
						e.printStackTrace();
					}
					return read();
				}else {
					try{
						keyReader.close();
					}catch(FileNotFoundException e){
						e.printStackTrace();
					}
					return null;
				}
			}
		}		
	}

	public void finish(){
		try{
			//TODO remove every temp file
			fileSystem.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
