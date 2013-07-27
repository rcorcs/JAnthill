package dcc.ufmg.anthill;
/**
 * @authors: Rodrigo Caetano O. ROCHA
 * @date: 24 July 2013
 */

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class AppSettings {
	private static String xmlFileName = null;
	private static String filePath = null;
	private static String fileName = null;
	private static String appName = null;

	private static HashMap<String, FilterInfo> filters = new HashMap<String, FilterInfo>();
	private static HashMap<String, StreamInfo> streams = new HashMap<String, StreamInfo>();
	private static HashMap<String, ModuleInfo> modules = new HashMap<String, ModuleInfo>();
	//private static ArrayList<ModuleInfo> modules = new ArrayList<ModuleInfo>();
	private static ArrayList<File> files = new ArrayList<File>();

	/*
	public AppSettings(String xmlFileName){
		this.filters = new HashMap<String, FilterInfo>();
		this.streams = new HashMap<String, StreamInfo>();
		this.modules = new ArrayList<ModuleInfo>();
		this.files = new ArrayList<File>();
		loadXML(xmlFileName);
	}
	*/

	public static ArrayList<File> getFiles(){
		return files;
	}

	public static String getFilePath(){
		return filePath;
	}

	public static String getFileName(){
		return fileName;
	}

	public static String getName(){
		return appName;
	}

	public static FilterInfo getFilterInfo(String name){
		return filters.get(name);
	}
	
	public static StreamInfo getStreamInfo(String name){
		return streams.get(name);
	}

	public static ModuleInfo getModuleInfo(String name){
		return modules.get(name);
	}
	
	public static Set<String> getModules(){
		return modules.keySet();
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
			appName = doc.getDocumentElement().getAttribute("name");

			Node nNode = null;
			Element eElement = null;
			NodeList nList = null;
			NodeList attrList = null;
			
			nList = doc.getElementsByTagName("file");
			for(int temp = 0; temp < nList.getLength(); temp++){
				nNode = nList.item(temp);
				if(nNode.getNodeType() == Node.ELEMENT_NODE){
					eElement = (Element)nNode;
					files.add(new File(eElement.getAttribute("path")+eElement.getAttribute("name")));
				}
			}

			nList = doc.getElementsByTagName("filter");
			FilterInfo filterInfo = null;
			for(int temp = 0; temp < nList.getLength(); temp++){
				nNode = nList.item(temp);
				if(nNode.getNodeType() == Node.ELEMENT_NODE){
					eElement = (Element)nNode;
					filterInfo = new FilterInfo(eElement.getAttribute("name"), eElement.getAttribute("class"));
					
					attrList = eElement.getElementsByTagName("attr");
					for(int attrId = 0; attrId<attrList.getLength(); attrId++){
						Node attrNode = attrList.item(attrId);
						if(attrNode.getNodeType() == Node.ELEMENT_NODE){
							String key = ((Element)attrNode).getAttribute("name");
							String value = ((Element)attrNode).getAttribute("value");
							if(key!=null && value!=null)
								filterInfo.setAttribute(key, value);
						}
					}
					filters.put(filterInfo.getName(), filterInfo);
				}
			}

			nList = doc.getElementsByTagName("stream");
			StreamInfo streamInfo = null;
			for(int temp = 0; temp < nList.getLength(); temp++){
				nNode = nList.item(temp);
				if(nNode.getNodeType() == Node.ELEMENT_NODE){
					eElement = (Element)nNode;
					streamInfo = new StreamInfo(eElement.getAttribute("name"), eElement.getAttribute("class"));
					attrList = eElement.getElementsByTagName("attr");
					for(int attrId = 0; attrId<attrList.getLength(); attrId++){
						Node attrNode = attrList.item(attrId);
						if(attrNode.getNodeType() == Node.ELEMENT_NODE){
							streamInfo.setAttribute(((Element)attrNode).getAttribute("name"), ((Element)attrNode).getAttribute("value"));
						}
					}
					streams.put(streamInfo.getName(), streamInfo);
				}
			}

			nList = ((Element)doc.getElementsByTagName("layout").item(0)).getElementsByTagName("module");
			ModuleInfo moduleInfo = null;
			for(int temp = 0; temp < nList.getLength(); temp++){
				nNode = nList.item(temp);
				if(nNode.getNodeType() == Node.ELEMENT_NODE){
					eElement = (Element)nNode;
					moduleInfo = new ModuleInfo(eElement.getAttribute("name"), getStreamInfo(eElement.getAttribute("from")), getFilterInfo(eElement.getAttribute("filter")), getStreamInfo(eElement.getAttribute("to")));
					if(eElement.getAttribute("instances")!=null){
						moduleInfo.setInstances(Integer.parseInt(eElement.getAttribute("instances")));
					}else {
						moduleInfo.setInstances(1);
					}
					attrList = eElement.getElementsByTagName("attr");
					for(int attrId = 0; attrId<attrList.getLength(); attrId++){
						Node attrNode = attrList.item(attrId);
						if(attrNode.getNodeType() == Node.ELEMENT_NODE){
							moduleInfo.setAttribute(((Element)attrNode).getAttribute("name"), ((Element)attrNode).getAttribute("value"));
						}
					}
					modules.put(moduleInfo.getName(), moduleInfo);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/*
	public static void main(String []args){
		AppSettings settings = new AppSettings("app-settings.xml");
		System.out.println(settings.getFilePath());
		System.out.println(settings.getFilterInfo("counter").getName());
		System.out.println(settings.getFilterInfo("counter").getClassName());
		System.out.println(settings.getFilterInfo("counter").getAttribute("attrName"));
	}
	*/
}
