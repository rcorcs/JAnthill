package dcc.ufmg.anthill.stream.net;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 01 August 2013
 */

import java.io.*;
import java.net.*;

import java.io.IOException;

import java.util.ArrayList;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class BroadcastLineWriter extends Stream<String> {
	int []ports;
	String []addresses;

	public void start(String hostName, int taskId){
		FlowInfo flowInfo = null;
		for(FlowInfo flow : AppSettings.getFlows()){
			if(flow.getFromModuleName().equals(getModuleInfo().getName())){
				flowInfo = flow;
				break;
			}
		}
		ModuleInfo toModuleInfo = AppSettings.getModuleInfo(flowInfo.getToModuleName());

		addresses = new String[toModuleInfo.getInstances()];
		for(TaskInfo taskInfo : TaskSettings.getTasks()){
			if(taskInfo.getModuleInfo().getName().equals(toModuleInfo.getName())){
				addresses[taskInfo.getTaskId()-1] = Settings.getHostInfo(taskInfo.getHostName()).getAddress();
			}
		}

		ports = new int[toModuleInfo.getInstances()];
		for(int i = 0; i<toModuleInfo.getInstances(); i++){
			String key = toModuleInfo.getName()+("."+(i+1))+".port";
			String value = null;
			try{
				value = WebClient.getContent(WebServerSettings.getStateGetURL(key));
			}catch(ConnectException e){
				e.printStackTrace();
				System.exit(-1);
			}catch(ProtocolException e){
				e.printStackTrace();
				System.exit(-1);
			}catch(IOException e){
				e.printStackTrace();
				System.exit(-1);
			}
			int p = -1;
			try{
				p = Integer.parseInt(value);
			}catch(Exception e){
				i = -1;
			}
			if(i==-1){
				try{Thread.sleep(500);}catch(InterruptedException e){}
			}else {
				ports[i] = p;
			}
		}
	}

	public void write(String data) throws StreamNotWritable, IOException{
		for(int i = 0; i<addresses.length; i++){
			Socket clientSocket = new Socket(addresses[i], ports[i]);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(data+"\n");
			clientSocket.close();
		}
	}

	public String read() throws StreamNotReadable, IOException {
		throw new StreamNotReadable();
	}

	public void finish() {
		for(int i = 0; i<addresses.length; i++){
			try{
				Socket clientSocket = new Socket(addresses[i], ports[i]);
				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
				outToServer.writeBytes("\0\n");
				clientSocket.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
