package dcc.ufmg.anthill.net;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 26 July 2013
 */

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;

import java.net.InetSocketAddress;
import java.net.URI;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class WebServer {
	private int port;
	private InetSocketAddress addr;
	private HttpServer server;
/*
  public static void main(String[] args) throws IOException {
    InetSocketAddress addr = new InetSocketAddress(8080);
    HttpServer server = HttpServer.create(addr, 0);

    server.createContext("/", new DefaultHandler());
    server.createContext("/module/", new ModuleHandler());
    server.createContext("/host/", new HostHandler());
    server.setExecutor(Executors.newCachedThreadPool());
    server.start();
    System.out.println("Server is listening on port 8080" );
  }
*/
	public WebServer(int port) throws IOException {
		this.port = port;
		addr = new InetSocketAddress(port);
		server = HttpServer.create(addr, 0);

		server.createContext("/", new DefaultHandler());
		server.createContext("/module/", new ModuleHandler());
		server.createContext("/host/", new HostHandler());
		server.createContext("/task/", new TaskHandler());
		server.setExecutor(Executors.newCachedThreadPool());
	}

	public void start(){
		//DEBUG LOG
		Logger.info("HTTP Server is listening on port "+port);
		server.start();
	}

	public void stop(){
		server.stop(1);
	}
}

/*
 * Return the specified moduleName, the instances and the hosts in which each instance is allocated.
 */
class ModuleHandler implements HttpHandler {
	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		URI requestURI = exchange.getRequestURI();
		if (requestMethod.equalsIgnoreCase("GET")){
			Headers responseHeaders = exchange.getResponseHeaders();
	      responseHeaders.set("Content-Type", "text/plain");
      	exchange.sendResponseHeaders(200, 0);

			OutputStream responseBody = exchange.getResponseBody();
			File uri = new File(requestURI.getPath());
			File parentUri = uri.getParentFile();
			if( parentUri!=null && "module".equals(parentUri.getName()) ){				
				for(TaskInfo taskInfo : TaskSettings.getTaskInfo()){
					if(taskInfo.getModuleInfo().getName().equals(uri.getName())){
						responseBody.write("<task><host>".getBytes());
						responseBody.write(taskInfo.getHostName().getBytes());
						responseBody.write("</host><module>".getBytes());
						responseBody.write(taskInfo.getModuleInfo().getName().getBytes());
						responseBody.write("</module><tid>".getBytes());
						responseBody.write((""+taskInfo.getTaskId()).getBytes());
						responseBody.write("</tid></task>\n".getBytes());
					}
				}
			}else{
				for(String moduleName : AppSettings.getModules()){
					ModuleInfo moduleInfo = AppSettings.getModuleInfo(moduleName);
					responseBody.write("<module name=\"".getBytes());
					responseBody.write(moduleInfo.getName().getBytes());
					responseBody.write("\" filter=\"".getBytes());
					responseBody.write(moduleInfo.getFilterInfo().getName().getBytes());
					responseBody.write("\" from=\"".getBytes());
					responseBody.write(moduleInfo.getInputStreamInfo().getName().getBytes());
					responseBody.write("\" to=\"".getBytes());
					responseBody.write(moduleInfo.getOutputStreamInfo().getName().getBytes());
					responseBody.write("\" instances=\"".getBytes());
					responseBody.write((""+moduleInfo.getInstances()).getBytes());
					responseBody.write("\" />\n".getBytes());
				}
			}
			responseBody.close();
		}
	}
}

class HostHandler implements HttpHandler {

	private void hostSettingsResponse(OutputStream responseBody) throws IOException {
		for(String hostName : Settings.getHosts()){
			HostInfo hostInfo = Settings.getHostInfo(hostName);
			SSHInfo sshInfo = hostInfo.getSSHInfo();
			HDFSInfo hdfsInfo = hostInfo.getHDFSInfo();

			responseBody.write("<host><name>".getBytes());
			responseBody.write(hostName.getBytes());
			responseBody.write("</name><address>".getBytes());
			responseBody.write(hostInfo.getAddress().getBytes());
			responseBody.write("</address><workspace>".getBytes());
			responseBody.write(hostInfo.getWorkspace().getBytes());
			responseBody.write("</workspace>".getBytes());
	
			if(sshInfo!=null){
				responseBody.write("<ssh><user>".getBytes());
				responseBody.write(sshInfo.getUser().getBytes());
				responseBody.write("</user><password>".getBytes());
				responseBody.write(sshInfo.getPassword().getBytes());
				responseBody.write("</password><port>".getBytes());
				responseBody.write((""+sshInfo.getPort()).getBytes());
				responseBody.write("</port></ssh>".getBytes());
			}
			if(hdfsInfo!=null){
				responseBody.write("<hdfs><port>".getBytes());
				responseBody.write((""+hdfsInfo.getPort()).getBytes());
				responseBody.write("</port><path>".getBytes());
				responseBody.write(hdfsInfo.getPath().getBytes());
				responseBody.write("</path></hdfs>".getBytes());
			}
			responseBody.write("</host>\n".getBytes());
		}
	}

	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		URI requestURI = exchange.getRequestURI();
		if (requestMethod.equalsIgnoreCase("GET")){
			Headers responseHeaders = exchange.getResponseHeaders();
	      responseHeaders.set("Content-Type", "text/plain");
      	exchange.sendResponseHeaders(200, 0);
			
			OutputStream responseBody = exchange.getResponseBody();
			File uri = new File(requestURI.getPath());
			File parentUri = uri.getParentFile();
			if( parentUri!=null && "host".equals(parentUri.getName()) ){				
				for(TaskInfo taskInfo : TaskSettings.getTaskInfo()){
					if(taskInfo.getHostName().equals(uri.getName())){
						responseBody.write("<task><host>".getBytes());
						responseBody.write(taskInfo.getHostName().getBytes());
						responseBody.write("</host><module>".getBytes());
						responseBody.write(taskInfo.getModuleInfo().getName().getBytes());
						responseBody.write("</module><tid>".getBytes());
						responseBody.write((""+taskInfo.getTaskId()).getBytes());
						responseBody.write("</tid></task>\n".getBytes());
					}
				}
			}else{
				hostSettingsResponse(responseBody);
			}
			responseBody.close();
		}
	}
}

class TaskHandler implements HttpHandler {
	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		URI requestURI = exchange.getRequestURI();
		if (requestMethod.equalsIgnoreCase("GET")){
			Headers responseHeaders = exchange.getResponseHeaders();
	      responseHeaders.set("Content-Type", "text/plain");
      	exchange.sendResponseHeaders(200, 0);

			OutputStream responseBody = exchange.getResponseBody();
			File uri = new File(requestURI.getPath());
			File parentUri = uri.getParentFile();		
			responseBody.write("<tasks>\n".getBytes());
			for(TaskInfo taskInfo : TaskSettings.getTaskInfo()){
				/*
				responseBody.write("<task><host>".getBytes());
				responseBody.write(taskInfo.getHostName().getBytes());
				responseBody.write("</host><module>".getBytes());
				responseBody.write(taskInfo.getModuleInfo().getName().getBytes());
				responseBody.write("</module><tid>".getBytes());
				responseBody.write((""+taskInfo.getTaskId()).getBytes());
				responseBody.write("</tid></task>\n".getBytes());
				*/
				responseBody.write("<task host=\"".getBytes());
				responseBody.write(taskInfo.getHostName().getBytes());
				responseBody.write("\" module=\"".getBytes());
				responseBody.write(taskInfo.getModuleInfo().getName().getBytes());
				responseBody.write("\" tid=\"".getBytes());
				responseBody.write((""+taskInfo.getTaskId()).getBytes());
				responseBody.write("\" />\n".getBytes());
			}
			responseBody.write("</tasks>".getBytes());
			responseBody.close();
		}
	}
}
class DefaultHandler implements HttpHandler {
  public void handle(HttpExchange exchange) throws IOException {
    String requestMethod = exchange.getRequestMethod();
    if (requestMethod.equalsIgnoreCase("GET")){
      Headers responseHeaders = exchange.getResponseHeaders();
      responseHeaders.set("Content-Type", "text/plain");
      exchange.sendResponseHeaders(200, 0);

      OutputStream responseBody = exchange.getResponseBody();
      Headers requestHeaders = exchange.getRequestHeaders();
      Set<String> keySet = requestHeaders.keySet();
      Iterator<String> iter = keySet.iterator();
      while (iter.hasNext()) {
        String key = iter.next();
        List values = requestHeaders.get(key);
        String s = key + " = " + values.toString() + "\n";
        responseBody.write(s.getBytes());
      }
      responseBody.close();
    }
  }
}
