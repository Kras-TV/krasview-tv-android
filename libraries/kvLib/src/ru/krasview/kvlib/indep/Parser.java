package ru.krasview.kvlib.indep;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Parser {
	public static Document XMLfromString(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes("UTF8"));
			doc = db.parse(is);
		} catch (ParserConfigurationException e) {
			System.out.println("XML parse error: " + e.getMessage());
			return null;
		} catch (SAXException e) {
			System.out.println("Wrong XML file structure: " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println("I/O exeption: " + e.getMessage());
			return null;
		}
		return doc;
	}

	public static String getValue(String tag, Node node) {
		Node n = node.getAttributes().getNamedItem(tag);
		if(n!=null) {
			return node.getAttributes().getNamedItem(tag).getTextContent();
		}
		NodeList nlList  = ((Element)node).getElementsByTagName(tag);
		try {
			if(nlList.item(0).getFirstChild() == null) {
				return "null";
			}
		} catch(Exception e) {
			return null;
		}
		return nlList.item(0).getFirstChild().getNodeValue();
	}
}
