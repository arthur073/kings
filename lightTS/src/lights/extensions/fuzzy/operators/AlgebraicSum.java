/*
 * Created on Nov 27, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy.operators;

/**
 * @author Paolo Costa <paolo.costa@polimi.it>
 *
 * To change the templates for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AlgebraicSum extends Operator {

	/**
	 * 
	 */
	public AlgebraicSum() {
		super();
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.Operator#calculateResult(float, float)
	 */
	public float calculateResult(float x, float y) {
		return (x + y - x * y);
	}

}
