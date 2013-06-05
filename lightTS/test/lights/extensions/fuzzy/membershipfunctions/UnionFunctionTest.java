/*
 * Project : lights.extensions.fuzzy.types
 * Created on Dec 19, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

import junit.framework.*;
import lights.extensions.fuzzy.membershipfunctions.ConstantFunction;
import lights.extensions.fuzzy.membershipfunctions.TriangleFunction;
import lights.extensions.fuzzy.membershipfunctions.UnionFunction;

/**
 * @author balzarot
 */
public class UnionFunctionTest extends TestCase {

	public UnionFunctionTest(String name) {
		super(name);
	}

	public void testGetMembershipValue()
	{
		ConstantFunction cf = new ConstantFunction(0.5f);
		TriangleFunction tf = new TriangleFunction(5.0f,2.0f,2.0f);
		UnionFunction    uf = new UnionFunction(cf,tf);

		assertEquals("p1", 0.5f, uf.getMembershipValue( -5f), 0.01f);
		assertEquals("p2", 0.5f, uf.getMembershipValue(  4f), 0.01f);
		assertEquals("p3", 1.0f, uf.getMembershipValue(  5f), 0.01f);
		assertEquals("p4", 0.5f, uf.getMembershipValue(  6f), 0.01f);
		assertEquals("p5", 0.5f, uf.getMembershipValue(100f), 0.01f);
	}

}

