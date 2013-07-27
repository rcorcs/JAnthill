package dcc.ufmg.anthill.util;

/**
 * @authors: Rodrigo Caetano O. ROCHA
 * @date: 25 July 2013
 */


import java.text.SimpleDateFormat;
import java.util.Date;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class Logger {
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:MM:ss:S");
	private static StringBuffer sBuffer = new StringBuffer();

	private static void output(){
		System.out.print(sBuffer.toString());
		//sBuffer.setLength(0);
		//sBuffer.delete(0, sBuffer.length());
		sBuffer = new StringBuffer();
	}
	
	public static void info(String message){
		//sBuffer.append("INFO: ");
		printlog("INFO: "+message);
	}
	
	public static void warning(String message){
		//sBuffer.append("WARNING: ");
		printlog("WARNING: "+message);
	}

	public static void severe(String message){
		//sBuffer.append("SEVERE: ");
		printlog("SEVERE: "+message);
	}

	public static void fatal(String message){
		//sBuffer.append("FATAL: ");
		printlog("FATAL: "+message);
	}

	public static void log(String message){
		printlog(message);
	}

	private static void printlog(String message){
		Date date = new Date();
		sBuffer.append(dateFormat.format(date));

		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		if(trace.length>=3){
			sBuffer.append(' ');
			sBuffer.append(trace[3].getClassName());
			sBuffer.append(' ');
			sBuffer.append(trace[3].getMethodName());
		}
		sBuffer.append('\n');
		sBuffer.append(message);
		sBuffer.append('\n');

		output();
	}
}
