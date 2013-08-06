/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 23 July 2013
 */

import java.io.IOException;

import java.net.SocketException;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;
import dcc.ufmg.anthill.environment.*;

public class Main{

	/**
	* @param args
	*/
	public static void main(String[] args) {
		Settings.loadXMLFile(args[0]);//"settings.xml"

		AppSettings.loadXMLFile(args[1]);//"app-settings.xml"

		RoundRobinTaskScheduler scheduler = new RoundRobinTaskScheduler();

		TaskManager manager = new TaskManager(scheduler, new SSHEnvironment());

		int webServerPort = 8000 + (int)(Math.random() * (9000 - 8000));
		WebServerSettings.setPort(webServerPort);
		try{
			Logger.info("Server at "+NetUtil.getLocalInet4Address().getHostAddress()+" on port "+webServerPort);
			WebServerSettings.setAddress(NetUtil.getLocalInet4Address().getHostAddress().toString());
			//Logger.info("Server at "+NetUtil.getLocalInet6Address().getHostAddress()+" on port "+webServerPort);
		}catch(SocketException e){
			e.printStackTrace();
		}
		
		WebServer webServer = null;
		try{
			webServer = new WebServer(webServerPort);
			webServer.start();
		}catch(IOException e){
			e.printStackTrace();
		}

		manager.start();

		manager.run();

		manager.finish();

		//if(webServer!=null)webServer.stop();

		Logger.info("DONE!");
	}
}
