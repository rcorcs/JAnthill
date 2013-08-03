/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 30 July 2013
 */

import java.io.IOException;

import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;
import dcc.ufmg.anthill.stream.hdfs.*;

public class MapFilter extends Filter<String, SimpleEntry<String,String> >{
	private HashMap<String, Integer> combiner;
	
	public void start(String hostName, int taskId){
		combiner = new HashMap<String, Integer>();
	}

	public void process(String data){
		String []strs = data.split("\\W");//non-word characters split

		//for each word emits a pair <word, 1> counting one more occurence of the word.
		//this example is the map phase of the count word MapReduce common application
		for(String word : strs){
			if(word.length()>0){
				if(combiner.containsKey(word)){
					combiner.put(word, new Integer(combiner.get(word).intValue()+1));
				}else{
					combiner.put(word, new Integer(1));
				}

			}
		}
	}

	public void finish(){
		for(String word : combiner.keySet()){
			try{
				SimpleEntry<String,String> pair = new SimpleEntry<String,String>(word, combiner.get(word).toString());
				getOutputStream().write(pair);
			}catch(StreamNotWritable e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
