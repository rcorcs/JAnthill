package dcc.ufmg.anthill.info;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 23 July 2013
 */

public class HDFSInfo {
	private String path;
	private int port;

	public HDFSInfo(String path, int port){
		this.path = path;
		this.port = port;
	}

	public void setPath(String path){
		this.path = path;
	}

	public String getPath(){
		return this.path;
	}

	public void setPort(int port){
		this.port = port;
	}

	public int getPort(){
		return this.port;
	}
}

