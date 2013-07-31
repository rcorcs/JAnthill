package dcc.ufmg.anthill.info;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 27 July 2013
 */

import java.util.HashMap;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class SequenceItemInfo {
	private String name;
	private String moduleName;
	private boolean isBreakValue;

	public SequenceItemInfo(String name, String moduleName, boolean isBreakValue){
		this.name = name;
		this.isBreakValue = isBreakValue;
		this.moduleName = moduleName;
	}

	public SequenceItemInfo(String name, String moduleName){
		this(name, moduleName, false);
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}
	
	public void setModuleName(String moduleName){
		this.moduleName = moduleName;
	}

	public String getModuleName(){
		return this.moduleName;
	}

	public boolean isBreak(){
		return this.isBreakValue;
	}

	public void setBreak(boolean isBreakValue){
		this.isBreakValue = isBreakValue;
	}
}

