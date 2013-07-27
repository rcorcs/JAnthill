package dcc.ufmg.anthill.stream;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 26 July 2013
 */

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class LineReaderStream extends Stream<String> {
	private BufferedReader reader;

	public LineReaderStream(){
		reader = null;
	}

	public void start(String hostName, int taskId){
		String fileName = Settings.getHostInfo(hostName).getWorkspace()+AppSettings.getName()+"/"+getModuleInfo().getAttribute("input");//"test.txt";
		//Logger.info("Reading from "+fileName+" in "+hostName);
		try{
			reader = new BufferedReader(new FileReader(fileName));
		}catch(FileNotFoundException e){
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
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}