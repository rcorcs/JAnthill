package dcc.ufmg.anthill.stream.net;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 02 August 2013
 */

import java.io.*;
import java.net.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.net.SocketTimeoutException;

import java.io.IOException;

import java.util.Set;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

class ConnectionHandler extends Thread {
	private NetStreamServer server;
	private Socket socket;
	public ConnectionHandler(NetStreamServer server, Socket socket){
		super("ConnectionHandler");
		super.setDaemon(true);
		this.server = server;
		this.socket = socket;
	}

	public void run() {
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			String inputLine;
			while(true) {
				inputLine = in.readLine();
				//if(inputLine == null || "\0".equals(inputLine)) {
				if(inputLine == null) {
					break;
				}
				else this.server.pushData(inputLine);
			}
			this.server.count++;
			in.close();
			this.socket.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}

public class NetStreamServer extends Thread {
	private final Lock lock = new ReentrantLock();

	private ServerSocket socket;
	private int port;
	private boolean listening;

	private Deque<String> buffer;
	//private ArrayList<Thread> threads;
	public int count = 0;

	public NetStreamServer(int port) throws IOException {
		this(port, 500);
	}

	public NetStreamServer(int port, int timeout) throws IOException {
		super("NetStreamServer");
		super.setDaemon(true);
		this.listening = true;
		this.socket = null;
		this.port = port;
		this.socket = new ServerSocket(this.port);
		this.socket.setSoTimeout(timeout);
		buffer = new ArrayDeque<String>();
		//threads = new ArrayList<Thread>();
	}

	public void setListening(boolean listening){
		this.listening = listening;
	}
	
	public void run(){
		while(this.listening){
			try{
				Thread t = new ConnectionHandler(this, socket.accept());
				//threads.add(t);
				t.start();
			}catch(SocketTimeoutException e){
			}catch(IOException e){
				e.printStackTrace();
			}
		}

		/*for(Thread t : threads){
			try{t.join();}catch(InterruptedException e){e.printStackTrace();}
		}*/
		try{socket.close();}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void pushData(String data){
		try{
			lock.lock();
			this.buffer.addLast(data);
		}finally{
			lock.unlock();
		}
	}

	public String popData(){
		try{
			lock.lock();
			try{
				return this.buffer.removeFirst();
			}catch(NoSuchElementException e){
				return null;
			}
		}finally{
			lock.unlock();
		}
	}

	public boolean hasData(){
		try{
			lock.lock();
			return (this.buffer.size()>0);
		}finally{
			lock.unlock();
		}		
	}
}
