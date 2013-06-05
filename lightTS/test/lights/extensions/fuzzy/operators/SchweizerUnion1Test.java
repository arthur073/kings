/*
 * Created on Dec 1, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy.operators;

import junit.framework.Assert;
import lights.extensions.fuzzy.operators.OutOfRangeException;
import lights.extensions.fuzzy.operators.SchweizerUnion1;
import lights.extensions.fuzzy.operators.YagerUnion;

/**
 * @author Paolo Costa <paolo.costa@polimi.it>
 *
 * To change the templates for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SchweizerUnion1Test extends Fixture {

	/**
	 * Constructor for SchweizerUnion1Test.
	 * @param arg0
	 */
	public SchweizerUnion1Test(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SchweizerUnion1Test.class);
	}

	final public void testOperator() {
		try {
			Assert.assertEquals(1.0F, operator.operator(0F, 0F), 0.0001);
			Assert.assertEquals(0.0F, operator.operator(1F, 1F), 0.0001);
			Assert.assertEquals(0.5344F, operator.operator(0.2F, 0.3F), 0.0001);
			Assert.assertEquals(0.0324F, operator.operator(0.6F, 0.7F), 0.0001);
		} catch (OutOfRangeException e) {
			Assert.fail();
		}

	}

	final public void testParameter() {
		try {
			new YagerUnion(-0.5F);
			Assert.fail("OutOfRangeException not generated");
		} catch (OutOfRangeException e) {
		}
	}
	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.Fixture#setUpOperator()
	 */
	public void setUpOperator() {
		try {
			operator = new SchweizerUnion1(0.5F);
		} catch (OutOfRangeException e) {
			Assert.fail();
		}
	}

}
