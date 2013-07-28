package dcc.ufmg.anthill;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 27 July 2013
 */

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class WebServerSettings {
	private static String address;
	private static int port;

	public static void setAddress(String addr){
		address = addr;
	}

	public static String getAddress(){
		return address;
	}

	public static void setPort(int p){
		port = p;
	}

	public static int getPort(){
		return port;
	}

	public static String getTaskPage(){
		return "http://"+address+":"+port+"/task/";
	}

	public static String getHostPage(){
		return "http://"+address+":"+port+"/host/";
	}

	public static String getModulePage(){
		return "http://"+address+":"+port+"/module/";
	}
}
