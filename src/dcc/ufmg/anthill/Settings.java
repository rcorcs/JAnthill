package dcc.ufmg.anthill;
/**
 * @authors: Rodrigo Caetano O. ROCHA
 * @date: 23 July 2013
 */

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

import java.util.HashMap;
import java.util.Set;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class Settings {
	private static HashMap<String, HostInfo> hosts = new HashMap<String, HostInfo>();
	private static String filePath = null;
	private static String fileName = null;
	private static String className = null;
	private static String xmlFileName = null;
	/*
	public Settings(String xmlFileName){
		hosts = new HashMap<String, HostInfo>();
		loadXML(xmlFileName);
	}
	*/
	public static String getFilePath(){
		return filePath;
	}

	public static String getFileName(){
		return fileName;
	}

	public static String getClassName(){
		return className;
	}

	public static Set<String> getHosts(){
		return hosts.keySet();
	}
	
	public static HostInfo getHostInfo(String name){
		return hosts.get(name);
	}
	
	public static void removeHost(String name){
		hosts.remove(name);
	}

	public static String getXMLFileName(){
		return xmlFileName;
	}

	public static void loadXMLFile(String xmlName){
		xmlFileName = xmlName;
		//DEBUG LOG
		Logger.info("Loading settings from "+xmlName);
		try {
			File fXmlFile = new File(xmlFileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
 
			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			filePath = doc.getDocumentElement().getAttribute("path");
			fileName = doc.getDocumentElement().getAttribute("file");
			className = doc.getDocumentElement().getAttribute("class");

			NodeList nList = doc.getElementsByTagName("host");

			Node nNode = null;
			Element eElement = null;
			HostInfo host = null;
			NodeList sshList = null;
			NodeList hdfsList = null;
			for(int temp = 0; temp < nList.getLength(); temp++){
				nNode = nList.item(temp);
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());
				if(nNode.getNodeType() == Node.ELEMENT_NODE){
					eElement = (Element)nNode;
					//System.out.println("Staff id : " + eElement.getAttribute("id"));
					host = new HostInfo(eElement.getElementsByTagName("name").item(0).getTextContent(), eElement.getElementsByTagName("address").item(0).getTextContent());
					host.setWorkspace(eElement.getElementsByTagName("workspace").item(0).getTextContent());
					sshList = eElement.getElementsByTagName("ssh");
					if(sshList.getLength()>0){
						host.setSSHInfo(new SSHInfo(((Element)sshList.item(0)).getElementsByTagName("user").item(0).getTextContent(), ((Element)sshList.item(0)).getElementsByTagName("password").item(0).getTextContent(), Integer.parseInt( ((Element)sshList.item(0)).getElementsByTagName("port").item(0).getTextContent())));
					}
					hdfsList = eElement.getElementsByTagName("hdfs");
					if(hdfsList.getLength()>0){
						host.setHDFSInfo(new HDFSInfo( ((Element)hdfsList.item(0)).getElementsByTagName("path").item(0).getTextContent(), Integer.parseInt(((Element)hdfsList.item(0)).getElementsByTagName("port").item(0).getTextContent())));
					}
					hosts.put(host.getName(), host);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/*
	public static void main(String []args){
		Settings settings = new Settings("settings.xml");
		System.out.println(settings.getHostInfo("victor-laptop").getName());
		System.out.println(settings.getHostInfo("victor-laptop").getAddress());
		System.out.println(settings.getHostInfo("victor-laptop").getSSHInfo().getUser());
		System.out.println(settings.getHostInfo("victor-laptop").getSSHInfo().getPassword());
		System.out.println(settings.getHostInfo("victor-laptop").getSSHInfo().getPort());
		System.out.println(settings.getHostInfo("victor-laptop").getHDFSInfo().getPort());
		System.out.println(settings.getHostInfo("victor-laptop").getHDFSInfo().getPath());
	}
	*/
}
