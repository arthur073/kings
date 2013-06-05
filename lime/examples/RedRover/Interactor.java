import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.event.*;
import lime.*;
import lime.util.*;
import gp.util.*;
import lights.adapters.*;
import lights.interfaces.*;
import java.awt.Image;

/**
 * The Interactor component handles all direct interaction with LIME.
 * For this reason, the Interactor extends the lime.Agent class. The
 * Interactor contains a handle to two LimeTupleSpaces: the publicTS
 * and the teamTS.
 * <p>
 * The configuration file should be called "config.txt" and should
 * have three lines of text.  The first line should be either "true"
 * or "false" indicating whether or not that Agent is the leader.
 * The second line is the name of the public tuple space and the
 * third line is the name of the team tuple space.
 * 
 *
 * @author Bryan D. Payne
 * @version 2/24/2000
 */

public class Interactor extends StationaryAgent {
    /*
     * Constants
     */
    protected final String PUBLIC = "public";
    protected final String TEAM = "team";

    /*
     * Lime specific information
     */
    protected LimeTupleSpace publicTS = null;
    protected LimeTupleSpace teamTS = null;
    protected LimeSystemTupleSpace lsts = null;
    protected Reaction[] publicReactions;
    protected Reaction[] teamReactions;
    protected Reaction[] objectSearchReactions;
    protected LimeSystemReaction[] LSTSReactions;
    protected RegisteredReaction[] publicReceipts;
    protected RegisteredReaction[] teamReceipts;
    protected RegisteredReaction[] LSTSReceipts;
    
    protected boolean leader;
    protected String publicTSName = null;
    protected String teamTSName = null;

    /*
     * Queue information
     */
    protected Queue queue;

    /*
     * Listener information
     */
    protected EventListenerList listenerList;

    /**
     * Creates a new Interactor
     */
    public Interactor (){
	// First get the config from the Game
	if (Game.getProperty("leader").compareTo("true") == 0){
	    leader = true;
	}
	else{
	    leader = false;
	}
	publicTSName = Game.getProperty("publicTSName");
	if (publicTSName == null){
	    publicTSName = "public";
	}
	teamTSName = Game.getProperty("team");
	if (teamTSName == null){
	    teamTSName = "team";
	    //System.err.println("RED_ROVER: Using default name for team tuple space");
	}
    
        queue = new Queue();
        listenerList = new EventListenerList();
    }

    /**
     * Writes Feature information into the public tuple space.
     *
     * @param str The info string for f
     * @param f A feature to be placed in the public tuple space
     */
    public void updatePublicFeatureInfo (String str, Feature f){
	TuplePackage temp = new TuplePackage(PUBLIC, f.toTuple(str));
	queue.add(temp);
    }
    
    /**
     * Writes Feature information into the team tuple space.
     *
     * @param str The info string for f
     * @param f A feature to be places in the team tuple space
     */
    public void updateTeamFeatureInfo (String str, Feature f){
	TuplePackage temp = new TuplePackage(TEAM, f.toTuple(str));
	queue.add(temp);
    }

    /**
     * Writes the object found to the team tuple space
     *
     * @param obj The object represented as a string
     */
    public void notifyFoundObject (String obj){
	ITuple objTuple = new Tuple().addActual("object").addActual(obj);
	TuplePackage temp = new TuplePackage(TEAM, objTuple);
	queue.add(temp);
    }

    /**
     * Registers a reaction to find the object desired
     *
     * @param objName The name of the object that you are looking for
     * @param type Indicates how many objects you are looking for
     * @return true if the reaction was installed properly, else false
     */
    public boolean findObject (String objName, int type){

	// create the template for the reaction
	ITuple template = new Tuple();
	template.addActual("object").addActual(objName);

	// set up the reactions
	objectSearchReactions = new Reaction[1];
	if (type == MapObjectEvent.OBJECT_LOOKFORALL){
	    objectSearchReactions[0] = new UbiquitousReaction(template,
							      new ObjectListener(this),
							      Reaction.ONCEPERTUPLE);
	}
	else if (type == MapObjectEvent.OBJECT_LOOKFORONE){
	    objectSearchReactions[0] = new UbiquitousReaction(template,
							      new ObjectListener(this),
							      Reaction.ONCE);
	}
	else{
	    return false;
	}

	// register the reactions
	queue.add(new TuplePackage("reactions", null));
	return true;
    }

    /**
     * Loads a picture into the team tuple space
     *
     * @param pic The picture to load
     * @param playerID The player's UID
     */
    public void loadPlayerInfo (PlayerInfo pinfo, UID playerID){
	ITuple infoTuple = new Tuple().addActual(playerID).addActual(pinfo);
	TuplePackage temp = new TuplePackage(TEAM, infoTuple);
	queue.add(temp);
    }

    /**
     * Get another players info out of the team tuple space
     *
     * @param playerID The UID of the player to get info on
     */
    public void getPlayerInfo (UID playerID){
      //System.err.println("Interactor:  Getting player info!");
	ITuple infoTuple = new Tuple().addActual(playerID).addFormal(PlayerInfo.class);
	TuplePackage temp = new TuplePackage("infoProbe", infoTuple);
	queue.add(temp);
    }

    /**
     * Starts the protocol to make this host part of the
     * community of mobile hosts through LimeServer.engage
     *
     * @return The result of LimeServer.engage
     */
    public boolean engage (){
	return LimeServer.getServer().engage();
    }

    /**
     * Starts the protocol to remove this host from the
     * community of mobile hosts through LimeServer.disengage
     *
     * @return The result of LimeServer.disengage
     */
    public boolean disengage (){
      //System.err.println("Interactor: disengaging");
      return LimeServer.getServer().disengage();
    }

    /**
     * Registers the LSTS Reaction Handlers
     */
    public void registerLSTSReactions (){
	try{
	    LSTSReceipts = lsts.addReaction(LSTSReactions);
	}
	catch (TupleSpaceEngineException tsee){ tsee.printStackTrace(); }
    }

    /**
     * Processes the queue, and updates the tuple spaces
     * accordingly.
     */
  public void run (){
    // Setting up the lime tuple spaces
    try{
      publicTS = new LimeTupleSpace(publicTSName);
      teamTS = new LimeTupleSpace(teamTSName);
      lsts = new LimeSystemTupleSpace();
    }
    catch (LimeException le){ 
      le.printStackTrace(); 
    }
    
    publicReactions = initPublicReactions();
    teamReactions = initTeamReactions();
    LSTSReactions = initLSTSReactions();

    try{
      publicTS.setShared(true);
      teamTS.setShared(true);
      publicReceipts = publicTS.addWeakReaction(publicReactions);
      teamReceipts = teamTS.addWeakReaction(teamReactions);
    }
    catch (LimeException le){ le.printStackTrace(); }

    // The leader should automatically engage, but the
    // follower should wait to be engaged by the user
    if (leader){
      LimeServer.getServer().declareLeader();
      leader = false;
      //System.err.println("I'm the leader...");
    }

    TuplePackage tp = null;
    String dest = "";
    ITuple template = null;

    while (true){
      //System.err.println("INTERACTOR:  waiting on queue");
      queue.waitOnEmpty();
      tp = (TuplePackage) queue.remove();
      //System.err.println("INTERACTOR:  no longer waiting:  tp.destination:" +tp.destination);


      if (tp.destination.equals(PUBLIC) || tp.destination.equals(TEAM)){
        IField[] fields = tp.t.getFields();
        template = new Tuple();
        template.add(fields[0]).addFormal(String.class).addFormal(Feature.class);
        if (tp.destination.equals(PUBLIC)){
          try{
            // remove previous public info (if any)
            publicTS.inp(new HostLocation(new LimeServerID(InetAddress.getLocalHost())),
                         new AgentLocation(this.getMgr().getID()),
                         template);
            // write the new public info
            publicTS.out(tp.t);
          }
          catch (LimeException le) { le.printStackTrace(); }
          catch (UnknownHostException uhe){ uhe.printStackTrace(); }
        }
        else if (tp.destination.equals(TEAM)){
          // write team information (note: team tuple space is never
          // implicitly cleaned out like the public tuple space is)
          try{ teamTS.out(tp.t); }
          catch (LimeException le) { le.printStackTrace(); }
        }
      }
      else if (tp.destination.equals("reactions")){
        try{
          teamTS.addWeakReaction(objectSearchReactions);
        }
        catch (TupleSpaceEngineException tsee){ tsee.printStackTrace(); }
      }
      else if (tp.destination.equals("infoProbe")){
        //System.err.println("INTERACTOR:  reading player info");
        IField[] fields = tp.t.getFields();
        UID playerID = (UID) fields[0].getValue();
        AgentID aid = new AgentID(playerID.getAddress(), 
                                  playerID.getN());
        ITuple response = null;
        try{
          //System.err.println("INTERACTOR:  going to the TS");
          response = teamTS.rdp(new AgentLocation(aid),
                                AgentLocation.UNSPECIFIED,
                                tp.t);
          //System.err.println("INTERACTOR:  back from the TS");
        }
        catch (LimeException le){ le.printStackTrace(); }
        if (response == null){ // did not find the info tuple
          response = new Tuple().add(fields[0]);
          //System.err.println("INTERACTOR:  no info tuple found, response:"+response);
        }
        else{
          //System.err.println("INTERACTOR:   info tuple found, response:"+response);
        }
        firePlayerInfoFound(response);
      }
    }
  }

    /**
     * Creates all of the upons for the public tuple space
     *
     * @return Reactions for the public tuple space
     */
  protected Reaction[] initPublicReactions (){
    Reaction[] results = new Reaction[1];
    
    // Prepare the values for the reaction
    ITuple template = new Tuple();
    template.add(new Field().setToFormal(UID.class))
      .add(new Field().setToFormal(String.class))
      .add(new Field().setToFormal(Feature.class));

    results[0] = new UbiquitousReaction(template, new PublicListener(this),
                                        Reaction.ONCEPERTUPLE);
    return results;
  }

  /**
   * Creates all of the upons for the team tuple space
   *
   * @return Reactions for the team tuple space
   */
  protected Reaction[] initTeamReactions (){
    Reaction[] results = new Reaction[1];

    //Prepare the values for the reaction
    ITuple template = new Tuple();
    template.add(new Field().setToFormal(UID.class)).
      add(new Field().setToFormal(String.class)).
      add(new Field().setToFormal(Feature.class));

    results[0] = new UbiquitousReaction(template, new TeamListener(this),
                                        Reaction.ONCEPERTUPLE);

    return results;
  }

  /**
   * Creates all of the upons for the LSTS
   *
   * @return Reactions for the LSTS
   */
  protected LimeSystemReaction[] initLSTSReactions (){
    LimeSystemReaction[] results = new LimeSystemReaction[2];
    
    //Prepare the values for the reaction
    ITuple template1 = new Tuple();
    template1.addActual("_agent").addFormal(AgentID.class).addFormal(LimeServerID.class);
    results[0] = new LimeSystemReaction(template1,
                                       new LSTSListener(this),
                                       Reaction.ONCEPERTUPLE);
    ITuple template2 = new Tuple();
    template2.addActual("_agent_gone").addFormal(AgentID.class).addFormal(LimeServerID.class);
    results[1] = new LimeSystemReaction(template2,
                                       new LSTSListener(this),
                                       Reaction.ONCEPERTUPLE);
    
    return results;
  }

  /*******************************************************************
   *        EVERYTHING BELOW HERE IS ONLY EVENT HANDLING CODE        *
   *******************************************************************/

  /**
   * Adds a listener for InteractorEvents.
   *
   * @param il The listener to add
   */
  public void addInteractorListener (InteractorListener il){
    listenerList.add(InteractorListener.class, il);
  }

  /**
   * Removes a listener for InteractorEvents.
   *
   * @param il The listener to add
   */
  public void removeInteractorListener (InteractorListener il){
    listenerList.remove(InteractorListener.class, il);
  }

  /**
   * Notify all listeners that have registered interest for
   * notification on this event type.  The event instance 
   * is lazily created using the parameters passed into 
   * the fire method.<br>
   * This code based on examples from the
   * javax.swing.event.EventListenerList in the Java API.
   *
   * @param t The tuple that we reacted to
   */
  public void fireFeatureUpdated (ITuple t){
    InteractorEvent interactorEvent = null;
    
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    
    IField[] ifields = t.getFields();
    String featureStr = (String) ifields[1].getValue();
    Feature f = (Feature) ifields[2].getValue();
    
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == InteractorListener.class) {
        // Lazily create the event
        if (interactorEvent == null){
          interactorEvent = new InteractorEvent(this, featureStr, f);
        }
                        
        //System.err.println("Interactor:  calling a listener");
        //System.err.println("Interactor:  calling a listener festureStr: "+featureStr);
        //System.err.println("Interactor:  calling a listener f: "+f);
        ((InteractorListener) listeners[i + 1]).featureUpdated(interactorEvent);
      }              
    }
  }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.<br>
     * This code based on examples from the
     * javax.swing.event.EventListenerList in the Java API.
     *
     * @param t The tuple that we reacted to
     */
  public void fireLSTSUpdated (ITuple t){
    InteractorEvent interactorEvent = null;

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    IField[] ifields = t.getFields();
    String featureStr = (String) ifields[0].getValue();
    HashMap temp = new HashMap();
    temp.put("agentid", new Attribute ("agentid",
                                       (AgentID) ifields[1].getValue(),
                                       Attribute.PRIVATE));
    Feature f = new Feature(null, temp);

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == InteractorListener.class) {
        // Lazily create the event
        if (interactorEvent == null){
          interactorEvent = new InteractorEvent(this, featureStr, f);
        }
        //System.err.println("Interactor:  calling a LSTS listener");
        //System.err.println("Interactor:  calling a LSTSlistener festureStr: "+featureStr);
        //System.err.println("Interactor:  calling a LSTSlistener f: "+f);
        ((InteractorListener) listeners[i + 1]).featureUpdated(interactorEvent);
      }              
    }
  }
  
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.<br>
     * This code based on examples from the
     * javax.swing.event.EventListenerList in the Java API.
     *
     * @param t The tuple that we reacted to
     */
    public void fireObjectFound (ITuple t){
	InteractorEvent interactorEvent = null;

	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();

	IField[] ifields = t.getFields();
        String featureStr = "object";
	HashMap temp = new HashMap();
	temp.put("type", new Attribute ("type",
					(String) ifields[1].getValue(),
					Attribute.PRIVATE));
	temp.put("agentID", new Attribute ("agentID",
					   (AgentID) ifields[2].getValue(),
					   Attribute.PRIVATE));
        Feature f = new Feature(null, temp);

	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == InteractorListener.class) {
		// Lazily create the event
		if (interactorEvent == null){
		    interactorEvent = new InteractorEvent(this, featureStr, f);
		}
		((InteractorListener) listeners[i + 1]).featureUpdated(interactorEvent);
	    }              
	}
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.<br>
     * This code based on examples from the
     * javax.swing.event.EventListenerList in the Java API.
     *
     * @param t The tuple that we reacted to
     */
    public void firePlayerInfoFound (ITuple t){
	InteractorEvent interactorEvent = null;

	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();

	IField[] ifields = t.getFields();
        String featureStr = "playerInfo";
	HashMap temp = new HashMap();
	temp.put("playerID", new Attribute ("playerID",
					    (UID) ifields[0].getValue(),
					    Attribute.PRIVATE));
	//System.err.println("Interactor:  length of ifields: "+ifields.length);
	//System.err.println("Interactor:  Tuple is:"+t);
	if (ifields.length > 1){
	    temp.put("pinfo", new Attribute ("pinfo",
					     (PlayerInfo) ifields[1].getValue(),
					     Attribute.PRIVATE));
	}
        Feature f = new Feature(null, temp);

	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == InteractorListener.class) {
		// Lazily create the event
		if (interactorEvent == null){
		    interactorEvent = new InteractorEvent(this, featureStr, f);
		}
		((InteractorListener) listeners[i + 1]).featureUpdated(interactorEvent);
	    }              
	}
    }
}

/*************************************************************/
/*************************************************************/

/**
 * Listener for the public tuple space.  This will start the
 * reaction when a new tuple is found that matches our pattern
 */
class PublicListener implements ReactionListener{
    /**
     * A reference to the calling class
     */
    private Interactor i = null;

    /**
     * Basic constructor
     *
     * @param Reference to the calling class
     */
    public PublicListener (Interactor i){
	this.i = i;
    }

    /**
     * Specifices the how to handle the presence of
     * this new tuple that we are interested in
     *
     * @param e The event that started this reaction     
     */
    public void reactsTo (ReactionEvent e){
      //System.err.println("INTERACTOR: Reacting to a new Tuple");
	i.fireFeatureUpdated( (ITuple) e.getEventTuple());
    }
}

/**
 * Listener for the team tuple space.  This will start the
 * reaction when a new tuple is found that matches our pattern
 */
class TeamListener implements ReactionListener{
    /**
     * A reference to the calling class
     */
    private Interactor i = null;

    /**
     * Basic constructor
     *
     * @param Reference to the calling class
     */
    public TeamListener (Interactor i){
	this.i = i;
    }

    /**
     * Specifices the how to handle the presence of
     * this new tuple that we are interested in
     *
     * @param e The event that started this reaction     
     */
    public void reactsTo (ReactionEvent e){
	i.fireFeatureUpdated( (ITuple) e.getEventTuple());
    }
}

/**
 * Listener for events in the LSTS.  This will start the
 * reaction when a new tuple is found that matches our pattern
 */
class LSTSListener implements ReactionListener{
    /**
     * A reference to the calling class
     */
    private Interactor i = null;

    /**
     * Basic constructor
     *
     * @param Reference to the calling class
     */
    public LSTSListener (Interactor i){
	this.i = i;
    }

    /**
     * Specifices the how to handle the presence of
     * this new tuple that we are interested in
     *
     * @param e The event that started this reaction     
     */
    public void reactsTo (ReactionEvent e){
      System.err.println("LSTSListener:  LSTS listener firing");
      i.fireLSTSUpdated( (ITuple) e.getEventTuple());
    }
}

/**
 * Listener for new objects to support object searches.
 */
class ObjectListener implements ReactionListener{
    /**
     * A reference to the calling class
     */
    private Interactor i = null;

    /**
     * Basic constructor
     *
     * @param Reference to the calling class
     */
    public ObjectListener (Interactor i){
	this.i = i;
    }

    /**
     * Specifices the how to handle the presence of
     * this new tuple that we are interested in
     *
     * @param e The event that started this reaction     
     */
    public void reactsTo (ReactionEvent e){
	// Find the AgentID
	AgentID aid = e.getSourceAgent();
	IField[] ifields = e.getEventTuple().getFields();
	ITuple newTuple = new Tuple().add(ifields[0]).add(ifields[1]).addActual(aid);
	i.fireObjectFound(newTuple);
    }
}

/**
 * A simple class to package the information on the queue
 */
class TuplePackage{
    /**
     * Where this tuple is headed
     */
    public String destination;

    /**
     * The tuple to be written
     */
    public ITuple t;

    /**
     * Basic constructor to package the data
     *
     * @param destination Where this tuple is headed
     * @param t The tuple to be written
     */
    public TuplePackage (String destination, ITuple t){
	this.destination = destination;
	this.t = t;
    }
}
