/*
 * Created on Nov 25, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy.operators;

import junit.framework.Assert;
import lights.extensions.fuzzy.operators.Maximum;
import lights.extensions.fuzzy.operators.OutOfRangeException;

/**
 * @author Paolo Costa <paolo.costa@polimi.it>
 *
 * To change the templates for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MaximumTest extends Fixture {

	/**
	* Constructor for MaximumTest.
	* @param arg0
	*/
	public MaximumTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(MaximumTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.Fixture#setUpOperator()
	 */
	public void setUpOperator() {
		operator = new Maximum();
	}

	public void testOperatorEquality() {
		try {
			Assert.assertEquals(0.3, operator.operator(0.3F, 0.3F), 0.0001);
			Assert.assertEquals(0.0, operator.operator(0.0F, 0.0F), 0.0001);
			Assert.assertEquals(1.0, operator.operator(1.0F, 1.0F), 0.0001);
		} catch (OutOfRangeException e) {
			Assert.fail();
		}
	}
	public void testOperatorMaximum() {
		try {
			Assert.assertEquals(0.7, operator.operator(0.3F, 0.7F), 0.0001);
		} catch (OutOfRangeException e) {
			Assert.fail();
		}
	}
}
