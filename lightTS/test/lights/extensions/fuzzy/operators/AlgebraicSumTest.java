/*
 * Created on Nov 27, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy.operators;

import junit.framework.Assert;
import lights.extensions.fuzzy.operators.AlgebraicSum;
import lights.extensions.fuzzy.operators.OutOfRangeException;

/**
 * @author Paolo Costa <paolo.costa@polimi.it>
 *
 * To change the templates for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AlgebraicSumTest extends Fixture {

	/**
	 * Constructor for AlgebraicSumTest.
	 * @param arg0
	 */
	public AlgebraicSumTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AlgebraicSumTest.class);
	}

	final public void testOperator() {
		try {
			Assert.assertEquals(1.0F, operator.operator(1, 1), 0.0001F);
			Assert.assertEquals(0.0, operator.operator(0, 0), 0.0001F);
			Assert.assertEquals(0.44, operator.operator(0.2F, 0.3F), 0.0001F);
		} catch (OutOfRangeException e) {
			Assert.fail();
		}
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.Fixture#setUpOperator()
	 */
	public void setUpOperator() {
		operator = new AlgebraicSum();
	}

}
