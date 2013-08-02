package dcc.ufmg.anthill.stream.net;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 01 August 2013
 */

import java.io.*;
import java.net.*;

import java.io.IOException;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class LineReader extends Stream<String> {
	private ServerSocket socket = null;
	private int socketPort;
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

	public void write(String data) throws StreamNotWritable, IOException{
		throw new StreamNotWritable();
	}

	public String read() throws StreamNotReadable, IOException {
		if(socket==null) throw new IOException();

		Socket connectionSocket = socket.accept();
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		String str = inFromClient.readLine();
		if("\0".equals(str)) return null;
		else return str;
	}

	public void finish() {
		try{
			socket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
