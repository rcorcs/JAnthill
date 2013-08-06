package dcc.ufmg.anthill.stream;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 05 August 2013
 */

import java.io.IOException;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class EnvironmentException extends Exception {
	public EnvironmentException(){
		super();
	}

	public EnvironmentException(String message){
		super(message);
	}

	public EnvironmentException(String message, Throwable cause){
		super(message, cause);
	}

	public EnvironmentException(Throwable cause){
		super(cause);
	}
}

