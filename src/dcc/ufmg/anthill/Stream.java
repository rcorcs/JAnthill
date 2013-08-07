package dcc.ufmg.anthill;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 23 July 2013
 */

import java.io.IOException;

import dcc.ufmg.anthill.info.StreamInfo;
import dcc.ufmg.anthill.info.ModuleInfo;

public abstract class Stream<StreamingType> {
	private ModuleInfo moduleInfo;
	private StreamInfo streamInfo;

	public void setModuleInfo(ModuleInfo moduleInfo){
		this.moduleInfo = moduleInfo;
	}

	public ModuleInfo getModuleInfo(){
		return this.moduleInfo;
	}

	public void setStreamInfo(StreamInfo streamInfo){
		this.streamInfo = streamInfo;
	}

	public StreamInfo getStreamInfo(){
		return this.streamInfo;
	}

	public abstract void start(String hostName, int taskId) throws IOException;
	public abstract void write(StreamingType data) throws IOException;
	public abstract StreamingType read() throws IOException;
	public abstract void finish() throws IOException;
}
