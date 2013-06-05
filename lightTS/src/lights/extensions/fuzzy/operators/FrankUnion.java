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
public class FrankUnion extends OperatorWithParameter {

	/**
	 * @param parameter
	 * @throws OutOfRangeException
	 */
	public FrankUnion(float parameter) throws OutOfRangeException {
		super(parameter);
		if (parameter < 0 || p == 1) {
			throw new OutOfRangeException("FrankUnion: Parameter must be grater than zero");
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
						+ ((Math.pow(p, 1 - x) - 1) * (Math.pow(p, 1 - y) - 1))
							/ (p - 1))
				/ Math.log(p));
	}

}
