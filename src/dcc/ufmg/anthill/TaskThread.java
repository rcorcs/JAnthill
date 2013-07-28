package dcc.ufmg.anthill;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 23 July 2013
 */

import java.io.File;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class TaskThread extends Thread{
	private int taskId;
	private String hostName;
	private ModuleInfo moduleInfo;
	private Environment environment;
	private int error;

	public TaskThread(Environment environment, String hostName, ModuleInfo moduleInfo, int taskId){
		this.environment = environment;
		this.hostName = hostName;
		this.moduleInfo = moduleInfo;
		this.taskId = taskId;
		this.error = 0;
		this.setDaemon(true);
	}

	public void setEnvironment(Environment environment){
		this.environment = environment;
	}

	public Environment getEnvironment(){
		return this.environment;
	}
	
	public void setHostName(String hostName){
		this.hostName = hostName;
	}

	public String getHostName(){
		return this.hostName;
	}

	public void setTaskId(int taskId){
		this.taskId = taskId;
	}

	public int getTaskId(){
		return this.taskId;
	}

	public void setModuleInfo(ModuleInfo moduleInfo){
		this.moduleInfo = moduleInfo;
	}

	public ModuleInfo getModuleInfo(){
		return this.moduleInfo;
	}

	public int getErrorCode(){
		return this.error;
	}

	public void run() {
		//DEBUG LOG
		//Log.get().info("Started Running "+moduleInfo.getName()+" taskId "+this.taskId+" host "+this.hostInfo.getName());
		Logger.info("Started Running "+this.moduleInfo.getName()+" taskId "+this.taskId+" host "+this.hostName);

		this.error = this.environment.instantiate(hostName, moduleInfo, taskId);

		//this.error = sshhost.executeInBackground("/usr/local/hadoop/bin/hadoop jar "+remoteFolder+fileName+" "+appName+" > output"+taskId);

		//Log.get().info("Finished Running "+moduleInfo.getName()+" taskId "+this.taskId+" host "+this.hostInfo.getName());
	}
}
