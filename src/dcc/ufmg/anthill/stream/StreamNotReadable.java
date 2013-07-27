package dcc.ufmg.anthill.stream;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class StreamNotReadable extends Exception {
	public StreamNotReadable(){
		super("Stream Not Readable");
	}
}

