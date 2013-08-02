package dcc.ufmg.anthill.stream;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 26 July 2013
 */

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

import org.apache.commons.lang.RandomStringUtils;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class LineWriterStream extends Stream<String> {
	private PrintWriter writer;

	public LineWriterStream(){
		writer = null;
	}

	public void start(String hostName, int taskId){
		String fileName = getStreamInfo().getAttribute("output");//"test.txt";
		if(fileName==null){
			fileName = RandomStringUtils.randomAlphanumeric(20);
		}
		fileName = Settings.getHostInfo(hostName).getWorkspace()+AppSettings.getName()+("/tid"+taskId)+"/"+fileName;//"test.txt";
		//Logger.info("Writing into "+fileName+" in "+hostName);
		try{
			File dir = new File(Settings.getHostInfo(hostName).getWorkspace()+AppSettings.getName()+("/tid"+taskId)+"/");
			dir.mkdirs();
			writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void write(String data) throws StreamNotWritable, IOException {
		writer.println(data);
	}

	public String read() throws StreamNotReadable, IOException {
		throw new StreamNotReadable();
	}

	public void finish(){
		writer.close();
	}
}
