package dcc.ufmg.anthill.info;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 01 August 2013
 */

public class FlowInfo {
	private String name;
	private String fromModuleName;
	private String toModuleName;
	
	public FlowInfo(String name, String fromModuleName, String toModuleName){
		this.name = name;
		this.fromModuleName = fromModuleName;
		this.toModuleName = toModuleName;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

	public void setFromModuleName(String fromModuleName){
		this.fromModuleName = fromModuleName;
	}

	public String getFromModuleName(){
		return this.fromModuleName;
	}
	
	public void setToModuleName(String toModuleName){
		this.toModuleName = toModuleName;
	}

	public String getToModuleName(){
		return this.toModuleName;
	}
}

