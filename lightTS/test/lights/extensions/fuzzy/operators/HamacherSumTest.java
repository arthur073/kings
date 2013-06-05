/*
 * Created on Nov 26, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy.operators;

import junit.framework.Assert;
import lights.extensions.fuzzy.operators.HamacherSum;
import lights.extensions.fuzzy.operators.OutOfRangeException;

/**
 * @author Paolo Costa <paolo.costa@polimi.it>
 *
 * To change the templates for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HamacherSumTest extends Fixture {

	/**
	 * Constructor for HamacherSumTest.
	 * @param arg0
	 */
	public HamacherSumTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(HamacherSumTest.class);
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.Fixture#setUpOperator()
	 */
	public void setUpOperator() {
		operator = new HamacherSum();
	}

	public void testDividePerZero() {
		try {
			operator.operator(1, 1);
			Assert.fail("Out of range exception has not been generated");
		} catch (OutOfRangeException e) {
		}
	}
	public void testOperator() {
		try {
			Assert.assertEquals(0.4042, operator.operator(0.2F, 0.3F), 0.0001);
		} catch (OutOfRangeException e) {
			Assert.fail("OutOfRangeException has been generated");
		}
	}
}