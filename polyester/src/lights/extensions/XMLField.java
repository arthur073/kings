package lights.extensions;

import lights.interfaces.IField;
import lights.interfaces.IValuedField;
import lights.utils.SerializeTuple;
//import lights.interfaces.IValuedField;

//import java.io.IOException;
import org.w3c.dom.*;
//import org.xml.sax.SAXException;
//import javax.xml.parsers.*;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

/**
 * Field for matching an XPath expression against a DOM document
 * Unlike other fields proposed for LightTS, the "matches"
 * relation is not symmetric here: the recipient field must be of type
 * XPath, and the parameter of type (XML) Document
 * 
 * @author babak
 *
 */
public class XMLField extends TypedField {
	
	/**
	 *default value 
	 */
	private static final long serialVersionUID = 1L;
	
	private XPathExpression path = null;
	private Document doc = null;
	
	public XMLField(XPathExpression path) {
		this.path = path;
		this.setType(path.getClass());
	}
	
	public XMLField(Document doc) {
		this.doc = doc;
		this.setType(doc.getClass());
	}

	@Override
	/**
	 * Determines the rule used for pattern matching between XMLFields. <BR>
	 * <code>this.matches(f)</code> returns <code>true</code> if:
	 * <OL>
	 * 
	 * <LI>the recipient is of type XPathExpression and the parameter of type Document </I>;
	 * 
	 * <LI>and the XPath expression matched against the Document returns a non-null result
	 * 
	 * </OL>
	 * @return <code>true</code> if the field passed as a parameter matches
	 *            this field, <code>false</code> otherwise.
	 */
	public boolean matches(IField field) {
		//recipient must be of type XPath and be non-null
		//field must be non null
		if ((this.path == null) || (field == null)) return false; 
		//field must be instance of a Document
		//if (!(field.getType().isAssignableFrom(Document.class))) return false;

		if (field instanceof XMLField ||
				( field instanceof IValuedField && Document.class.isAssignableFrom(field.getType()))
				//this case is when we have an IValuedField and the value is of a class that implements the Document interface
				){

			XMLField xf = (XMLField) field;
			
			if (!xf.isFormal()){ //if the field contains an XML Document
				Document otherdoc = (Document)(xf.getValue());
				Object result = null;
				try {
					result = path.evaluate(otherdoc, XPathConstants.NODESET); //evaluate XPath expression over target doc
				}
				catch (Exception e) {
					return false;
					}

				return (result != null);
			} 

		}  //if we get here either the other field is not an XMLField, or it's formal as well
		return false;
	}
		
	public int hashCode(){
		try{
			
			if (path != null) return 31; // I'm not sure if the xpath implementation we use returns a proper hashCode or ToString() that we could use instead
			else {
				if (doc.getDocumentElement() !=null) {
					int hash = 7;
					hash = hash + doc.getDocumentElement().getNodeName().hashCode()*31;
					hash = hash *31 + (null == doc.getDocumentElement().getNodeValue() ? 0 : doc.getDocumentElement().getNodeValue().hashCode());
					return hash; // a simple hashcode to avoid getting into any null pointer exceptions
				}
				return 29;
			}
		}
		catch(Exception e){ //just to avoid any problems
			return 73;
		}
	}
	
	public String toString() {
		if (path != null) return path.toString();
		else {
			if (doc.getDocumentElement() !=null)
				return "[XMLField:"+ doc.getDocumentElement().getNodeName() +" " + doc.getDocumentElement().getNodeValue()+"...]";//.substring(arg0, arg1)
			return doc.toString();
		}
	}

	public Object getValue() {
		if (path != null) return path;
		else return doc;
	}

	public boolean isFormal() {
		if (path != null) return true;
		return false;
	}

	public IField setValue(Object obj) {
		if (obj instanceof XPathExpression) path = (XPathExpression) obj;
		else if (obj instanceof Document) doc = (Document) obj;
		return this;
	}

	
	//@Override
	/*public Object clone()  {
		if (path != null) {
			return super.clone(); //use method from superclass (get deep copy...)
		}
		else {
			Document newdoc = (Document)doc.cloneNode(true);
			return new XMLField(newdoc);
		}
	}*/
	
	@Override
	public boolean equals(Object field) {
		if (field != null && field instanceof XMLField){
			XMLField xfield = (XMLField)field;
			Object val = xfield.getValue();
			if (doc !=null) { //it's an XML-type node

				if(val !=null && val instanceof Document){ 
					//the other field also has a doc
					return doc.isEqualNode((Document)val);
				} else 
					return false; //the other field doesn't have XML or is null
			} else if (path !=null) {
				if(val !=null && val instanceof XPathExpression){ 
					//the other field also has an Xpath field
					return path.equals(val);
				} else 
					return false; //the other field doesn't have an Xpath (or is null)
			} 
			else //we shouldn't be in this case, but in one of the above : an XML field must have a path OR a doc
				return false;
		} 
		else	
			return false;
	}

}
