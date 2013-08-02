package dcc.ufmg.anthill;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 24 July 2013
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
	private static ArrayList<FlowInfo> flows = new ArrayList<FlowInfo>();
	private static ArrayList<SequenceItemInfo> sequence = new ArrayList<SequenceItemInfo>();

	private static ArrayList<File> files = new ArrayList<File>();

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

	public static ArrayList<FlowInfo> getFlows(){
		return flows;
	}

	public static ArrayList<SequenceItemInfo> getSequence(){
		return sequence;
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

			nList = doc.getElementsByTagName("module");
			ModuleInfo moduleInfo = null;
			for(int temp = 0; temp < nList.getLength(); temp++){
				nNode = nList.item(temp);
				if(nNode.getNodeType() == Node.ELEMENT_NODE){
					eElement = (Element)nNode;
					moduleInfo = new ModuleInfo(eElement.getAttribute("name"), getStreamInfo(eElement.getAttribute("input")), getFilterInfo(eElement.getAttribute("filter")), getStreamInfo(eElement.getAttribute("output")));
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
			
			nList = doc.getElementsByTagName("flow");
			FlowInfo flowInfo = null;
			for(int temp = 0; temp < nList.getLength(); temp++){
				nNode = nList.item(temp);
				if(nNode.getNodeType() == Node.ELEMENT_NODE){
					eElement = (Element)nNode;
					flowInfo = new FlowInfo(eElement.getAttribute("name"), eElement.getAttribute("from"), eElement.getAttribute("to"));
					flows.add(flowInfo);
				}
			}

			nList = doc.getElementsByTagName("sequence");
			SequenceItemInfo itemInfo = null;
			if(nList.getLength()>0){
				nNode = nList.item(0);
				if(nNode.getNodeType() == Node.ELEMENT_NODE){
					nList = ((Element)nNode).getElementsByTagName("item");
					for(int temp = 0; temp < nList.getLength(); temp++){
						nNode = nList.item(temp);
						if(nNode.getNodeType() == Node.ELEMENT_NODE){
							eElement = (Element)nNode;
							String name = eElement.getAttribute("name");
							String moduleName = eElement.getAttribute("module");
							String isbreak = eElement.getAttribute("break");
							if(isbreak!=null){
								itemInfo = new SequenceItemInfo(name, moduleName, "true".equalsIgnoreCase(isbreak));
							}else if(moduleName!=null){
								itemInfo = new SequenceItemInfo(name, moduleName);
							}
							if(itemInfo!=null) sequence.add(itemInfo);
							itemInfo = null;
						}
					}
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
