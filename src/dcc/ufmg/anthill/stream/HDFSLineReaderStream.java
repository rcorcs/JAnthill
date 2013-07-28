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

public class HDFSLineReaderStream extends Stream<String> {
	private BufferedReader reader;
	private FileSystem fileSystem;

	public HDFSLineReaderStream(){
		reader = null;
		fileSystem = null;
	}

	public void start(String hostName, int taskId){
		try{
			fileSystem = FileSystem.get(new Configuration());
		}catch(IOException e){
			e.printStackTrace();
			return;
		}
		//String fileName = Settings.getHostInfo(hostName).getWorkspace()+AppSettings.getName()+"/"+getModuleInfo().getAttribute("input");
		String fileName = getModuleInfo().getAttribute("input");
		//String nameNodeAddr = Settings.getHostInfo(hostName).getAddress()+":"+Settings.getHostInfo(hostName).getHDFSInfo().getPort();
		String nameNodeAddr = "localhost"+":"+Settings.getHostInfo(hostName).getHDFSInfo().getPort();
		Path path = new Path("hdfs://"+nameNodeAddr+fileName);
		//Path path = new Path("hdfs://localhost:54310/home/hduser/gutenberg/pg5000.txt");
		try{
			if(fileSystem.exists(path)){
				reader = new BufferedReader(new InputStreamReader(fileSystem.open(path)));
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void write(String data) throws StreamNotWritable, IOException {
		throw new StreamNotWritable();
	}

	public String read() throws StreamNotReadable, IOException {
		if(reader==null) throw new IOException();
		return reader.readLine(); //return null if EOF
	}

	public void finish(){
		try{
			reader.close();
			fileSystem.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
