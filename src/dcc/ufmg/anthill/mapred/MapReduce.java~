package dcc.ufmg.anthill.mapred;

/**
	It generates the app-settings.xml and executes the JAnthill properly.
*/

import dcc.ufmg.anthill.mapred.Mapper;
import dcc.ufmg.anthill.mapred.Reducer;
public class MapReduce {
	private Mapper mapper;
	private Reducer reducer;

	public MapReduce(Mapper mapper, Reducer reducer){
		this.mapper = mapper;
		this.reducer = reducer;
	}
	
	private generateXMLAppSettings(){
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("<?xml version="1.0"?>\n");
		sBuffer.append("<app-settings name=\"WordCounter\" path=\"/home/rcor/dev/Java/JAnthill/app/wordCounter/\" file=\"wordCounter.jar\">\n");
		//...
		sBuffer.append("<filter name=\"mapperfilter\" class=\""+this.mapper.getClass().getName()+"\" />\n");
		sBuffer.append("<filter name=\"reducerfilter\" class=\""+this.reducer.getClass().getName()+"\" />\n");
	}
}
