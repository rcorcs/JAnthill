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

public class SSHInfo {
	private String user;
	private String password;
	private int port;

	public SSHInfo(String user, String password, int port){
		this.user = user;
		this.password = password;
		this.port = port;
	}
	
	public void setUser(String user){
		this.user = user;
	}

	public String getUser(){
		return this.user;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return this.password;
	}
		
	public void setPort(int port){
		this.port = port;
	}

	public int getPort(){
		return this.port;
	}
}

