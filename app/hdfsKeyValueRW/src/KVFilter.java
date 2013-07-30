/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 30 July 2013
 */

import java.io.IOException;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class KVFilter extends Filter< SimpleEntry<String,String> , SimpleEntry<String,String> >{
	private HashMap<String, Integer> wordCounter;

	public void start(String hostName, int taskId){
		wordCounter = new HashMap<String, Integer>();
	}
	public void process(SimpleEntry<String,String> data){
		String word = data.getKey();
		Integer count = new Integer(data.getValue());
		if(wordCounter.containsKey(word)){
			wordCounter.put(word, new Integer(wordCounter.get(word).intValue()+count.intValue()));
		}else{
			wordCounter.put(word, new Integer(count.intValue()));
		}
	}
	public void finish(){
		for(String word : wordCounter.keySet()){
			try{
				SimpleEntry<String,String> pair = new SimpleEntry<String,String>(word, wordCounter.get(word).toString());
				getOutputStream().write(pair);
			}catch(StreamNotWritable e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
