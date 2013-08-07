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

public class TestFilter extends Filter< SimpleEntry<String, ArrayList<Integer> >, SimpleEntry<String, ArrayList<Integer> > >{
	int taskId;
	public void start(String hostName, int taskId){this.taskId = taskId;}
	public void process(SimpleEntry<String, ArrayList<Integer> > data){
		try{
			ArrayList<Integer> arr = data.getValue();
			for(int i = 0; i<arr.size(); i++){
				arr.set(i, new Integer(arr.get(i).intValue()+10));
			}
			getOutputStream().write(new SimpleEntry<String, ArrayList<Integer> >(data.getKey(), arr));
		}catch(StreamNotWritable e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void finish(){}
}
