/*
 * Created on Nov 27, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy.operators;

import junit.framework.Assert;
import lights.extensions.fuzzy.operators.BoundedDifference;
import lights.extensions.fuzzy.operators.OutOfRangeException;

/**
 * @author Paolo Costa <paolo.costa@polimi.it>
 *
 * To change the templates for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BoundedDifferenceTest extends Fixture {

	/**
	 * Constructor for BoundedDifferenceTest.
	 * @param arg0
	 */
	public BoundedDifferenceTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(BoundedDifferenceTest.class);
	}

	final public void testOperator() {
		try {
			Assert.assertEquals(0.0F, operator.operator(0.0F, 0.0F), 0.0001);
			Assert.assertEquals(1.0F, operator.operator(1.0F, 1.0F), 0.0001);
			Assert.assertEquals(0.0F, operator.operator(0.2F, 0.3F), 0.0001);		
			Assert.assertEquals(0.3F, operator.operator(0.6F, 0.7F), 0.0001);		
		} catch (OutOfRangeException e) {
			Assert.fail();
		}

	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.Fixture#setUpOperator()
	 */
	public void setUpOperator() {
		operator = new BoundedDifference();
	}

}
