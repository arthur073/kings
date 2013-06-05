package polyester.util;

import javax.xml.xpath.*;

@SuppressWarnings("serial")

/**
 * A wrapper class for XPathExpressions. Hides some of the boilerplate
 * and adds Serializable (useful in particular for tuple spaces).
 * String is final, so we're using delegation instead of inheritance here.
 * @author babak
 */
public class SearchString implements java.io.Serializable {
	
	private String s;
	
	public SearchString(String s) {
		this.s = s;
	}
	
	public String getValue() {
		return s;
	}
	
	public XPathExpression getXPathExpression() throws XPathExpressionException {
		//XPATH boilerplate
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		
		//create matching tuple
		XPathExpression expr = xpath.compile(s);	
		return expr;
	}
	
	public String toString() {
		return s;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof SearchString && s.equals(o.toString()));
	}
	
	@Override
	public int hashCode() {
		if (s == null) 
			return 0; 
		else return s.hashCode();
	}

}
