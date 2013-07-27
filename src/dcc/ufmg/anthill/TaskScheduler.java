package dcc.ufmg.anthill;
/**
 * @authors: Rodrigo Caetano O. ROCHA
 * @date: 24 July 2013
 */

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public abstract class TaskScheduler {
	public abstract void start();
	public abstract String nextHost(ModuleInfo moduleInfo, int taskId);
}
