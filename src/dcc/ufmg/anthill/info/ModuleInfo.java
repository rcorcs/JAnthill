package dcc.ufmg.anthill.info;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 24 July 2013
 */

import java.util.HashMap;

import dcc.ufmg.anthill.info.FilterInfo;
import dcc.ufmg.anthill.info.StreamInfo;

public class ModuleInfo {
	private String name;
	private int instances;
	private HashMap<String, String> attrs;

	private FilterInfo filterInfo;
	private StreamInfo inStreamInfo;
	private StreamInfo outStreamInfo;

	public ModuleInfo(){
		this(null, null, null, null);
	}

	public ModuleInfo(String name, StreamInfo inStreamInfo, FilterInfo filterInfo, StreamInfo outStreamInfo){
		this.name = name;
		this.instances = 1;
		this.inStreamInfo = inStreamInfo;
		this.filterInfo = filterInfo;
		this.outStreamInfo = outStreamInfo;
		this.attrs = new HashMap<String, String>();
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

	public void setInstances(int instances){
		this.instances = instances;
	}

	public int getInstances(){
		return this.instances;
	}
	
	public void setInputStreamInfo(StreamInfo inStreamInfo){
		this.inStreamInfo = inStreamInfo;
	}

	public StreamInfo getInputStreamInfo(){
		return this.inStreamInfo;
	}

	public void setOutputStreamInfo(StreamInfo outStreamInfo){
		this.outStreamInfo = outStreamInfo;
	}

	public StreamInfo getOutputStreamInfo(){
		return this.outStreamInfo;
	}

	public void setFilterInfo(FilterInfo filterInfo){
		this.filterInfo = filterInfo;
	}

	public FilterInfo getFilterInfo(){
		return this.filterInfo;
	}
	
	public void setAttribute(String key, String value){
		this.attrs.put(key, value);
	}

	public String getAttribute(String key){
		return this.attrs.get(key);
	}
}
