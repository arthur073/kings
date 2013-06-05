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
public abstract class OperatorWithParameter implements IOperator {

	protected float p;
	/**
	 * 
	 */
	public OperatorWithParameter(float parameter) throws OutOfRangeException {
		p = parameter;
	}

	/**
		 * Checks whether x and y values are between 0 and 1
		 * @param x the first lights.extensions.fuzzy.types value
		 * @param y the second lights.extensions.fuzzy.types value
		 * @return false if at least one value is out of range, true otherwise
		 */
	public boolean validInput(float x, float y) {
		if ((x < 0) || (x > 1) || (y < 0) || (y > 1)) {
			return false;
		} else {
			return true;
		}
	}

	protected abstract float calculateResult(float x, float y);

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.IOperator#operator(float, float)
	 */
	public float operator(float x, float y) throws OutOfRangeException {
		float result;

		if (validInput(x, y)) {
			result = calculateResult(x, y);
			if (Double.isNaN(result)) {
				throw new OutOfRangeException("Denom must be different from zero");
			} else {
				return result;
			}
		} else {
			throw new OutOfRangeException("Fuzzy values must be between 0 and 1");
		}
	}

}
