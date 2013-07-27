package dcc.ufmg.anthill.info;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 23 July 2013
 */

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class HostInfo {
	private String name;
	private String address;
	private String workspace;
	//private int port;
	private SSHInfo sshinfo;
	private HDFSInfo hdfsinfo;
	
	public HostInfo(String name, String address){
		this.name = name;
		this.address = address;
		this.workspace = "./";
		//this.port = port;
		this.sshinfo = null;
		this.hdfsinfo = null;
	}

	public String getName(){
		return this.name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getWorkspace(){
		return this.workspace;
	}

	public void setWorkspace(String workspace){
		this.workspace = workspace;
	}

	public String getAddress(){
		return this.address;
	}

	public void setAddress(String address){
		this.address = address;
	}

	public SSHInfo getSSHInfo(){
		return this.sshinfo;
	}

	public void setSSHInfo(SSHInfo sshinfo){
		this.sshinfo = sshinfo;
	}

	public HDFSInfo getHDFSInfo(){
		return this.hdfsinfo;
	}

	public void setHDFSInfo(HDFSInfo hdfsinfo){
		this.hdfsinfo = hdfsinfo;
	}
}

