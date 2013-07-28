package dcc.ufmg.anthill.net;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 23 July 2013
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;

import java.io.InputStream;
import java.io.IOException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.JSchException;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

/**
 * This class offers some useful tools for manipulating a remote host via a SSH connection.
 */
public class SSHHost {
	private String user;
	private String password;
	private String host;
	private int port;

	private int sleepTime;
	private int pingTime;

	private InetAddress inetAddr;
	private JSch jsch;
	private Session session;

	/**
	 * Class constructor specifying only the username, the user password and the host address.
    * This constructor assumes the default SSH port 22.
	 */
	public SSHHost(String user, String pwd, String host){
		this(user, pwd, host, 22);
	}

	/**
	 * Class constructor specifying the username, the user password, the host address and the SSH port.
	 * This construction creates a SSH session that connects with the specified host.
	 */
	public SSHHost(String user, String pwd, String host, int port){
		this.user = user;
		this.password = pwd;
		this.host = host;
		this.port = port;
		inetAddr = null;
		sleepTime = 500;
		pingTime = 1000;

		try{
			inetAddr = InetAddress.getByName(host);
		}catch(UnknownHostException e){ //TODO throw this exception
			e.printStackTrace();
		}

		try{
			this.jsch = new JSch();
			this.session = jsch.getSession(user,host,port);
			this.session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			this.session.setConfig(config);
			this.session.connect();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Close the SSH session with the remote host.
	 */
	public void close(){
		try{
			this.session.disconnect();
		}catch(Exception e){
			e.printStackTrace();
		}		
	}

	/**
	 * Executes in background a command at the remote host.
	 * It keeps verifying if the host is still reachable and if the remote host is not reachable it throws an {@link SSHConnectionLost}.
	 * @param command any valid command to be executed from the ~/ directory.
	 * @return the exit status of the command execution.
	 */
	public int executeInBackground(String command) throws SSHConnectionLost {
		Channel 	channel 	= null;
		ChannelExec channelExec = null;
		int exitStatus = -1;
		try{
			channel = this.session.openChannel("exec");
			channelExec = ((ChannelExec)channel);
		   channelExec.setCommand(command);
		   channel.setInputStream(null);
		   channelExec.setErrStream(System.err);

		   channel.connect();
		   while(true){
				if(channel.isClosed()){
					exitStatus = channel.getExitStatus();
					break;
				}
				try{Thread.sleep(sleepTime);}catch(InterruptedException e){}
				try{
					if(!inetAddr.isReachable(pingTime)){
						//System.out.println("Lost Connection:"+channel.getExitStatus());
						//exitStatus = -2;
						//break;
						throw new SSHConnectionLost();
					}
				}catch(IOException e){
				}
			}
			channel.disconnect();
		}catch(JSchException e){
			e.printStackTrace();
		}
		return exitStatus;
	}

	/**
	 * Executes a command at the remote host printing on the local host the standard output from the remote host.
	 * It keeps verifying if the host is still reachable and if the remote host is not reachable it throws an {@link SSHConnectionLost}.
	 * @param command any valid command to be executed from the ~/ directory.
	 * @return the exit status of the command execution.
	 */
	public int execute(String command) throws SSHConnectionLost {
		Channel 	channel 	= null;
		ChannelExec channelExec = null;
		int exitStatus = -1;
		try{
			channel = this.session.openChannel("exec");
			channelExec = ((ChannelExec)channel);
		   channelExec.setCommand(command);
		   channel.setInputStream(null);
		   channelExec.setErrStream(System.err);

			InputStream in = null;
			try{
			   in = channel.getInputStream();
			}catch(IOException e){
				e.printStackTrace();
			}
		   channel.connect();

		   byte[] tmp=new byte[1024];
		   while(true){
				try{
					while(in.available()>0){
						int i=in.read(tmp, 0, 1024);
						if(i<0)break;
						System.out.print(new String(tmp, 0, i));
					}
				}catch(IOException e){
					e.printStackTrace();
				}
				if(channel.isClosed()){
					exitStatus = channel.getExitStatus();
					break;
				}
				try{Thread.sleep(sleepTime);}catch(InterruptedException e){}
				try{
					if(!inetAddr.isReachable(pingTime)){
						//System.out.println("Lost Connection:"+channel.getExitStatus());
						//exitStatus = -2;
						//break;
						throw new SSHConnectionLost();
					}
				}catch(IOException e){
				}
			}
		   channel.disconnect();
		}catch(JSchException e){
			e.printStackTrace();
		}
		return exitStatus;
	}

	private void mkdirs(ChannelSftp channelSftp, String remoteFolderPath){
		File dir = new File(remoteFolderPath);
		String []folders = dir.getPath().split("/");
		String path = "";
		for(String folder : folders){
			path = path+"/"+folder;
			if(folder.length()>0){
				try{
					channelSftp.mkdir(path);
				}catch(SftpException e){
					//e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Uploads a file from the local host into the remote host. It creates the remote folder path if it does not exist yet, and then sends to the remote folder path the file specified by the file name and the local folder path.
	 * @param localFileName name of the file to be uploaded.
	 * @param localFolderPath local folder that contains the file to be uploaded.
	 * @param remoteFolderPath remote folder that will receive the file.
	 */
	public void uploadFile(String localFileName, String localFolderPath, String remoteFolderPath){
		Channel 	channel 	= null;
		ChannelSftp channelSftp = null;
		if(localFolderPath.charAt(localFolderPath.length()-1)!='/'){
			localFolderPath=localFolderPath+"/";
		}
		try{
			channel = this.session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp)channel;

			this.mkdirs(channelSftp, remoteFolderPath); //make sure the folders path exist. create the remote path

			channelSftp.cd(remoteFolderPath);
			File f = new File(localFolderPath+localFileName);
			//if(f.exists())
			channelSftp.put(new FileInputStream(f), f.getName()); //IO Exception, file doesn't exist
			//else System.out.println(""+localFileName+" doesn't exists.");
			channel.disconnect();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Downloads a file from the remote host into the local host. It creates the local folder path if it does not exist yet, and then receives the file specified by the file name and the remote folder path.
	 * @param remoteFileName name of the file to be downloaded.
	 * @param remoteFolderPath remote folder that contains the file to be downloaded.
	 * @param localFolderPath local folder that will receive the file.
	 */
	public void downloadFile(String remoteFileName, String remoteFolderPath, String localFolderPath){
		Channel 	channel 	= null;
		ChannelSftp channelSftp = null;
		try{
			channel = this.session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp)channel;
			channelSftp.cd(remoteFolderPath);

			File newFolder = new File(localFolderPath);
			newFolder.mkdirs(); //make sure the folders path exists. create the local path

			File newFile = new File(localFolderPath+remoteFileName);
			OutputStream os = new FileOutputStream(newFile);

			BufferedOutputStream bos = new BufferedOutputStream(os);
			int readCount;

			BufferedInputStream bis = new BufferedInputStream(channelSftp.get(remoteFileName));
			byte[] buffer = new byte[1024];
			while( (readCount = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, readCount);
			}
			buffer = null;

			bis.close();
			bos.close();
			channel.disconnect();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

