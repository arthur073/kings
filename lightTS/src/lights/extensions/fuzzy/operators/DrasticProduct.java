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
public class DrasticProduct extends Operator {

	/**
	 * 
	 */
	public DrasticProduct() {
		super();
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.Operator#calculateResult(float, float)
	 */
	protected float calculateResult(float x, float y) {
		if (y == 1) {
			return x;
		} else if (x == 1) {
			return y;
		} else {
			return 0;
		}
	}

}
