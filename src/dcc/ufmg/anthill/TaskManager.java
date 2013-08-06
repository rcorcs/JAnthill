package dcc.ufmg.anthill;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 24 July 2013
 */

import java.io.IOException;
import java.io.File;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

import java.util.Deque;
import java.util.ArrayDeque;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;
import dcc.ufmg.anthill.environment.*;

public class TaskManager {
	private TaskScheduler taskScheduler;
	private Environment environment;

	private ArrayList<TaskMonitor> tasks;
	private Deque<SequenceItemInfo> sequenceDeque;

	public TaskManager(TaskScheduler taskScheduler, Environment environment){
		this.taskScheduler = taskScheduler;
		this.environment = environment;

		tasks = new ArrayList<TaskMonitor>();

		sequenceDeque = new ArrayDeque<SequenceItemInfo>();
	}

	/*
	private TaskThread cloneTask(TaskThread task){
		String hostName = taskScheduler.nextHost(task.getModuleInfo(), task.getTaskId());
		//DEBUG LOG
		Logger.info("Cloning Task "+task.getModuleInfo().getName()+" taskId "+task.getTaskId()+" host "+hostName);
		TaskThread thread = new TaskThread(environment, hostName, task.getModuleInfo(), task.getTaskId());
		this.tasks.add(thread);
		TaskSettings.addTaskInfo(new TaskInfo(hostName, task.getModuleInfo(), task.getTaskId()));
		return thread;
	}
	*/

	private void createTask(String hostName, ModuleInfo moduleInfo, int taskId){
		//DEBUG LOG
		Logger.info("Creating Task "+moduleInfo.getName()+" taskId "+taskId+" host "+hostName);

		TaskInfo taskInfo = new TaskInfo(hostName, moduleInfo, taskId);
		TaskMonitor monitor = environment.instantiate(taskInfo);
		this.tasks.add(monitor);
		TaskSettings.addTaskInfo(taskInfo);
	}

	public void start(){
		environment.start();
		taskScheduler.start();

		for(SequenceItemInfo info : AppSettings.getSequence()){
			sequenceDeque.addLast(info);
		}

		Logger.info("Sequence: "+sequenceDeque.size());

		environment.setup();
	}

	public void createTasks(){
		while(sequenceDeque.size()>0){
			SequenceItemInfo sequenceItem = sequenceDeque.removeFirst(); // Remove the host from the top
			if(sequenceItem.getModuleName()!=null && sequenceItem.getModuleName().trim().length()>0){
				Logger.info("Sequence Item: "+sequenceItem.getModuleName());
				ModuleInfo moduleInfo = AppSettings.getModuleInfo(sequenceItem.getModuleName());
				//DEBUG LOG
				Logger.info("Creating "+moduleInfo.getInstances()+" instances of the module "+moduleInfo.getName());
				for(int i = 0; i<moduleInfo.getInstances(); i++){
					String hostName = taskScheduler.nextHost(moduleInfo, i+1);
					createTask(hostName, moduleInfo, i+1);
				}
			}
			if(sequenceItem.isBreak()){
				break;
			}
		}
	}

	public void runTasks(){
		//DEBUG LOG
		Logger.info("Starting "+this.tasks.size()+" tasks");

		for(TaskMonitor monitor : this.tasks){
			monitor.start();
		}
		boolean done;
		do{
			done = true;
			for(int i = 0; i<tasks.size(); i++){
				TaskMonitor monitor = tasks.get(i);
				if(monitor.isAlive() && !monitor.isInterrupted()){
					done = false;
				}/*else{
					if(thread.getErrorCode()!=0){
						//DEBUG LOG
						Logger.warning("Task "+thread.getModuleInfo().getName()+" taskId "+thread.getTaskId()+" host "+thread.getHostName()+" Error("+thread.getErrorCode()+")");

						tasks.remove(i);
						TaskSettings.removeTaskInfo(new TaskInfo(thread.getHostName(), thread.getModuleInfo(), thread.getTaskId()));
						TaskThread newTask = cloneTask(thread);
						newTask.start();
					}
				}*/
			}
		}while(!done);

		//DEBUG LOG
		Logger.info("All "+this.tasks.size()+" tasks have finished");
		for(TaskMonitor monitor : this.tasks){
			try{monitor.interrupt(); monitor.join();}catch(InterruptedException e){e.printStackTrace();}
		}
	}

	public void finishTasks(){
		this.tasks.clear();
		TaskSettings.clear();
	}

	public void run(){
		do{
			createTasks();
			runTasks();
			finishTasks();
		}while(sequenceDeque.size()>0);
	}
	public void finish(){
		environment.finish();
	}
}

