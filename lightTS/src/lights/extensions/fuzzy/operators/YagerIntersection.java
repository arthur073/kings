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
 */
public class YagerIntersection extends OperatorWithParameter {

	/**
	 * @param parameter
	 * @throws OutOfRangeException
	 */
	public YagerIntersection(float parameter) throws OutOfRangeException {
		super(parameter);
		if (parameter <= 0) {
			throw new OutOfRangeException("Yager: Parameter must be grater than zero");
		}
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.OperatorWithParameter#calculateResult(float, float)
	 */
	protected float calculateResult(float x, float y) {
		float result;

		result =
			(float) Math.pow(
				(Math.pow(1 - x, p) + Math.pow(1 - y, p)),
				(1 / p));

		if (result < 1) {
			return (1 - result);
		} else {
			return 0;
		}
	}
}
