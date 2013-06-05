/*
 * Created on Jan 20, 2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.utils;

import junit.framework.TestCase;
import lights.Field;
import lights.Tuple;
import lights.TupleSpace;
import lights.interfaces.ITuple;
import lights.interfaces.ITupleSpace;
import lights.interfaces.TupleSpaceException;
import lights.utils.ObjectTuple;
import lights.utils.Foobar;


/**
 * @author Davide Balzarotti 
 * 
 */
public class ObjectTupleTest extends TestCase {


	private ITupleSpace ts;
	private Tuple t1;
	private Foobar o;

	/**
	 * Constructor for TupleFromClassTest
	 * 
	 * @param arg0
	 */
	public ObjectTupleTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ObjectTupleTest.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		//      Creating tuple space...
		ts = new TupleSpace();

		o = new Foobar(5, 7.2f);

	}

	final public void test() {
		try {
			ts.out(o.toTuple());

			ITuple template = new Tuple()
					.add(new Field().setValue(new Integer(5)))
					.add(new Field().setValue(new Float(7.2f)));

			ITuple tuple = ts.rdp(template);
			if (tuple != null) {
				ObjectTuple ot = (ObjectTuple)tuple;
				if (ot.getClassName().equals("lights.utils.Foobar")==false)
					fail("Not a Foobar tuple "+ot.getClassName());
				Foobar res = (Foobar)ot.getObject();
				if(res == null) fail("TupleFromClass is not able to rebuid the object");
				assertEquals(o.toString(), res.toString());
			} else {
				fail("Tuple not found");
			}
		} catch (TupleSpaceException e) {
			fail();
		}
	}
}