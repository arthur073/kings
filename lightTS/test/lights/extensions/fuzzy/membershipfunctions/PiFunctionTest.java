/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 18, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

import junit.framework.*;
import lights.extensions.fuzzy.membershipfunctions.PiFunction;

/**
 * @author balzarot
 */
public class PiFunctionTest extends TestCase {

	public PiFunctionTest(String name) {
		super(name);
	}

	public void testConstructors() {
		PiFunction pif = new PiFunction(10,8);
		assertEquals("Center", 10f, pif.getCenter(), 0.01f);
		assertEquals("Delta",   8f, pif.getDelta(),  0.01f);

		try{
			pif = new PiFunction(10,-1);
			fail(" (10,-1) without exception ");
		}catch(Exception e){}

		try{
			pif = new PiFunction(2,0);
			fail(" (2,0) without exception ");
		}catch(Exception e){}

	}

	public void testGetMembershipValue()
	{
		PiFunction pif = new PiFunction(10.0f, 5.0f);
		assertEquals("p1", 0.00f, pif.getMembershipValue( 1f), 0.01f);
		assertEquals("p2", 0.00f, pif.getMembershipValue(15f), 0.01f);
		assertEquals("p3", 1.00f, pif.getMembershipValue(10f), 0.01f);
		assertEquals("p4", 0.68f, pif.getMembershipValue(12f), 0.01f);
		assertEquals("p5", 0.68f, pif.getMembershipValue( 8f), 0.01f);
	}
}

