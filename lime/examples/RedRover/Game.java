
/**
 * The Game contains a MapViewer, a Map, and an Interactor. 
 * 
 * The Game class contains a main() method that starts the game.
 * At startup, it creates instances of the above three classes, and
 * registers itself as event-listener for those components. 
 *
 * @author Jason Ginchereau
 */

import java.util.Properties;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import lime.*;
import lights.adapters.*;
import lights.interfaces.*;
import java.net.*;
import java.util.HashMap;
import lime.util.*;

public class Game implements LocationListener, OrientationListener,
                             GameStateListener, MapWindowListener,
                             InteractorListener, ActionListener,
                             MapObjectListener, InfoRequestListener{
  public static final String DEFAULT_PROPERTIES_FILE = "rr.props";

  public static final int DEFAULT_UPDATE_TIME = 1000;

  public static final double DEFAULT_TAG_DISTANCE = 10.0;

  public static void main(String[] args) {
    String propertiesFileName = DEFAULT_PROPERTIES_FILE;

    
    if(args.length < 1 ) {
      System.err.println("Usage: java Game [propertiesfile]");
      return;
    } else 
      propertiesFileName = args[0];

    props = new Properties();
    try {
      props.load(new java.io.FileInputStream(propertiesFileName));
    } catch(java.io.IOException ioex) {
      System.err.println("Warning: failed to load properties file \"" +
                         propertiesFileName + "\"");
    }
    new Game(args);
  }

  public Game(String[] args) {
    // Set up the Lime Server 
    new Launcher().launch(args,1);


    // set the tag distnace from the property file
    try {
      tagDistance = Double.parseDouble(getProperty("tagdistance", "" +
                                                   DEFAULT_TAG_DISTANCE));
    } catch(NumberFormatException ex) {
      tagDistance = DEFAULT_TAG_DISTANCE;
    }

    // set the timerdelay from the property file
    int timerDelay;
    try {
      timerDelay = Integer.parseInt(getProperty("updatetime", "" +
                                                DEFAULT_UPDATE_TIME));
    } catch(NumberFormatException nfex) {
      timerDelay = DEFAULT_UPDATE_TIME;
    }
    // create new timers for how often to send location/orientation updates to tuple space
    locationTimer = new Timer(timerDelay, this);
    orientationTimer = new Timer(timerDelay, this);
    locationTimer.setRepeats(false);
    orientationTimer.setRepeats(false);

    // create the new player feature (the thing on the map)
    playerID = new UID();
    player = new Feature(playerID);

    // create a new initial location for the player from the properties startX and startY
    String sX = getProperty("startX");
    double startx = Math.random() * 200 - 100; // init with a random value
    if (sX != null)  startx = (new Double(sX)).doubleValue(); // use the user property
    //System.err.println("startX:"+startx);

    String sY = getProperty("startY");
    double starty = Math.random() * 200 - 100;  // init with a random
    if (sY != null)  starty = (new Double(sY)).doubleValue(); // use the user property
    //System.err.println("startY:"+starty);


//      double startx = Math.random() * 200 - 100;
//      double starty = Math.random() * 200 - 100;


    Location    startLocation    = new Location(startx, starty);
    Orientation startOrientation = Orientation.NORTH;

    // get various properties from the poperty file
    Boolean leader = new Boolean(getProperty("leader","false"));
    Boolean iAmIT = new Boolean(getProperty("IT", "false"));
    String team = getProperty("team", "default");
    String name = getProperty("name", "anonymous");
    String pict = getProperty("picture", "slate2.gif");

    // set the attributes that you just got from the property file
    player.setAttribute(
        new Attribute(Feature.TEAM       , team            , Attribute.PUBLIC));
    player.setAttribute(
        new Attribute(Feature.LOCATION   , startLocation   , Attribute.PUBLIC));
    player.setAttribute(
        new Attribute(Feature.ORIENTATION, startOrientation, Attribute.TEAM));
    player.setAttribute(
        new Attribute(Feature.FROZEN     , Boolean.FALSE   , Attribute.PUBLIC));
    player.setAttribute(
        new Attribute(Feature.GHOST      , Boolean.FALSE   , Attribute.PUBLIC));
    player.setAttribute(
        new Attribute(Feature.IT         , iAmIT           , Attribute.PUBLIC));
    player.setAttribute(
        new Attribute(Feature.NAME       , name            , Attribute.PUBLIC));


    /////////////////////////////////////////////////////////

    // load the interactor as the agent
    try{
      interactor = (Interactor) LimeServer.getServer()
        .loadAgent("Interactor", null);
    }
    catch (LimeException le){ le.printStackTrace(); }
    interactor.addInteractorListener(this);
    /////////////////////////////////////////////////////////


    // create the local map and register the Game as the listener
    map = new SimpleMap();
    map.updateFeature(player);

    //System.err.println("MapViewer created with leader="+leader);
    mapViewer = new MapViewer(playerID, leader.booleanValue());
    mapViewer.addLocationListener(this);
    mapViewer.addOrientationListener(this);
    mapViewer.addGameStateListener(this);
    mapViewer.addMapWindowListener(this);
    mapViewer.addMapObjectListener(this);
    mapViewer.addInfoRequestListener(this);

    mapViewer.updateAll(new Feature[] { player });

    interactor.updatePublicFeatureInfo("all", player.copy(Attribute.PUBLIC));
    interactor.updateTeamFeatureInfo("all", player.copy(Attribute.TEAM));
    interactor.registerLSTSReactions();

    //    java.awt.Image myPic= Toolkit.getDefaultToolkit().createImage(pict);
    //new PlayerReporter (myPic);
    //PlayerInfo myInfo = new PlayerInfo(myPic);

    PlayerInfo myInfo = new PlayerInfo(pict);
    interactor.loadPlayerInfo(myInfo, playerID);
  }

  /**
   * The player has changed location. Update the player Feature
   * in the MapViewer, Map, and Interactor. 
   */
  public void playerMoved(LocationEvent evt) {
    if(Boolean.TRUE.equals(player.getAttributeValue(Feature.FROZEN))) {
      System.err.println("YOU CANNOT MOVE WHILE FROZEN!");
      return;
    }

    player.setAttributeValue(Feature.LOCATION, evt.getNewLocation());
    mapViewer.updateFeature(player);

    performTags();

    if(locationTimer.isRunning()) {
      locationChanged = true;
    }
    else {
      interactor.updatePublicFeatureInfo(Feature.LOCATION,
                                         player.copy(Attribute.PUBLIC));
      locationTimer.start();
      locationChanged = false;
    }
  }

  /**
   * The player has changed orientation. Update the player Feature
   * in the MapViewer, Map, and Interactor.
   */
  public void playerTurned(OrientationEvent evt) {
    // COMMENTED OUT BY ALM --- we don't see orientation right now, so don't
    // waste the computation
//      player.setAttributeValue(Feature.ORIENTATION, evt.getNewOrientation());
//      mapViewer.updateFeature(player);


//      if(orientationTimer.isRunning()) {
//        orientationChanged = true;
//      }
//      else {
//        interactor.updateTeamFeatureInfo(Feature.ORIENTATION,
//                                         player.copy(Attribute.TEAM));
//        orientationTimer.start();
//        orientationChanged = false;
//      }
  }

  /**
   * One of the timers has fired.
   */
  public void actionPerformed(ActionEvent evt) {
    if(evt.getSource() == locationTimer) {
      if(locationChanged) {
        interactor.updatePublicFeatureInfo(Feature.LOCATION,
                                           player.copy(Attribute.PUBLIC));
        locationTimer.restart();
        locationChanged = false;
      }
    }
    else if(evt.getSource() == orientationTimer) {
      if(orientationChanged) {
        // COMMENTED OUT BY ALM : we don't have orientations right now, so
        // don't waste the computation
        //interactor.updateTeamFeatureInfo(Feature.ORIENTATION,
        //                                           player.copy(Attribute.TEAM));
        orientationTimer.restart();
        orientationChanged = false;
      }
    }
  }

  /**
   * The user has chosen to either enter or leave the game.
   */
  public void gameStateChanged(GameStateEvent evt) {
    if(evt.getID() == GameStateEvent.PLAYER_LEAVING_GAME) {
      interactor.disengage();
    } else {
      if(evt.getID() == GameStateEvent.PLAYER_EXITING_GAME) {
        interactor.disengage();
        LimeServer.getServer().shutDown();
        System.exit(0);
      }
      else{  // Player must be trying to engage
        interactor.engage();
      }
    }
  }

  /**
   * The window of the MapViewer has changed. A new set of Features
   * will need to be passed to the MapViewer to be drawn.
   */
  public void mapWindowChanged(MapWindowEvent evt) {
    mapViewer.updateAll(map.getFeatures(evt.getWindow()));
  }

    /**
     * The Interactor has received updated Feature information. Update
     * this Feature in the Map and MapViewer.
     */
    public void featureUpdated(InteractorEvent evt) {
      //System.err.println("GAME:  featureUpdated: "+evt.getTag());

      // ** modified 2-26-00 by bdp to support LSTS ghost management **
      Feature newFeature = null;

      // Check to see if the tag came from LSTS
      if (evt.getTag().equals("_agent") || evt.getTag().equals("_agent_gone")){
        // First find feature (player) to update in the local list of features
        AgentID realID = (AgentID) evt.getFeature().getAttributeValue("agentid");
        Feature[] allFeatures = map.getFeatures();
        int i = 0;
        for (i = 0; i < allFeatures.length; ++i){
          AgentID tempID = new AgentID(allFeatures[i].getID().getAddress(),
                                       allFeatures[i].getID().getN());
          if (tempID.equals(realID)){ break; }
        }
          
        if (i == allFeatures.length){ 
          return; 
        }


        Boolean ghostValue = new Boolean(!evt.getTag().equals("_agent"));
        newFeature = new Feature(allFeatures[i].getID());
        Attribute a = new Attribute(Feature.GHOST,ghostValue,Attribute.PUBLIC);
        newFeature.setAttribute(a);
      }

      // Check to see if this is a found object
      else if (evt.getTag().equals("object")){
        // pass the information up to the GUI for display
        String objFound = (String) evt.getFeature().getAttributeValue("type");
        AgentID realID = (AgentID) evt.getFeature().getAttributeValue("agentID");
        Feature[] allFeatures = map.getFeatures();
        int i = 0;
        for (i = 0; i < allFeatures.length; ++i){
          AgentID tempID = new AgentID(allFeatures[i].getID().getAddress(), 
                                       allFeatures[i].getID().getN());
          if (tempID.equals(realID)){ break; }
        }
        
        String name = "";
        if (i != allFeatures.length){
          name = (String) allFeatures[i].getAttributeValue(Feature.NAME);
        }
        
        mapViewer.objectMessage("A "+objFound+" was found by "+name);
        return;
      }

      // Check for a player info request being returned
      else if (evt.getTag().equals("playerInfo")){
        UID playerID = (UID) evt.getFeature().getAttributeValue("playerID");
        
        java.awt.Image pic = null;
        try{
          PlayerInfo pinfo = (PlayerInfo) evt.getFeature().getAttributeValue("pinfo");
          if (pinfo!=null)
            pic = pinfo.getPicture();
        }
        catch (java.util.NoSuchElementException nsee){}

        mapViewer.playerPicture(map.getFeature(playerID), pic);
        return;
      }

      // If tag is not from LSTS then handle as usual
      else{
        newFeature = evt.getFeature();
      }

      // Now perform the normal updates
      if(map.updateFeature(newFeature)) {
        newFeature = map.getFeature(newFeature.getID());
        mapViewer.updateFeature(newFeature);
        performTags();
        
        // Ghosts are now handled by the LSTS above so
        // this is no longer needed
        //
        //if(evt.getTag().equals("goodbye")) {
        //    interactor.updateTeamFeatureInfo("ghost", f);
        //}
      }
    }

  /**
   * The MapViewer has received a map object notification or search request
   * Pass on the request to the Interactor to input or retrieve the object.
   */
  public void mapObjectNotification(MapObjectEvent evt){
      // Are we looking for an object
      if (evt.getActionType() != MapObjectEvent.OBJECT_FOUND){
        //System.err.println("Searching for object: "+evt.getMessage());
	  interactor.findObject(evt.getMessage(), evt.getActionType());
      }

      // Or did we find an object?
      else{
        //System.err.println("Inserting object: "+ evt.getMessage());
	  interactor.notifyFoundObject(evt.getMessage());
      }
  }

  /**
   * A request for player information is made.  Determine needed action.
   */
  public void infoRequested(InfoRequestEvent evt){
      interactor.getPlayerInfo (evt.getFeature().getID());
  }

  private void performTags() {
    // If I am IT, then I can't be tagged.
    if(Boolean.TRUE.equals(player.getAttributeValue(Feature.IT))) {
      return;
    }

    Location myLoc = (Location) player.getAttributeValue(Feature.LOCATION);
    String myTeam = (String) player.getAttributeValue(Feature.TEAM);

    // Get the set of nearby features.
    Feature[] f = map.getFeatures(new java.awt.geom.Ellipse2D.Double(
        myLoc.getX() - tagDistance, myLoc.getY() - tagDistance,
        tagDistance * 2, tagDistance * 2));

    // If I can be tagged by an IT player, then freeze me and return.
    for(int i = 0; i < f.length; i++) {
      // I can't tag myself.
      if(f[i].getID().equals(player.getID())) continue;

      Location fLocation = (Location) f[i].getAttributeValue(Feature.LOCATION);
      if (fLocation==null)
        continue; // this feature has no location


      if(fLocation.distance(myLoc) <= tagDistance) {
        if(Boolean.TRUE.equals(f[i].getAttributeValue(Feature.IT))) {
          if(myTeam.equals(f[i].getAttributeValue(Feature.TEAM))) {
            setIT(true);  // I should be IT because my teammate is!
          }
          else {
            setFrozen(true);
          }
          return;
        }
      }
    }

    // Otherwise if I can be tagged by a teammate the unfreeze and return.
    for(int i = 0; i < f.length; i++) {
      // I can't tag myself.
      if(f[i].getID().equals(player.getID())) continue;

      Location fLocation = (Location) f[i].getAttributeValue(Feature.LOCATION);
      if (fLocation==null)
        continue; // this feature has no location

      if(fLocation.distance(myLoc) <= tagDistance) {
        if(myTeam.equals(f[i].getAttributeValue(Feature.TEAM))) {
          setFrozen(false);
          return;
        }
      }
    }
  }

  private void setFrozen(boolean frozen) {
    Boolean fB = new Boolean(frozen);
    if(!player.getAttributeValue(Feature.FROZEN).equals(fB)) {
      player.setAttributeValue(Feature.FROZEN, fB);
      interactor.updatePublicFeatureInfo(Feature.FROZEN,
                                         player.copy(Attribute.PUBLIC));
    }
  }

  private void setIT(boolean it) {
    Boolean iB = new Boolean(it);
    if(!player.getAttributeValue(Feature.IT).equals(iB)) {
      player.setAttributeValue(Feature.IT, iB);
      interactor.updatePublicFeatureInfo(Feature.IT,
                                         player.copy(Attribute.PUBLIC));
    }
  }

  private UID        playerID;
  private Feature    player;
  private MapViewer  mapViewer;
  private Map        map;
  private Interactor interactor;
  private Timer      locationTimer;
  private Timer      orientationTimer;
  private boolean    locationChanged;
  private boolean    orientationChanged;
  private double     tagDistance;

  private static Properties props;

  /**
   * Returns the value of a named property, or null if the property
   * is not set.
   */
  public static String getProperty(String name) {
    return props.getProperty(name);
  }

  /**
   * Returns the value of a named property, or a default value if
   * the property is not set.
   */
  public static String getProperty(String name, String defaultVal) {
    return props.getProperty(name, defaultVal);
  }

  /**
   * Sets the value of a named property.  Returns the previously
   * set value, or null if there was no previous value.
   */
  public static String setProperty(String name, String val) {
    return (String) props.setProperty(name, val);
  }

}
