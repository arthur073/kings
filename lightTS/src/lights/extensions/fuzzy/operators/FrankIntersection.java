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
public class FrankIntersection extends OperatorWithParameter {

	/**
	 * @param parameter
	 * @throws OutOfRangeException
	 */
	public FrankIntersection(float parameter) throws OutOfRangeException {
		super(parameter);
		if (parameter < 0 || Float.compare(p, 1) == 0) {
			throw new OutOfRangeException("FrankIntersection: Parameter must be grater than zero");
		}
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.OperatorWithParameter#calculateResult(float, float)
	 */
	protected float calculateResult(float x, float y) {
		return (float)
			(Math
				.log(
					1
						+ ((Math.pow(p, x) - 1) * (Math.pow(p, y) - 1))
							/ (p - 1))
				/ Math.log(p));
	}
}
