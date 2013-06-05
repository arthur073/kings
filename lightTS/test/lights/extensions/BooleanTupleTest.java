/*
 * Created on Jan 20, 2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions;

import junit.framework.TestCase;
import lights.Field;
import lights.Tuple;
import lights.TupleSpace;
import lights.interfaces.ITuple;
import lights.interfaces.ITupleSpace;
import lights.interfaces.TupleSpaceException;
import lights.extensions.LabelledField;
import lights.extensions.RangeField;


/**
 * @author Davide Balzarotti 
 * 
 */
public class BooleanTupleTest extends TestCase {


	private ITupleSpace ts;
	private Tuple t1;

	/**
	 * Constructor for BooleanTupleTest.
	 * 
	 * @param arg0
	 */
	public BooleanTupleTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(BooleanTupleTest.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		//      Creating tuple space...
		ts = new TupleSpace();

		t1 = new Tuple();
		t1.add(new Field().setValue(new Integer(50))).add(
				new Field().setValue(new Integer(150))).add(
				new Field().setValue(new Integer(100)));

		ts.out(t1);
	}

	final public void testMatchingExpression() {
		ITuple template = new BooleanTuple();
		try{
			((BooleanTuple) template).setMatchingExpression("l2 || l1 && #1");
			fail("I was expecting an IllegalArgumentException... 1");
		}
		catch(IllegalArgumentException iae){}
	
		try{
			((BooleanTuple) template).setMatchingExpression("(l2 l1) && #1");
			fail("I was expecting an IllegalArgumentException... 2");
		}
		catch(IllegalArgumentException iae){}
	
		try{
			((BooleanTuple) template).setMatchingExpression("(l2 || l1)) && #1");
			fail("I was expecting an IllegalArgumentException... 3");
		}
		catch(IllegalArgumentException iae){}
	}
	
	final public void testMatches() {
		try {
			LabelledField f1 = new LabelledField();
			f1.setLabel("l1");
			f1.setValue(new Integer(50));
			
			LabelledField f2 = new LabelledField();
			f2.setLabel("l2");
			f2.setValue(new Integer(400));
			
			RangeField rf = new RangeField();
			rf.setType(Integer.class);
			rf.setUpperBound(new Integer(300),true);

			ITuple template = new BooleanTuple().add(f1).add(rf).add(f2);

			((BooleanTuple) template).setMatchingExpression("l2 || (l1 && #1)");

			ITuple tuple = ts.rdp(template);
			if (tuple != null) {
				assertEquals(tuple.toString(), t1.toString());
			} else {
				fail("Tuple not found");
			}
		} catch (TupleSpaceException e) {
			fail();
		}
	}
}
