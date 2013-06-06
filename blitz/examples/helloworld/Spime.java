package helloworld;
import java.util.Set;

import com.sun.jini.reggie.UuidGenerator;

import net.jini.core.entry.Entry;
import net.jini.id.Uuid;

/**
   Anything you wish to write to a JavaSpace must implement Entry.
   Only fields which are declared public will be stored and these fields
   should not be primitives such as int or long.  See the Entry specification
   for full details.
 */
public class Spime implements Entry {
	public Uuid id;
	public String manufacturer;
	public String owner;
	public Set<Sensor> sensors;

	/* Minimal constructor => template */
    public Spime() {
    }

    public Spime(Set<Sensor> sensors) {
    	id = new UuidGenerator().generate();
    	NameGenerator gen = new NameGenerator();
    	manufacturer = gen.getName();
    	owner = gen.getName();
        this.sensors = sensors;
    }    
    
    public String toString() {
    	return "Spime details :\n\tID : " + id + "\n\tManufacturer : " + manufacturer + "\n\tOwner : " + owner;
    }
}
