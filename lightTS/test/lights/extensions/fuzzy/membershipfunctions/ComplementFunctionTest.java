/*
 * Project : lights.extensions.fuzzy.types
 * Created on Dec 19, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

import junit.framework.*;
import lights.extensions.fuzzy.membershipfunctions.ComplementFunction;
import lights.extensions.fuzzy.membershipfunctions.ConstantFunction;

/**
 * @author balzarot
 */
public class ComplementFunctionTest extends TestCase {

	public ComplementFunctionTest(String name) {
		super(name);
	}

	public void testGetMembershipValue()
	{
		ConstantFunction cf = new ConstantFunction(0.7f);
		ComplementFunction compf = new ComplementFunction(cf);

		assertEquals("p1", 0.3f, compf.getMembershipValue( -5f), 0.01f);
		assertEquals("p2", 0.3f, compf.getMembershipValue(  0f), 0.01f);
		assertEquals("p3", 0.3f, compf.getMembershipValue(110f), 0.01f);
	}

}

