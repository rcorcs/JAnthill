package dcc.ufmg.anthill.scheduler;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 27 July 2013
 */

import java.util.Set;
import java.util.Deque;
import java.util.ArrayDeque;

import dcc.ufmg.anthill.Settings;
import dcc.ufmg.anthill.TaskScheduler;
import dcc.ufmg.anthill.info.ModuleInfo;

//TODO schedule the modules considering data locality
public class HDFSTaskScheduler extends TaskScheduler {
	private Deque<String> hostsDeque;

	public HDFSTaskScheduler(){
		hostsDeque = new ArrayDeque<String>();
	}

	public void start(){
		hostsDeque.clear();
		Set<String> hostsSet = Settings.getHosts();
		for(String hostName : hostsSet){
			hostsDeque.add(hostName);
		}
	}
	
	public String nextHost(ModuleInfo moduleInfo, int taskId){
		String hostName = hostsDeque.removeFirst(); // Remove the host from the top
		hostsDeque.addLast(hostName); // Put the host back at the end
		return hostName;
	}
}
