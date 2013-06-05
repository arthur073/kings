 
import lime.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class LimePuzzleAgent extends StationaryAgent
                             implements ActionListener, PuzzlePieceSetListener {

  public boolean DEBUG;

  private Queue              opQueue;
  private LimeTupleSpace     lts;
  private LimePuzzlePieceSet pieceSet;
  private PuzzleCanvas       canvas;
  private JFrame             frame;
  private JToolBar           toolBar;
  private JButton            engageButton;
  private JButton            shareButton;
  private JButton            loadButton;
  private JButton            quitButton;
  private boolean            alive;
  private boolean            leader = false;

  public LimePuzzleAgent() throws LimeException {
    super();
  }

  public Frame getFrame() {
    return frame;
  }

  public synchronized void actionPerformed(ActionEvent evt) {
    if     (evt.getActionCommand().equals("Engage"    )) actionEngage();
    else if(evt.getActionCommand().equals("Disengage" )) actionDisengage();
    else if(evt.getActionCommand().equals("Share"     )) actionShare();
    else if(evt.getActionCommand().equals("Unshare"   )) actionUnshare();
    else if(evt.getActionCommand().equals("Quit"      )) actionQuit();
    else if(evt.getActionCommand().equals("New Puzzle"))
      enqueueOp(new Runnable() { public void run() { actionLoad(); }});
  }

  private void actionEngage() {
      System.err.println("Engaging host...");
    if(!lts.isShared()){
      actionShare();
    }
    enqueueOp(new Runnable(){public void run(){
      LimeServer.getServer().engage();
      if(LimeServer.getServer().isEngaged()){
        System.err.println("Puzzle: engagement successful!");
      }
      else{
        System.err.println("Puzzle: engagement failed!");
      }
    }});
    engageButton.setText("Disengage");
    quitButton.setEnabled(false);
  }

  private void actionDisengage() {
    enqueueOp(new Runnable(){public void run(){
      LimeServer.getServer().disengage();
      if(!LimeServer.getServer().isEngaged()){
        System.err.println("Puzzle: disengagement successful!");
      }
      else{
        System.err.println("Puzzle: disengagement failed!");
      }
    }});
    engageButton.setText("Engage");
    quitButton.setEnabled(true);
  }

  private void actionShare(){
    enqueueOp(new Runnable(){public void run(){
      lts.setShared(true);
      shareButton.setText("Unshare");
    }});

  }

  private void actionUnshare(){
    enqueueOp(new Runnable(){public void run(){
      lts.setShared(false);
      shareButton.setText("Share");
      if(!LimeServer.getServer().isEngaged()){
        engageButton.setText("Engage");
      }
    }});
  }

  private void actionLoad() {
    URL url = null;
    Image image = null;
    String urlString = JOptionPane.showInputDialog(frame,
        "Enter the URL for the puzzle image:", "Puzzle Image URL",
        JOptionPane.QUESTION_MESSAGE);
    if(urlString == null) return;
    try { url = new URL(new java.io.File(".").toURL(), urlString); }
    catch(MalformedURLException mfuex) { url = null; }
    if(url != null) image = loadImage(url);
    while(url == null || image == null) {
      urlString = JOptionPane.showInputDialog(frame,
          "Image not found.  Enter the correct URL for the puzzle image:",
          "Puzzle Image URL", JOptionPane.ERROR_MESSAGE);
      if(urlString == null) return;
      try { url = new URL(new java.io.File(".").toURL(), urlString); }
      catch(MalformedURLException mfuex) { url = null; }
      if(url != null) image = loadImage(url);
    }

    Dimension imageSize = new Dimension(image.getWidth(null),
                                        image.getHeight(null));
    java.util.List sizeChoices = new LinkedList();
    /*
     * Compute some reasonable choices for pieces based on these constraints:
     * w >= 2, h >= 2
     * W/w >= 40, H/h >= 40
     * 0.75 < (W/w)/(H/h) < 1.3333
     */
    float W = imageSize.width;
    float H = imageSize.height;
    int w, h;
    for(w = 2; W/w >= 40; w++) {
      for(h = 2; H/h >= 40; h++) {
        float aspect = (W/w)/(H/h);
        if(0.75f < aspect && aspect < 1.3333f) {
          sizeChoices.add("" + w + "x" + h);
        }
      }
    }
    if(sizeChoices.size() == 0) {
      sizeChoices.add("1x1");
    }

    String sizeChoice = (String) JOptionPane.showInputDialog(frame,
        "How many pieces (COLUMNSxROWS)?", "Puzzle Pieces",
        JOptionPane.QUESTION_MESSAGE, null,
        sizeChoices.toArray(new String[sizeChoices.size()]),
        sizeChoices.get(0));

    try {
      w = Integer.parseInt(sizeChoice.substring(0, sizeChoice.indexOf('x')));
      h = Integer.parseInt(sizeChoice.substring(sizeChoice.indexOf('x') + 1));
    }
    catch(NumberFormatException nfex) {
      nfex.printStackTrace(System.err);
      w = h = 1;
    }

    pieceSet.init(new Dimension(w, h), image);
    pieceSet.generatePieces(edgeSet, new Random(), true,
        new Rectangle(0, 0, canvas.getSize().width, canvas.getSize().height),
        true);
    
    pieceSet.shareAll();
    canvas.repaint();
  }

  private void actionQuit() {
    if(LimeServer.getServer().isEngaged()) {
      JOptionPane.showMessageDialog(frame, 
          "You cannot quit while engaged.", "Cannot Quit Puzzle",
          JOptionPane.ERROR_MESSAGE);
    }
    else {
      int selCount = pieceSet.getSelectedPieceCount();
      if(selCount > 0) {
        int answer = JOptionPane.showConfirmDialog(frame,
            "Are you sure you want to quit and discard your " +
            selCount + " piece" + (selCount > 1 ? "s?" : "?"),
            "Confirm Puzzle Quit", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if(answer != JOptionPane.YES_OPTION) return;
      }
      alive = false;
      frame.setVisible(false);
      frame.dispose();
      System.exit(0);
    }
  }

  public void puzzlePieceSetChanged(Rectangle r) {
    loadButton.setVisible(false);
    Rectangle oldBounds = frame.getBounds();
    canvas.revalidate();
    frame.pack();
    Rectangle newBounds = frame.getBounds();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    int x = oldBounds.x - (newBounds.width - oldBounds.width)/2;
    x = Math.max(0, Math.min(screenSize.width - newBounds.width, x));
    int y = oldBounds.y - (newBounds.height - oldBounds.height)/2;
    y = Math.max(0, Math.min(screenSize.height - newBounds.height, y));
    frame.setLocation(x, y);

    pieceSet.removePuzzlePieceSetListener(this);
  }

  private String queryName() {
    String name = "";
    while(name != null && name.equals("")) {
      name = JOptionPane.showInputDialog(frame,
          "Enter the name of the puzzle game:", "Puzzle Name",
          JOptionPane.QUESTION_MESSAGE);
    }
    return name;
  }

  private Color queryColor() {
    return JColorChooser.showDialog(frame, "Choose Your Color", Color.white);
  }

  void enqueueOp(Runnable op) {
    opQueue.add(op);
  }

  /**
   * This agent's thread just executes items from the piece set's op queue.
   */
  public void run() {
    opQueue = new Queue();
    frame = new JFrame("Puzzle");

    String name = queryName();
    if(name == null) return;
    Color color = queryColor();
    if(color == null) return;

    LimeServer ls = LimeServer.getServer();
    String ld = ls.properties.getProperty("lime.debug");
    DEBUG = (ld != null && ld.equals("true"));
    
    if(!LimeServer.getServer().isEngaged()) {
      int answer = JOptionPane.showConfirmDialog(frame,
          "Declare this host is leader?", "Declare Leader",
          JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if(answer == JOptionPane.YES_OPTION) {
        if(DEBUG) System.err.println("LimePuzzle: Declaring this host is leader");
        LimeServer.getServer().declareLeader();
        if(!LimeServer.getServer().isEngaged())
          System.err.println("LimePuzzle: ENGAGEMENT FAILED");
        else if(DEBUG) System.err.println("LimePuzzle: LimeServer engaged");
        leader = true;
      }
    }

    try {
      if(DEBUG) System.err.println("LimePuzzle: Creating LTS");
      lts = new LimeTupleSpace(name);
      if(DEBUG) System.err.println("LimePuzzle: LTS created");
    }
    catch(LimeException lex) {
      lex.printStackTrace();
      return;
    }
    if(DEBUG) System.err.println("LimePuzzle: Creating LimePuzzlePieceSet");
    pieceSet = new LimePuzzlePieceSet(name, new Owner(color, getMgr().getID()),this,lts);
    if(DEBUG) System.err.println("LimePuzzle: LimePuzzlePieceSet created");
    pieceSet.addPuzzlePieceSetListener(this);
    canvas = new PuzzleCanvas(pieceSet);

    engageButton = new JButton("Engage");
    engageButton.addActionListener(this);
    shareButton = new JButton("Share");
    shareButton.addActionListener(this);
    loadButton = new JButton("New Puzzle");
    loadButton.addActionListener(this);
    quitButton = new JButton("Quit");
    quitButton.addActionListener(this);
    toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.setBorder(new CompoundBorder(
        new LineBorder(pieceSet.getSelectionColor(), 3),
        new EmptyBorder(5, 5, 5, 5)));
    toolBar.setLayout(new BorderLayout());
    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    leftPanel.add(engageButton);
    leftPanel.add(shareButton);
    toolBar.add(BorderLayout.WEST, leftPanel);
    toolBar.add(BorderLayout.EAST, quitButton);
    JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    centerPanel.add(loadButton);
    toolBar.add(BorderLayout.CENTER, centerPanel);

    frame.setTitle("Puzzle - " + name);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(BorderLayout.NORTH, toolBar);
    frame.getContentPane().add(BorderLayout.CENTER, canvas);
    if(LimeServer.getServer().isEngaged()){
      if(leader){
        lts.setShared(true);
        shareButton.setText("Unshare");
      }
      engageButton.setText("Disengage");
      quitButton.setEnabled(false);
    }
    frame.pack();
    Dimension screenSize = frame.getToolkit().getScreenSize();
    frame.setLocation(screenSize.width/2 - frame.getWidth()/2,
                      screenSize.height/2 - frame.getHeight()/2);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) { actionQuit(); }
    });
    alive = true;
    while(alive) {
      Runnable op = (Runnable) opQueue.getNext();
      if(op == null) break;

      try { op.run(); }
      catch(Exception ex) { ex.printStackTrace(System.err); }
    }
  }

  protected final Image loadImage(URL url) {
    Image img = Toolkit.getDefaultToolkit().getImage(url);

    MediaTracker mt = new MediaTracker(frame);
    mt.addImage(img, 0);
    while(true) {
      try {
        mt.waitForID(0);
        break;
      } catch(InterruptedException iex) { }
    }
    return mt.isErrorID(0)? null : img;
  }

  public static Edge[][] edgeSet = new Edge[][] {
    { new PuzzleTabEdge(false), new PuzzleTabEdge(true)  },
    { new PuzzleTabEdge(false), new PuzzleTabEdge(true)  },
    { new PuzzleTabEdge(false), new PuzzleTabEdge(true)  },
    { new PuzzleTabEdge(true) , new PuzzleTabEdge(false) },
    { new PuzzleTabEdge(true) , new PuzzleTabEdge(false) },
    { new PuzzleTabEdge(true) , new PuzzleTabEdge(false) },
    { new WaveEdge()          , new WaveEdge()           }
  };
}




