package dcc.ufmg.anthill;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 26 July 2013
 */

import dcc.ufmg.anthill.info.TaskInfo;
import dcc.ufmg.anthill.TaskMonitor;

public abstract class Environment {
	public abstract void start();
	public abstract void setup();
	public abstract TaskMonitor instantiate(TaskInfo taskInfo);
	public abstract void finish();
}
