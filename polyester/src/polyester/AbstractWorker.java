package polyester;

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
//import org.w3c.dom.Document;

//import lights.Field;
//import lights.Tuple;
//import lights.interfaces.IField;
import lights.interfaces.ITuple;
import lights.interfaces.ITupleSpace;
import lights.interfaces.TupleSpaceException;
import polyester.Worker;
//import polyester.XMLField;
//import up2p.core.LocationEntry;
//import up2p.core.WebAdapter;
//import up2p.search.SearchResponse;
//import up2p.search.SearchResponseListener;
//import up2p.xml.TransformerHelper;

/**
 * Abstract "Query"-based worker. The idea of this worker is that it has a list of templates for tuples that it is "interested in",
 * and for which it associates a process.
 * Implementations of this worker should be built with a list of templates (using the method "addquerytemplate"), and should 
 * implement the abstract method "answerqueries" to process the collected tuples.
 * 
 * Basic usage: 
 * 1) define a list of templates and their associated processing
 * 2) add each template to the worker using addQueryTemplate(ITuple)
 * 3) define the processing in abstract method answerQueries(ITuple template, Ituple query)
 * 4) start the worker using method start()
 * 5) if applicable, stop the agent whenever necessary
 * 6) during normal running of the agent, it is possible to temporarily listen for the asynchronous arrival of certain tuples, using a listener
 * pattern. Use the addQueryListener method. 
 */

public abstract class AbstractWorker extends Worker {

	public static Logger LOG;
	public static final String LOGGER = "polyester.worker";



	protected String name;

	protected ITuple searchtemplate;

	/**lists to manage the tuples that have already been read 
	 *  they are indexed by template (i.e. for each template / 
	 *  query accepted by the worker, there is a list)
	 * 
	 */

	/** Incoming queue of tuples to be processed by the worker
	 * 
	 */
	protected Inbox workerInbox;
	
	/**
	 * Listeners for asynchronous arrival of tuples 
	 */
	protected List<QueryListener> searchListeners;

	/**
	 *  Threads that read the tuplespace waiting for new tuples
	 *  They are mapped to their respective templates
	 */
	private Map<ITuple,TSScanner> templateScanners;

	/**
	 * Constructor of a worker
	 * @param ts The tuplespace that this worker interacts with
	 */
	public AbstractWorker(ITupleSpace ts){
		super(ts);
		workerInbox = new Inbox();
		LOG = Logger.getLogger(LOGGER);
		searchListeners = new LinkedList<QueryListener>();
		templateScanners = new HashMap<ITuple, TSScanner>();
		//doneList = new HashMap<ITuple,List<ITuple>>();
		searchtemplate = TupleFactory.createSearchTemplate();
	}

	/**
	 * Abstract method for answering "queries" (generic processing associated to particular tuples).
	 *
	 * @param template the template that was used to collect the tuple
	 * @param query the tuple
	 * @return a possibly empty list of tuples to be placed in the tuple space as a result of the processing, e.g. "answers" to the query
	 */
	protected abstract List<ITuple> answerQuery(ITuple template, ITuple query);


	/**
	 * add a query template, and a thread to scan for that template
	 * @param tpl
	 */
	protected void addQueryTemplate(ITuple tpl){
		TSScanner scanner = new TSScanner(tpl, workerInbox);
		templateScanners.put(tpl,scanner);
		scanner.start();
	}
	
	
	/**
	 * Stop this worker.
	 * TODO: make sure that everything is stopped properly
	 */
	public void stop(){
		super.stop();
		for (TSScanner scanner: templateScanners.values()){
			scanner.mystop();
		}
		
	}

	/**
	 *  adds a tuple to the list of 'done' queries. 
	 *  The idea is that this should be done to avoid the worker 
	 *  outputting a query and attempting to process it himself 
	 * 
	 * @param query the query to be added (then ignored)
	 * @param the template that should not react on matching the query
	 */
	protected void ignoreQuery(ITuple template, ITuple query){

		ITuple key = template; 
		//List<ITuple> list=null;
		if(key!=null && templateScanners.containsKey(key)) {
			TSScanner searchscanner = templateScanners.get(key);
			searchscanner.ignore(query);
			LOG.debug(name+ " added query to be ignored : "+ query);
		}
		
		else 
		{
			LOG.debug(name+ " doesn't have SEARCH among his templates ");
		}
	}

	/**
	 * Answer queries. Go through the supported templates, check for a matching query, and answer it
	 *
	 */
	private void answerQueries() {

		//gets next query pair [template, query] from inbox : the template is necessary for the dynamic agents
		TuplePair querypair = workerInbox.get(); 
		
			LOG.debug(name+" processing query:" + querypair.query );

			//for (ITuple query:querylist){


				List<ITuple> ansTuple = answerQuery(querypair.template, querypair.query);

				if(ansTuple!=null && //don't think it will be null... but it could be empty.
						ansTuple.size()>0){

					//for (ITuple itu: ansTuple){ //output all answers
					try {
						space.outg(ansTuple.toArray(new ITuple[ansTuple.size()]));
					} catch (TupleSpaceException e) {
						LOG.error("TupleSpace Error:"+e);
					}
					//}
					LOG.debug(name+" worker posting "+ansTuple.size()+" answers for query "+querypair.query.toString());
				}
				else
					LOG.debug(name+": No immediate answer for query "+querypair.query.toString());
				//note: may not be an error (e.g. "remove")
			//}//for all found queries

		//}//end for all templates
	}


	/**
	 *  Add a listener for asynchronous search responses
	 *
	 * @param listener
	 * @param template
	 */
	protected void addListener(TupleListener listener, ITuple template){
		
			QueryListener lis = new QueryListener(listener, template);
			lis.start();
		synchronized(searchListeners){
			searchListeners.add(lis);
		}
		LOG.debug("returning from launching an async search");
	}

	/**
	 *  Method to do synchronous queries on the tuple space -- output query, wait for answer
	 *  this one should be used to retrieve multiple answers
	 * @param query a tuple defining the query
	 * @param answerTemplate a tuple template defining the answer
	 * @return the tuple found to match the answer tuple
	 * @throws TupleSpaceException for whatever reason TupleSpace.in(ITuple) may throw an exception
	 */
	protected List<ITuple> synchronousMultiQuery(ITuple query, ITuple answerTemplate) throws TupleSpaceException{
		space.out(query);
		ITuple [] answer = space.rdg(answerTemplate); //listen for answer using template
		if (answer==null){
			space.rd(answerTemplate); //block until there is *one* answer
			answer = space.rdg(answerTemplate); //get all answers
		}
		if (answer==null){
			LOG.error("syncMultiQuery got a null answer!");
			return new ArrayList<ITuple>();
		}
		return Arrays.asList(answer);
		//return answer;

	}

	/**only for testing purpose
	 * 
	 * @return the tuple space object!!
	 */
	public ITupleSpace getTS(){
		return space;
	}

	/**
	 *  Method to do asynchronous queries on the tuple space -- output query, register listener
	 *
	 * @param query a tuple defining the query
	 * @param answerTemplate a tuple template defining the answer
	 * @return the tuple found to match the answer tuple
	 * @throws TupleSpaceException for whatever reason TupleSpace.in(ITuple) may throw an exception
	 */
	protected void asynchronousQuery(ITuple query, ITuple answerTemplate, TupleListener listener) throws TupleSpaceException{
		space.out(query);
		//addListener(listener,answerTemplate); // add a listener for this query
	}

	/**
	 *  outputs a tuple [ "Error", exception] so the exception can be handled by user interface
	 * @param e the exception
	 */
	protected void notifyErrorToUser (Exception e){
		if (e!=null)
			try {
				String info = e.toString();

				space.out(TupleFactory.createTuple(TupleFactory.NOTIFY_ERROR, info));
			} catch (TupleSpaceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}

	/**
	 * give a name to the worker (will appear in the logs)
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Work: normal processing of this worker. The principle of such a worker is that it has a standard list of templates that it is
	 * listening for, and associated processing for these tuples. Thus the standard processing by this agent is to process the tuples
	 * using the abstract method "answerqueries". This method should be customized for the worker.
	 */
	public void work() {

		// answer all queries from supported templates
		answerQueries();
	
	}

	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	//private classes
	//////////////////////////////////
	/**
	 *  this class is a thread that scans the tuplespace with a specific template, 
	 *  and drops the tuples to be processed in an "Inbox" object, which is a message queue 
	 * @author alan
	 *
	 */
	private class TSScanner extends Thread {
	
		//template that this particular thread will be looking for
		private ITuple template ;
		//list of "already seen" tuples matching the template, not to be re-processed
		private Set<ITuple> doneList;
		//flag to know that the thread should stop
		private boolean shutdownflag;
		//where to drop newly read tuples
		private Inbox inbox; 
		
		/**
		 * constructor
		 * @param tuple template that this scanner looks for
		 * @param inbox inbox where the new tuples are stored
		 */
		public TSScanner(ITuple tuple, Inbox inbox){
			super("TSSCanner/"+tuple.get(0));
			template = tuple;
			doneList= new HashSet<ITuple>();
			shutdownflag = false;
			this.inbox=inbox;
		}
		
		public void ignore(ITuple query) {
			doneList.add(query);
			
		}

		/**
		 * Collects tuples matching the input template.
		 * If these tuples have been recently read, then we drop them
		 * field 0 is a string "Search", field 1 is a formal field of class "SearchString"
		 *
		 * @return an array of tuples
		 */
		protected List<ITuple> scanForQueries(boolean checkDuplicates) {

			//template for the queries to be answered

			//retrieve results, see if they match
			ITuple[] allqueries = null;
			//LOG.debug(name+ " worker looking for work...");
			
				try {
					LOG.debug(name+"/"+template.get(0)+ " looking for work...");
					allqueries = space.rdg(template); // read query
					//TODO: how to handle the problem of seeing several times the same query

					if (allqueries==null){ //if there's no matching tuples, then we want to wait rather than looping
						//we can also empty the donelist:
						doneList.clear();
						LOG.debug(name+"/"+template.get(0)+ "going into WAIT mode...");
						space.rd(template); //wait until one matching tuple shows up (do not save read tuple since we'll get it right after)
						LOG.debug(name+": scanner "+ template+ " reading something!!");						
						allqueries= space.rdg(template); //re-scan in order to get all the matching tuples (in case several were output at the same time)
					}
				} catch (TupleSpaceException e) {}
			

			List<ITuple> toReturn = new ArrayList<ITuple>();
			Set<ITuple> seen = new HashSet<ITuple>();

			if (allqueries !=null) { //if we didn't find any queries we may as well leave!

				Set<ITuple> readqueries = doneList;

				for (ITuple q : allqueries){
					ITuple qt= (ITuple)q;

					if (readqueries!= null && readqueries.contains(qt)) //check if we've had this query recently
					{seen.add(qt); //if we've seen it then we will put it in the cache
					//LOG.debug(name+" ignoring tuple:"+qt);

					}
					else{
						toReturn.add(qt); // if not we will process it

					}

				}

				/*we now only store in the cache the queries that we found this time around.
		    			Queries that we've seen three rounds before then disappeared should not be in the cache
				 */
				seen.addAll(toReturn); //get the full list of queries we just picked up (allqueries except in List format rather than [])


			}

			//this must be done whether or not we've found tuples! if we haven't found any then there are not more "seen" tuples, nothing to ignore next time there actually are any tuples...
			doneList= seen; // make that the new cache.

			//TODO: add a check mechanism to ensure that the doneList does not explode in size
			return toReturn; //we will now return the tuples that are new this time around for processing
		}
		
		/**
		 * run: scan the tuplespace for tuples matching the template, filter out those that have been seen recently,
		 * and place the remaining ones to the "inbox" of this worker. Loop until the thread is told to shutdown using the mystop() method
		 * @throws InterruptedException 
		 */
		public void run() {
			while(!shutdownflag){
			List<ITuple> newtuples = scanForQueries(true);
			List<TuplePair> pairs = new ArrayList<TuplePair>();
			for (ITuple tup : newtuples)
				pairs.add(new TuplePair(template, tup));
			if(!newtuples.isEmpty())
				inbox.put(pairs);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
		
		/**
		 * stop this thread (could not call it stop because that's a deprecated & final method
		 * the thread will not stop immediately but finish one iteration of scanning for queries, etc..
		 */
		public void mystop(){ 
			shutdownflag=true;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A tuplePair is a couple of tuples (template, tuple), where template should <code>match</code> tuple.
	 * It is used in the queue of tuples to be processed by the worker. 
	 * Processing tasks are associated to the different templates of a worker, and as the matching tuples are collected by templates,
	 * this structure allows new tasks in the queue to appear as a task (represented by the template) associated to the tuple.
	 * @author alan  
	 */
	private class TuplePair{
		public ITuple template;
		public ITuple query;
		public TuplePair(ITuple temp, ITuple quer){
			template = temp;
			query= quer;
		}
	}
	
	
	/**
	 * Inbox is a class that implements a synchronized message queue, 
	 * of the producer / consumer pattern. Producers are TS Scanners, and the consumer 
	 * is the main thread of this worker 
	 * @author alan
	 *
	 */
	private class Inbox {

		/** Queue of tuples/templates (TuplePair) in the order that the tuples arrive in the tuplespace 
		 * 
		 */
		private LinkedList<TuplePair> todoQueue; 
		
		/**
		 * constructor. All that is needed is a linked list for the message queue
		 */
		public Inbox(){
			todoQueue = new LinkedList<TuplePair>();	
		}

		public synchronized TuplePair get() {
			while (todoQueue.isEmpty()) {
				try {
					// Wait here until the inbox contains any tuples to process
					wait();
					LOG.debug(name+ " waking up!");
				} catch (InterruptedException e) { }
			}

			return todoQueue.removeFirst();

			// Finished up here so notify waiting threads.
			//notifyAll(); not necessary?
			//return cavity;
		}

		public synchronized void put(List<TuplePair> tuplepairs ) {
			//appends the new tuples at the end of the queue
			todoQueue.addAll(tuplepairs);
			notifyAll(); //notify the waiting thread. At this point there's only one, but perhaps there will be several
		}
	} 

//////////////////////////////////////////////////////////////////////////////////
	
	/**
	 *  a query listener that can be added in order to collect asynchronous search responses and 
	 *  do callbacks to the objects who placed the searches (or their specified listeners)
	 */
	private class QueryListener implements Runnable{
		public TupleListener listener;
		public ITuple template;
		public boolean stopflag= false;

		public QueryListener(TupleListener l, ITuple t){
			listener=l;
			template =t;
		}

		public void start() { 
		//TODO: interrupt and quit after a fixed delay... for now the thread stops after receiving a response (or a set of responses) 
			
			
		Thread runner = new Thread(this);
		runner.start();//run();
		}
		
		//@Override
		public void run() {
			//while(!stopflag){ //TODO: for now we only get one set of responses
			try {
				ITuple [] alltuples = space.rdg(template); // read query
				//TODO: how to handle the problem of seeing several times the same query ... maybe migrate this to standard "inbox" processing

				if (alltuples==null){ //if there's no matching tuples, then we want to wait rather than looping
					space.rd(template); //wait until one matching tuple shows up (do not save read tuple since we'll get it right after)
					LOG.debug(name+": listener for search "+ template+ " getting async answers!!");						
					alltuples= space.rdg(template); //re-scan in order to get all the matching tuples (in case several were output at the same time)
				}
				sendResults(alltuples);
			} catch (TupleSpaceException e) {}
			//}
		}

		/**
		 * Extract the results found in the TS and send them back to the listener as SearchResponse[]
		 * TODO: replace all this by simply sending the list of tuples. The subscriber can manage that
		 * @param res
		 */
		public void sendResults(ITuple[] res) {


			listener.receiveTuple(new TupleEvent(Arrays.asList(res))); // notify of new responses... 

		}

		
	}


}