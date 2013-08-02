package dcc.ufmg.anthill.net;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 01 August 2013
 */

import java.util.HashMap;
import java.util.Set;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class GlobalState {
	private static HashMap<String, String> states = new HashMap<String, String>();
	
	public static String get(String name){
		//return WebClient.getContent(WebServerSettings.getStateGetURL(name));
		return states.get(name);
	}

	public static void set(String name, String value){
		//WebClient.getContent(WebServerSettings.getStateSetURL(name, value));
		states.put(name, value);
	}
}

