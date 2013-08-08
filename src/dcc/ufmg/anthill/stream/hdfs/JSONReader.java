package dcc.ufmg.anthill.stream.hdfs;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 07 August 2013
 */

import java.io.IOException;
import java.io.EOFException;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import dcc.ufmg.anthill.Settings;
import dcc.ufmg.anthill.stream.JSONStream;
import dcc.ufmg.anthill.stream.StreamNotWritable;

public class JSONReader<StreamingType> extends JSONStream<StreamingType> {
	private FSDataInputStream reader;
	private FileSystem fileSystem;
	private HashMap<String, FileStatus> files;
	private SortedSet<String> fileSet;
	private long totalBytes;
	private long beginPos;
	private long endPos;
	private long pos;
	private StringBuffer buffer;

	public JSONReader(){
		reader = null;
		fileSystem = null;
		files = new HashMap<String, FileStatus>();
		fileSet = new TreeSet<String>();
		totalBytes = 0;
		beginPos = endPos = 0;
		buffer = new StringBuffer();
	}

	
	private boolean nextFile() throws IOException{
		if(reader!=null){
			reader.close();
		}
		reader=null;
		long totalRead = 0;
		String nextFileName = null;
		long filePos = 0;
		for(String fName : fileSet){
			if((totalRead+files.get(fName).getLen())>pos){
				nextFileName = fName;
				filePos = pos-totalRead;
				break;
			}
			totalRead += files.get(fName).getLen();
		}
		if(nextFileName!=null){
			reader = fileSystem.open(files.get(nextFileName).getPath());
			if(filePos>0){ //if the position to read is in the middle of the file, skip the previous line.
				reader.seek(filePos-1);
				
				read();//go to the next line of the file
			}
			return true;
		}else {
			pos = endPos;
			return false;
		}
	}

	public void start(String hostName, int taskId) throws IOException{
		Configuration conf = new Configuration();
		conf.setBoolean("fs.hdfs.impl.disable.cache", true);
		fileSystem = FileSystem.get(conf);

		String nameNodeAddr = "localhost"+":"+Settings.getHostInfo(hostName).getHDFSInfo().getPort();

		String fileName = getStreamInfo().getAttribute("filename");
		if(fileName==null){
			FileStatus[] status = fileSystem.listStatus(new Path("hdfs://"+nameNodeAddr+getStreamInfo().getAttribute("path")));

			totalBytes = 0;
			for(int i=0;i<status.length;i++){
				totalBytes += status[i].getLen();
				fileSet.add(status[i].getPath().getName());
				files.put(status[i].getPath().getName(), status[i]);
			}
			
			int split = (int)Math.ceil((double)(totalBytes)/(double)(getModuleInfo().getInstances()));
			beginPos = (taskId-1)*split;
			endPos = taskId*split;
		}else{
			//TODO parse <attr name="fileName" value="${module}${tid}pg5000.txt"/>
			FileStatus status = fileSystem.getFileStatus(new Path("hdfs://"+nameNodeAddr+getStreamInfo().getAttribute("path")+fileName));

			files.put(fileName, status);
			fileSet.add(fileName);
			totalBytes = status.getLen();
			int split = (int)Math.ceil((double)(totalBytes)/(double)(getModuleInfo().getInstances()));
			beginPos = (taskId-1)*split;
			endPos = taskId*split;
		}

		pos = beginPos;
		nextFile();
	}

	public void write(StreamingType data) throws IOException{
		throw new StreamNotWritable();
	}

	public StreamingType read() throws IOException{
		if(pos>=endPos) return null;

		if(reader==null) throw new IOException();
		else {
			while(true){
				byte ch;
				try{
					pos++; //position tracker
					ch = reader.readByte();
				}catch(EOFException e){
					String str = buffer.toString();
					buffer = new StringBuffer(); //TODO reset the same StringBuffer object
					nextFile();
					return decode(str);
				}
				if(ch==((byte)'\n')){
					String str = buffer.toString();
					buffer = new StringBuffer(); //TODO reset the same StringBuffer object
					return decode(str);
				}else if(ch==((byte)'\r')){
					pos++; //position tracker
					ch = reader.readByte();
					if(ch==((byte)'\n')){
						String str = buffer.toString();
						buffer = new StringBuffer(); //TODO reset the same StringBuffer object
						return decode(str);
					}else {
						String str = buffer.toString();
						buffer = new StringBuffer(); //TODO reset the same StringBuffer object
						buffer.append((char)ch);
						return decode(str);
					}
				}else {
					buffer.append((char)ch);
				}
			}
		}
	}

	public void finish() throws IOException{
		if(reader!=null){
			reader.close();
		}
		fileSystem.close();
	}
}
