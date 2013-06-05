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
public class DrasticSum extends Operator {

	/**
	 * 
	 */
	public DrasticSum() {
		super();
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.Operator#calculateResult(float, float)
	 */
	protected float calculateResult(float x, float y) {
		if (y == 0) {
			return x;
		} else if (x == 0) {
			return y;
		} else {
			return 1;
		}
	}

}

