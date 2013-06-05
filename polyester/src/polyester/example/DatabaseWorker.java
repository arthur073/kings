package polyester.example;

import java.io.File;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.*;

//import polyester.Worker;
import polyester.AbstractWorker;
import polyester.util.SearchString;
import lights.*;
import lights.extensions.XMLField;
import lights.interfaces.*;

public class DatabaseWorker extends AbstractWorker {
	
	//fake db for now that contains a list of XML docs and their "url" (the String key)
	//should be replaced with an XML DB in the future
	private Map<String,Document> db; 

	public DatabaseWorker(ITupleSpace space) {
		super(space);
		db = new HashMap<String,Document>();
	
		//create template
		ITuple t2 = new Tuple();
		t2.add(new Field().setValue("Search")); //must be exact match	
		t2.add(new Field().setType(SearchString.class));
	
		//add template [see AbstractWorker]
		addQueryTemplate(t2);
	}
	
	//removed because now handled by AbstractWorker
	/*public void work() {
			postMatches(retrieveAllMatches(handleSearch()));
	}*/
	
	/**
	 * loads XML files to the "database"
	 */
	public void add(String filename) throws Exception {
		//DOM factory boilerplate
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		File thefile = new File(filename);
		System.out.println(thefile.getAbsolutePath());
		Document doc = builder.parse(thefile);
		this.add(filename, doc);
	}
	
	public void add(String path, Document doc) {
		db.put(path, doc);
		System.out.println("Adding document to db:"+path+":"+doc.getDocumentElement().getTextContent());
	}
	
	/*
	 * Collects "search" tuples in the tuple space.
	 * field 0 is a string "Search", field 1 is a formal field of class "SearchString"
	 * 
	 * @return an array of tuples
	 * 
	 *  Alan oct 19th, 2009: this method made redundant by use of AbstractWorker
	 * /
	public ITuple[] handleSearch() {
		//advertize search answering capability
		//create and populate the second tuple 
		ITuple t2 = new Tuple();
		t2.add(new Field().setValue("Search")); //must be exact match	
		t2.add(new Field().setType(SearchString.class));
		
		//retrieve results, see if they match
		ITuple[] results = null;
		try {
			results = space.rdg(t2);
			if (results!=null)
			for (int k = 0;k<results.length;k++){
				System.out.println("got tuple("+Integer.toString(k)+"):"+results[k].toString());
				k++;
				}
		} catch (TupleSpaceException e) {}
		
		return results;
	}*/
	
	/**
	 * Merge all the matches found for the various tuples passed as argument.
	 * Uses a Set to remove duplicates, BUT
	 * this might not be useful when the answers for each tuple require specific
	 * formatting of the response tuple.
	 * 
	 * update Alan 21.10.2008: changed treeset to hashset because Documents are not comparable
	 * @param tuples
	 * @return a Set of Documents that match the tuples
	 * @throws XPathExpressionException
	 */
	public Set<Document> retrieveAllMatches(ITuple[] tuples) {
		if (tuples == null) return null;
		Set<Document> matches = new HashSet<Document>();
		for (ITuple tuple : tuples) {
			List<Document> myresults = retrieveMatches(tuple);
			for (Document dd:myresults){
				matches.add(dd);	
				//System.out.println("Added: "+dd.getDocumentElement().getTextContent());
			}
			
		}
		return matches;
	}
	
	/**
	 * Match the XPATH expression in the tuple against the database
	 * @return documents in the database that match the expression
	 * @throws XPathExpressionException 
	 */
	public List<Document> retrieveMatches(ITuple tuple) {
		//made public for testing purposes - should really be private
		
		List<Document> matches = new ArrayList<Document>(); 
			
		SearchString ss = (SearchString) (( (Field) tuple.get(PAYLOAD))).getValue();			
		XPathExpression expr = null;
		try {
			expr = ss.getXPathExpression();
		} catch (XPathExpressionException e1) {
			return matches;
		}
		
		for (Document doc : db.values()) {
			try {
				if ((Boolean) expr.evaluate(doc, XPathConstants.BOOLEAN)) {
					//System.out.println("about to add"+ expr.evaluate(doc));
					matches.add(doc);
				}
			} catch (XPathExpressionException e2) {e2.printStackTrace();}
		}
		return matches;
	}
	
	/*made redundant by use of AbstractWorker, incorporated in method answerqueries()
	public void postMatches(Set<Document> l) {
		//made public for testing purposes - should really be private
		
		if (l == null) return;
		//post matches in the tuple space
		for (Document d : l) {
			ITuple t = new Tuple();
			t.add(new Field().setValue("Result"));
			t.add(new Field().setValue(d));
		
			try {
				space.out(t);
				System.out.println("DBW posting result"+t.toString());
			} catch (TupleSpaceException e) {}
		}
	}*/

	@Override
	protected List<ITuple> answerQuery(ITuple template, ITuple query) {
		Set<Document> docset = retrieveAllMatches(new ITuple[]{query});
		
		List<ITuple> anslist = new ArrayList<ITuple>();
		
		if (docset == null) return anslist;
		
		System.out.println("DBWorker got "+docset.size()+" matches");
		//post matches in the tuple space
		for (Document d : docset) {
			ITuple t = new Tuple();
			t.add(new Field().setValue("Result"));
			t.add(new XMLField(d));
			
			anslist.add(t);
		}
		return anslist;
	}

}





