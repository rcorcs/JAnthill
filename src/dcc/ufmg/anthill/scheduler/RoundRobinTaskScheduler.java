package dcc.ufmg.anthill.scheduler;
/**
 * @authors: Rodrigo Caetano O. ROCHA
 * @date: 24 July 2013
 */

import java.util.Set;
import java.util.Deque;
import java.util.ArrayDeque;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class RoundRobinTaskScheduler extends TaskScheduler {
	private Deque<String> hostsDeque;

	public RoundRobinTaskScheduler(){
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
