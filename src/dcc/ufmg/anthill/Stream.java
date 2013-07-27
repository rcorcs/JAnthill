package dcc.ufmg.anthill;
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

public abstract class Stream<StreamingType> {
	private ModuleInfo moduleInfo;

	public void setModuleInfo(ModuleInfo moduleInfo){
		this.moduleInfo = moduleInfo;
	}

	public ModuleInfo getModuleInfo(){
		return this.moduleInfo;
	}

	public abstract void start(String hostName, int taskId);
	public abstract void write(StreamingType data) throws StreamNotWritable, IOException;
	public abstract StreamingType read() throws StreamNotReadable, IOException;
	public abstract void finish();
}
