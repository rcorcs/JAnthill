package dcc.ufmg.anthill.stream.net;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 02 August 2013
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

public class KeyValueReader extends JSONStream< SimpleEntry<String,String> > {
	private NetStreamServer server;
	private int endCount;

	public KeyValueReader(){
		server = null;
		endCount = -1;
	}

	public void start(String hostName, int taskId) throws IOException{
		int socketPort = 8000 + (int)(Math.random() * (9000 - 8000));
		try{
			server = new NetStreamServer(socketPort);
			WebClient.getContent(WebServerSettings.getStateSetURL(getModuleInfo().getName()+("."+taskId)+".port", ""+socketPort));
			server.start();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1); //if there is something wrong, exits
		}
		
		FlowInfo flowInfo = null;
		for(FlowInfo flow : AppSettings.getFlows()){
			if(flow.getToModuleName().equals(getModuleInfo().getName())){
				flowInfo = flow;
				break;
			}
		}
		ModuleInfo fromModuleInfo = AppSettings.getModuleInfo(flowInfo.getFromModuleName());

		endCount = fromModuleInfo.getInstances();//change this, use the voting thing
	}

	public void write(SimpleEntry<String,String> data) throws IOException{
		throw new StreamNotWritable();
	}

	public SimpleEntry<String,String> read() throws IOException{
		if(server==null) throw new IOException();
		while(server.isAlive() && !server.hasData()){
			if(server.count>=endCount) {
				server.finish();
				break;
			}
			try{Thread.sleep(100);}catch(InterruptedException e){}
		}
		if(server.hasData()){
			String str = server.popData();
			if(str!=null){
				SimpleEntry<String,String> data = decode(str);
				return data;
			}else return null;
		}else{
			return null;
		}
	}

	public void finish() throws IOException{
		server.finish();
		try{server.join();}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}
