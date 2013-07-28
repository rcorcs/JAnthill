/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 23 July 2013
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

		RoundRobinTaskScheduler scheduler = new RoundRobinTaskScheduler();

		TaskManager manager = new TaskManager(scheduler, new SSHEnvironment());

		WebServer webServer = null;
		try{
			webServer = new WebServer(8080);
			webServer.start();
		}catch(IOException e){
			e.printStackTrace();
		}

		manager.createTasks();

		manager.runTasks();

		manager.finishTasks();

		if(webServer!=null)webServer.stop();

		Logger.info("DONE!");
	}
}
