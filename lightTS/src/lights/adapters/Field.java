/* lighTS - An extensible and lightweight Linda-like tuplespace
 * Copyright (C) 2001, Gian Pietro Picco
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package lights.adapters;

import lights.interfaces.IField;
import lights.interfaces.IValuedField;

/**
 * Represents a single typed field of a tuple. The type of a field can be any
 * class implementing the <code>Serializable</code> interface. A field can
 * either contain a value, or be characterized just by its type. In Linda
 * jargon, the former is called an <I>actual</I> field, while the latter is
 * called a <I>formal</I> field. Tuples with formals are typically used as a
 * <I>templates</I> to query the tuple space by pattern matching.
 * <P>
 * Formals and actuals are set by using two separate methods,
 * <code>setToFormal</code> and <code>setToActual</code>. <code>null</code>
 * is a legal value for the domain on which the type of the field is defined,
 * thus fields whose value is <code>null</code> are considered to be actuals.
 * Nevertheless, the <code>null</code> value must be specified by using a
 * special method <code>setToNullActual</code>. Had the <code>null</code>
 * value been set using <code>setToActual(null)</code>, it would have been
 * impossible to determine the type of the actual. Hence, <code>null</code>
 * fields are <I>not</I> considered as formals.
 * 
 * <P>
 * <code>this.matches(f)</code> returns <code>true</code> if:
 * <OL>
 * <LI><code>this</code> and <code>f</code> are both actuals, they have the
 * <I>same type</I>, and they have the <I>same value</I>;
 * <LI>either <code>this</code> or <code>f</code> is a formal, and both
 * have the same type.
 * </OL>
 * The above definition assumes that:
 * <OL>
 * <LI>the type of <code>this</code> and <code>f</code> is the same when
 * their class object (as determined by invoking <code>getClass()</code> on
 * the field objects) is the same.
 * 
 * <LI><code>this</code> and <code>f</code> have the same value when
 * <code>this.equals(f)</code> returns <code>true</code>.
 * 
 * </OL>
 * 
 * Notably, this matching rule does not allow for matching based on subtypes.
 * Thus, for instance, if <code>this</code> and <code>f</code> both
 * implement <code>Serializable</code>, they are not considered to match
 * according to the rule above. Different matching rules, e.g., taking into
 * account subtyping, can be provided by overriding the method
 * <code>matches</code>.
 * 
 * @author <a href="mailto:picco@elet.polimi.it">Gian Pietro Picco</a>
 * @version 1.0
 */

// XXX: check if serializable is really useless
public class Field implements IValuedField {
	protected IValuedField adapter = null;

	Field(IValuedField field) {
		this.adapter = field;
	}

	/**
	 * Creates an uninitialized field.
	 */
	public Field() {
		adapter = TupleSpaceFactory.getFactory().createFieldAdapter();
	}

	/**
	 * Returns the value of this field.
	 * 
	 * @exception IllegalArgumentException
	 *                if this field is a formal. <code>isFormal()</code>
	 *                should be used to check whether this field is an actual,
	 *                before calling this method.
	 */
	public Object getValue() {
		if (isFormal())
			throw new IllegalArgumentException(
					"Impossible to get the value of a formal field.");
		return adapter.getValue();
	}

	/**
	 * Returns the type of this field.
	 */
	public Class getType() {
		return adapter.getType();
	}

	/**
	 * Sets the value of this field to the object passed as a parameter. The
	 * type of the field is set automatically to the one of the object.
	 * 
	 * @param obj
	 *            the new value of the field.
	 * @exception IllegalArgumentException
	 *                if <code>obj</code> is <code>null</code>.
	 *                <code>setToNullActual</code> should be used instead.
	 * @return the resulting field.
	 */
	public IValuedField setValue(Object obj) {
		if (obj == null)
			throw new IllegalArgumentException(
					"This method is used only for actuals whose value is not null");
		adapter.setValue(obj);
		return this;
	}

	/**
	 * Sets the value of this field to <code>null</code>. The type of the
	 * field is set to the class passed as a parameter.
	 * 
	 * @param classObj
	 *            the class to be associated to the <code>null</code> actual.
	 * @return the resulting field.
	 */
	/*
	 * public IField setToNullActual(Class classObj) {
	 * adapter.setToNullActual(classObj); return this; }
	 */
	/**
	 * Sets this field to be a formal whose type becomes the class passed as a
	 * parameter.
	 * 
	 * @param classObj
	 *            the new type of this field.
	 * @return the resulting field.
	 */
	public IField setType(Class classObj) {
		adapter.setType(classObj);
		return this;
	}

	/**
	 * Returns <code>true</code> if the field is a formal, <code>false</code>
	 * otherwise.
	 */
	public boolean isFormal() {
		return adapter.isFormal();
	}

	/**
	 * Determines the rule used for pattern matching between fields. <BR>
	 * <code>this.matches(f)</code> returns <code>true</code> if:
	 * <OL>
	 * 
	 * <LI><code>this</code> and <code>f</code> are both actuals, they have
	 * the <I>same type</I>, and they have the <I>same value</I>;
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
	 *         this field, <code>false</code> otherwise.
	 */
	public boolean matches(IField field) {
		return adapter.matches(((Field) field).adapter);
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

	@Override
	public Object clone() {
		return adapter.clone();
	}

}
