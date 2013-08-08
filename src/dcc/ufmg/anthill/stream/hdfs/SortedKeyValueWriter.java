package dcc.ufmg.anthill.stream.hdfs;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 30 July 2013
 */

//import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import dcc.ufmg.anthill.stream.hdfs.KeyValueWriter;

public class SortedKeyValueWriter<KeyType, ValueType> extends KeyValueWriter<KeyType, ValueType> {

	private SortedSet<KeyType> keySet;
	private HashMap<KeyType, ArrayList<ValueType> > keyValues;

	public SortedKeyValueWriter(){
		super();
		//setDataType( new TypeToken< SimpleEntry<KeyType, ValueType> >() {}.getType() );

		keySet = new TreeSet<KeyType>();
		keyValues = new HashMap<KeyType, ArrayList<ValueType> >();
	}

	public void write(SimpleEntry<KeyType,ValueType> data) throws IOException{
		keySet.add(data.getKey());
		if(!keyValues.containsKey(data.getKey())){
			keyValues.put(data.getKey(), new ArrayList<ValueType>());
		}
		keyValues.get(data.getKey()).add(data.getValue());
	}

	public void finish() throws IOException{
		for(KeyType key : keySet){
			for(ValueType val : keyValues.get(key)){
				super.write(new SimpleEntry<KeyType,ValueType>(key, val));
			}
		}
		super.finish();
	}
}
