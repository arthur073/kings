import lime.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import lights.interfaces.*;
import lights.adapters.*;
import gp.util.*;

public class LChat extends StationaryAgent implements ActionListener{
  
  public LChat (){
    super();
  }
  
  private void initReaction(){
    enqueueOp(new Runnable() { public void run(){
      ITuple template = new Tuple();
      template.addFormal(String.class).addFormal(LChatMessage.class);
      try{
        messageTS.addWeakReaction(new Reaction[] {
          new UbiquitousReaction(template, new MessageListener(LChat.this), 
                                 Reaction.ONCEPERTUPLE)});
      }
      catch(TupleSpaceEngineException ex){
        ex.printStackTrace(System.err);
      }
    }});
    System.out.println("Reactions initialized");
  }

  private void enqueueOp(Runnable r){
    opQueue.add(r);
  }

  public void run(){
    this.opQueue = new Queue();
    this.frame = new JFrame();
    this.outputText = "";
    
    while(userid != null && userid.equals("")){
      userid = JOptionPane.showInputDialog(frame, "User ID?", "User id", 
                                           JOptionPane.QUESTION_MESSAGE);
    }
    if(!LimeServer.getServer().isEngaged()){
      int answer = JOptionPane.showConfirmDialog(frame,
                                                 "Declare this host as leader?", 
                                                 "Declare Leader",
                                                 JOptionPane.YES_NO_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE);
      if(answer == JOptionPane.YES_OPTION){
        LimeServer.getServer().declareLeader();
        if(!LimeServer.getServer().isEngaged()){
          System.err.println("Chat: ENGAGEMENT FAILED!!");
        }
      }
    }
    try {
      messageTS = new LimeTupleSpace("messageTS");
    } 
    catch (TupleSpaceEngineException tsee) {
      tsee.printStackTrace();
      System.exit(1);
    }
    Container mainGUI = makeMainGUI();
    frame.getContentPane().add(mainGUI, BorderLayout.CENTER);
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });
    frame.setTitle("LChat (" + userid + ")");
    frame.pack();
    frame.setVisible(true);
    initReaction();
    while(true){
      Runnable op = (Runnable) opQueue.remove();
      if(op == null){
        break;
      }
      try{
        op.run();
      }
      catch(Exception e){
       e.printStackTrace(System.err);
      }
    }
  }

    /**
     * Adds a message to the GUI's output display.
     *
     * @param m The message to display
     */
    public void processMessage (LChatMessage m){
	outputText += m.toString();
	output.setText(outputText);
        JScrollBar sb = outputPane.getVerticalScrollBar();
        sb.setValue(sb.getMaximum());
    }

  /**
   * Handles the action received from the main GUI
   *
   * @param e The incoming event to handle
   */
  public void actionPerformed (ActionEvent e){
    if (e.getActionCommand().equals(SEND_BUTTON)){
      sendMessage();
    }
    else if (e.getActionCommand().equals(BOLD_BUTTON)){
      boldOn = !boldOn;
      if (boldOn){
        bold.setIcon(boldDownIcon);
      }
      else{
        bold.setIcon(boldIcon);
      }
    }
    else if (e.getActionCommand().equals(ITALICIZE_BUTTON)){
      italicizeOn = !italicizeOn;
      if (italicizeOn){
        italicize.setIcon(italicizeDownIcon);
      }
      else{
        italicize.setIcon(italicizeIcon);
      }
    }
    else if (e.getActionCommand().equals(UNDERLINE_BUTTON)){
      underlineOn = !underlineOn;
      if (underlineOn){
        underline.setIcon(underlineDownIcon);
      }
      else{
        underline.setIcon(underlineIcon);
      }
    }
    else if (e.getActionCommand().equals(ENGAGE_HOST)){
      engage();
    }
    else if (e.getActionCommand().equals(DISENGAGE_HOST)){
      disengage();
    }
    else if (e.getActionCommand().equals(ENGAGE_AGENT)){
      share();
    }
    else if (e.getActionCommand().equals(DISENGAGE_AGENT)){
      unshare();
    }
  }

  private void share(){
    enqueueOp(new Runnable(){public void run(){
      messageTS.setShared(true);
      if(messageTS.isShared()){
        System.err.println("LimeChat: sharing successful");
        agentEngage.setEnabled(false);
        agentDisengage.setEnabled(true);
      }
      else{
        System.err.println("LimeChat: sharing failed!!");
      }
    }});
  }

  private void unshare(){
    enqueueOp(new Runnable(){public void run(){
      messageTS.setShared(false);
      if(!messageTS.isShared()){
        System.err.println("LimeChat: unsharing successful");
        agentEngage.setEnabled(true);
        agentDisengage.setEnabled(false);
        if(!LimeServer.getServer().isEngaged()){
            hostDisengage.setEnabled(false);
            hostEngage.setEnabled(true);
        }
      }
      else{
        System.err.println("LimeChat: unsharing failed!!");
      }
    }});
  }

  private void engage(){
    enqueueOp(new Runnable(){public void run(){
      if(LimeServer.getServer().engage()){
        System.err.println("LimeChat: engagement successful");
        hostEngage.setEnabled(false);
        hostDisengage.setEnabled(true);
      }
      else{
        System.err.println("LimeChat: engagement failed!!");
      }
    }});
  }
  
  private void disengage(){
    enqueueOp(new Runnable(){public void run(){
        System.out.println(LimeServer.getServer().isEngaged());
      if(LimeServer.getServer().disengage()){
        System.err.println("LimeChat: disengagement successful");
        hostDisengage.setEnabled(false);
        hostEngage.setEnabled(true);
      }
      else{
        System.err.println("LimeChat: disengagement failed");
      }
    }});
  }

  /**
   * Creates a LChatMessage based on the text currently in the
   * input box and then passes it to the agent so that it can be
   * send to the other clients
   */
  private void sendMessage (){
    // Create the new message
    LChatMessage m = new LChatMessage(userid, input.getText(), boldOn,
                                      italicizeOn, underlineOn, null);
    final ITuple temp = new Tuple().addActual(m.getID()).addActual(m);
    enqueueOp(new Runnable(){ public void run(){
      IField[] fields = temp.getFields();
      ITuple template = new Tuple();
      template.add(fields[0]).addFormal(LChatMessage.class);
      try{
        messageTS.inp(new HostLocation(new LimeServerID(InetAddress.getLocalHost())),
                      new AgentLocation(LChat.this.getMgr().getID()),
                      template);
        messageTS.out(temp);
      }
      catch(LimeException le){
        le.printStackTrace();
      }
      catch(UnknownHostException uhe){
        uhe.printStackTrace();
      }
    }});
    
    // Clear the input window
    input.setText("");
  }

  public void processEvent(ITuple t){
    IField[] ifields = t.getFields();
    String id = (String)ifields[0].getValue();
    LChatMessage m = (LChatMessage) ifields[1].getValue();
    processMessage(m);
  }

  /**
   * Creates the main GUI for the chat program
   *
   * @return The GUI packed into a java.awt.Container
   */
  private Container makeMainGUI (){
    boldIcon = new ImageIcon(boldFilename);
    italicizeIcon = new ImageIcon(italicizeFilename);
    underlineIcon = new ImageIcon(underlineFilename);
    boldDownIcon = new ImageIcon(boldDownFilename);
    italicizeDownIcon = new ImageIcon(italicizeDownFilename);
    underlineDownIcon = new ImageIcon(underlineDownFilename);
    
    JPanel panel = new JPanel();
    
    // Setup the output area of the GUI
    output = new JEditorPane();
    output.setEditable(false);
    output.setContentType("text/html");
    outputPane = new JScrollPane(output);
    outputPane.setPreferredSize(new Dimension(320,350));
    
    // Setup the input area of the GUI
    JPanel inputPanel = new JPanel();
    JToolBar optionsToolBar = new JToolBar();
    JPanel buttonPanel = new JPanel();
    inputPanel.setLayout(new BorderLayout());
    input = new JEditorPane();
    input.addKeyListener(new KeyListener (){
        public void keyTyped (KeyEvent e){
          if (e.getKeyChar() == '\n'){
            sendMessage();
          }
        }
        public void keyPressed (KeyEvent e){
          //nothing
        }
        public void keyReleased (KeyEvent e){
          //nothing
        }
      });
    JScrollPane inputPane = new JScrollPane(input);
    inputPane.setPreferredSize(new Dimension(320,60));
    
    bold = new JButton("", boldIcon);
    bold.setToolTipText("Bold");
    bold.setActionCommand(BOLD_BUTTON);
    bold.addActionListener(this);
    italicize = new JButton("", italicizeIcon);
    italicize.setToolTipText("Italicize");
    italicize.setActionCommand(ITALICIZE_BUTTON);
    italicize.addActionListener(this);
    underline = new JButton("", underlineIcon);
    underline.setToolTipText("Underline");
    underline.setActionCommand(UNDERLINE_BUTTON);
    underline.addActionListener(this);
    optionsToolBar.add(bold);
    optionsToolBar.add(italicize);
    optionsToolBar.add(underline);
    optionsToolBar.setFloatable(false);
    
    JPanel bPanel1 = new JPanel();
    bPanel1.setLayout(new GridLayout(2,1));
    JPanel bPanel2 = new JPanel();
    bPanel2.setLayout(new GridLayout(2,1));
    JPanel bPanel3 = new JPanel();
    bPanel3.setLayout(new GridLayout(2,1));

    hostEngage = new JButton("Engage Host");
    hostEngage.setActionCommand(ENGAGE_HOST);
    hostEngage.addActionListener(this);
    hostDisengage = new JButton("Disengage Host");
    hostDisengage.setActionCommand(DISENGAGE_HOST);
    hostDisengage.addActionListener(this);
    agentEngage = new JButton("Engage Agent");
    agentEngage.setActionCommand(ENGAGE_AGENT);
    agentEngage.addActionListener(this);
    agentDisengage = new JButton("Disengage Agent");
    agentDisengage.setActionCommand(DISENGAGE_AGENT);
    agentDisengage.addActionListener(this);
    if(!LimeServer.getServer().isEngaged()){
      hostDisengage.setEnabled(false);
    }
    else{
      hostEngage.setEnabled(false);
    }
    agentDisengage.setEnabled(false);

    bPanel1.add(agentEngage);
    bPanel1.add(hostEngage);
    bPanel2.add(agentDisengage);
    bPanel2.add(hostDisengage);

    JButton sendButton = new JButton("Send");
    sendButton.setActionCommand(SEND_BUTTON);
    sendButton.addActionListener(this);

    bPanel3.add(sendButton);

    buttonPanel.add(bPanel1);
    buttonPanel.add(bPanel2);
    buttonPanel.add(bPanel3);
    
    inputPanel.add(optionsToolBar, BorderLayout.NORTH);
    inputPanel.add(inputPane, BorderLayout.CENTER);
    inputPanel.add(buttonPanel, BorderLayout.SOUTH);
    
    // Add everything to the main panel
    panel.setLayout(new BorderLayout());
    panel.add(outputPane, BorderLayout.CENTER);
    panel.add(inputPanel, BorderLayout.SOUTH);
    
    return panel;
  }
  
  // action command labels for the buttons
  private final String SEND_BUTTON = "sendbutton";
  private final String COLOR_BUTTON = "colorbutton";
  private final String BOLD_BUTTON = "boldbutton";
  private final String ITALICIZE_BUTTON = "italicizebutton";
  private final String UNDERLINE_BUTTON = "underlinebutton";
  private final String ENGAGE_HOST = "engagehost";
  private final String DISENGAGE_HOST = "disengagehost";
  private final String ENGAGE_AGENT = "engageagent";
  private final String DISENGAGE_AGENT = "disengageagent";
  
  // filenames for the icon images
  private final String boldFilename = "bold.jpg";
  private final String italicizeFilename = "italicize.jpg";
  private final String underlineFilename = "underline.jpg";
  private final String boldDownFilename = "bold_dn.jpg";
  private final String italicizeDownFilename = "italicize_dn.jpg";
  private final String underlineDownFilename = "underline_dn.jpg";
  
  // icons to put on the buttons
  private ImageIcon boldIcon, italicizeIcon, underlineIcon;
  private ImageIcon boldDownIcon, italicizeDownIcon, underlineDownIcon;
  
  // variables for the UserID GUI
  private JTextField idInput;
  private JFrame idFrame, frame;
  
  // variables for the main GUI
  private JButton bold, italicize, underline, agentEngage, agentDisengage;
  private JButton hostEngage, hostDisengage;
  private JEditorPane output, input;
  private JScrollPane outputPane;
  private boolean colorOn, boldOn, italicizeOn, underlineOn;
  
  //other variables
  private String userid = "";
  private String outputText;
  private Queue opQueue;
  private LimeTupleSpace messageTS;
  private AgentID agentID;
}

class MessageListener implements ReactionListener{
  private LChat agent = null;
  public MessageListener(LChat agent){
    this.agent = agent;
  }

  public void reactsTo(ReactionEvent e){
    agent.processEvent((ITuple)e.getEventTuple());
  }
}
