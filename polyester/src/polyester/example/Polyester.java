package polyester.example;

import polyester.CleaningWorker;
import polyester.Worker;
import lights.extensions.FastTupleSpace;
import lights.interfaces.*;
import lights.*;

/**Example application built using polyester primitives.
 * 
 * There are 4 agents: WebInputWorker, WebWorker, and DatabaseWorker, and CleaningAgent.
 * 
 * DatabaseWorker has a collection of XML documents, and listens for 2-field tuples with "Search" 
 * in the first field and a SearchString in the second field. It processes such tuples by matching the 
 * XPath search string to the documents. If the result is non-null, a tuple ["Result", Document] is output to the tuple-space.
 * 
 * WebInputWorker asks the user for an XPath search string. When the user enters a string, WebInputWorker places 
 * a tuple ["Search", (searchstring)] in the tuple space.
 * 
 * WebWorker listens for tuples like ["Result", (document)], and each time it finds a new one it writes the Document URI to the standard out.
 * 
 * CleaningAgent is a standard agent that keeps track of new tuples in the tuple space and removes them after a set time in the space. 
 * This keeps memory usage low while allowing all other agents to simply read tuples of interest without actually removing them.
 *  
 * The collective behavior is that a user is prompted for XPath queries, and on entering an XPath query, the URI 
 * of all matching books is returned. Built-in behavior of the agents [see AbstractWorker] makes them keep track of seen tuples, so that each 
 * new tuple in the tuple space is only processed once, but if another identical tuple appears at a later time, it is processed again.
 * 
 * Note: as of this release, the log4j logging is not setup properly.
 * 
 * @author babak, alan
 *
 */
public class Polyester {
	public static void main(String args[]) throws Exception {
		ITupleSpace ts = new FastTupleSpace();
		Worker ww = new WebWorker(ts);
		DatabaseWorker dw = new DatabaseWorker(ts);
		dw.add("books.xml");
		dw.add("book1.xml");
		dw.add("book2.xml");
		dw.add("book3.xml");
		dw.add("book4.xml");
		Worker wiw = new WebInputWorker(ts);
		CleaningWorker cleaning = new CleaningWorker(ts);
		
		dw.start();
		wiw.start();
		ww.start();
		cleaning.start();
		Thread.sleep(200000); //let it run for a while!
		System.out.println("Shutting down Polyester...");
		wiw.stop();
		dw.stop();
		ww.stop();
		cleaning.stop();
		System.out.println("Thanks for using Polyester!");
		System.exit(0);
	}

}
