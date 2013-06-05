/*
 * Created on Nov 25, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy.operators;

/**
 * 
 * @author Paolo Costa  <paolo.costa@polimi.it>
 *
 *
 */
public interface IOperator {
	/**
	 * Returns the lights.extensions.fuzzy.types value according to the operator specified
	 * @param x
	 * @param y
	 * @return
	 * @throws OutOfRangeException
	 */
	float operator(float x, float y) throws OutOfRangeException;
}
