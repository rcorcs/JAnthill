package dcc.ufmg.anthill.stream.net;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 01 August 2013
 */

import java.io.*;
import java.net.*;

import java.io.IOException;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.lang.reflect.Type;

import org.apache.commons.lang.RandomStringUtils;

import java.util.AbstractMap.SimpleEntry;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class KeyValueReader extends Stream< SimpleEntry<String,String> > {
	private Type dataType;
	private Gson gson;

	private ServerSocket socket;
	private int socketPort;

	public KeyValueReader(){
		dataType = new TypeToken< SimpleEntry<String,String> >() {}.getType();
		gson = new Gson();

		socket = null;
		socketPort = -1;
	}

	public void start(String hostName, int taskId){
		socketPort = 8000 + (int)(Math.random() * (9000 - 8000));
		try{
			WebClient.getContent(WebServerSettings.getStateSetURL(getModuleInfo().getName()+("."+taskId)+".port", ""+socketPort));
			socket = new ServerSocket(socketPort);
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1); //if there is something wrong, exits
		}
	}

	public void write(SimpleEntry<String,String> data) throws StreamNotWritable, IOException{
		throw new StreamNotWritable();
	}

	public SimpleEntry<String,String> read() throws StreamNotReadable, IOException {
		if(socket==null) throw new IOException();

		Socket connectionSocket = socket.accept();
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		String str = inFromClient.readLine();
		if("\0".equals(str)) return null;
		else if(str!=null){
			SimpleEntry<String,String> data = gson.fromJson(str, dataType);
			return data;
		}else return null;
	}

	public void finish() {
		try{
			socket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
