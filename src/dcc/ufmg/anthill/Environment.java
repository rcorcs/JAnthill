package dcc.ufmg.anthill;
/**
 * @authors: Rodrigo Caetano O. ROCHA
 * @date: 26 July 2013
 */

import dcc.ufmg.anthill.info.ModuleInfo;

public abstract class Environment {
	public abstract void start();
	public abstract void setup();
	public abstract int instantiate(String hostName, ModuleInfo moduleInfo, int taskId);
	public abstract void finish();
}
