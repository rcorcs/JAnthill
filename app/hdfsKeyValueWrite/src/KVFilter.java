/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 30 July 2013
 */

import java.io.IOException;

import java.util.AbstractMap.SimpleEntry;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;
import dcc.ufmg.anthill.stream.hdfs.*;

public class KVFilter extends Filter<String, SimpleEntry<String,String> >{
	public void start(String hostName, int taskId){}
	public void process(String data){
		try{
			String []strs = data.split("\\W");//non-word characters split

			//for each word emits a pair <word, 1> counting one more occurence of the word.
			//this example is the map phase of the count word MapReduce common application
			for(String word : strs){
				if(word.length()>0){
					SimpleEntry<String,String> pair = new SimpleEntry<String,String>(word, "1");
					getOutputStream().write(pair);
				}
			}
		}catch(StreamNotWritable e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void finish(){}
}
