/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 18, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

import junit.framework.*;
import lights.extensions.fuzzy.membershipfunctions.SFunction;

/**
 * @author balzarot
 */
public class SFunctionTest extends TestCase {

	public SFunctionTest(String name) {
		super(name);
	}

	public void testConstructors() {
		SFunction sf = new SFunction(10,8);
		assertEquals("Center", 10f, sf.getCenter(), 0.01f);
		assertEquals("Delta",   8f, sf.getDelta(),  0.01f);

		try{
			sf = new SFunction(10,-1);
			fail(" (10,-1) without exception ");
		}catch(Exception e){}

		try{
			sf = new SFunction(2,0);
			fail(" (2,0) without exception ");
		}catch(Exception e){}

	}

	public void testGetMembershipValue()
	{
		SFunction sf = new SFunction(10.0f, 5.0f);
		assertEquals("p1", 0.00f, sf.getMembershipValue( 1f), 0.01f);
		assertEquals("p2", 1.00f, sf.getMembershipValue(15f), 0.01f);
		assertEquals("p3", 0.50f, sf.getMembershipValue(10f), 0.01f);
		assertEquals("p4", 0.02f, sf.getMembershipValue( 6),  0.01f);
		assertEquals("p5", 0.68f, sf.getMembershipValue(11f), 0.01f);
	}

}
