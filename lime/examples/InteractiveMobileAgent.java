import lime.util.console.*;
import lime.mobileagent.mucode.*;
import lime.*;
import lights.interfaces.*;
import lights.adapters.*;
import mucode.*;

/**
 * An interactive agent used for testing the tuple spaces.  A user interface
 * appears where the user can directly perform tuple space operations on a
 * default tuple space.  */
public class InteractiveMobileAgent extends MobileAgent
  implements IConsoleProvider {
  
  LimeTupleSpace lts = null;
  transient private LimeConsole c;
  boolean started = false;

  public InteractiveMobileAgent() throws LimeException {
    super();
  }
  
  public void setConsole(LimeConsole c) { this.c = c; }
  public LimeConsole getConsole() { return c; }

  public void doRun() {
    try { 
      if (!started) {
        lts = new LimeTupleSpace();
        started = true;
      }
      c = new LimeConsole(getMgr().getID(), lts, this);
      lts.print();
      while(true) {
        String dest = c.performQueuedOp(); 
        if (dest != null) {
          c.quit();
	  //          migrate(dest, MuServer.FULL, null, false);
          migrate(dest);
        }
      }
    } catch (Exception e) { e.printStackTrace(); }
  }
}
