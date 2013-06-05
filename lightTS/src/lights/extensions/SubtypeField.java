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

import lights.Field;
import lights.interfaces.IField;
import lights.interfaces.IValuedField;

public class SubtypeField extends LabelledField {

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
		} else if (formal || !(field instanceof IValuedField)
				|| ((IValuedField) field).isFormal()) {
			// i.e. either "this" or field is formal
			return type.isAssignableFrom(field.getType());
		} else {
			// i.e. both of them are actual
			IValuedField f = (IValuedField) field;
			return type.isAssignableFrom(field.getType())
					&& value.equals(f.getValue());
		}
	}

	/** Returns a string representation of the tuple. */
	public String toString() {
		return new String(
				"Rappresentazione di un SubTypeField non implementata");
	}
}