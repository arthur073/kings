/*
 * lighTS - An extensible and lightweight Linda-like tuplespace Copyright (C)
 * 2001, Gian Pietro Picco
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package lights.extensions;

import lights.interfaces.IField;
import lights.interfaces.IValuedField;

/**
 * The class <code>RangeField</code> provides methods for specifying the value
 * range's lower and upper bounds, and whether they are included in the range.
 * Bounds can be represented by any object implementing the
 * <code>Comparable</code> interface. <code>RangeField</code> extends
 * </code> lights.extensions.TypedField</code> by simply adding additional
 * attributes holding information about bound values, and by redefining <code>
 * matches</code> to verify that the field being compared against falls in the
 * requested range.
 * 
 * <code>this.matches(f)</code> returns <code>true</code> if:
 * <OL>
 * <LI><code>this</code> and <code>f</code> are both actuals, they have the
 * <I>same type </I>, and <code>f.getValue()</code> is lesser (or equal if
 * upper bound has been included in the comparison) than upper bound (if one)
 * and is greater (or equal if lower bound has been included in the comparison)
 * than lower bound (if one)
 * <LI>either <code>f</code> is a formal, and both have the same type.
 * </OL>
 * 
 * If you set both a lower and upper bound, you must care that upper bound is
 * strictly greater than upper bound otherwise an <code>
 * IllegalArgumentException</code> is thrown.
 * 
 * @author costa
 * 
 * Added unimplemented methods inherited from changes to TypedField
 * 
 * @author babak
 *  
 */
public class RangeField extends TypedField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Comparable low, up;

	private boolean lowinc = false;

	private boolean upinc = false;

	/**
	 * Creates an uninitialized field.
	 */
	public RangeField() {
		super();
	}

	/**
	 * Set the range's lower bound. If <code>null</code>, only the upper
	 * bound is considered.
	 * 
	 * @param low
	 *                 the lower bound
	 * @param included
	 *                 if <code>true</code>, the lower bound is included in the
	 *                 comparison
	 * @return
	 */
	public RangeField setLowerBound(Comparable low, boolean included) {
		if (up != null && up.compareTo(low) <= 0) {
			throw new IllegalArgumentException(
					"Lower bound must be strictly lesser than Upper bound");
		} else {
			this.low = low;
			lowinc = included;
			return this;
		}
	}

	/**
	 * Set the range's upper bound. If <code>null</code>, only the lower
	 * bound is considered.
	 * 
	 * @param up
	 *                 the upper bound
	 * @param included
	 *                 if <code>true</code>, the upper bound is included in the
	 *                 comparison
	 * @return
	 */
	public RangeField setUpperBound(Comparable up, boolean included) {
		if (low != null && low.compareTo(up) >= 0) {
			throw new IllegalArgumentException(
					"Upper bound must be strictly greater than Upper bound");
		} else {
			this.up = up;
			upinc = included;
			return this;
		}
	}

	/**
	 * Returns the lower bound object.
	 */
	public Comparable getLowerBound() {
		return low;
	}

	/**
	 * Returns the upper bound object.
	 */
	public Comparable getUpperBound() {
		return up;
	}

	/**
	 * Returns <code>true</code> if the lower bound is included in the
	 * comparison, <code>false</code> otherwise.
	 */
	public boolean isLowerBoundIncluded() {
		return lowinc;
	}

	/**
	 * Returns <code>true</code> if the lower bound is included in the
	 * comparison, <code>false</code> otherwise.
	 */
	public boolean isUpperBoundIncluded() {
		return upinc;
	}

	/**
	 * Determines the rule used for pattern matching between fields. <BR>
	 * <code>this.matches(f)</code> returns <code>true</code> if:
	 * <OL>
	 * 
	 * <LI><code>this</code> and <code>f</code> are both actuals, they have
	 * the <I>same type </I>, and they have the <I>same value </I>;
	 * 
	 * <LI>either <code>this</code> or <code>f</code> is a formal, and both
	 * have the same type.
	 * 
	 * </OL>
	 * The above definition assumes that:
	 * 
	 * <OL>
	 * 
	 * <LI>the type of <code>this</code> and <code>f</code> is the same
	 * when their class object (as determined by invoking
	 * <code>getClass()</code> on the field objects) is the same.
	 * 
	 * <LI><code>this</code> and <code>f</code> have the same value when
	 * <code>this.equals(f)</code> returns <code>true</code>.
	 * 
	 * </OL>
	 * 
	 * @return <code>true</code> if the field passed as a parameter matches
	 *            this field, <code>false</code> otherwise.
	 */
	public boolean matches(IField field) {
		if (type == null) {
			throw new IllegalArgumentException(
					"Template fields' type must refer to a non-null object");
		} else if (!(field instanceof IValuedField)
				|| ((IValuedField) field).isFormal()) {
			// i.e. field is formal
			return (type.equals(field.getType()));
		} else if (type.equals(field.getType()) == false) {
			// pre-matching based on type: if types are different, matching is
			// false
			return false;
		}

		IValuedField f = (IValuedField) field;
		boolean valueMatch = false;

		Comparable fValue = (Comparable) f.getValue();
		// both extremes of the range are specified
		if (low != null && up != null)
			valueMatch = matchLow(fValue) && matchUp(fValue);
		// lower bound unspecified: match only against upper bound
		else if (up != null)
			valueMatch = matchUp(fValue);
		// upper bound unspecified: match only against lower bound
		else if (low != null)
			valueMatch = matchLow(fValue);
		// upper bound and lowerbound uspecified: every value is accepted
		else
			valueMatch = true;
		return valueMatch;
	}

	private boolean matchUp(Comparable fValue) {
		if (upinc)
			return fValue.compareTo(up) <= 0;
		else
			return fValue.compareTo(up) < 0;
	}

	private boolean matchLow(Comparable fValue) {
		if (lowinc)
			return fValue.compareTo(low) > 0;
		else
			return fValue.compareTo(low) >= 0;
	}

	/** Returns a string representation of the tuple. */
	public String toString() {
		return new String("Rappresentazione di un RangeField non implementata");
	}

	public Object getValue() {
		throw new IllegalArgumentException();
	}

	public boolean isFormal() {
		throw new IllegalArgumentException();
	}

	public IValuedField setValue(Object obj) {
		throw new IllegalArgumentException();
	}

	@Override
	public boolean equals(Object field) {
		// TODO Auto-generated method stub
		return false;
	}
}