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
package lights;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import lights.interfaces.IField;
import lights.interfaces.ITuple;
import lights.interfaces.IValuedField;
import lights.utils.SerializeTuple;

/**
 * Represents a single typed field of a tuple. The type of a field can be any
 * class implementing the <code>Serializable</code> interface. A field can
 * either contain a value, or be characterized just by its type. In Linda
 * jargon, the former is called an <I>actual </I> field, while the latter is
 * called a <I>formal </I> field. Tuples with formals are typically used as a
 * <I>templates </I> to query the tuple space by pattern matching.
 * <P>
 * If both {@link #setType(Class) setType} and
 * {@link #setValue(Object) setValue} are invoked, the last method called will
 * determine the nature of the field.
 * <P>
 * <code>this.matches(f)</code> returns <code>true</code> if:
 * <OL>
 * <LI><code>this</code> and <code>f</code> are both actuals, they have the
 * <I>same type </I>, and they have the <I>same value </I>;
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
 * <code>matches</code> (an extesion of <code>Field</code> provide subtype
 * matching is available in lights.extension.
 * 
 * @author <a href="mailto:picco@elet.polimi.it">Gian Pietro Picco </a>
 * @version 1.0
 */
public class Field implements IValuedField {

	protected Class type = null;

	protected Object value = null;

	protected Object metaField = null;

	protected boolean formal = true;

	/**
	 * Creates an uninitialized field.
	 */
	public Field() {
		super();
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
		return value;
	}

	/**
	 * Returns the type of this field.
	 */
	public Class getType() {
		return type;
	}

	/**
	 * Sets this field to be a formal whose type becomes the class passed as a
	 * parameter. Only non <code>null</code> parameters are accepted. If a
	 * <code>null</code> parameter is provide, an
	 * <code>IllegalArgumentException</code> is generated.
	 * 
	 * @param classObj
	 *            the new type of this field.
	 * @return the resulting field.
	 */
	public IField setType(Class classObj) {
		if (classObj == null) {
			throw new IllegalArgumentException(
					"Typemust refer to a non-null object");
		}

		this.type = classObj;
		formal = true;
		value = null;
		return this;
	}

	/**
	 * Sets the value of this field to the object passed as a parameter. The
	 * type of the field is set automatically to the one of the object. Only
	 * <code>Serializable</code> parameters are accepted. If a non
	 * <code>Serializable</code> or a <code>null</code> parameter is
	 * provide, an <code>IllegalArgumentException</code> is generated.
	 * 
	 * 
	 * @param obj
	 *            the new value of the field.
	 * @return the resulting field.
	 */
	public IValuedField setValue(Object value) {
		if (value == null) {
			throw new IllegalArgumentException(
					"Value must refer to a non-null object");
		}

		if (!(value instanceof Serializable)) {
			throw new IllegalArgumentException(
					"Values of lights.Field must be Serializable.");
		}
		this.value = (Serializable) value;
		this.type = value.getClass();
		formal = false;
		return this;
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
	 *         this field, <code>false</code> otherwise.
	 */
	public boolean matches(IField field) {
		if (type == null) {
			throw new IllegalArgumentException(
					"Template fields' type must refer to a non-null object");
		} else if (formal || (field instanceof IValuedField) == false
				|| ((IValuedField) field).isFormal())
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

	@Override
	public Object clone() {
		Field field = new Field();

		field.setType((Class) SerializeTuple.getDeepCopy(this.type));
		field.setValue(SerializeTuple.getDeepCopy((Serializable) this.value));

		return field;
	}


}