package dcc.ufmg.anthill.stream.hdfs;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 07 August 2013
 */

import java.io.IOException;

import org.apache.commons.lang.RandomStringUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcc.ufmg.anthill.Settings;
import dcc.ufmg.anthill.stream.JSONStream;
import dcc.ufmg.anthill.stream.StreamNotReadable;

public class Writer<StreamingType> extends JSONStream<StreamingType> {
	private FSDataOutputStream writer;
	private FileSystem fileSystem;

	public Writer(){
		writer = null;
		fileSystem = null;
	}

	public void start(String hostName, int taskId) throws IOException{
		Configuration conf = new Configuration();
		conf.setBoolean("fs.hdfs.impl.disable.cache", true);
		fileSystem = FileSystem.get(conf);

		//String fileName = Settings.getHostInfo(hostName).getWorkspace()+AppSettings.getName()+"/"+getModuleInfo().getName()+"/tid"+taskId+"_"+getModuleInfo().getAttribute("output");
		String fileName = getStreamInfo().getAttribute("filename");
		if(fileName==null){
			fileName = RandomStringUtils.randomAlphanumeric(20);
		}else{
			//parse <attr name="fileName" value="${module}${tid}pg5000.txt"/>
		}
		fileName = getStreamInfo().getAttribute("path")+fileName;
		//String nameNodeAddr = Settings.getHostInfo(hostName).getAddress()+":"+Settings.getHostInfo(hostName).getHDFSInfo().getPort();
		String nameNodeAddr = "localhost"+":"+Settings.getHostInfo(hostName).getHDFSInfo().getPort();
		Path path = new Path("hdfs://"+nameNodeAddr+fileName);
		
		//if(fileSystem.exists(path)) {
			//DEBUG LOG
			//Logger.severe("Output file "+fileName+" already exists");
			//return;
		//}
		writer = fileSystem.create(path);
		
	}

	public void write(StreamingType data) throws IOException{
		if(writer!=null){
			byte[] bytes = (encode(data)+"\n").getBytes();
			writer.write(bytes, 0, bytes.length);
		}else throw new IOException();
	}

	public StreamingType read() throws IOException{
		throw new StreamNotReadable();
	}

	public void finish() throws IOException{
		writer.close();
		fileSystem.close();
	}
}
