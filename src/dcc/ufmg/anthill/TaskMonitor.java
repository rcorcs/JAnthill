package dcc.ufmg.anthill;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 05 August 2013
 */

import dcc.ufmg.anthill.Executor;
import dcc.ufmg.anthill.info.TaskInfo;

public class TaskMonitor extends Thread {
	private TaskInfo taskInfo;
	private Executor executor;

	public TaskMonitor(TaskInfo taskInfo, Executor executor){
		this.taskInfo = taskInfo;
		this.executor = executor;
	}

	//get status, useful for fault-tolerance

	public void run(){
		executor.run(taskInfo);//generate status
	}
}
