package dcc.ufmg.anthill;
/**
 * @authors: Rodrigo Caetano O. ROCHA
 * @date: 24 July 2013
 */

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class Anthill {
	public static Filter createFilter(String inStreamName, String filterName, String outStreamName){
		Stream inStream = null;
		Filter filter = null;
		Stream outStream = null;
		try{
			Class<?> inStreamClass = Class.forName(inStreamName);
			Class<?> filterClass = Class.forName(filterName);
			Class<?> outStreamClass = Class.forName(outStreamName);

			inStream = (Stream)inStreamClass.newInstance();
			filter = (Filter)filterClass.newInstance();
			outStream = (Stream)outStreamClass.newInstance();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}catch(InstantiationException e){
			e.printStackTrace();
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}
		filter.setInputStream(inStream);
		filter.setOutputStream(outStream);
		return filter;
	}
	
	public static void main(String []args){
		String xmlFileName = null;
		String appxmlFileName = null;
		String moduleName = null;
		String taskId = null;
		String hostName = null;
		for(int i = 0; i<args.length; i++){
			if("-tid".equals(args[i])){
				taskId = args[i+1];
			}else if("-h".equals(args[i])){
				hostName = args[i+1];
			}else if("-xml".equals(args[i])){
				xmlFileName = args[i+1];
			}else if("-app-xml".equals(args[i])){
				appxmlFileName = args[i+1];
			}else if("-m".equals(args[i])){
				moduleName = args[i+1];
			}
		}

		//Filter filter = createFilter(inStreamName, filterName, outStreamName);

		if(appxmlFileName!=null && xmlFileName!=null && moduleName!=null && taskId!=null){
			Settings.loadXMLFile(xmlFileName);
			AppSettings.loadXMLFile(appxmlFileName);
			
			ModuleInfo moduleInfo = AppSettings.getModuleInfo(moduleName);
			Filter filter = createFilter(moduleInfo.getInputStreamInfo().getClassName(), moduleInfo.getFilterInfo().getClassName(), moduleInfo.getOutputStreamInfo().getClassName());
			filter.setModuleInfo(moduleInfo);
			filter.getInputStream().setModuleInfo(moduleInfo);
			filter.getOutputStream().setModuleInfo(moduleInfo);
			filter.run(hostName, Integer.parseInt(taskId));
		}else System.exit(-1);
	}
}
