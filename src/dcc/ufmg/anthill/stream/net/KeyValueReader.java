package dcc.ufmg.anthill.stream.net;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 02 August 2013
 */

import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import java.util.AbstractMap.SimpleEntry;

import dcc.ufmg.anthill.Settings;
import dcc.ufmg.anthill.AppSettings;
import dcc.ufmg.anthill.WebServerSettings;
import dcc.ufmg.anthill.info.FlowInfo;
import dcc.ufmg.anthill.info.ModuleInfo;
import dcc.ufmg.anthill.net.WebClient;
import dcc.ufmg.anthill.stream.StreamNotWritable;
import dcc.ufmg.anthill.stream.JSONStream;
import dcc.ufmg.anthill.stream.net.NetStreamServer;

public class KeyValueReader<KeyType, ValueType> extends JSONStream< SimpleEntry<KeyType, ValueType> > {
	private NetStreamServer server;
	private int endCount;

	public KeyValueReader(){
		super();
		setDataType( new TypeToken< SimpleEntry<KeyType, ValueType> >() {}.getType() );
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

	public void write(SimpleEntry<KeyType, ValueType> data) throws IOException{
		throw new StreamNotWritable();
	}

	public SimpleEntry<KeyType, ValueType> read() throws IOException{
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
				return decode(str);
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
