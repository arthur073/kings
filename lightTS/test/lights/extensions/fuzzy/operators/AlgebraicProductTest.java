/*
 * Created on Nov 26, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy.operators;

import junit.framework.Assert;
import lights.extensions.fuzzy.operators.AlgebraicProduct;
import lights.extensions.fuzzy.operators.OutOfRangeException;

/**
 * @author Paolo Costa <paolo.costa@polimi.it>
 *
 * To change the templates for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AlgebraicProductTest extends Fixture {

	/**
	 * Constructor for AlgebraicProductTest.
	 * @param arg0
	 */
	public AlgebraicProductTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AlgebraicProductTest.class);
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.Fixture#setUpOperator()
	 */
	public void setUpOperator() {
		operator = new AlgebraicProduct();
	}
	final public void testOperator() {
		try {
			Assert.assertEquals(0.0, operator.operator(0, 0), 0.0001);
			Assert.assertEquals(1.0, operator.operator(1, 1), 0.0001);
			Assert.assertEquals(0.0, operator.operator(0, 1), 0.0001);
		} catch (OutOfRangeException e) {
			Assert.fail();
		}

	}
}
