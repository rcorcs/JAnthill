/**
 * @authors: Rodrigo Caetano O. ROCHA
 * @date: 24 July 2013
 */

import java.io.IOException;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class TestStream extends Stream<String> {// implements Cloneable{
	int taskId;
	int temp;
	public void start(String hostName, int taskId){this.taskId = taskId; this.temp = 0;}
	public void write(String data) throws StreamNotWritable, IOException { System.out.println(data); }
	public String read() throws StreamNotReadable, IOException { if(temp<10){temp++; return("test "+temp+" ");}else{return null;}}
	public void finish(){}
}
