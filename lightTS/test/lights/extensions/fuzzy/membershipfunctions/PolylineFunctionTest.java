/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 18, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

import junit.framework.*;
import lights.extensions.fuzzy.membershipfunctions.PolylineFunction;

/**
 * @author balzarot
 */
public class PolylineFunctionTest extends TestCase {

	public PolylineFunctionTest(String name) {
		super(name);
	}

	public void testCreation()
	{
		PolylineFunction plf = new PolylineFunction();
		assertTrue(plf.isEmpty());
		plf.addPoint(4f,0.5f);
		plf.addPoint(2f,0.3f);
		assertEquals("Number of points", 2, plf.count());
		try{
			plf.addPoint(2f,-0.3f);
			fail();
		}
		catch(IllegalArgumentException e)
		{}

		try{
			plf.addPoint(2f,5.3f);
			fail();
		}
		catch(IllegalArgumentException e)
		{}
		assertEquals("Number of points", 2, plf.count());
	}

	public void testGetMembershipValue()
	{
		PolylineFunction plf = new PolylineFunction();

		plf.addPoint(4f,0.5f);
		plf.addPoint(2f,0.3f);
		plf.addPoint(6f,0.1f);
		plf.addPoint(5f,1.0f);

		assertEquals("p1", 0.0f, plf.getMembershipValue(0.0f), 0.01f);
		assertEquals("p2", 0.3f, plf.getMembershipValue(2.0f), 0.01f);
		assertEquals("p3", 0.5f, plf.getMembershipValue(4.0f), 0.01f);
		assertEquals("p4", 1.0f, plf.getMembershipValue(5.0f), 0.01f);
		assertEquals("p5", 0.1f, plf.getMembershipValue(6.0f), 0.01f);
		assertEquals("p6", 0.0f, plf.getMembershipValue(8.0f), 0.01f);

		assertEquals("p7",  0.40f, plf.getMembershipValue(3.0f), 0.01f);
		assertEquals("p8",  0.75f, plf.getMembershipValue(4.5f), 0.01f);
		assertEquals("p9",  0.55f, plf.getMembershipValue(5.5f), 0.01f);
	}
}

