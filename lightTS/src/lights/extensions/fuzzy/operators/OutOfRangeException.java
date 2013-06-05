/*
 * Created on Nov 25, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy.operators;

/**
 * 
 * This exception is thrown when a lights.extensions.fuzzy.types value is either greater than 1 or lesser than 0
 * @author Paolo Costa <paolo.costa@polimi.it>
 *
 * 
 */
public class OutOfRangeException extends Exception {

	/**
	 * 
	 */
	public OutOfRangeException() {
		super();
	}

	/**
	 * @param message
	 */
	public OutOfRangeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public OutOfRangeException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OutOfRangeException(String message, Throwable cause) {
		super(message, cause);
	}

}
