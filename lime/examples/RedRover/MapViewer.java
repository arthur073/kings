import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;

/**
 * This is the GUI / Map Interface of RedRover version 1.0
 * This version curently handles both location and orientation changes
 * through mouse input and generates the corresponding events.
 *
 * @author Brian Mesh
 * @version 12/15/99
 */

public class MapViewer extends Frame{
  
  //initial map height and width
    private int windowHeight = 400;
    private int windowWidth = 600;

  //initial map and player positions
    private Location thePosition = new Location(5000,5000);
    private Location myLoc = new Location(5000,5000);

  //compass and tick mark colors
    private Color compC = new Color(7,0,117);
    private Color tickC = Color.blue;
  
  //max zoom factor  
    private double zoomMax = 4;

  //offscreen drawing canvases
    private BufferedImage theWindow, bgBig, contWindow;
    private Graphics winCanvas, contCanvas;

  //imported images
    private Image bgImg, forwardDot, team1, team2, team3, team4, team5, team6,
	ghost1, ghost2, ghost3, ghost4, ghost5, ghost6,
	shawdow, north, bMagnifyUp, bMagnifyDn, bMapUp, bMapDn, bPlayerUp,
	bPlayerDn, bCenterUp, bCenterDn, bNorthUp, bNorthDn, bEngageUp, bEngageDn,
	bObjFoundUp, bObjFoundDn, bObjLookUp, bObjLookDn, bPlayerInfoUp, bPlayerInfoDn;

  //Viewer's current features
    private Hashtable features;

  //used for moving, resizing, etc...
    private Location compPt, center, oldPos, moveStart;
    private int compSize;
    private double direction, olddirection, newdirection, zoom, oldZoom, mouseStart;
    private UID myID;

  //GUI interface controls
    private boolean newPress=false;
    private boolean compGrab=false;
    private boolean zoomGrab=true;
    private boolean moveGrab=false;
    private boolean playerMove = false;
    private boolean playerInfo = false;
    private boolean northdir=false;
    private boolean engaged;
    private boolean objFound = false;
    private boolean objLook = false;
    protected EventListenerList listenerList;

    public static void main (String args[]){
	new MapViewer(null, false);
    }

  /**
   *  Creates a new instance of the map viewer
   *
   * @param myID The ID of this player
   * @param initEngaged The initial state of engagement
   */
    public MapViewer(UID myID, boolean initEngaged){
	engaged = initEngaged;
        listenerList = new EventListenerList();
        this.myID = myID;
	setupDisplay();
        zoom = 1;
        features = new Hashtable();
	direction = 0;
	setVisible(true);
	try{
	    Thread.sleep(1000);
	}
	catch(InterruptedException e){System.err.println("Sleep interrupted");}
	//System.err.println("Resizing Window at beginning");
	resizeMapWindow();
    }
 
  // setup display with appropriate listeners and defaults
    private void setupDisplay(){
	setTitle("Red Rover v1.0");
	setSize(windowWidth+30, windowHeight);
	addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent e){
		    fireGameStateChange(GameStateEvent.PLAYER_EXITING_GAME);
		    setVisible(false);
		    if (myID==null)
			System.exit(0);
		}
		public void windowActivated(WindowEvent e){
		    repaint();
		}
		public void windowOpened(WindowEvent e){
		    resizeMapWindow();
		}
		public void windowDeiconified(WindowEvent e){
		    repaint();
		}
	});
	addComponentListener(new ComponentAdapter(){
	    public void componentResized(ComponentEvent e){
		resizeMapWindow();
	    }
	    public void componentMoved(ComponentEvent e){
		repaint();
	    }
	});
	addMouseListener(new MouseAdapter(){
	    public void mousePressed(MouseEvent e){
		newPress=true;
	    }

	    public void mouseClicked(MouseEvent e){
		checkButtons(new Location(e.getX(),e.getY()));
	    }
	});
	addMouseMotionListener(new MouseMotionAdapter(){
	    public void mouseDragged(MouseEvent e){
		mouseOnMap(new Location(e.getX(),e.getY()));
	    }
	});

        //Import Graphics
	Toolkit tk= Toolkit.getDefaultToolkit();
	bgImg = tk.createImage("slate4.gif");
	forwardDot = tk.createImage("bluedot.gif");
	team1 = tk.createImage("reddiamond.gif");
	team2 = tk.createImage("orangediamond.gif");
        team3 = tk.createImage("yellowdiamond.gif");
        team4 = tk.createImage("greendiamond.gif");
        team5 = tk.createImage("bluediamond.gif");
        team6 = tk.createImage("purplediamond.gif");
	ghost1 = tk.createImage("redghost.gif");
	ghost2 = tk.createImage("orangeghost.gif");
	ghost3 = tk.createImage("yellowghost.gif");
	ghost4 = tk.createImage("greenghost.gif");
	ghost5 = tk.createImage("blueghost.gif");
	ghost6 = tk.createImage("purpleghost.gif");
	bMagnifyUp = tk.createImage("bmagup.gif");
	bMagnifyDn = tk.createImage("bmagdn.gif");
	bMapUp = tk.createImage("b4dirup.gif");
	bMapDn = tk.createImage("b4dirdn.gif");
	bPlayerUp = tk.createImage("bplyup.gif");
	bPlayerDn = tk.createImage("bplydn.gif");
	bCenterUp = tk.createImage("btgtup.gif");
	bCenterDn = tk.createImage("btgtdn.gif");
	bNorthUp = tk.createImage("bnthup.gif");
	bNorthDn = tk.createImage("bnthdn.gif");
	bEngageUp = tk.createImage("bengup.gif");
	bEngageDn = tk.createImage("bengdn.gif");
	bObjFoundUp = tk.createImage("bobjfoundup.gif");
	bObjFoundDn = tk.createImage("bobjfounddn.gif");
	bObjLookUp = tk.createImage("bobjlookup.gif");
	bObjLookDn = tk.createImage("bobjlookdn.gif");
	bPlayerInfoUp = tk.createImage("bplyinfoup.gif");
	bPlayerInfoDn = tk.createImage("bplyinfodn.gif");
	shawdow = tk.createImage("shawdow.gif");
	north = tk.createImage("north.gif");
    }

  //handle resizing of window by user
    private void resizeMapWindow(){
	windowWidth = getWidth()-30;
	windowHeight = getHeight();
	theWindow = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
	winCanvas = theWindow.createGraphics();
	contWindow = new BufferedImage(30, windowHeight, BufferedImage.TYPE_INT_RGB);
	contCanvas = contWindow.createGraphics();
	contCanvas.setColor(Color.black);
	contCanvas.fillRect(0,0,30,windowHeight);
	contCanvas.setColor(Color.gray);
	int theLine = 32;
	while(theLine < windowHeight){
	    contCanvas.drawLine(0,theLine,30,theLine);
	    theLine += 30;
	}
	contCanvas.drawLine(30,0,30,windowHeight);
	bgBig = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
	Graphics bg = bgBig.createGraphics();
	for (int i = 0; i <= windowWidth/96; i++)
	    for (int j = 0; j <= windowHeight/96; j++)
		bg.drawImage(bgImg,i*96,j*96,this);
	compSize = Math.min(windowWidth,windowHeight);
	compSize *= .35;
	compPt = new Location(1.0*windowWidth/2-compSize,1.0*windowHeight/2-compSize);
	center = new Location(1.0*windowWidth/2,1.0*windowHeight/2);
	repaint();
    }

  //called for mouse clicks to determine if button is pushed, adjust accordingly
    private void checkButtons(Location mouse){
	if (mouse.getX() > 3 && mouse.getX() < 28){
	    if (mouse.getY() > 35 && mouse.getY() < 60){
		zoomGrab = true;
		moveGrab = false;
		playerMove = false;
		playerInfo = false;
                //System.err.println("Zoom");
	    }
	    else if (mouse.getY() >65 && mouse.getY() < 90){
		zoomGrab = false;
		moveGrab = true;
		playerMove = false;
		playerInfo = false;
                //System.err.println("Map Move");
	    }
	    else if (mouse.getY() >95 && mouse.getY() < 120){
		zoomGrab = false;
		moveGrab = false;
		playerMove = true;
		playerInfo = false;
                //System.err.println("Player Move");
            }
	    else if (mouse.getY() >125 && mouse.getY() <150){
		zoomGrab = false;
		moveGrab = false;
		playerMove = false;
		playerInfo = true;
		//System.err.println("Player Info");
	    }
	    else if (mouse.getY() >185 && mouse.getY() < 210){
		thePosition.setLocation(myLoc.getX(),myLoc.getY());
		northdir = false;
                //System.err.println("Player Centered");
	    }
	    else if (mouse.getY() >215 && mouse.getY() < 240){
	        northdir = !northdir;
                //System.err.println("North Lock");
	    }
            else if (mouse.getY() >245 && mouse.getY() < 270){
                if (engaged)
                  fireGameStateChange(GameStateEvent.PLAYER_LEAVING_GAME);
                else
                  fireGameStateChange(GameStateEvent.PLAYER_JOINING_GAME);
                engaged = !engaged;
		//System.err.println("Engagement: "+engaged);
            }
	    else if (mouse.getY() >305 && mouse.getY()<330){
		if (!objFound){
		    new ObjectDialog(ObjectDialog.FOUND,this);
		    objFound = true;
		}
		//System.err.println("Object Found");
	    }
	    else if (mouse.getY() >335 && mouse.getY()<360){
		if (!objLook){
		    new ObjectDialog(ObjectDialog.LOOKFOR,this);
		    objLook = true;
		}
		//System.err.println("Look for object");
	    }
	    repaint();
	}
	else{
	    Enumeration thePlayers = features.elements();
	    Location mapPos;
	    Feature curPlayer;
	    int tol = (int)(5.0/zoom);  //click tolerence
	    while (thePlayers.hasMoreElements()){
		curPlayer = (Feature)(thePlayers.nextElement());
		mapPos = convertPoint((Location)(curPlayer.getAttribute(Feature.LOCATION).
                                                      getValue()),true);
		if (Math.abs(mapPos.getX()-mouse.getX()+30)<= tol && //plus 30 to account for buttons
		    Math.abs(mapPos.getY()-mouse.getY())<= tol){
                  //System.err.println("Getting Player Info");
		    fireInfoRequest(InfoRequestEvent.PICTURE,curPlayer);
		}
	    }
	}
    }

  // Called for mouse movement.  This handles zooming, map movement, player movement,
  // orientation changes, etc...
    private void mouseOnMap(Location mouse){
        mouse.setLocation(mouse.getX()-30,mouse.getY());
	double dist = Math.sqrt(Math.pow(center.getX()-mouse.getX(),2)+
                                Math.pow(center.getY()-mouse.getY(),2));

        //on first movement, determine type of adjustment and initialize appropriate variables
	if (newPress){ 
	    newPress = false;
	    compGrab = false;
	    if (Math.abs(dist-compSize) < 8){
		olddirection = Math.atan(1.0*(mouse.getY()-center.getY())/
                                         (mouse.getX()-center.getX()));
		if (mouse.getX()<center.getX())
		    olddirection+=Math.PI;
		olddirection = direction - olddirection - Math.PI/2;
		compGrab = true;
	    }
	    else if (zoomGrab){
		mouseStart = mouse.getY();
		oldZoom = zoom;
	    }
	    else if (moveGrab || playerMove){
		oldPos = new Location (thePosition.getX(), thePosition.getY());
		moveStart = new Location(mouse);
	      	oldZoom = zoom;
	    }
	}

        //handle direction change activities
	if (compGrab){
	    newdirection = Math.atan(1.0*(mouse.getY()-center.getY())/
                                     (mouse.getX()-center.getX()))+Math.PI/2;
	    if (mouse.getX()<center.getX())
		newdirection+=Math.PI;
            fireOrientationChange(new Orientation(newdirection+olddirection));
	}

        //handle zoom changes
	else if (zoomGrab){
	    zoom = mouse.getY() - mouseStart;
	    zoom = zoom*8/windowHeight + oldZoom;
	    if (zoom > zoomMax)
		zoom = zoomMax;
	    else if (zoom < 1.0/zoomMax)
		zoom = 1.0/zoomMax;
	    repaint();
	}

        //handle movement of player and map
	else if (moveGrab || playerMove){
	    dist = Math.sqrt(Math.pow(moveStart.getX() - mouse.getX(),2)
                             +Math.pow(moveStart.getY() - mouse.getY(),2));
	    double dir = Math.PI/2;
	    if (moveStart.getX() != mouse.getX())
		dir = Math.atan(1.0*(moveStart.getY()-mouse.getY())/
                                   (moveStart.getX()-mouse.getX())) + Math.PI;
	    if (mouse.getX()<moveStart.getX() || (mouse.getX()==moveStart.getX()&&
						  mouse.getY()>moveStart.getY()))
		dir+=Math.PI;
	    if (!northdir)
		dir -= direction;
	    double xDiff = Math.cos(dir)*dist*oldZoom;
	    double yDiff = Math.sin(dir)*dist*oldZoom;
            //handle movement of player
	    if (myLoc !=null && playerMove && 
		myLoc.getX() == thePosition.getX() && myLoc.getY() == thePosition.getY()){
              //System.err.println("oldPos: "+oldPos+", xdiff: "+xDiff+", ydiff: "+yDiff);
              fireLocationChange(new Location(oldPos.getX()-xDiff,oldPos.getY()-yDiff));
	    }

            // handle map movement
	    else if (moveGrab){
		thePosition.setLocation(oldPos.getX()+xDiff,oldPos.getY()+yDiff);
                repaint();
            }
	}
    }
    
  // based on current direction and zoom, takes in a Location and converts it either 
  // from the general map coordinates to the screen coordinates, or vise versa.
    private Location convertPoint(Location oldPt, boolean mapToGUI){
	if (mapToGUI){
	    Location tempPt = new Location((oldPt.getX()-thePosition.getX()+
                                            zoom*windowWidth/2)/zoom,
                                           (oldPt.getY()-thePosition.getY()+
                                            zoom*windowHeight/2)/zoom);
	    return rotatePoint(tempPt,direction);
	}
	else{
	    Location tempPt = rotatePoint(oldPt,-direction);
	    return new Location(zoom*oldPt.getX()+thePosition.getX()-zoom*windowWidth/2,
                                zoom*oldPt.getY()+thePosition.getY()-zoom*windowHeight/2);
	}
    }
	    
  // rotates the input location (screen coordinates) and rotates it about the center
  // of the screen based on the given rotation.
    private Location rotatePoint(Location oldPt, double convDir){
	if (oldPt.getX() == center.getX() && oldPt.getY() == center.getY())
	    return new Location(oldPt);
	double dist = Math.sqrt(Math.pow(center.getX()-oldPt.getX(),2)+
                                Math.pow(center.getY()-oldPt.getY(),2));
	double dir = Math.atan(1.0*(oldPt.getY()-center.getY())/
                               (oldPt.getX()-center.getX()))+convDir;
	if (oldPt.getX()<center.getX())
	    dir+=Math.PI;
	return new Location(Math.cos(dir)*dist+center.getX(),Math.sin(dir)*dist+center.getY());
    }


    // called to submit a notifaction or request for a map object
    protected synchronized void submitObject(int dType, String objMsg, boolean getOne){
	int eventType;
	if (dType == ObjectDialog.FOUND)
	    eventType = MapObjectEvent.OBJECT_FOUND;
	else{
	    if (getOne)
		eventType = MapObjectEvent.OBJECT_LOOKFORONE;
	    else
		eventType = MapObjectEvent.OBJECT_LOOKFORALL;
	}
	fireMapObjectChange(eventType,objMsg);
    }

    // called when an open dialog is closed - reseting for a future dialog
    protected synchronized void objectDialogComplete(int dialogType){
	if (dialogType == ObjectDialog.FOUND)
	    objFound = false;
	else if (dialogType == ObjectDialog.LOOKFOR)
	    objLook = false;
	repaint();
    }


  // redraws the screen using double buffering.
    public void update(Graphics g){
      winCanvas.drawImage(bgBig,0,0,this);
      //get enumeration of players to draw on screen
      Enumeration thePlayers = features.elements();
      double plsize = (int)(5/zoom);
      double curDirection = direction;
      if (northdir)
        direction = 0;   
      try{
        while(thePlayers.hasMoreElements()){
          Feature curPlayer = (Feature)(thePlayers.nextElement());
          
          if (curPlayer.getAttribute(Feature.LOCATION)==null) {
            continue; // this feature has no location!!
          }
          

          Location mapPos = convertPoint((Location)(curPlayer.getAttribute(Feature.LOCATION).
                                                    getValue()),true);
          boolean playerIsGhost = (Math.random()>1);
          String teamColor = ((String)(curPlayer.getAttribute(Feature.TEAM).getValue())).
            toLowerCase();
          if (((Boolean)(curPlayer.getAttribute(Feature.FROZEN).getValue())).booleanValue()){
            winCanvas.setColor(new Color(200, 200, 255));
            winCanvas.fillOval((int)(mapPos.getX()-plsize-2),
                               (int)(mapPos.getY()-plsize-2),
                               (int)(plsize*2)+4,(int)(plsize*2)+4);
          }
          else
            winCanvas.drawImage(shawdow,(int)(mapPos.getX()-6/zoom),(int)(mapPos.getY()-3/zoom),
                                (int)(plsize*2),(int)(plsize*2),this);
          boolean isGhost = (((Boolean)(curPlayer.getAttribute(Feature.GHOST).getValue())).
                             booleanValue());
          if (teamColor.compareTo("red")==0){
            if (isGhost)
              winCanvas.drawImage(ghost1,(int)(mapPos.getX()-plsize),
                                  (int)(mapPos.getY()-plsize),
                                  (int)(plsize*2),(int)(plsize*2),this);
            else
              winCanvas.drawImage(team1,(int)(mapPos.getX()-plsize),
                                  (int)(mapPos.getY()-plsize),
                                  (int)(plsize*2),(int)(plsize*2),this);
          }
          else if (teamColor.compareTo("orange")==0){
            if (isGhost)
              winCanvas.drawImage(ghost2,(int)(mapPos.getX()-plsize),
                                  (int)(mapPos.getY()-plsize),
                                  (int)(plsize*2),(int)(plsize*2),this);
            else
              winCanvas.drawImage(team2,(int)(mapPos.getX()-plsize),
                                  (int)(mapPos.getY()-plsize),
                                  (int)(plsize*2),(int)(plsize*2),this);
          }
          else if (teamColor.compareTo("yellow")==0){
            if (isGhost)
              winCanvas.drawImage(ghost3,(int)(mapPos.getX()-plsize),
                                  (int)(mapPos.getY()-plsize),
                                  (int)(plsize*2),(int)(plsize*2),this);
            else
              winCanvas.drawImage(team3,(int)(mapPos.getX()-plsize),
                                  (int)(mapPos.getY()-plsize),
                                  (int)(plsize*2),(int)(plsize*2),this);
          }
          else if (teamColor.compareTo("green")==0){
            if (isGhost)
              winCanvas.drawImage(ghost4,(int)(mapPos.getX()-plsize),
                                  (int)(mapPos.getY()-plsize),
                                  (int)(plsize*2),(int)(plsize*2),this);
            else
              winCanvas.drawImage(team4,(int)(mapPos.getX()-plsize),
                                  (int)(mapPos.getY()-plsize),
                                  (int)(plsize*2),(int)(plsize*2),this);
          }
          else if (teamColor.compareTo("blue")==0){
            if (isGhost)
              winCanvas.drawImage(ghost5,(int)(mapPos.getX()-plsize),
                                  (int)(mapPos.getY()-plsize),
                                  (int)(plsize*2),(int)(plsize*2),this);
            else
              winCanvas.drawImage(team5,(int)(mapPos.getX()-plsize),
                                  (int)(mapPos.getY()-plsize),
                                  (int)(plsize*2),(int)(plsize*2),this);
          }
          else {
            if (isGhost)
              winCanvas.drawImage(ghost6,(int)(mapPos.getX()-plsize),
                                  (int)(mapPos.getY()-plsize),
                                  (int)(plsize*2),(int)(plsize*2),this);
            else
              winCanvas.drawImage(team6,(int)(mapPos.getX()-plsize),
                                  (int)(mapPos.getY()-plsize),
                                  (int)(plsize*2),(int)(plsize*2),this);
          }
        }
      }
      catch(NoSuchElementException e){
        System.err.println("Drawing Problem with Enumerated Features\n"+e);
      }
      //draw compass and tick marks
      winCanvas.setColor(compC);
      winCanvas.drawOval((int)compPt.getX(),(int)compPt.getY(),compSize*2,compSize*2);
      winCanvas.drawOval((int)compPt.getX()+1,(int)compPt.getY()+1,compSize*2-2,compSize*2-2);
      winCanvas.drawOval((int)compPt.getX()+2,(int)compPt.getY()+2,compSize*2-4,compSize*2-4);
      winCanvas.drawOval((int)compPt.getX()+3,(int)compPt.getY()+3,compSize*2-6,compSize*2-6);
      winCanvas.setColor(tickC);
      double drawdirection = direction + Math.PI*3/2;
      for (int j = 0; j<4; j++){
        drawdirection += Math.PI/4;
        winCanvas.drawLine((int)(Math.cos(drawdirection)*(compSize-7)+center.getX()),
                           (int)(Math.sin(drawdirection)*(compSize-7)+center.getY()),
                           (int)(Math.cos(drawdirection)*(compSize+7)+center.getX()),
                           (int)(Math.sin(drawdirection)*(compSize+7)+center.getY()));
        drawdirection += Math.PI/4;
        winCanvas.drawLine((int)(Math.cos(drawdirection)*(compSize-10)+center.getX()),
                           (int)(Math.sin(drawdirection)*(compSize-10)+center.getY()),
                           (int)(Math.cos(drawdirection)*(compSize+15)+center.getX()),
                           (int)(Math.sin(drawdirection)*(compSize+15)+center.getY()));
      }
      winCanvas.drawImage(north,(int)(Math.cos(drawdirection)*(compSize+20)+center.getX())-7,
                          (int)(Math.sin(drawdirection)*(compSize+20)+center.getY())-7,14,14,this);
      for (int k = 0; k<31; k++){
        drawdirection += Math.PI/16;
        winCanvas.drawLine((int)(Math.cos(drawdirection)*(compSize-5)+center.getX()),
                           (int)(Math.sin(drawdirection)*(compSize-5)+center.getY()),
                           (int)(Math.cos(drawdirection)*(compSize+3)+center.getX()),
                           (int)(Math.sin(drawdirection)*(compSize+3)+center.getY()));
      }
      if (!northdir)
        drawdirection = Math.PI*3/2;
      else
        drawdirection = Math.PI*3/2+direction-curDirection;
      winCanvas.drawImage(forwardDot,(int)(Math.cos(drawdirection)*compSize + center.getX())-10,
                          (int)(Math.sin(drawdirection)*compSize+center.getY())-10,this);
      direction = curDirection;
      
      //draw buttons
      if (zoomGrab)
        contCanvas.drawImage(bMagnifyDn,3,35,this);
      else
        contCanvas.drawImage(bMagnifyUp,3,35,this);
      if (moveGrab)
        contCanvas.drawImage(bMapDn,3,65,this);
      else
        contCanvas.drawImage(bMapUp,3,65,this);	
      if (playerMove)
        contCanvas.drawImage(bPlayerDn,3,95,this);
      else
        contCanvas.drawImage(bPlayerUp,3,95,this);
      if (playerInfo)
        contCanvas.drawImage(bPlayerInfoDn,3,125,this);
      else
        contCanvas.drawImage(bPlayerInfoUp,3,125,this);
      if (myLoc.getX() == thePosition.getX() &&
          myLoc.getY() == thePosition.getY())
        contCanvas.drawImage(bCenterDn,3,185,this);
      else
        contCanvas.drawImage(bCenterUp,3,185,this);
      if (northdir)
        contCanvas.drawImage(bNorthDn,3,215,this);
      else
        contCanvas.drawImage(bNorthUp,3,215,this);
      if (engaged)
        contCanvas.drawImage(bEngageDn,3,245,this);
      else
        contCanvas.drawImage(bEngageUp,3,245,this);
      if (objFound)
        contCanvas.drawImage(bObjFoundDn,2,305,this);
      else
        contCanvas.drawImage(bObjFoundUp,2,305,this);
      if (objLook)
        contCanvas.drawImage(bObjLookDn,2,335,this);
      else
        contCanvas.drawImage(bObjLookUp,2,335,this);
      //draw predrawn images to screen
      g.drawImage(theWindow,31,0,this);
      g.drawImage(contWindow,3,0,this);
    }
  
  /***********************************************************
   **                     Event Management                  **
   **********************************************************/

  /**
   * Handles the update of a single Feature
   *
   * @param newF The feature to update
   */
  public void updateFeature(Feature newF){
    features.put(newF.getID(),newF);
    if(myID.equals(newF.getID())){
       direction=((Orientation)(newF.getAttribute(Feature.ORIENTATION).getValue())).getRadians();
       Location tempLoc = new Location((Location)(newF.getAttribute(Feature.LOCATION).getValue()));
       if (myLoc.getX()==thePosition.getX()&&myLoc.getY()==thePosition.getY()){
         thePosition = new Location(tempLoc);
       }
       myLoc = tempLoc;
    }
    else{
	features.put(newF.getID(),newF);
    }
    //System.err.println("Updating Feature: "+newF);
    repaint();
  }

  /**
   * Handles a whole new set of Features
   *
   * @param newFs An array of all features to update
   */
  public void updateAll(Feature[] newFs){
    features.clear();
    for (int i = 0; i < newFs.length; i++){
      //System.err.println("Updating All: "+newFs[i]);
      features.put(newFs[i].getID(),newFs[i]);
    }
    direction=((Orientation)(((Feature)(features.get(myID))).getAttribute(Feature.ORIENTATION).
                             getValue())).getRadians();
    Location tempLoc = (Location)(((Feature)(features.get(myID))).
                                   getAttribute(Feature.LOCATION)).getValue();
    if (myLoc.getX()==thePosition.getX()&&myLoc.getY()==thePosition.getY())
      thePosition = new Location(tempLoc);
    myLoc = tempLoc;
    repaint();
  }

  /**
   * Handles an incoming message about map objects
   *
   * @param objMesg The message to be displayed to the user.
   */
  public void objectMessage(String objMesg){
      new MessageReporter(objMesg);
  }

  /**
   * Handles an incoming player picture and Feature to display to screen
   */
  public void playerPicture(Feature thisPlayer, Image playerPic){
      new PlayerReporter(thisPlayer,playerPic);
  }

  /**
   * Add a MapWindowListener
   *
   * @param mpl The MapWindowListener to be added
   */
  public void addMapWindowListener(MapWindowListener mpl){
    listenerList.add(MapWindowListener.class, mpl);
  }

  /**
   * Remove a MapWindowListener
   *
   * @param mpl The MapWindowListener to be removed
   */
  public void removeMapWindowListener(MapWindowListener mpl){
    listenerList.remove(MapWindowListener.class, mpl);
  }

  /**
   * Add a GameStateListener
   *
   * @param gsl The GameStateListener to be added
   */
  public void addGameStateListener(GameStateListener gsl){
    listenerList.add(GameStateListener.class, gsl);
  }

  /**
   * Remove a GameStateListener
   *
   * @param gsl The GameStateListener to be removed
   */
  public void removeGameStateListener(GameStateListener gsl){
    listenerList.remove(GameStateListener.class, gsl);
  }

  /**
   * Add a LocationListener
   *
   * @param ll The LocationListener to be added
   */
  public void addLocationListener(LocationListener ll){
    listenerList.add(LocationListener.class, ll);
  }

  /**
   * Remove a LocationListener
   *
   * @param ll The LocationListener to be removed
   */
  public void removeLocationListener(LocationListener ll){
    listenerList.remove(LocationListener.class, ll);
  }

  /**
   * Add an OrientationListener
   *
   * @param ol The OrientationListener to be addd
   */ 
  public void addOrientationListener(OrientationListener ol){
    listenerList.add(OrientationListener.class, ol);
  }

  /**
   * Remove an OreientationListener
   *
   * @param ol The OrienationListener to be removed
   */
  public void removeOrienationListener(OrientationListener ol){
    listenerList.remove(OrientationListener.class, ol);
  }

  /**
   * Add an MapObjectListener
   *
   * @param mol The MapObjectListener to be addd
   */ 
  public void addMapObjectListener(MapObjectListener mol){
    listenerList.add(MapObjectListener.class, mol);
  }

  /**
   * Remove an MapObjectListener
   *
   * @param ol The MapObjectListener to be removed
   */
  public void removeMapObjectListener(MapObjectListener mol){
    listenerList.remove(MapObjectListener.class, mol);
  }

  /**
   * Add an InfoRequestListener
   *
   * @param irl InfoRequestListener to be addd
   */ 
  public void addInfoRequestListener(InfoRequestListener irl){
    listenerList.add(InfoRequestListener.class, irl);
  }

  /**
   * Remove an InfoRequestListener
   *
   * @param ol InfoRequestListener to be removed
   */
  public void removeInfoRequestListener(InfoRequestListener irl){
    listenerList.remove(InfoRequestListener.class, irl);
  }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.<br>
     * This code based on examples from the
     * javax.swing.event.EventListenerList in the Java API.
     **/
  protected void fireWindowChange(Shape newArea){
    MapWindowEvent mapWindowEvent = null;
    
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == MapWindowListener.class) {
        // Lazily create the event
        if (mapWindowEvent == null){
          mapWindowEvent = new MapWindowEvent(this, newArea);
        }
        ((MapWindowListener) listeners[i + 1]).mapWindowChanged(mapWindowEvent);
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
     **/
  protected void fireGameStateChange (int id){
    GameStateEvent gameStateEvent = null;
    
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == GameStateListener.class) {
        // Lazily create the event
        if (gameStateEvent == null){
          gameStateEvent = new GameStateEvent(this, id);
        }
        ((GameStateListener) listeners[i + 1]).gameStateChanged(gameStateEvent);
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
     **/
  protected void fireLocationChange (Location myLoc){
    //System.err.println("Fired: "+myLoc);
    LocationEvent locationEvent = null;
    
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == LocationListener.class) {
        // Lazily create the event
        if (locationEvent == null){
          locationEvent = new LocationEvent(this, myLoc);
        }
        ((LocationListener) listeners[i + 1]).playerMoved(locationEvent);
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
     **/
  protected void fireOrientationChange (Orientation newDir){
    OrientationEvent orientationEvent = null;
    
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == OrientationListener.class) {
        // Lazily create the event
        if (orientationEvent == null){
          orientationEvent = new OrientationEvent(this, newDir);
        }
        ((OrientationListener) listeners[i + 1]).playerTurned(orientationEvent);
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
     **/
  protected void fireMapObjectChange (int actionType, String objMesg){
    MapObjectEvent mapObjectEvent = null;
    
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == MapObjectListener.class) {
        // Lazily create the event
        if (mapObjectEvent == null){
          mapObjectEvent = new MapObjectEvent(this, actionType, objMesg);
        }
        ((MapObjectListener) listeners[i + 1]).mapObjectNotification(mapObjectEvent);
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
     **/
  protected void fireInfoRequest (int actionType, Feature thePlayer){
    InfoRequestEvent infoRequestEvent = null;
    
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == InfoRequestListener.class) {
        // Lazily create the event
        if (infoRequestEvent == null){
          infoRequestEvent = new InfoRequestEvent(this, actionType, thePlayer);
        }
        ((InfoRequestListener) listeners[i + 1]).infoRequested(infoRequestEvent);
      }              
    }
  }
}
