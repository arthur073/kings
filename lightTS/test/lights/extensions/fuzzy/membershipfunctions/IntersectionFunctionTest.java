/*
 * Project : lights.extensions.fuzzy.types
 * Created on Dec 19, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

import junit.framework.*;
import lights.extensions.fuzzy.membershipfunctions.ConstantFunction;
import lights.extensions.fuzzy.membershipfunctions.IntersectionFunction;
import lights.extensions.fuzzy.membershipfunctions.TriangleFunction;

/**
 * @author balzarot
 */
public class IntersectionFunctionTest extends TestCase {

	public IntersectionFunctionTest(String name) {
		super(name);
	}

	public void testGetMembershipValue()
	{
		ConstantFunction 		cf = new ConstantFunction(0.5f);
		TriangleFunction 		tf = new TriangleFunction(5.0f,2.0f,2.0f);
		IntersectionFunction   inf = new IntersectionFunction(cf,tf);

		assertEquals("p1", 0.0f, inf.getMembershipValue( -5f), 0.01f);
		assertEquals("p2", 0.0f, inf.getMembershipValue(  3f), 0.01f);
		assertEquals("p3", 0.5f, inf.getMembershipValue(  4f), 0.01f);
		assertEquals("p4", 0.5f, inf.getMembershipValue(  5f), 0.01f);
		assertEquals("p5", 0.5f, inf.getMembershipValue(  6f), 0.01f);
		assertEquals("p6", 0.0f, inf.getMembershipValue(  7f), 0.01f);
		assertEquals("p7", 0.0f, inf.getMembershipValue(100f), 0.01f);
	}

}

