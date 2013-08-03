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

/**
 * This class represents one of the key components of a module in the Anthill programming model.
 * A filter is responsible for receiving data from the input stream, processing the data, and then sending the processed data to the output stream.
 * The Filter has an event oriented abstraction and it is composed by three main methods: <code>start</code>, <code>process</code>, and <code>finish</code>.
 */
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

		Logger.warning("Finished Running the Filter");
	}
	
	/**
	 * Initializes the filter instance. This method is called at the begining of the execution of an instance of a module.
	 * @param hostName is the name of the host in which this instance of the filter is being executed.
	 * @param taskId is the identifier of the task that is executing this filter instance.
	 */
	public abstract void start(String hostName, int taskId);
	
	/**
	 * Processes the data read from the input stream.
	 * The filter receive data to process while there is data available in the input stream.
	 * @param data is the data read from the input stream.
	 */
	public abstract void process(InputType data);
	
	/**
	 * Finilizes the filter instance. This method is called at the end of the execution of an instance of a module.
	 */
	public abstract void finish();
}
