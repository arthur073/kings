import lime.util.console.*;
import lime.*;
import lights.adapters.*;
import lights.interfaces.*;
import java.io.*;

/**
This is an extremely simple Lime application which demonstrates how to launch
Lime from within an application.  To run this application:

java SimpleLime one_word_message [lime args]

The one word message (a single word) will be put in a tuple and written to the
tuple space, the lime args are optional and can be any of the standard Lime
arguments.  When the tuple is successfully written, a message will be printed
to standard out.

Because there is currently no way to shut down a LimeServer gracefully, you
will need to press '<ctrl> C' to terminate this application.

Sample output:
>java SimpleLime Hi -mcast off
Lime:Factory set to lights.adapters.builtin.TupleSpaceFactory
Lime:Lime server murphy:1973 activated
Lime:Listening for agents
Lime:Agent SimpleLime loaded and started.
I wrote the tuple: <Hi>
Lime:Shutting down Lime server...
Lime:Done.



*/
public class SimpleLime extends StationaryAgent {
  static final int NUMLOCALPARAMETERS = 1;
  String msg = null;
  public SimpleLime (String msg) {
    this.msg = msg;
  }
  public static void main (String[] args) {


    // must be at least one argument 
    if (args.length == 0) {
      System.out.println("Usage: java SimpleLime one_word_message [lime args]\n");
      System.exit(1);
    }

    // Pass Lime arguments (if any) through the Launcher and launch the
    // LimeServer. In this case, NUMLOCALPARAMETERS is the index of the first
    // Lime parameter (as opposed to the index of the application parameter)
    new lime.util.Launcher().launch(args,NUMLOCALPARAMETERS);

    
    // load a SimpleLime, passing the first command line argument as the only
    // paramter
    try{
      LimeServer.getServer().loadAgent("SimpleLime", new String[]{args[0]});
    } catch (LimeException le){ 
      System.out.println("Trouble Loading an agent");
      le.printStackTrace(); 
    }
  }

  public void run (){ 
    LimeTupleSpace lts = null;
    ITuple myTuple = new Tuple().addActual(msg);
    // create the new tuple space (default name)
    try{
      lts = new LimeTupleSpace();
      lts.out(myTuple);
    } catch(LimeException le) {
      System.out.println("Trouble creating tuple space and writing to it");
      le.printStackTrace();
      System.exit(1);
    }
    System.out.println("I wrote the tuple: "+myTuple);
    
    // shut down Lime gracefully.  Does nothing now, but shutDown should be
    // implemented soon.
    LimeServer.getServer().shutDown();
  }
}
