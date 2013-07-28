package dcc.ufmg.anthill.net;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 28 July 2013
 */

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class SSHConnectionLost extends Exception {
	public SSHConnectionLost(){
		super("SSH connection lost with the remote host");
	}
}

