package polyester;

import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


import lights.Field;
import lights.Tuple;
import lights.extensions.XMLField;
import lights.interfaces.ITuple;

public class TupleFactory {

	//the different possible verbs
	public static final String LOOKUPXPATH = "LookupXpath";
	public static final String LOOKUPXPATHANSWER = "LookupXpathAnswer";
	public static final String SEARCHXPATH = "SearchXpath";
	public static final String SEARCHXPATHANSWER = "SearchXpathAnswer";
	public static final String REMOVE = "Remove";
	public static final String RESID_FROM_URI = "ResIDFromURI"; //function
	public static final String COMMUNITY_FROM_URI = "CommunityFromURI"; //function
	public static final String GET_RESID_FROM_URI = "GetResIdFromURI";
	public static final String GET_COMMFROMURI = "GetCommunityFromURI";
	public static final String PUBLISH = "PublishXML";
	public static final String NOTIFY_ERROR = "NotifyError";
	public static final String FILEMAP = "MapFile";
	public static final String GETLOCAL = "GetLocal";
	public static final String GETLOCALRESPONSE = "GetLocalR";
	public static final String RESOLVEURI = "ResolveURI";
	public static final String CONCAT = "Concatenate";
	public static final String CONCATANSWER = "ConcatenateAnswer";
	public static final String COMMUNITY_NOT_FOUND = "CommunityNotFound";
	public static final String RESOURCE_NOT_FOUND = "ResourceNotFound";
	public static final String LOOKUPSEARCHRESP = "LookupSearchResponse"; //to lookup metadata of searchresponses
	public static final String LOOKUPSEARCHRESP_ANS = "LookupSearchResponseAnswer";
	public static final String LOCALSYNCSEARCH = "SynchroLocalSearch";
	public static final String LOCALSYNCSEARCHRESPONSE = "SynchroLocalSearchResp";
	public static final String CR_TO_URI = "CRtoURI"; //function to compose a URi from Community / Resource
	public static final String CR_TO_URI_RESP = "CRtoURIResp"; //response to the above
	public static final String URI_TO_CR = "URItoCR"; //function to decompose a URI into Community / Resource
	public static final String URI_TO_CR_RESP = "URItoCRResp"; //resp[onse to the above
	public static final String COMPLEXQUERY = "ComplexQuery"; // trigger of a complex query. Parameters are in the subqueries, and trigger ID

	/** create a tuple template with a verb and n string fields
	 * @param verb the verb to place in the first field of the tuple
	 * @return howManyFields how many fields of type String to make as template 
	 */
	public static ITuple createQueryTupleTemplate(String verb, int howManyFields) {
		ITuple t = new Tuple();

		t.add(new Field().setValue(verb)); // the verb given as input 

		for (int i=0;i<howManyFields;i++){ //the n formal fields of the tuple, expecting the String arguments
			t.add(new Field().setType(String.class)); //new field of type "String"	
		}

		return t;
	}
	/** create a tuple with a verb and an array of objects
	 */
	public static ITuple createTuple(String verb, Object[] values) {
		ITuple t = new Tuple();

		t.add(new Field().setValue(verb)); // the verb given as input 

		for (Object o: values){ //TODO: check that o is not null and figure out what to do
			t.add(new Field().setValue(o)); //this sets also the class of the object
		}

		return t;
	}
	
	/** create a tuple template with a verb and an array of objects
	 */
	public static ITuple createTuple(List<String> fieldvalues) {
		
		ITuple t = new Tuple();

		for (String s: fieldvalues){
			t.add(new Field().setValue(s)); 
		}

		return t;


	}
	
	/** create a tuple template with a verb and an array of objects
	 */
	public static ITuple createTuple(String[] fieldvalues) {
		ITuple t = new Tuple();

		for (String s: fieldvalues){
			t.add(new Field().setValue(s)); 
		}

		return t;


	}
	
	/**
	 * Create a tuple of Type `SEARCH` from the provided fields
	 * @param communityId id of the community to search
	 * @param xPath XPATH expression to use as search criterion
	 * @param listener object to 
	 * @param qid
	 * @return
	 */
	public static ITuple createSearchTuple(String communityId, String xPath, 
			String qid) {
		//define a tuple for this query.
			
		String[] fields = new String[] {communityId, xPath, qid};
	
		 return createTuple(TupleFactory.SEARCHXPATH, fields);
	}
	
	//template for search
	public static ITuple createSearchTemplate(){
			
		 return createQueryTupleTemplate(TupleFactory.SEARCHXPATH, 3);////3 fields for the search: comId, xpath, qid
	}
	
	//search response
	public static ITuple createSearchReply(String comId, String id , String title, String filename , String location, String qid){
		return createTuple(TupleFactory.SEARCHXPATHANSWER, new String [] {comId, id ,title , filename, location, qid});
	}
	
	//Template for search responses
	public static ITuple createSearchReplyTemplate(){
		return createQueryTupleTemplate(TupleFactory.SEARCHXPATHANSWER, 6);////6 fields for the answer template: comId, rid, title, location)
	}
	
	//Template for search responses
	public static ITuple createSearchReplyTemplateWithDOM(){
		ITuple tpl= createQueryTupleTemplate(TupleFactory.SEARCHXPATHANSWER, 6);////6 fields for the answer template: comId, rid, title, location)
		tpl.add(createXMLTemplateField());
		return tpl;
	}

	//Template for search responses of a specific queryId
	public static ITuple createSearchReplyTemplate(String qid){
		ITuple tuple = createQueryTupleTemplate(TupleFactory.SEARCHXPATHANSWER, 5);////5 fields for the answer template: comId, rid, title, location)
		tuple.add(new Field().setValue(qid));// add the qid as actual field at the end
		return tuple;
	}
	
	//Template for search responses of a specific queryId
	public static ITuple createSearchReplyTemplateWithDOM(String qid){
		ITuple tuple = createQueryTupleTemplate(TupleFactory.SEARCHXPATHANSWER, 5);////5 fields for the answer template: comId, rid, title, location)
		tuple.add(new Field().setValue(qid));// add the qid as actual field at the end
		tuple.add(createXMLTemplateField());//new XMLField with wildcard
		return tuple;
	}
	/////////////////////////////////////////
	
	/**
	 *  create a tuple from a verb and a single value 
	 *  @return a Tuple with two fields, one containing the verb (a String) and the other the provided value
	 *  @param verb the verb to be placed as first field
	 *  @param singleValue the object to be placed in the second field of the tuple
	 */
	public static ITuple createTuple(String verb, Object singleValue) {
		ITuple t = new Tuple();

		t.add(new Field().setValue(verb)); // the verb given as input 

		
		t.add(new Field().setValue(singleValue)); //this sets also the class of the object
		

		return t;


	}





	

	public static ITuple createTemplate(String verb, Class<?>[] classes) {
			ITuple t = new Tuple();

			t.add(new Field().setValue(verb)); // the verb given as input 

			for (int i=0;i<classes.length;i++){ //the n formal fields of the tuple, expecting the String arguments
				t.add(new Field().setType(classes[i])); //new field of type [whatever class is classes[i]]	
			}

			return t;
		
	}

	public static ITuple createPublishTemplate() {
		//create a template for "Publish"+ two formal "String" fields  
		ITuple newtuple = TupleFactory.createTemplate(TupleFactory.PUBLISH, new Class[] {String.class, String.class});
		newtuple.add(createXMLTemplateField()); //add a formal field that matches any XML doc
		return newtuple;
	}

		
	public static XMLField createXMLTemplateField (){
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = null;
		try {
			expr = xpath
					.compile("."); //the wildcard!! this formal field will match any field containing an XMLDocument
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 
		}
		return  new XMLField(expr); //if there's an error it will create a field with a null Xpath but it shouldn't happen
	}
	
	/**
	 *  create an enhanced search response, with the additional field containing the DOM.
	 * @param comId
	 * @param id
	 * @param title
	 * @param filename
	 * @param location
	 * @param qid
	 * @param resourceDOM
	 * @return
	 */
	public static ITuple createSearchReplyWithDOM(String comId, String id,
			String title, String filename, String location, String qid,
			Document resourceDOM) {
		ITuple tup = createSearchReply(comId, id, title, filename, location, qid);
		tup.add(new XMLField(resourceDOM));
		return tup;
	}
	
	/** maybe not...
	 * Generate a unique (enough) identifier for a query tuple
	 * 
	 * @return
	 * /
	public static String generateID(){
		return "00";
	}*/
	
	
}
