package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * Manage the settings of the gridworld gui. All settings are stored in the gridworld.xml in the program path.
 * The Manager can read and write the settings using SAX and DOM.
 * showGUI = if true the gui will be shown else not.
 * sleepTime = Time to sleep before return a reward.
 * loadFilePath = Path of a file automatically open if the gridword editor start
 * lastFilePath = set by the gridworld editor. The path of the last file opened.
 * activeColor = Color of the active Cell in the GridTable
 */
public class GridWorldPropertyManager implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5274462951411079193L;
	private static GridWorldPropertyManager instance = new GridWorldPropertyManager();
	private static String filePath = "";
	private static Document doc = null;

	public static GridWorldPropertyManager getInstance() {
		return instance;
	}

	public static void loadConfigFile(String filePath) {
		GridWorldPropertyManager.filePath = filePath;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new File(filePath));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static String getElement(String elementName) {
		Element e = doc.getDocumentElement();

		NodeList childNodes = e.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node n = childNodes.item(i);
			String name = n.getNodeName();
			if (name.equals(elementName)) {
				return n.getTextContent();
			}
		}
		return "";
	}

	private static void setElement(String elementName, String value) {
		Element e = doc.getDocumentElement();

		NodeList childNodes = e.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node n = childNodes.item(i);
			String name = n.getNodeName();
			if (name.equals(elementName)) {
				n.setTextContent(value);
			}
		}
		try {
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			DOMSource source = new DOMSource(doc);
			FileOutputStream os = new FileOutputStream(new File(filePath));
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result);
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
			.parse(new File(filePath));
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		} catch (TransformerFactoryConfigurationError e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException el) {
			el.printStackTrace();
		} catch (TransformerException el) {
			el.printStackTrace();
		} catch (SAXException el) {
			el.printStackTrace();
		} catch (IOException el) {
			el.printStackTrace();
		} catch (ParserConfigurationException el) {
			el.printStackTrace();
		}
		
	}

	public static void setLastFilePath(String filePath) {
		setElement("lastFilePath", filePath);
	}
	
	public static Color getColor(){
		Color color = null;
		Element e = doc.getDocumentElement();

		NodeList childNodes = e.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node n = childNodes.item(i);
			String name = n.getNodeName();
			if (name.equals("activeColor")) {
				NamedNodeMap nnm = n.getAttributes();
				Node tmp_node = nnm.getNamedItem("r");
				int r = Integer.parseInt(tmp_node.getTextContent());
				tmp_node = nnm.getNamedItem("g");
				int g = Integer.parseInt(tmp_node.getTextContent());
				tmp_node = nnm.getNamedItem("b");
				int b = Integer.parseInt(tmp_node.getTextContent());
				color = new Color(r, g, b);
			}
		}
		return color;
	}
	
	public static void setColor(Color color){

		Element e = doc.getDocumentElement();

		NodeList childNodes = e.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node n = childNodes.item(i);
			String name = n.getNodeName();
			if (name.equals("activeColor")) {
				NamedNodeMap nnm = n.getAttributes();
				Node tmp_node = nnm.getNamedItem("r");
				tmp_node.setNodeValue(String.valueOf(color.getRed()));
				tmp_node = nnm.getNamedItem("g");
				tmp_node.setNodeValue(String.valueOf(color.getGreen()));
				tmp_node = nnm.getNamedItem("b");
				tmp_node.setNodeValue(String.valueOf(color.getBlue()));
			}
		}
		try {
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			DOMSource source = new DOMSource(doc);
			FileOutputStream os = new FileOutputStream(new File(filePath));
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result);
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
			.parse(new File(filePath));
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		} catch (TransformerFactoryConfigurationError e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException el) {
			el.printStackTrace();
		} catch (TransformerException el) {
			el.printStackTrace();
		} catch (SAXException el) {
			el.printStackTrace();
		} catch (IOException el) {
			el.printStackTrace();
		} catch (ParserConfigurationException el) {
			el.printStackTrace();
		}
	}
}
