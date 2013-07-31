package dcc.ufmg.anthill.stream.hdfs;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 26 July 2013
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.RandomStringUtils;

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

public class LineWriter extends Stream<String> {
	private FSDataOutputStream writer;
	private FileSystem fileSystem;

	public LineWriter(){
		writer = null;
		fileSystem = null;
	}

	public void start(String hostName, int taskId){
		try{
			fileSystem = FileSystem.get(new Configuration());
		}catch(IOException e){
			e.printStackTrace();
			return;
		}
		//String fileName = Settings.getHostInfo(hostName).getWorkspace()+AppSettings.getName()+"/"+getModuleInfo().getName()+"/tid"+taskId+"_"+getModuleInfo().getAttribute("output");
		String fileName = getStreamInfo().getAttribute("filename");
		if(fileName==null){
			fileName = RandomStringUtils.randomAlphanumeric(20);
		}else{
			//parse <attr name="fileName" value="${module}${tid}pg5000.txt"/>
		}
		fileName = getStreamInfo().getAttribute("path")+fileName;
		//String nameNodeAddr = Settings.getHostInfo(hostName).getAddress()+":"+Settings.getHostInfo(hostName).getHDFSInfo().getPort();
		String nameNodeAddr = "localhost"+":"+Settings.getHostInfo(hostName).getHDFSInfo().getPort();
		Path path = new Path("hdfs://"+nameNodeAddr+fileName);
		try{
			if(fileSystem.exists(path)) {
				//DEBUG LOG
				Logger.severe("Output file "+fileName+" already exists");
				return;
			}
			writer = fileSystem.create(path);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void write(String data) throws StreamNotWritable, IOException {
		if(writer!=null){
			byte[] bytes = (data+"\n").getBytes();
			writer.write(bytes, 0, bytes.length);
		}else throw new IOException();
	}

	public String read() throws StreamNotReadable, IOException {
		throw new StreamNotReadable();
	}

	public void finish(){
		try{
			writer.close();
			fileSystem.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
