/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 18, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

import java.util.*;
import junit.framework.*;
import lights.extensions.fuzzy.membershipfunctions.ObjectListMembershipFunction;

/**
 * @author balzarot
 */
public class ObjectListFunctionTest extends TestCase {

	public ObjectListFunctionTest(String name) {
		super(name);
	}

	public void testGetMembershipValue()
	{
		ObjectListMembershipFunction olf = new ObjectListMembershipFunction();
		
		assertTrue(olf.isEmpty());
		
		Object obj1 = new Integer(5);
		Object obj2 = new String("test");
		Object obj3 = new HashMap();

		olf.addObject(obj1, 0.5f);
		olf.addObject(obj2, 0.3f);
		olf.addObject(obj3, 0.1f);

		try{
			olf.addObject(new Integer(2), 1.2f);
			fail(" value > 1 without exception ");
		}catch(Exception e){}

		try{
			olf.addObject(new Integer(2), -1.2f);
			fail(" value < 0 without exception ");
		}catch(Exception e){}
		
		assertEquals("Number of points", 3, olf.count());

		assertEquals("Object 1",	0.5f, olf.getMembershipValue(obj1), 0.01f);
		assertEquals("Object 2",	0.3f, olf.getMembershipValue(obj2), 0.01f);
		assertEquals("Object 3",	0.1f, olf.getMembershipValue(obj3), 0.01f);
		assertEquals("Bad Object",  0.0f, olf.getMembershipValue(new Float(5.6)), 0.01f);
		assertEquals("Null Object", 0.0f, olf.getMembershipValue(null), 0.01f);

		olf.addObject(obj1, 0.2f);
		assertEquals("Number of points", 3, olf.count());
		assertEquals("Object 1", 0.2f, olf.getMembershipValue(obj1), 0.01f);

	}
}

