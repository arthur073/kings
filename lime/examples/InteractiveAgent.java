import lime.util.console.*;
import lime.*;

/**
 * An interactive agent used for testing the tuple spaces.  A user interface
 * appears where the user can directly perform tuple space operations on a
 * default tuple space.  */
public class InteractiveAgent extends StationaryAgent 
  implements IConsoleProvider {
  LimeTupleSpace lts = null;
  LimeConsole c;
  
  public InteractiveAgent() throws LimeException {
    super("InteractiveAgent");
  }
  
  public void setConsole(LimeConsole c) { this.c = c; }
  public LimeConsole getConsole() { return c; }

  public void run() { 
    try { 
      lts = new LimeTupleSpace(); 
    } catch (LimeException le) { le.printStackTrace(); }
    c = new LimeConsole(getMgr().getID(), lts, this);
    while(true) {
      String s = c.performQueuedOp(); 
    }
  }
}
