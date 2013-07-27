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

public abstract class Filter<InputType, OutputType>{
	private ModuleInfo moduleInfo;

	private Stream<InputType> inputStream;
	private Stream<OutputType> outputStream;

	public Filter(){
	}

	public Filter(Stream<InputType> inputStream, Stream<OutputType> outputStream){
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	public void setInputStream(Stream<InputType> stream){
		this.inputStream = stream;
	}

	public void setOutputStream(Stream<OutputType> stream){
		this.outputStream = stream;
	}

	public Stream<InputType> getInputStream(){
		return this.inputStream;
	}

	public Stream<OutputType> getOutputStream(){
		return this.outputStream;
	}

	public void setModuleInfo(ModuleInfo moduleInfo){
		this.moduleInfo = moduleInfo;
	}

	public ModuleInfo getModuleInfo(){
		return this.moduleInfo;
	}

	public void run(String hostName, int taskId){
		getInputStream().start(hostName, taskId);
		getOutputStream().start(hostName, taskId);
		start(hostName, taskId);

		InputType data = null;
		while(true){
			try{
				data = getInputStream().read();
			}catch(StreamNotReadable e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
			if(data==null){ //instead of cheking if data==null, do the voting thing to stop running.
				break;
			}
			process(data);
		}

		finish();
		getInputStream().finish();
		getOutputStream().finish();
	}
	
	public abstract void start(String hostName, int taskId);
	public abstract void process(InputType data);
	public abstract void finish();
}
