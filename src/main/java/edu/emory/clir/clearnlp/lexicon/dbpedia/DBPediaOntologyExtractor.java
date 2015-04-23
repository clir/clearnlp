package edu.emory.clir.clearnlp.lexicon.dbpedia;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.kohsuke.args4j.IllegalAnnotationError;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.XmlUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

public class DBPediaOntologyExtractor implements DBPediaXML
{
	public void extract(InputStream in, PrintStream out) throws Exception
	{
		DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dFactory.newDocumentBuilder();
		Document doc = builder.parse(in);
		
		NodeList classes = doc.getElementsByTagName(OWL_CLASS);
		int i, j, len, size = classes.getLength();
		String rdfAbout, rdfResource;
		Element owlClass, subClass;
		NodeList subClasses;
		StringBuilder sb = new StringBuilder();
		
		for (i=0; i<size; i++)
		{	
			sb.setLength(0);;
			owlClass = (Element)classes.item(i);
			subClasses = owlClass.getElementsByTagName(RDFS_SUBCLASS_OF);
			len = subClasses.getLength();

			rdfAbout = extractType(XmlUtils.getTrimmedAttribute(owlClass, RDF_ABOUT));
			sb.append(extractType(rdfAbout));

			for (j=0; j<len; j++)
			{
				subClass = (Element)subClasses.item(j);
				rdfResource = XmlUtils.getTrimmedAttribute(subClass, RDF_RESOURCE);
				
				if (rdfResource.startsWith(DBPEDIA_ORG_ONTOLOGY))
				{
					sb.append(StringConst.TAB);
					sb.append(extractType(rdfResource));
				}
			}
			
			if (rdfAbout.equals("Mayor")) sb.append("\tPolitician");
			out.println(sb.toString());
		}
		
		out.close();
	}
	
	private String extractType(String url)
	{
		int idx = url.lastIndexOf(StringConst.FW_SLASH) + 1;
		if (idx >= url.length()) throw new IllegalAnnotationError(url);
		return url.substring(idx);
	}
	
	static public void main(String[] args) throws Exception
	{
		// args[0] = "dbpedia.owl";
		new DBPediaOntologyExtractor().extract(new FileInputStream(args[0]), new PrintStream(IOUtils.createXZBufferedOutputStream(args[1])));
	}
}
