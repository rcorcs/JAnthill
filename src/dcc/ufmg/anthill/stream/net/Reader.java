package dcc.ufmg.anthill.stream.net;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 02 August 2013
 */

import java.io.IOException;

import dcc.ufmg.anthill.Settings;
import dcc.ufmg.anthill.AppSettings;
import dcc.ufmg.anthill.WebServerSettings;
import dcc.ufmg.anthill.info.FlowInfo;
import dcc.ufmg.anthill.info.ModuleInfo;
import dcc.ufmg.anthill.net.WebClient;
import dcc.ufmg.anthill.stream.StreamNotWritable;
import dcc.ufmg.anthill.stream.JSONStream;
import dcc.ufmg.anthill.stream.net.NetStreamServer;

/**
 * This class is a input stream that reads a String from other streams in the network.
 * @see BroadcastLineWriter
 * @see RoundRobinLineWriter
 */
public class Reader<StreamingType> extends JSONStream<StreamingType> {
	private NetStreamServer server;
	private int endCount;

	/**
	 * The default constructor of the LineReader class.
	 */
	public Reader(){
		super();
		endCount = 0;
		server = null;
	}

	/**
	 * ...
	 */
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

	public void write(StreamingType data) throws IOException{
		throw new StreamNotWritable();
	}

	public StreamingType read() throws IOException{
		if(server==null) throw new IOException();
		while(server.isAlive() && !server.hasData()){
			if(server.count>=endCount) {
				server.finish();
				break;
			}
			try{Thread.sleep(100);}catch(InterruptedException e){}
		}
		if(server.hasData()){
			return decode(server.popData());
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
