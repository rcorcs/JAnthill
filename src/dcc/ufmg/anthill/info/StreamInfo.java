package dcc.ufmg.anthill.info;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 24 July 2013
 */

import java.util.HashMap;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class StreamInfo {
	private String name;
	private String className;
	private HashMap<String, String> attrs;

	public StreamInfo(String name, String className){
		this.name = name;
		this.className = className;
		this.attrs = new HashMap<String, String>();
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

	public void setClassName(String className){
		this.className = className;
	}

	public String getClassName(){
		return this.className;
	}
	
	public void setAttribute(String key, String value){
		this.attrs.put(key, value);
	}

	public String getAttribute(String key){
		return this.attrs.get(key);
	}
}

