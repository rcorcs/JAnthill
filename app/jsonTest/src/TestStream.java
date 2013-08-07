/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 24 July 2013
 */

import java.io.IOException;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class TestStream extends JSONStream< SimpleEntry<String, ArrayList<Integer> > > {// implements Cloneable{
	int taskId;
	int temp;
	public void start(String hostName, int taskId){this.taskId = taskId; this.temp = 0;}
	public void write(SimpleEntry<String, ArrayList<Integer> > data) throws IOException { System.out.println(encode(data)); }
	public SimpleEntry<String, ArrayList<Integer> > read() throws IOException {
		if(temp<10){
			temp++;
			ArrayList<Integer> arr = new ArrayList<Integer>();
			for(int i = 0; i<=temp;i++)
				arr.add(new Integer(i));
			return(new SimpleEntry<String, ArrayList<Integer> >("k"+temp, arr));
		}else{
			return null;
		}
	}
	public void finish(){}
}
