/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 18, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

import junit.framework.*;
import lights.extensions.fuzzy.membershipfunctions.StepFunction;

/**
 * @author balzarot
 */
public class StepFunctionTest extends TestCase {

	public StepFunctionTest(String name) {
		super(name);
	}

	public void testConstructors() {
		StepFunction sf = new StepFunction(0.2f,0.8f,100f);
		assertEquals("Before", 0.2f, sf.getBeforeValue(), 0.01f);
		assertEquals("Delta",  0.8f, sf.getAfterValue(),  0.01f);
		assertEquals("X",      100f, sf.getXStep(),  0.01f);

		sf = new StepFunction(0f,0f,0f);

		try{
			sf = new StepFunction(10f,0f,0f);
			fail(" (10,0,0) without exception ");
		}catch(Exception e){}

		try{
			sf = new StepFunction(0f,5f,0f);
			fail(" (0,5,0) without exception ");
		}catch(Exception e){}

		try{
			sf = new StepFunction(-1f,0f,0f);
			fail(" (-1,0,0) without exception ");
		}catch(Exception e){}

		try{
			sf = new StepFunction(0f,-0.1f,0f);
			fail(" (0,-0.1f,0f) without exception ");
		}catch(Exception e){}

	}

	public void testGetMembershipValue()
	{
		StepFunction sf = new StepFunction(0.2f, 0.5f ,0f);
		assertEquals("p1", 0.2f, sf.getMembershipValue( -1f), 0.01f);
		assertEquals("p2", 0.5f, sf.getMembershipValue(  1f), 0.01f);
		assertEquals("p3", 0.5f, sf.getMembershipValue(  0f), 0.01f);

		sf = new StepFunction(0.5f, 0.2f ,0f);
		assertEquals("p1", 0.5f, sf.getMembershipValue( -1f), 0.01f);
		assertEquals("p2", 0.2f, sf.getMembershipValue(  1f), 0.01f);
		assertEquals("p3", 0.5f, sf.getMembershipValue(  0f), 0.01f);
	}
}

