/*
 * Created on Dec 7, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy.operators;

import junit.framework.Assert;
import lights.extensions.fuzzy.operators.HamacherIntersection;
import lights.extensions.fuzzy.operators.OutOfRangeException;

/**
 * @author Paolo Costa <paolo.costa@polimi.it>
 *
 * To change the templates for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HamacherIntersectionTest extends Fixture {

	/**
	 * Constructor for HamacherIntersectionTest.
	 * @param arg0
	 */
	public HamacherIntersectionTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(HamacherIntersectionTest.class);
	}

	final public void testOperator() {
		try {
			Assert.assertEquals(0.0F, operator.operator(0F, 0F), 0.0001);
			Assert.assertEquals(1.0F, operator.operator(1F, 1F), 0.0001);
			Assert.assertEquals(0.0833F, operator.operator(0.2F, 0.3F), 0.0001);
		} catch (OutOfRangeException e) {
			Assert.fail();
		}
	}

	final public void testParameter() {
		try {
			new HamacherIntersection(-0.5F);
			Assert.fail("OutOfRangeException not generated");
		} catch (OutOfRangeException e) {
		}
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.Fixture#setUpOperator()
	 */
	public void setUpOperator() {
		try {
			operator = new HamacherIntersection(0.5F);
		} catch (OutOfRangeException e) {
			Assert.fail();
		}
	}

}
