package dcc.ufmg.anthill;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 26 July 2013
 */
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.IOException;
import java.io.File;
import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import dcc.ufmg.anthill.*;
import dcc.ufmg.anthill.util.*;
import dcc.ufmg.anthill.net.*;
import dcc.ufmg.anthill.info.*;
import dcc.ufmg.anthill.scheduler.*;
import dcc.ufmg.anthill.stream.*;

public class TaskSettings {
	private static ArrayList<TaskInfo> tasks = new ArrayList<TaskInfo>();
	
	public static void addTaskInfo(TaskInfo taskInfo){
		tasks.add(taskInfo);
	}

	public static void clear(){
		tasks.clear();
	}

	public static void removeTaskInfo(TaskInfo taskInfo){
		for(int i = 0; i<tasks.size(); i++){
			TaskInfo task = tasks.get(i);
			if(task.getHostName().equals(taskInfo.getHostName()) && task.getModuleInfo().getName().equals(taskInfo.getModuleInfo().getName()) && task.getTaskId()==taskInfo.getTaskId()){
				tasks.remove(i);
				break;
			}
		}
	}

	public static ArrayList<TaskInfo> getTasks(){
		return tasks;
	}

	public static void loadXMLString(String xml) throws ParserConfigurationException, SAXException, IOException{
		ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(stream);
		//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			Node nNode = null;
			Element eElement = null;
			NodeList nList = null;
			NodeList attrList = null;
			
			nList = doc.getElementsByTagName("task");
			for(int temp = 0; temp < nList.getLength(); temp++){
				nNode = nList.item(temp);
				if(nNode.getNodeType() == Node.ELEMENT_NODE){
					eElement = (Element)nNode;
					addTaskInfo(new TaskInfo(eElement.getAttribute("host"), AppSettings.getModuleInfo(eElement.getAttribute("module")), Integer.parseInt(eElement.getAttribute("tid"))));
				}
			}

	}
}
