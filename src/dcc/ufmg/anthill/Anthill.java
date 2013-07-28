package dcc.ufmg.anthill;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 24 July 2013
 */

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

/**
 * This class will be executed on each slave node, for each instance of a module.
 * It is responsible for instantiating an object of the filter class, the input stream class and the output stream class of the module, as specified by the application settings file (usually app-settings.xml).
 */
public class Anthill {
	
	/**
	 * Returns a Filter object that can then be executed. The inStreamName, the filterName, and the outStreamName must specify absolute class name (e.g. dcc.ufmg.anthill.stream.LineReaderStream).
	 * @param inStreamName name of the class of the input stream, i.e. the name of a class that extends {@link Stream}.
	 * @param filterName name of the class of the filter, i.e. the name of a class that extends {@link Filter}.
	 * @param outStreamName name of the class of the output stream, i.e. the name of a class that extends {@link Stream}.
	 * @return the filter instance with instances of the input stream and output stream classes.
	 * @see Filter
	 * @see Stream
	 */
	public static Filter newFilterInstance(String inStreamName, String filterName, String outStreamName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Stream inStream = null;
		Filter filter = null;
		Stream outStream = null;
		
		Class<?> inStreamClass = Class.forName(inStreamName);
		Class<?> filterClass = Class.forName(filterName);
		Class<?> outStreamClass = Class.forName(outStreamName);

		inStream = (Stream)inStreamClass.newInstance();
		filter = (Filter)filterClass.newInstance();
		outStream = (Stream)outStreamClass.newInstance();
		
		filter.setInputStream(inStream);
		filter.setOutputStream(outStream);
		return filter;
	}
	
	/**
	 * The main function that will be called for each slave node, for each instance of a module. This default application will be automaticaly executed by the master application.
	 * @param args array of arguments needed to run a slave Anthill application. The default arguments are: -tid taskId -h slaveHostName -m moduleName -xml settingsXML -app-xml appSettingsXML -sa webServerAddress -sp webServerPort
	 * @see Filter
	 * @see Stream
	 */
	public static void main(String []args){
		String xmlFileName = null;
		String appxmlFileName = null;
		String moduleName = null;
		String taskId = null;
		String hostName = null;
		String webServerAddr = null;
		String webServerPort = null;
		for(int i = 0; i<args.length; i++){
			if("-tid".equals(args[i])){
				taskId = args[i+1];
				Logger.info("-tid "+taskId);
			}else if("-h".equals(args[i])){
				hostName = args[i+1];
				Logger.info("-h "+hostName);
			}else if("-xml".equals(args[i])){
				xmlFileName = args[i+1];
				Logger.info("-xml "+xmlFileName);
			}else if("-app-xml".equals(args[i])){
				appxmlFileName = args[i+1];
				Logger.info("-app-xml "+appxmlFileName);
			}else if("-m".equals(args[i])){
				moduleName = args[i+1];
				Logger.info("-m "+moduleName);
			}else if("-sa".equals(args[i])){
				webServerAddr = args[i+1];
				Logger.info("-sa "+webServerAddr);
			}else if("-sp".equals(args[i])){
				webServerPort = args[i+1];
				Logger.info("-sp "+webServerPort);
			}
		}

		Logger.info("checking IF");
		if(appxmlFileName!=null && xmlFileName!=null && moduleName!=null && taskId!=null && hostName!=null && webServerAddr!=null && webServerPort!=null){
			Logger.info("Loading Settings");
			Settings.loadXMLFile(xmlFileName);
			Logger.info("Loading AppSettings");
			AppSettings.loadXMLFile(appxmlFileName);
			/*WebServerSettings.setAddress(webServerAddr);
			WebServerSettings.setPort(Integer.parseInt(webServerPort));
			try{
				TaskSettings.loadXMLString(WebClient.getContent(WebServerSettings.getTaskPage()));
			}catch(Exception e){
				e.printStackTrace();
				System.exit(-1); //if there is something wrong, exits
			}*/
			Logger.info("Gettings ModuleInfo");
			ModuleInfo moduleInfo = AppSettings.getModuleInfo(moduleName);
			Filter filter = null;
			try{
				Logger.info("Instantiating module");
				filter = newFilterInstance(moduleInfo.getInputStreamInfo().getClassName(), moduleInfo.getFilterInfo().getClassName(), moduleInfo.getOutputStreamInfo().getClassName());
			}catch(Exception e){
				e.printStackTrace();
				System.exit(0); //if there is something wrong, exits
				//System.exit(-1); //if there is something wrong, exits
			}
			filter.setModuleInfo(moduleInfo);
			filter.getInputStream().setModuleInfo(moduleInfo);
			filter.getOutputStream().setModuleInfo(moduleInfo);
			filter.run(hostName, Integer.parseInt(taskId));
		}else System.exit(-1);
	}
}
