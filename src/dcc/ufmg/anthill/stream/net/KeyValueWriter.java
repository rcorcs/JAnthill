package dcc.ufmg.anthill.stream.net;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 02 August 2013
 */

import com.google.gson.reflect.TypeToken;

import java.net.Socket;

import java.io.IOException;
import java.io.DataOutputStream;

import java.util.AbstractMap.SimpleEntry;

import dcc.ufmg.anthill.Settings;
import dcc.ufmg.anthill.AppSettings;
import dcc.ufmg.anthill.TaskSettings;
import dcc.ufmg.anthill.WebServerSettings;
import dcc.ufmg.anthill.info.FlowInfo;
import dcc.ufmg.anthill.info.ModuleInfo;
import dcc.ufmg.anthill.info.TaskInfo;
import dcc.ufmg.anthill.net.WebClient;
import dcc.ufmg.anthill.stream.StreamNotReadable;
import dcc.ufmg.anthill.stream.JSONStream;

public class KeyValueWriter<KeyType, ValueType> extends JSONStream< SimpleEntry<KeyType, ValueType> > {
	int []ports;
	String []addresses;
	Socket []sockets;
	DataOutputStream []outs;
	int divisor;

	public KeyValueWriter(){
		super();
		setDataType( new TypeToken< SimpleEntry<KeyType, ValueType> >() {}.getType() );

		ports = null;
		addresses = null;
		sockets = null;
		outs = null;
		divisor = -1;
	}

	public void start(String hostName, int taskId) throws IOException{
		FlowInfo flowInfo = null;
		for(FlowInfo flow : AppSettings.getFlows()){
			if(flow.getFromModuleName().equals(getModuleInfo().getName())){
				flowInfo = flow;
				break;
			}
		}
		ModuleInfo toModuleInfo = AppSettings.getModuleInfo(flowInfo.getToModuleName());

		divisor = toModuleInfo.getInstances();

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
			//try{
				value = WebClient.getContent(WebServerSettings.getStateGetURL(key));
			/*}catch(ConnectException e){
				e.printStackTrace();
				System.exit(-1);
			}catch(ProtocolException e){
				e.printStackTrace();
				System.exit(-1);
			}catch(IOException e){
				e.printStackTrace();
				System.exit(-1);
			}*/
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
			//try{
				sockets[i] = new Socket(addresses[i], ports[i]);
				outs[i] = new DataOutputStream(sockets[i].getOutputStream());
			/*}catch(IOException e){
				e.printStackTrace();
				System.exit(-1);
			}*/
		}
	}

	public void write(SimpleEntry<KeyType, ValueType> data) throws IOException{
		if(data!=null){
			String jsonStr = encode(data);
			//byte[] bytes = (jsonStr.replace('\n', ' ')+"\n").getBytes();

			int writerIndex = (Math.abs(data.getKey().hashCode()))%divisor;

			//outs[writerIndex].writeBytes(jsonStr.replace('\n', ' ')+"\n");
			outs[writerIndex].writeBytes(jsonStr+"\n");
		}
	}

	public SimpleEntry<KeyType, ValueType> read() throws IOException{
		throw new StreamNotReadable();
	}

	public void finish() throws IOException{
		for(int i = 0; i<addresses.length; i++){
			//try{
			//outs[i].writeBytes("\0\n");
			outs[i].close();
			sockets[i].close();
			/*}catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}*/
		}
	}
}
