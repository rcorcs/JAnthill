package dcc.ufmg.anthill.stream;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 27 July 2013
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.EOFException;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Deque;
import java.util.ArrayDeque;
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

/**
 * 
 */
public class HDFSLineReaderStream extends Stream<String> {
	private FSDataInputStream reader;
	private FileSystem fileSystem;
	private HashMap<String, FileStatus> files;
	private SortedSet<String> fileSet;
	private long totalBytes;
	private long beginPos;
	private long endPos;
	private long pos;
	private StringBuffer buffer;

	public HDFSLineReaderStream(){
		reader = null;
		fileSystem = null;
		files = new HashMap<String, FileStatus>();
		fileSet = new TreeSet<String>();
		totalBytes = 0;
		beginPos = endPos = 0;
		buffer = new StringBuffer();
	}

	private boolean nextFile(){
		if(reader!=null){
			try{
				reader.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		reader=null;
		long totalRead = 0;
		String nextFileName = null;
		long filePos = 0;
		for(String fName : fileSet){
			if((totalRead+files.get(fName).getLen())>pos){
				nextFileName = fName;
				filePos = pos-totalRead;
				break;
			}
			totalRead += files.get(fName).getLen();
		}
		if(nextFileName!=null){
			try{
				reader = fileSystem.open(files.get(nextFileName).getPath());
			}catch(IOException e){
				e.printStackTrace();
				reader = null;
				return false;
			}
			if(filePos>0){ //if the position to read is in the middle of the file, skip the previous line.
				try{
					reader.seek(filePos-1);
				}catch(IOException e){
					e.printStackTrace();
					reader = null;
					return false;
				}
				String temp = null;
				try{
					temp = read();//go to the next line of the file
				}catch(Exception e){}
			}
			return true;
		}else {
			pos = endPos;
			return false;
		}
	}

	public void start(String hostName, int taskId){
		try{
			fileSystem = FileSystem.get(new Configuration());
		}catch(IOException e){
			e.printStackTrace();
			return;
		}
		String nameNodeAddr = "localhost"+":"+Settings.getHostInfo(hostName).getHDFSInfo().getPort();

		String fileName = getStreamInfo().getAttribute("filename");
		if(fileName==null){
			FileStatus[] status = null;
			try{
				status = fileSystem.listStatus(new Path("hdfs://"+nameNodeAddr+getStreamInfo().getAttribute("path")));
			}catch(IOException e){
				e.printStackTrace();
				reader = null;
				return;
			}
			totalBytes = 0;
			for(int i=0;i<status.length;i++){
				totalBytes += status[i].getLen();
				fileSet.add(status[i].getPath().getName());
				files.put(status[i].getPath().getName(), status[i]);
			}
			
			int split = (int)Math.ceil((double)(totalBytes)/(double)(getModuleInfo().getInstances()));
			beginPos = (taskId-1)*split;
			endPos = taskId*split;
		}else{
			//TODO parse <attr name="fileName" value="${module}${tid}pg5000.txt"/>
			FileStatus status = null;
			try{
				status = fileSystem.getFileStatus(new Path("hdfs://"+nameNodeAddr+getStreamInfo().getAttribute("path")+fileName));
			}catch(IOException e){
				e.printStackTrace();
				reader = null;
				return;
			}
			files.put(fileName, status);
			fileSet.add(fileName);
			totalBytes = status.getLen();
			int split = (int)Math.ceil((double)(totalBytes)/(double)(getModuleInfo().getInstances()));
			beginPos = (taskId-1)*split;
			endPos = taskId*split;
		}

		pos = beginPos;
		nextFile();
	}

	public void write(String data) throws StreamNotWritable, IOException {
		throw new StreamNotWritable();
	}

	public String read() throws StreamNotReadable, IOException {
		if(pos>=endPos) return null;

		if(reader==null) throw new IOException();
		else {
			while(true){
				byte ch;
				try{
					pos++; //position tracker
					ch = reader.readByte();
				}catch(EOFException e){
					String str = buffer.toString();
					buffer = new StringBuffer(); //TODO reset the same StringBuffer object
					nextFile();
					return str;
				}
				if(ch==((byte)'\n')){
					String str = buffer.toString();
					buffer = new StringBuffer(); //TODO reset the same StringBuffer object
					return str;
				}else if(ch==((byte)'\r')){
					pos++; //position tracker
					ch = reader.readByte();
					if(ch==((byte)'\n')){
						String str = buffer.toString();
						buffer = new StringBuffer(); //TODO reset the same StringBuffer object
						return str;
					}else {
						String str = buffer.toString();
						buffer = new StringBuffer(); //TODO reset the same StringBuffer object
						buffer.append((char)ch);
						return str;
					}
				}else {
					buffer.append((char)ch);
				}
			}
		}
	}

	public void finish(){
		try{
			if(reader!=null){
				reader.close();
			}
			fileSystem.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
