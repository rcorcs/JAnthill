package dcc.ufmg.anthill;
/**
 * @authors: Rodrigo Caetano O. ROCHA
 * @date: 24 July 2013
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

public class TaskManager {
	//DONE
	//load the app-settings and instantiate the TaskThreads with the Anthill app filters.
	//load the settings, check the reachable hosts and delete the hosts that are not reachable.
	//schedule the tasks properly in the hosts.
	//Round Robin Scheduler - just run a round robin

	//TODO
	//HDFS Scheduler - get the input file info, check the DataNodes that have the file blocks, and schedule properly. 
	//File Scheduler - load the xml input files info, check the hosts that have the pieces of the file, and schedule properly.

	private TaskScheduler taskScheduler;
	private Environment environment;

	private ArrayList<TaskThread> tasks;

	public TaskManager(TaskScheduler taskScheduler, Environment environment){
		this.taskScheduler = taskScheduler;
		this.environment = environment;

		tasks = new ArrayList<TaskThread>();
	}

	private TaskThread cloneTask(TaskThread task){
		String hostName = taskScheduler.nextHost(task.getModuleInfo(), task.getTaskId());
		//DEBUG LOG
		Logger.info("Cloning Task "+task.getModuleInfo().getName()+" taskId "+task.getTaskId()+" host "+hostName);
		TaskThread thread = new TaskThread(environment, hostName, task.getModuleInfo(), task.getTaskId());
		this.tasks.add(thread);
		TaskSettings.addTaskInfo(new TaskInfo(hostName, task.getModuleInfo(), task.getTaskId()));
		return thread;
	}

	private TaskThread createTask(String hostName, ModuleInfo moduleInfo, int taskId){
		//DEBUG LOG
		Logger.info("Creating Task "+moduleInfo.getName()+" taskId "+taskId+" host "+hostName);
		TaskThread thread = new TaskThread(environment, hostName, moduleInfo, taskId);
		this.tasks.add(thread);
		TaskSettings.addTaskInfo(new TaskInfo(hostName, moduleInfo, taskId));
		return thread;
	}

	public void createTasks(){
		environment.start();
		taskScheduler.start();
	
		for(String moduleName : AppSettings.getModules()){
			ModuleInfo moduleInfo = AppSettings.getModuleInfo(moduleName);
			//DEBUG LOG
			Logger.info("Creating "+moduleInfo.getInstances()+" instances of the module "+moduleInfo.getName());
			for(int i = 0; i<moduleInfo.getInstances(); i++){
				String hostName = taskScheduler.nextHost(moduleInfo, i+1);
				createTask(hostName, moduleInfo, i+1);
			}
		}

		environment.setup();
	}

	public void runTasks(){
		//DEBUG LOG
		Logger.info("Starting "+this.tasks.size()+" tasks");

		for(TaskThread thread : this.tasks){
			thread.start();
		}
		boolean done;
		do{
			done = true;
			for(int i = 0; i<tasks.size(); i++){
				TaskThread thread = tasks.get(i);
				if(thread.isAlive()){
					done = false;
				}else{
					if(thread.getErrorCode()!=0){
						//DEBUG LOG
						Logger.warning("Task "+thread.getModuleInfo().getName()+" taskId "+thread.getTaskId()+" host "+thread.getHostName()+" Error("+thread.getErrorCode()+")");

						tasks.remove(i);
						TaskSettings.removeTaskInfo(new TaskInfo(thread.getHostName(), thread.getModuleInfo(), thread.getTaskId()));
						TaskThread newTask = cloneTask(thread);
						newTask.start();
					}
				}
			}
		}while(!done);

		//DEBUG LOG
		Logger.info("All "+this.tasks.size()+" tasks have finished");
		for(TaskThread thread : this.tasks){
			try{thread.join();}catch(InterruptedException e){e.printStackTrace();}
		}
	}

	public void finishTasks(){
		environment.finish();
	}
}

