/*
 * Created on Nov 25, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy.operators;

import junit.framework.Assert;
import junit.framework.TestCase;
import lights.extensions.fuzzy.operators.IOperator;
import lights.extensions.fuzzy.operators.OutOfRangeException;

/**
 * @author Paolo Costa <paolo.costa@polimi.it>
 *
 * To change the templates for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class Fixture extends TestCase {
	IOperator operator;

	/**
	 * Constructor for Fixture.
	 * @param arg0
	 */
	public Fixture(String arg0) {
		super(arg0);
	}
	protected void setUp() throws Exception {
		setUpOperator();
		super.setUp();
	}
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public abstract void setUpOperator();

	public void testOutOfRangeException() {
		try {
			operator.operator(-1, 0);
			Assert.fail("OutOfRangeException has not been generated");
		} catch (OutOfRangeException e) {
		}
		try {
			operator.operator(0, -1);
			Assert.fail("OutOfRangeException has not been generated");
		} catch (OutOfRangeException e) {
		}
		try {
			operator.operator(1.5F, 0);
			Assert.fail("OutOfRangeException has not been generated");
		} catch (OutOfRangeException e) {
		}
		try {
			operator.operator(0, 1.5F);
			Assert.fail("OutOfRangeException has not been generated");
		} catch (OutOfRangeException e) {
		}
	}

}
