package dcc.ufmg.anthill.info;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 26 July 2013
 */

import dcc.ufmg.anthill.info.ModuleInfo;

public class TaskInfo {
	private String hostName;
	private ModuleInfo moduleInfo;
	private int taskId;

	public TaskInfo(String hostName, ModuleInfo moduleInfo, int taskId){
		this.hostName = hostName;
		this.moduleInfo = moduleInfo;
		this.taskId = taskId;
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
}

