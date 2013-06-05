/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 18, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

import junit.framework.*;
import lights.extensions.fuzzy.membershipfunctions.ZFunction;

/**
 * @author balzarot
 */
public class ZFunctionTest extends TestCase {

	public ZFunctionTest(String name) {
		super(name);
	}

	public void testConstructors() {
		ZFunction zf = new ZFunction(10,8);
		assertEquals("Center", 10f, zf.getCenter(), 0.01f);
		assertEquals("Delta",   8f, zf.getDelta(),  0.01f);

		try{
			zf = new ZFunction(10,-1);
			fail(" (10,-1) without exception ");
		}catch(Exception e){}

		try{
			zf = new ZFunction(2,0);
			fail(" (2,0) without exception ");
		}catch(Exception e){}

	}

	public void testGetMembershipValue()
	{
		ZFunction zf = new ZFunction(10.0f, 5.0f);
		assertEquals("p1", 1.00f, zf.getMembershipValue( 1f), 0.01f);
		assertEquals("p2", 0.00f, zf.getMembershipValue(15f), 0.01f);
		assertEquals("p3", 0.50f, zf.getMembershipValue(10f), 0.01f);
		assertEquals("p4", 0.98f, zf.getMembershipValue( 6f), 0.01f);
		assertEquals("p5", 0.32f, zf.getMembershipValue(11f), 0.01f);
	}

}
