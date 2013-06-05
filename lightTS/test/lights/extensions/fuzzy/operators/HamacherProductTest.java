/*
 * Created on Nov 26, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy.operators;

import junit.framework.Assert;
import lights.extensions.fuzzy.operators.HamacherProduct;
import lights.extensions.fuzzy.operators.OutOfRangeException;

/**
 * @author Paolo Costa <paolo.costa@polimi.it>
 *
 * To change the templates for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HamacherProductTest extends Fixture {

	/**
	 * Constructor for HamacherProductTest.
	 * @param arg0
	 */
	public HamacherProductTest(String arg0) {
		super(arg0);
	}
	public void testOperatorDividePerZero() {
		try {
			Assert.assertEquals(0, operator.operator(0, 0), 0.0001);
			Assert.fail();
		} catch (OutOfRangeException e) {
		}
	}

	final public void testOperator() {
		try {
			Assert.assertEquals(0.1538, operator.operator(0.2F, 0.4F), 0.0001);
			Assert.assertEquals(1, operator.operator(1, 1), 0.0001);
		} catch (OutOfRangeException e) {
			Assert.fail();
		}
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.Fixture#setUpOperator()
	 */
	public void setUpOperator() {
		operator = new HamacherProduct();
	}
	public static void main(String[] args) {
		junit.textui.TestRunner.run(HamacherProductTest.class);
	}
}
