package polyester.tests;

import java.io.File;
import java.io.IOException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.junit.Before;

public class TestXMLMatching {

	@Before
	public void setUp() throws Exception {
	}
	
	
	public static void main(String[] args) throws Exception {
		
		//parsing xml file
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse("books.xml");

		//creating xpath expression and matching
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath
				.compile("//book[author='Neal Stephenson']/title/text()");

		//collecting results
		Object res = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) res;
		for (int i = 0; i < nodes.getLength(); i++) {
			System.out.println(nodes.item(i).getNodeValue());
		}

		//testing xslt transformation
		File xsltFile = new File("transform.xsl");
	    javax.xml.transform.Source xmlSource =
	        new javax.xml.transform.dom.DOMSource(doc);
	    javax.xml.transform.Source xsltSource =
	        new javax.xml.transform.stream.StreamSource(xsltFile);
	    javax.xml.transform.Result result =
	        new javax.xml.transform.stream.StreamResult(System.out);
	 
	    // create an instance of TransformerFactory
	    javax.xml.transform.TransformerFactory transFact =
	        javax.xml.transform.TransformerFactory.newInstance( );
	 
	    javax.xml.transform.Transformer trans = transFact.newTransformer(xsltSource);
	 
	    trans.transform(xmlSource, result);
	    
	}

}
