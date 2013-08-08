package dcc.ufmg.anthill.stream.hdfs;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 30 July 2013
 */

//import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import java.util.AbstractMap.SimpleEntry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Iterator;

import dcc.ufmg.anthill.stream.hdfs.KeyValueReader;

public class SortedKeyValueReader<KeyType, ValueType> extends KeyValueReader<KeyType, ValueType> {

	private KeyType currentKey;
	private HashMap<KeyType, Deque<ValueType> > pairs;
	private SortedSet<KeyType> keySet;
	private Iterator<KeyType> keyIterator;

	public SortedKeyValueReader(){
		super();
		//setDataType( new TypeToken< SimpleEntry<KeyType, ValueType> >() {}.getType() );

		keyIterator = null;
		currentKey = null;

		pairs = new HashMap<KeyType, Deque<ValueType> >();
		keySet = new TreeSet<KeyType>();
	}

	public void start(String hostName, int taskId) throws IOException{
		super.start(hostName, taskId);

		SimpleEntry<KeyType,ValueType> p;
		while((p=super.read())!=null){
			keySet.add(p.getKey());
			if(!pairs.containsKey(p.getKey())){
				pairs.put(p.getKey(), new ArrayDeque<ValueType>());
			}
			pairs.get(p.getKey()).addLast(p.getValue());
		}
		
		super.finish();

		keyIterator = keySet.iterator();
		if(keyIterator.hasNext()){
			currentKey = keyIterator.next();
		}
	}

	public SimpleEntry<KeyType,ValueType> read() throws IOException{
		if(currentKey==null) throw new IOException();
		else {
			if(pairs.get(currentKey).size()>0){
				return new SimpleEntry<KeyType,ValueType>(currentKey, pairs.get(currentKey).removeFirst());
			}else{
				if(keyIterator.hasNext()){
					currentKey = keyIterator.next();
					return read();
				}else return null;
			}
		}
	}

	public void finish() throws IOException{}
}
