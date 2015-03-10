/**
 * Copyright 2014, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.clir.clearnlp.util;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class XmlUtils
{
	private XmlUtils() {}
	
	static public String getPrettyPrint(Document doc)
	{
		try
		{
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			return writer.getBuffer().toString();			
		}
		catch (Exception e) {e.printStackTrace();}
		
		return null;
	}
	
	static public Element getElementByTagName(Element element, String name, int index)
	{
		NodeList list = element.getElementsByTagName(name);
		return (Element)list.item(index);
	}
	
	static public Element getFirstElementByTagName(Document document, String name)
	{
		return getFirstElement(document.getElementsByTagName(name));
	}
	
	static public Element getFirstElementByTagName(Element element, String name)
	{
		return getFirstElement(element.getElementsByTagName(name));
	}
	
	static private Element getFirstElement(NodeList list)
	{
		return list.getLength() > 0 ? (Element)list.item(0) : null;
	}
	
	static public String getTrimmedTextContent(Element element)
	{
		return (element != null) ? element.getTextContent().trim() : null;
	}
	
	static public int getIntegerTextContent(Element element)
	{
		return Integer.parseInt(getTrimmedTextContent(element));
	}
	
	static public double getDoubleTextContent(Element element)
	{
		return Double.parseDouble(getTrimmedTextContent(element));
	}
	
	static public boolean getBooleanTextContent(Element element)
	{
		return Boolean.parseBoolean(getTrimmedTextContent(element));
	}
	
	static public String getTrimmedAttribute(Element element, String name)
	{
		return element.getAttribute(name).trim();
	}
	
	static public boolean getBooleanAttribute(Element element, String name)
	{
		return Boolean.parseBoolean(getTrimmedAttribute(element, name));
	}
	
	static public int getIntegerAttribute(Element element, String name)
	{
		return Integer.parseInt(getTrimmedAttribute(element, name));
	}
	
	static public double getDoubleAttribute(Element element, String name)
	{
		return Double.parseDouble(getTrimmedAttribute(element, name));
	}
	
	static public List<Node> getAttributeNodeList(Element element, Pattern name)
	{
		NamedNodeMap nodes = element.getAttributes();
		List<Node> attributes = Lists.newArrayList();
		int i, size = nodes.getLength();
		Node node;
		
		for (i=0; i<size; i++)
		{
		    node = nodes.item(i);
		    
		    if (name.matcher(node.getNodeName()).find())
		    	attributes.add(node);
		}

		return attributes;
	}
	
	static public Element getDocumentElement(InputStream in)
	{
		DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
		
		try
		{
			DocumentBuilder builder = dFactory.newDocumentBuilder();
			Document        doc     = builder.parse(in);
			
			return doc.getDocumentElement();
		}
		catch (Exception e) {e.printStackTrace();}
		
		return null;
	}
	
	static public List<Element> getChildElementList(Element root)
	{
		List<Element> list = Lists.newArrayList();
		NodeList nodes = root.getChildNodes();
		int i, size = nodes.getLength();
		Node node;
		
		for (i=0; i<size; i++)
		{
			node = nodes.item(i);
			
			if(node.getNodeType() == Node.ELEMENT_NODE)
				list.add((Element)node);
		}
		
		return list;
	}
}