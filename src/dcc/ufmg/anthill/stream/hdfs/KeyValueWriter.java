package dcc.ufmg.anthill.stream.hdfs;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 30 July 2013
 */

import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import org.apache.commons.lang.RandomStringUtils;

import java.util.AbstractMap.SimpleEntry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcc.ufmg.anthill.Settings;
import dcc.ufmg.anthill.AppSettings;
import dcc.ufmg.anthill.info.FlowInfo;
import dcc.ufmg.anthill.info.ModuleInfo;
import dcc.ufmg.anthill.stream.JSONStream;
import dcc.ufmg.anthill.stream.StreamNotReadable;

public class KeyValueWriter<KeyType, ValueType> extends JSONStream< SimpleEntry<KeyType,ValueType> > {
	private FSDataOutputStream []writers;
	private FileSystem fileSystem;
	private int divisor;

	public KeyValueWriter(){
		super();
		setDataType( new TypeToken< SimpleEntry<KeyType, ValueType> >() {}.getType() );

		writers = null;
		fileSystem = null;
		divisor = 0;
	}

	public void start(String hostName, int taskId) throws IOException{
		Configuration conf = new Configuration();
		conf.setBoolean("fs.hdfs.impl.disable.cache", true);
		fileSystem = FileSystem.get(conf);

		//check for possible errors
		if(getStreamInfo().getAttribute("path")==null) return;

		if(getStreamInfo().getAttribute("divisor")!=null){
			divisor = Integer.parseInt(getStreamInfo().getAttribute("divisor"));
		}else{
			FlowInfo flowInfo = null;
			for(FlowInfo flow : AppSettings.getFlows()){
				if(flow.getFromModuleName().equals(getModuleInfo().getName())){
					flowInfo = flow;
					break;
				}
			}
			if(flowInfo!=null){
				ModuleInfo toModuleInfo = AppSettings.getModuleInfo(flowInfo.getToModuleName());
				divisor = toModuleInfo.getInstances();
			}else{
				divisor = 1;
			}
		}
		
		String folder = 	getStreamInfo().getAttribute("path");
		String nameNodeAddr = "localhost"+":"+Settings.getHostInfo(hostName).getHDFSInfo().getPort();
		writers = new FSDataOutputStream[divisor];
		//writers = new JsonWriter[divisor];
		for(int i = 0; i<divisor; i++){
			String fileName = RandomStringUtils.randomAlphanumeric(20);
			Path path = new Path("hdfs://"+nameNodeAddr+folder+("keyset"+(i+1))+"/"+fileName);
			//if(fileSystem.exists(path)) {
				//DEBUG LOG
				//Logger.severe("Output file "+(folder+("keyset"+(i+1))+"/"+fileName)+" already exists");
				//return;
			//}
			writers[i] = fileSystem.create(path);
		}
	}

	public void write(SimpleEntry<KeyType,ValueType> data) throws IOException{
		if(writers!=null && divisor>0){
			String jsonStr = encode(data);
			byte[] bytes = (jsonStr+"\n").getBytes();
			int writerIndex = (Math.abs(data.getKey().hashCode()))%divisor;
			writers[writerIndex].write(bytes, 0, bytes.length);
		}else throw new IOException();
	}

	public SimpleEntry<KeyType,ValueType> read() throws IOException{
		throw new StreamNotReadable();
	}

	public void finish() throws IOException{
		if(writers!=null){
			for(int i = 0; i<divisor; i++){
				writers[i].close();	
			}
		}
		fileSystem.close();
	}
}
