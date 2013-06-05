package helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RMISecurityManager;

import net.jini.space.JavaSpace;

import net.jini.core.lease.Lease;

import java.util.Random;


/**
   Writer puts 5 randomly generated Entry's in the space which should
   hopefully be found by Taker.
 */
public class Writer {
	
	private int id = 0;
	
	
	public void myPrintln(String s) {
        System.out.println("Writer #"+id+": "+s);
	}
	
	public void myPrint(String s) {
        System.out.print("Writer #"+id+": "+s);
	}
	
	public Writer(int id) {
		this.id = id;
	}
	

    public void exec() throws Exception {
        // Locate a JavaSpace - we will only find those registered
        // BEFORE we were started up - doing it properly requires
        // ServiceDiscoveryManager - for examples of lookup (including
        // the use of SDM) see:
        //      http://www.dancres.org/cottage/service_lookup.html
        //
        myPrintln("Looking for JavaSpace");

        Lookup myFinder = new Lookup(JavaSpace.class);

        JavaSpace mySpace = (JavaSpace) myFinder.getService();

        myPrintln("Got space - writing Entry's");

        myPrintln("Running Automatic test - Sending random messages");
        Random myRNG = new Random();

        for (int i = 0; i < 10; i++) {
            TestEntry myEntry =
                new TestEntry(Integer.toString(myRNG.nextInt()));

            mySpace.write(myEntry, null, Lease.FOREVER);
            myPrintln("Wrote: " + myEntry);
        }
        myPrintln("Automatic test Completed");

        myPrintln("Running Manual test - Sending user input");
    	BufferedReader br = new BufferedReader(new InputStreamReader(
				System.in));
        String input = null;
        while (input == null || (input != null && !input.equals("quit"))) {
			myPrint("Please enter a message [enter 'quit' to exit] :");
			//  open up standard input
		
			try {
				input = br.readLine();
				mySpace.write(new TestEntry(input), null,
								Lease.FOREVER);
				// we also write an hidden entry
				mySpace.write(new HiddenEntry(input), null,
						Lease.FOREVER);
			} catch (IOException ioe) {
				System.out.println("IO error trying to read your message!");
				System.exit(1);
			}
		}
        
		myPrintln("End of Test");
    }

    public static void main(String args[]) {
        // We must set the RMI security manager to allow downloading of code
        // which, in this case, is the Blitz proxy amongst other things
        int StartID = 1;
        try {
            if (System.getSecurityManager() == null)
                System.setSecurityManager(new RMISecurityManager()); 
            
            Writer w = new Writer(StartID++);
            w.exec();
        } catch (Exception anE) {
            System.err.println("Whoops");
            anE.printStackTrace(System.err);
        }
    }
}
