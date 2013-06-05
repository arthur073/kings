/*
 * Created on Dec 7, 2003
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
public class DombiUnion extends OperatorWithParameter {

	/**
	 * @param parameter
	 * @throws OutOfRangeException
	 */
	public DombiUnion(float parameter) throws OutOfRangeException {
		super(parameter);
		if (parameter <= 0) {
					throw new OutOfRangeException("DombiUnion: Parameter must be grater than zero");
				}
			}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.OperatorWithParameter#calculateResult(float, float)
	 */
	protected float calculateResult(float x, float y) {
				return (float) (1 - (1 / (1 + Math.pow( (x * p) / (1 - x) + (y * p) / (1 - y), 1/p))));
	}

}
