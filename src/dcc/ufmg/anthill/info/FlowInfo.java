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

public class FlowInfo {
	private ModuleInfo fromModuleInfo;
	private ModuleInfo toModuleInfo;

	public FlowInfo(ModuleInfo fromModuleInfo, ModuleInfo toModuleInfo){
		this.fromModuleInfo = fromModuleInfo;
		this.toModuleInfo = toModuleInfo;
	}

	public void setFromModuleInfo(ModuleInfo fromModuleInfo){
		this.fromModuleInfo = fromModuleInfo;
	}

	public ModuleInfo getFromModuleInfo(){
		return this.fromModuleInfo;
	}

	public void setToModuleInfo(ModuleInfo toModuleInfo){
		this.toModuleInfo = toModuleInfo;
	}

	public ModuleInfo getToModuleInfo(){
		return this.toModuleInfo;
	}
}

