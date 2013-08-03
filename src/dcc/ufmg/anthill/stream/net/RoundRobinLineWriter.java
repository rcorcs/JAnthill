package dcc.ufmg.anthill.stream.net;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 02 August 2013
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

public class RoundRobinLineWriter extends Stream<String> {
	int []ports;
	String []addresses;
	Socket []sockets;
	DataOutputStream []outs;
	int next;

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

		sockets = new Socket[addresses.length];
		outs = new DataOutputStream[addresses.length];
		for(int i = 0; i<addresses.length; i++){
			try{
				sockets[i] = new Socket(addresses[i], ports[i]);
				outs[i] = new DataOutputStream(sockets[i].getOutputStream());
			}catch(IOException e){
				e.printStackTrace();
				System.exit(-1);
			}
		}
		next = 0;
	}

	public void write(String data) throws StreamNotWritable, IOException{
		outs[next].writeBytes(data+"\n");
		next++;
		if(next>=addresses.length) next = 0;
	}

	public String read() throws StreamNotReadable, IOException {
		throw new StreamNotReadable();
	}

	public void finish() {
		for(int i = 0; i<addresses.length; i++){
			try{
				//outs[i].writeBytes("\0\n");
				outs[i].close();
				sockets[i].close();
			}catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
}
