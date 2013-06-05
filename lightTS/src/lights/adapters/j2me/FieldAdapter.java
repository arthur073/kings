/*
 * Copyright (C) 2004 costa

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package lights.adapters.j2me;

import lights.interfaces.IField;
import lights.interfaces.IValuedField;

/**
 * @author costa
 * 
 * TODO Insert comment
 */
public class FieldAdapter implements IValuedField {
	protected Class type = null;

	protected Object value = null;

	protected Object metaField = null;

	protected boolean formal = true;

	/**
	 * Creates an uninitialized field.
	 */
	public FieldAdapter() {
		super();
	}

	/**
	 * Returns the value of this field.
	 * 
	 * @exception IllegalArgumentException
	 *                     if this field is a formal. <code>isFormal()</code>
	 *                     should be used to check whether this field is an actual,
	 *                     before calling this method.
	 */
	public Object getValue() {
		if (isFormal())
			throw new IllegalArgumentException(
					"Impossible to get the value of a formal field.");
		return value;
	}

	/**
	 * Returns the type of this field.
	 */
	public Class getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lights.IField#setType(java.lang.Class)
	 */
	public IField setType(Class classObj) {
		if (classObj.equals(new String().getClass())
				|| classObj.equals(new Integer(0).getClass())) {
			type = classObj;
			value = null;
			formal = true;
			return this;
		} else {
			throw new IllegalArgumentException(
					"In J2ME only String and Integer are supported");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lights.IValuedField#setValue(java.lang.Object)
	 */
	public IValuedField setValue(Object value) {
		if (value.getClass().equals(new String().getClass())
				|| value.getClass().equals(new Integer(0).getClass())) {
			this.value = value;
			this.type = value.getClass();
			formal = false;
			return this;
		} else {
			throw new IllegalArgumentException(
					"In J2ME only String and Integer are supported");
		}
	}

	/**
	 * Returns <code>true</code> if the field is a formal, <code>false</code>
	 * otherwise.
	 */
	public boolean isFormal() {
		return formal;
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
		} else if (formal || (field instanceof IValuedField) == false)
			// i.e. either "this" or field is formal
			return this.type.equals(field.getType());
		else
			// i.e. both of them are actual
			return this.type.equals(field.getType())
					&& this.value.equals(((IValuedField) field).getValue());
	}

	/** Returns a string representation of the tuple. */
	public String toString() {
		String result = null;
		if (isFormal())
			result = "[type = " + getType().getName() + "]";
		else
			result = (getValue() == null) ? "null" : getValue().toString();
		return result;
	}
}