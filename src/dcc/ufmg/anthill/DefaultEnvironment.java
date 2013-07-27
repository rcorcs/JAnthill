package dcc.ufmg.anthill;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 26 July 2013
 */

import java.io.IOException;
import java.io.File;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class DefaultEnvironment extends Environment {
	private HashMap<String, SSHHost> sshHosts = new HashMap<String, SSHHost>();

	public void start(){
		validateHosts();

		HostInfo hostInfo;
		for(String hostName : Settings.getHosts()){
			hostInfo = Settings.getHostInfo(hostName);
			this.sshHosts.put(hostName, new SSHHost(hostInfo.getSSHInfo().getUser(), hostInfo.getSSHInfo().getPassword(), hostInfo.getAddress(), hostInfo.getSSHInfo().getPort()) );
		}
		
	}

	public void setup(){
		for(String hostName : Settings.getHosts()){
			SSHHost host = this.sshHosts.get(hostName);
			host.uploadFile(Settings.getFileName(), Settings.getFilePath(), Settings.getHostInfo(hostName).getWorkspace());
			host.uploadFile(AppSettings.getFileName(), AppSettings.getFilePath(), Settings.getHostInfo(hostName).getWorkspace()+AppSettings.getName()+"/");
			for(File file : AppSettings.getFiles()){
				host.uploadFile(file.getName(), file.getParent(), Settings.getHostInfo(hostName).getWorkspace()+AppSettings.getName()+"/");
			}
		}
	}
	
	public int instantiate(String hostName, ModuleInfo moduleInfo, int taskId){
		HostInfo hostInfo = Settings.getHostInfo(hostName);

		String anthillPath = hostInfo.getWorkspace()+Settings.getFileName();
		String appPath = hostInfo.getWorkspace()+AppSettings.getName()+"/"+AppSettings.getFileName();
		String appXml = hostInfo.getWorkspace()+AppSettings.getName()+"/"+(new File(AppSettings.getXMLFileName())).getName();
		String xml = hostInfo.getWorkspace()+AppSettings.getName()+"/"+(new File(Settings.getXMLFileName())).getName();

		String cmd = "java -cp "+anthillPath+":"+appPath+" "+Settings.getClassName()+" -tid "+taskId+" -app-xml "+appXml+" -m "+moduleInfo.getName()+" -xml "+xml+" -h "+hostName+" -sa localhost -sp 8080"+" > "+hostInfo.getWorkspace()+AppSettings.getName()+"/logTaskId"+taskId+".log";

		
		int error = this.sshHosts.get(hostName).executeInBackground(cmd);

		if(error!=0){
			InetAddress inetAddr = null;
			try{
				inetAddr = InetAddress.getByName(hostInfo.getAddress());
			}catch(UnknownHostException e){
				e.printStackTrace();
			}
			try{
				if(!inetAddr.isReachable(500)){ //the host is not reachable
					//DEBUG LOG
					Logger.warning("Host "+hostName+" NOT reachable");
					Settings.removeHost(hostName); //remove host
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}

		return error;
	}
	
	public void finish(){
		//DEBUG LOG
		Logger.info("Closing "+Settings.getHosts().size()+" hosts connections");
		for(String hostName : Settings.getHosts()){
			//DEBUG LOG
			Logger.info("Closing connection with host "+hostName);
			this.sshHosts.get(hostName).close();
		}
	}

	private void validateHosts(){
		Set<String> hosts = Settings.getHosts();
		Set<String> invalidHosts = new HashSet<String>();
		InetAddress inetAddr = null;
		for(String hostName : hosts){
			HostInfo hostInfo = Settings.getHostInfo(hostName);
			try{
				inetAddr = InetAddress.getByName(hostInfo.getAddress());
			}catch(UnknownHostException e){
				e.printStackTrace();
			}
			try{
				if(!inetAddr.isReachable(500)){ //the host is not reachable
					invalidHosts.add(hostName);
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		hosts = null;

		for(String hostName : invalidHosts){
			//DEBUG LOG
			Logger.warning("Host "+hostName+" NOT Reachable");
			Settings.removeHost(hostName);
		}
	}
}