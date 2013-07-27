package dcc.ufmg.anthill.info;
/**
 * @authors: Rodrigo Caetano O. ROCHA
 * @date: 23 July 2013
 */

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class HDFSInfo {
	private String path;
	private int port;

	public HDFSInfo(String path, int port){
		this.path = path;
		this.port = port;
	}

	public void setPath(String path){
		this.path = path;
	}

	public String getPath(){
		return this.path;
	}

	public void setPort(int port){
		this.port = port;
	}

	public int getPort(){
		return this.port;
	}
}

