/*
 * Created on Dec 7, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy.operators;

import junit.framework.Assert;
import lights.extensions.fuzzy.operators.HamacherUnion;
import lights.extensions.fuzzy.operators.OutOfRangeException;
/**
 * @author Paolo Costa <paolo.costa@polimi.it>
 *
 * To change the templates for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HamacherUnionTest extends Fixture {

	/**
	 * Constructor for HamacherUnionTest.
	 * @param arg0
	 */
	public HamacherUnionTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(HamacherUnionTest.class);
	}

	final public void testOperator() {
		try {
			Assert.assertEquals(0.0F, operator.operator(0F, 0F), 0.0001);
			Assert.assertEquals(1.0F, operator.operator(1F, 1F), 0.0001);
			Assert.assertEquals(0.4226F, operator.operator(0.2F, 0.3F), 0.0001);
		} catch (OutOfRangeException e) {
			Assert.fail();
		}
	}

	final public void testParameter() {
		try {
			new HamacherUnion(-0.5F);
			Assert.fail("OutOfRangeException not generated");
		} catch (OutOfRangeException e) {
		}
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.Fixture#setUpOperator()
	 */
	public void setUpOperator() {
		try {
			operator = new HamacherUnion(0.5F);
		} catch (OutOfRangeException e) {
			Assert.fail();
		}
	}

}
