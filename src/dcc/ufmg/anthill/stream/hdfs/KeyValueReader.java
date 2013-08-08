package dcc.ufmg.anthill.stream.hdfs;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 30 July 2013
 */

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;

import java.io.BufferedReader;

import java.util.AbstractMap.SimpleEntry;

import java.util.Deque;
import java.util.ArrayDeque;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileStatus;

import dcc.ufmg.anthill.Settings;
import dcc.ufmg.anthill.stream.JSONStream;
import dcc.ufmg.anthill.stream.StreamNotWritable;

public class KeyValueReader<KeyType, ValueType> extends JSONStream< SimpleEntry<KeyType, ValueType> > {

	private FileSystem fileSystem;
	private Deque<Path> pathsDeque;
	private BufferedReader reader;

	public KeyValueReader(){
		super();
		setDataType( new TypeToken< SimpleEntry<KeyType, ValueType> >() {}.getType() );

		fileSystem = null;
		pathsDeque = new ArrayDeque<Path>();
		reader = null;
	}

	public void start(String hostName, int taskId) throws IOException{
		Configuration conf = new Configuration();
		conf.setBoolean("fs.hdfs.impl.disable.cache", true);
		fileSystem = FileSystem.get(conf);

		//check for possible errors
		if(getStreamInfo().getAttribute("path")==null) return;

		String folder = 	getStreamInfo().getAttribute("path");
		
		String nameNodeAddr = "localhost"+":"+Settings.getHostInfo(hostName).getHDFSInfo().getPort();
		
		FileStatus[] status = fileSystem.listStatus(new Path("hdfs://"+nameNodeAddr+folder+("keyset"+(taskId))+"/"));

		for(int i=0;i<status.length;i++){
			pathsDeque.add(status[i].getPath());
		}

		Path path = pathsDeque.removeFirst();
		//reader = new JsonReader(new BufferedReader(new InputStreamReader(fileSystem.open(path))));
		reader = new BufferedReader(new InputStreamReader(fileSystem.open(path)));
	}

	public void write(SimpleEntry<KeyType, ValueType> data) throws IOException{
		throw new StreamNotWritable();
	}

	public SimpleEntry<KeyType, ValueType> read() throws IOException{
		if(reader==null) throw new IOException();
		else {
			String line = reader.readLine();
			if(line != null){
				return decode(line);
			}else {
				reader.close();
				if(pathsDeque.size()==0) return null;
				else {
					Path path = pathsDeque.removeFirst();
					reader = new BufferedReader(new InputStreamReader(fileSystem.open(path)));
					return read();
				}
			}
			
		}
	}

	public void finish() throws IOException{
		if(reader!=null) reader.close();
		fileSystem.close();
	}
}
