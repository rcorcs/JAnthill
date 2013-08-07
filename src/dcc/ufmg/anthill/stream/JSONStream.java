package dcc.ufmg.anthill.stream;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 06 August 2013
 */
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.lang.reflect.Type;

import java.io.IOException;

import dcc.ufmg.anthill.Stream;

public abstract class JSONStream<StreamingType> extends Stream<StreamingType> {
	private Type dataType;
	private Gson gson;

	public JSONStream(){
		super();
		this.gson = new Gson();
		this.dataType = new TypeToken< StreamingType >() {}.getType();
	}

	public void setDataType(Type dataType){
		this.dataType = dataType;
	}

	public Type getDataType(){
		return this.dataType;
	}

	public String encode(StreamingType data){
		return gson.toJson(data, this.dataType);
	}

	public StreamingType decode(String json){
		return gson.fromJson(json, this.dataType);
	}
}
