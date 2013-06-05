package lights.utils;


import lights.Field;
import lights.Tuple;
import lights.interfaces.ITuple;

public class TupleFactory {

	//the different possible verbs
	public static final String LOOKUPXPATH = "LookupXpath";
	public static final String LOOKUPXPATHANSWER = "LookupXpathAnswer";
	public static final String SEARCHXPATH = "SearchXpath";
	public static final String SEARCHXPATHANSWER = "SearchXpathAnswer";
	public static final String REMOVE = "Remove";
	

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
	/** create a tuple template with a verb and n string fields
	 * @param pizzaType Pizza type you want to make
	 * @return Actual Pizza that was requested 
	 */
	public static ITuple createTuple(String verb, Object[] values) {
		ITuple t = new Tuple();

		t.add(new Field().setValue(verb)); // the verb given as input 

		for (Object o: values){
			t.add(new Field().setValue(o)); //this sets also the class of the object
		}

		return t;


	}
	
	/**
	 * SEARCH
	 * @param communityId
	 * @param xPath
	 * @param listener
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
			
		 return createQueryTupleTemplate(TupleFactory.SEARCHXPATH, 5);////5 fields for the answer template: comId, rid, title, location)
	}
	
	//search response
	public static ITuple createSearchReply(String comId, String id ,String title , String location, String qid){
		return createTuple(TupleFactory.SEARCHXPATHANSWER, new String [] {comId, id ,title ,location, qid});
	}
	
	//Template for search responses
	public static ITuple createSearchReplyTemplate(){
		return createQueryTupleTemplate(TupleFactory.SEARCHXPATHANSWER, 5);////5 fields for the answer template: comId, rid, title, location)
	}
	
	/////////////////////////////////////////
	
	
	
	
	
	/** create a tuple template with a verb and n string fields
	 * @param pizzaType Pizza type you want to make
	 * @return Actual Pizza that was requested 
	 */
	public static ITuple createTuple(String verb, Object singleValue) {
		ITuple t = new Tuple();

		t.add(new Field().setValue(verb)); // the verb given as input 

		
		t.add(new Field().setValue(singleValue)); //this sets also the class of the object
		

		return t;


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
