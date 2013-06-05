/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 18, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

import junit.framework.*;
import lights.extensions.fuzzy.membershipfunctions.ConstantFunction;

/**
 * @author balzarot
 */
public class ConstantFunctionTest extends TestCase {

	public ConstantFunctionTest(String name) {
		super(name);
	}

	public void testConstructors() {
		ConstantFunction cf = new ConstantFunction(0.8f);
		assertEquals("Value", 0.8f, cf.getValue(), 0.01f);

		try{
			cf = new ConstantFunction(10);
			fail(" (10) without exception ");
		}catch(Exception e){}

		try{
			cf = new ConstantFunction(-0.2f);
			fail(" (-0.2) without exception ");
		}catch(Exception e){}

	}

	public void testGetMembershipValue()
	{
		ConstantFunction cf = new ConstantFunction(0.7f);
		assertEquals("p1", 0.7f, cf.getMembershipValue( -5f), 0.01f);
		assertEquals("p2", 0.7f, cf.getMembershipValue(  0f), 0.01f);
		assertEquals("p3", 0.7f, cf.getMembershipValue(110f), 0.01f);
	}

}

