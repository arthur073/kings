package lights.extensions;

//import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import lights.interfaces.ITuple;
import lights.interfaces.ITupleSpace;
import lights.interfaces.TupleSpaceError;
import lights.interfaces.TupleSpaceException;

/** This tuplespace implementation uses fine-grain concurrency management to
 * speed up reads and writes. In runs much faster than the basic LighTS implementation.
 * It uses its own thread-safe linked list implementation supporting 10 concurrent reads on each list element, 
 * and writes that only need to lock a single element in the list.
 * 
 * @author alan
 *
 */
public class FastTupleSpace implements ITupleSpace{

	public enum Mode
	{READ, WRITE, REMOVE};

	private static final int PERMIT_NUMBER = 10; //allows 10 concurrent reads, no concurrent write
	private SynchronizedLinkedList tuples;
	
	//private long lastTupleInsertion;
	private String name;
	
	public static Logger LOG;
	public static final String LOGGER = "up2p.tuplespace.fastTS";
	
	/** default constructor
	 * 
	 */
	public FastTupleSpace() {	
		this("AnonymousFastTupleSpace");
	}

	/**
	 * constructor with a name for the tuple space
	 * @param name
	 */
	public FastTupleSpace(String name) {
		LOG= Logger.getLogger(LOGGER);
		this.name=name;
		tuples = new SynchronizedLinkedList();
		//lastTupleInsertion=0;
	}
	
	
	/** 
	 * warning: not thread safe! only present for test purposes
	 */
	public String toString(){
		String toreturn = "";
		
		TupleNode node= tuples.getRoot();
		
		 while (node!= null){
			 toreturn += node.tuple.toString() +"\n";
			// toreturn += "Sem permits:"+node.semaphore.availablePermits() +"\n";
			 node = node.next;
		}
		
		return toreturn;
	}
	
	
	/**
	 *  a thread-safe linked list to optimize concurrent access for read, write, insert.
	 *  Read / remove should be done with the iterator. The write mode is not used at the moment for a tuple-space.
	 * @author Alan
	 *
	 */
	private class SynchronizedLinkedList {
		
		private TupleNode root;
		private Semaphore listSemaphore;
		
		public SynchronizedLinkedList(){
			listSemaphore = new Semaphore(PERMIT_NUMBER, true);
		}
		
		/**
		 * acquire permits on the semaphore controlling the head of this list
		 * @param permits number of permits
		 * @throws InterruptedException
		 */
		public void lockHead(int permits) throws InterruptedException{	
				listSemaphore.acquire(permits);
		}
		/**
		 *  release the number of permits of the semaphore controlling the head of this list
		 * @param permits
		 */
		public void releaseHead(int permits){
			listSemaphore.release(permits);
		}
		
		/**
		 * check if the list isn't empty.
		 * Warning: this is only valid at a snapshot in time when the query is made, 
		 * and operations may happen before the caller can execute his next line
		 * @return true is the list is empty, otherwise false
		 */
		public boolean isEmpty(){
			
			boolean ans = (root==null);
			
			return ans;
		}
		
		/*public Semaphore getRootSemaphore(){
			return listSemaphore;
		}*/
		
		/**
		 *  remove and return the first tuple in this list.
		 *  @return the tuple in the first node of the linked list
		 */
		public ITuple removeHeadTuple(){
			if (root==null)
				return null;
			ITuple toReturn=null;
			try {
				lockHead(PERMIT_NUMBER);
				root.semaphore.acquire(); //get a read right on the root node
				toReturn = root.tuple; //get the tuple in the first node
				root.semaphore.release(); //release just in case it has an effect on memory (?)
				root = root.next; // define the new root as the first node inserted
				releaseHead(PERMIT_NUMBER);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} // lock the list for writing
			return toReturn;
		}
		
		/**
		 *  read the first tuple in the List
		 * @return the tuple in the head node of the linked list
		 */
		public ITuple readHeadTuple(){
			if (root==null)
				return null;
			ITuple toReturn=null;
			try {
				lockHead(1); //get reading rights on the list
				root.semaphore.acquire(); //get a read right on the root node
				toReturn = root.tuple; //get the tuple in the first node
				releaseHead(1);
				root.semaphore.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} // lock the list for writing
			return toReturn;
		}
		
		/** 
		 * get the root node of the linked list
		 * @return the root node
		 */
		public TupleNode getRoot(){
			return root;
		}
		
		/**
		 *  creates an iterator for this linked list, capable of the *mode* operations
		 * @param mode READ, WRITE, or REMOVE : rights that the iterator has on the list [determines how the list is locked for concurrent access]
		 * @return
		 */
		public SyncIterator iterator(Mode mode){
			
			return new SyncIterator(this, mode);
		}

		/*
		 *  insert tuples into the list. The method is thread safe (I hope!!).
		 *  remove this for now: we want to use the iterator
		 * @param tuplelist
		 * /
		public void insertTuples(ITuple[] tuplelist){
			if (tuplelist.length ==0)
				return;
			TupleNode templast =null, builder=null;
			for (ITuple tup:tuplelist){
				builder = new TupleNode(tup, builder); // build a new node prepended to the previous one built
				if (builder.next==null) //for the first one built
					templast = builder;
			}
			//now templast and builder are respectively the first and last of our new list
			try {
				lockHead(PERMIT_NUMBER);
				templast.next = root; //plug the last node inserted into the existing list
				root = builder; // define the new root as the first node inserted
				releaseHead(PERMIT_NUMBER);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			} // lock the list for writing
			
		}*/
		
		public void setRoot(TupleNode node) {
			root = node;
		}
		
	}
	
	//an iterator for the synchronized list
	private class SyncIterator {
		
		private SynchronizedLinkedList thelist;
		private TupleNode current;
		private TupleNode previous; //keep track of previous in case removal is necessary
		private Mode currentMode; //the iterator can be used in any of 3 modes : write, read, remove which have different locking constraints.
		private boolean removalFlag; // to indicate if we've just removed a tuple from the list
		
		/** create an iterator based on the root node
		 *  wait to
		 * @param tup
		 * @param mode
		 */
		public SyncIterator(SynchronizedLinkedList list, Mode mode){
			thelist = list;
			removalFlag=false;
			currentMode = mode;
		}
		
		/**
		 * determine whether the iterator is at the end of the list or not
		 * @return
		 */
		public boolean hasNext(){
			if (current ==null){ //case where we haven't started iterating
				if (thelist.isEmpty() || removalFlag) //list is empty OR we've reached the end by removing the last element of the list
					return false;
				return true;}
			if (current.next ==null)
				return false;
			return true;
		}
		
		
		/** Initialize the iterator by locking the head of the list*/
		public void start(){
			int permits= PERMIT_NUMBER;
			if(currentMode==Mode.READ)
				permits =1;	
			
			try{ //lock the head of the list [i.e. acquire the right number of permits from it]
				thelist.lockHead(permits);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * standard iterator next() function, except that depending on the current mode it will lock the 
		 * nodes with different constraints
		 * @return
		 */
		public ITuple next(){
			switch(currentMode){
			case READ: return getNext(1);
			case WRITE: return getNext(PERMIT_NUMBER);
			case REMOVE: return getNextWithRemoveLock();
			default: return null; //not really necessary, there are only 3 modes.
			}
		}
		
		/**
		 * move to the next node in the linked list by acquiring the specified number 
		 * of permits from the tuples's semaphore. This is used in READ and WRITE mode.
		 * @param permits
		 * @return
		 */
		private ITuple getNext(int permits){
			try {	
				if (thelist.getRoot()==null) //case where the list is empty
					return null;
				if (current==null){ //case:we're about to get the first node in the list
					//[have already acquired the root using "start()"]  
					//acquire rights to the root node 
					//System.out.println("root node: num of permits"+thelist.getRoot().semaphore.availablePermits());
					thelist.getRoot().semaphore.acquire(permits); 
					current = thelist.getRoot();
					thelist.releaseHead(permits); //release root semaphore
					return current.tuple;
				}
				else { 
					//move to next node...	
					TupleNode temp = current; //current isn't null
					current = current.next;
					if (current != null){ //if this is the last node (in case the user didn't check with "hasnext()")
						current.semaphore.acquire(permits);
						temp.semaphore.release(permits); //release current node
					} else{
						temp.semaphore.release(permits); //release current node
						return null;
					}
					
					//System.out.println("next node: num of permits"+temp.semaphore.availablePermits());
					
					return current.tuple;
				}
				
			} catch (InterruptedException e) {
				
				e.printStackTrace();
				return null; // interruption not likely to occur... but just in case
			}
		}
		
		/**
		 *  insert tuples into the list after the current node. The method is thread safe (I hope!!).
		 * @param tuplelist
		 * @return success
		 */
		public boolean insertTuplesHere(ITuple[] tuplelist){
			if (tuplelist.length ==0)
				return true;
			if (currentMode != Mode.WRITE) //can't do it: don;t have the right permissions
				return false;
			TupleNode templast =null, builder=null;
			for (ITuple tup:tuplelist){
				builder = new TupleNode(tup, builder); // build a new node prepended to the previous one built
				if (builder.next==null) //for the first one built
					templast = builder;
			}
			//now templast and builder are respectively the last and first of our new list
			if (current== null){
				//inserting at the head (root)
				templast.next = thelist.getRoot(); //plug the last node inserted into the existing list
				thelist.setRoot(builder); // define the new root as the first node inserted	
			} else{
				templast.next = current.next; //plug the last node inserted into the existing list tail
				current.next = builder; // plus the existing list head into the first inserted node
			}
			return true;
			
		}
		
		/**
		 * move one node forward in the iterator, locking the current and previous for possible removal of the 
		 * current node.
		 * States are :
		 * at initialization, before reading the first node: previous == null, current == null
		 * when reading the first node : previous == null, list is locked, current = root node of list, current locked
		 * when reading any other node : previous and current both locked.
		 * After reading the last node, all locks are released. 
		 * @return the tuple associated with the current node
		 */
		private ITuple getNextWithRemoveLock(){
			//note: PERMIT_NUMBER is the *total* number of permits for the node
			if(removalFlag){
				removalFlag = false;
				return current.tuple; // by removing one we've actually made the future "next" our current node
			}
			try {	
				if (thelist.getRoot()==null) //case where the list is empty
					return null;
				if (current==null){ //case:we're about to get the first node in the list
					//acquire rights to the root node 
					thelist.getRoot().semaphore.acquire(PERMIT_NUMBER); 
					current = thelist.getRoot();
					return current.tuple;
				}
				else if(current==thelist.getRoot()){ //case where we were reading the first node and now moving to number 2
					//release rights to the list 
					thelist.releaseHead(PERMIT_NUMBER);			
				}
				else //release "previous" node  
					previous.semaphore.release(PERMIT_NUMBER);
				
				//move to next node.. standard processing
				// move to next node if it exists
				previous = current; 
				TupleNode temp = current.next; //current isn't null
				
				if (temp == null){ //if this is the last node (in case the user didn't check with "hasnext()")
					return null;
				}
				temp.semaphore.acquire(PERMIT_NUMBER); 
				current = temp;
				return current.tuple;
				
				
			} catch (InterruptedException e) {
				
				e.printStackTrace();
				return null; // interruption not likely to occur... but just in case
			}
		}
		
		/**
		 * remove the current tuple / node from the list 
		 * @return
		 */
		public boolean removeCurrent(){
			if (current==null || currentMode!=Mode.REMOVE) 
				return false;
			try {
				if (previous == null){ //case where we're reading the first node in the list
					thelist.setRoot(current.next);
				}
				else{ //case where it's a node in the middle
					previous.next = current.next;
				}///////////////////////////////////////////
				if (current.next != null) //acquire rights on the next if it exists
				{
					current.next.semaphore.acquire(PERMIT_NUMBER);
				}
				TupleNode temp = current;
				current = current.next; //if we've just removed the last element of the list we fall off the end!
				temp.semaphore.release(PERMIT_NUMBER); //release the node being deleted (just in case ?)
				removalFlag=true;
				return true;

				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return false;
			
		}
		/**Stop iterating and release all locks
		 * 
		 */
		public void quit(){ 
			//release all locks including list, previous, current
			int permits = PERMIT_NUMBER; // for write lock the permits involved in a lock are the max number
			switch(currentMode){
			case READ:
				permits =1; // for a read, only one lock
			case WRITE: //the processing for read and write is the same except for the number of permits 
				if (current != null){ //we're looking at a node: must be released
					current.semaphore.release(permits); //release 1 or [permit_number] permits to the semaphore depending on write or read mode
					
				}	else
					//we haven't started iterating [then we need to release the list]
					thelist.releaseHead(permits);
				break;
			case REMOVE:
				if (current != null) //we're looking at a node: must be released
					current.semaphore.release(PERMIT_NUMBER);

				if (previous != null){ //there is a previous : release it
					previous.semaphore.release(PERMIT_NUMBER);
				} else //there's no previous, so we're looking at root node: list needs to be released
					thelist.releaseHead(PERMIT_NUMBER);
				
			}//end switch
		}

	}
	
	/**
	 *  A tuple node is a node in a linked list, with an ITuple for value, 
	 *  and it comes with a pointer to the next node of the list plus a semaphore for concurrent access control. 
	 * @author alan
	 *
	 */
	private class TupleNode{
		
		public ITuple tuple; //the tuple
		public TupleNode next; // the next
		public Semaphore semaphore; // an flag to prevent concurrent modification
		
		
		public TupleNode(ITuple tup, TupleNode nex){
			tuple = tup;
			next= nex;
			semaphore = new Semaphore(10, true);//10 permits, assigned first come, first serve
		}
		
	}
////////////////////////////////////////////////////////////////////////////////////
	/////////////// Tuple space methods //////////////////////////
	//////////////////////////////////////////////////////////////
	
	protected void insertTuple(ITuple[] tuplist) {
		ITuple[] clones = new ITuple[tuplist.length];
		
		for (int i =0; i<tuplist.length; i++){
			if (tuplist[i].length() == 0)
				throw new IllegalArgumentException(
						"Tuples with no fields can be used only as templates.");
			clones[i] = (ITuple)tuplist[i].clone();
		}
		
	//insert them to the linked list
		SyncIterator iter = tuples.iterator(Mode.WRITE);
		
		iter.start();
		
		for (; iter.hasNext(); ){
			iter.next();
		} // we reach the last (hasNext returns false) 
		iter.insertTuplesHere(clones);
		iter.quit();
		
		//tuples.insertTuples(clones);
		
	}
	
	// insert one tuple : make a list of 1 element and call the list method
	protected void insertTuple(ITuple singletuple) {
		ITuple[] list = new ITuple[] {singletuple};
		insertTuple(list);
	}
	
	protected ITuple lookupTuple(ITuple template, boolean isRead) {
		//TODO: randomize which matching tuple is returned
		ITuple toReturn = null;
		
		// Handles matching of any tuple.
		if (template.length() == 0) {
			if (!isRead){
				toReturn = tuples.removeHeadTuple(); //select the first, remove it from the list
			}
			else {
				toReturn =  tuples.readHeadTuple();
				if (toReturn != null) //avoids error when the list is empty
					toReturn = (ITuple)toReturn.clone(); //select the first, clone it
			}

		} else { 
			ITuple tuple;
			Mode mymode = Mode.REMOVE;
			if(isRead){
				mymode= Mode.READ;
			}
			SyncIterator iter = tuples.iterator(mymode);
			for (iter.start(); iter.hasNext();){
				tuple = iter.next();
				if (template.matches(tuple)) {
					if (isRead) //need a clone of the matching tuple
						toReturn= (ITuple)tuple.clone();
					else { //extract the tuple (remove it from the TS)
						toReturn=tuple;
						iter.removeCurrent();
					}
					
				}
			}//end for
			iter.quit(); // release all locks, necessary with this particular implementation
		}
		return toReturn;
	}
	
	/**
	 *  lookup tuples matching the template
	 * @param template
	 * @param isRead
	 * @return
	 */
	protected ITuple[] lookupTuples(ITuple template, boolean isRead) {
		//ITuple result = null;
		
		List<ITuple> allResult = new LinkedList<ITuple>();
		ITuple[] toReturn = null;
		// Handles matching of any tuple.
		if (template.length() == 0) {
			if(!isRead){
				ITuple t;
				SyncIterator iter = tuples.iterator(Mode.REMOVE);
				for (iter.start(); iter.hasNext();){
					t = iter.next();
					allResult.add(t);
					iter.removeCurrent();
				}
				iter.quit(); //finish cleanly
			} else{
				ITuple t;
				SyncIterator iter = tuples.iterator(Mode.READ);
				for (iter.start(); iter.hasNext();){
					t = iter.next();
					allResult.add((ITuple)t.clone());
				}
				iter.quit(); //finish cleanly
			}
		} else {
			ITuple tuple;
			Mode mymode = Mode.REMOVE;
			if(isRead){
				mymode= Mode.READ;
			}
			SyncIterator iter = tuples.iterator(mymode);
			for (iter.start(); iter.hasNext();){
				
				tuple = iter.next();
				if (template.matches(tuple)) {
					if (isRead) //need a clone of the matching tuple
						allResult.add((ITuple)tuple.clone());
					else { //extract the tuple (remove it from the TS)
						allResult.add(tuple);
						iter.removeCurrent();
					}
				}
			}
			iter.quit();//finish cleanly
		}
		if (allResult.isEmpty())
			toReturn = null;
		else {
			toReturn = allResult.toArray(new ITuple[allResult.size()]);
		}
		
		return toReturn;
	}
	
	///////////// high level methods //////////////////////////////////////////

	public int count(ITuple template) throws TupleSpaceException {
		return lookupTuples(template, true).length;
	}


	public String getName() {	
		return name;
	}

	
	public ITuple in(ITuple template) throws TupleSpaceException {
		return blockingInput(template, false);
	}

	
	public ITuple[] ing(ITuple template) throws TupleSpaceException {
		ITuple[] result = null;
		result = lookupTuples(template, false);	
		return result;
	}

	
	/**
	 * in this implementation the returned tuple is the first one. In future improvements this may be randomized
	 */
	public ITuple inp(ITuple template) throws TupleSpaceException {
		return lookupTuple(template, false);
	}

	
	public void out(ITuple tuple) throws TupleSpaceException {	
		insertTuple(tuple);
		
		synchronized(this) {
			//lastTupleInsertion++;
			notifyAll();
		}
		
	}

	
	public void outg(ITuple[] tuples) throws TupleSpaceException {
		insertTuple(tuples);
		synchronized(this) {
			//lastTupleInsertion++; //increment insertion count for the benefit of threads that started reading before the insertion and are not yet waiting when this notify occurs. 
			notifyAll(); // notify waiting threads
		}
	}


	protected ITuple blockingInput(ITuple template, boolean isRead){
		
		
		ITuple result =null;
		do{
			//look for tuples, read or remove according to isRead variable
			result = lookupTuple(template, isRead); 
			//now synchronize to check the result and insertion count 
		//////////////////////////////////////////////////////////////
			synchronized (this) {
				//we wait if both conditions are true: we have no matching tuples and 
				// there has been no concurrent write while we were doing the lookup
				if (result == null ) { 
					try {
						wait(); // release TS and wait for new writes to occur
						//we wake up because one or several inserts have occured: we go and read them
					} catch (InterruptedException e) {
						throw new TupleSpaceError("Internal Error. Halting...");
					}
				}
			}
		} while (result == null);
		
		return result;
		
	}
	
	public ITuple rd(ITuple template) throws TupleSpaceException {
		return blockingInput(template, true);
	}

	
	public ITuple[] rdg(ITuple template) throws TupleSpaceException {
		return lookupTuples(template, true);
	}


	public ITuple rdp(ITuple template) throws TupleSpaceException {
		return lookupTuple(template, true);
	}
}
