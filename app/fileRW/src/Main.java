/**
 * @authors: Rodrigo Caetano O. ROCHA
 * @date: 23 July 2013
 */

import java.io.IOException;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class Main{

	/**
	* @param args
	*/
	public static void main(String[] args) {
		Settings.loadXMLFile(args[0]);//"settings.xml"

		AppSettings.loadXMLFile(args[1]);//"app-settings.xml"

		//System.out.println("Starting Scheduler...");
		RoundRobinTaskScheduler scheduler = new RoundRobinTaskScheduler();

		//System.out.println("Creating Manager...");
		TaskManager manager = new TaskManager(scheduler, new DefaultEnvironment());

		WebServer webServer = null;
		try{
			webServer = new WebServer(8080);
			webServer.start(); //TEMP
		}catch(IOException e){
			e.printStackTrace();
		}

		//System.out.println("Creating Tasks...");
		manager.createTasks();

		//System.out.println("Running Tasks...");
		manager.runTasks();

		//System.out.println("Finishing Tasks...");
		manager.finishTasks();

		/*
		if(webServer!=null)webServer.stop();
		*/

		Logger.info("DONE!");
	}
}
