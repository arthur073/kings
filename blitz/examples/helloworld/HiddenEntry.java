package helloworld;

import net.jini.core.entry.Entry;

/**
   Anything you wish to write to a JavaSpace must implement Entry.
   Only fields which are declared public will be stored and these fields
   should not be primitives such as int or long.  See the Entry specification
   for full details.
 */
public class HiddenEntry implements Entry {
    public String theValue;

    public HiddenEntry() {
		// TODO Auto-generated constructor stub
	}

    public HiddenEntry(String aValue) {
        theValue = aValue;
    }

    public String toString() {
        return "TestEntry: " + theValue;
    }
    
    public String getString() {
    	return theValue;
    }
}
