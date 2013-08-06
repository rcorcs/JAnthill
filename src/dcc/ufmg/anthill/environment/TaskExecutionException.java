package dcc.ufmg.anthill.stream;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 26 July 2013
 */

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class TaskExecutionException extends EnvironmentException {
	private TaskInfo taskInfo;

	public TaskExecutionException(TaskInfo taskInfo){
		super("Task Execution Exception");
		this.taskInfo = taskInfo;
	}

	public TaskInfo getTaskInfo(){
		return this.taskInfo;
	}
}

