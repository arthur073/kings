/*
 * Created on Nov 26, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy.operators;

/**
 * @author Paolo Costa <paolo.costa@polimi.it>
 *
 * 
  */
public class AlgebraicProduct extends Operator {

	/**
	 * 
	 */
	public AlgebraicProduct() {
		super();
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.Operator#calculateResult(double, double)
	 */
	public float calculateResult(float x, float y) {
		return (x * y);
	}
}
