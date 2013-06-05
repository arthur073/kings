/*
 * Created on Dec 3, 2003
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
public class SchweizerUnion3 extends OperatorWithParameter {

	/**
	 * @param parameter
	 * @throws OutOfRangeException
	 */
	public SchweizerUnion3(float parameter) throws OutOfRangeException {
		super(parameter);
		if (parameter <= 0) {
			throw new OutOfRangeException("Yager: Parameter must be grater than zero");
			// TODO Cambiare tutti i nomi di questi eccezioni ;P
		}
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.operators.OperatorWithParameter#calculateResult(float, float)
	 */
	protected float calculateResult(float x, float y) {
			return (float) (Math.pow(Math.pow(x, p) + Math.pow(y, p) - Math.pow(x, p)  * Math.pow(y, p), 1/p));
	}
}
