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

import java.util.Vector;

import lights.interfaces.IField;
import lights.interfaces.ITuple;

/**
 * @author costa
 * 
 * TODO Insert comment
 */

// FIXME: qui vanno ricopiati tutti i metodi, non si pu? usare l'ereditariet?
public class TupleAdapter implements ITuple {
	/** Holds the tuple fields. */
	protected Vector _fields = null;

	/**
	 * Used to hold the tuple's serialized form, to speed up query operations.
	 */
	private transient byte[] serForm;

	/** Creates an uninitialized tuple. */
	public TupleAdapter() {
		_fields = new Vector();
	}

	/**
	 * Adds a field at the end of the tuple.
	 * 
	 * @return the resulting tuple.
	 */
	public ITuple add(IField field) {
		_fields.addElement(field);
		return this;
	}

	/**
	 * Replaces the field at position <code>index</code> with the given one.
	 * 
	 * @return the resulting tuple.
	 */
	public ITuple set(IField field, int index) {
		_fields.setElementAt(field, index);
		return this;
	}

	/** Returns the field at position <code>index</code>. */
	public IField get(int index) {
		return ((IField) _fields.elementAt(index));
	}

	/**
	 * Inserts the given field at position <code>index</code>. All the fields
	 * whose position is greater than <code>index</code> are shifted
	 * downwards, i.e., their index is increased by one.
	 * 
	 * @return the resulting tuple.
	 */
	public ITuple insertAt(IField field, int index) {
		_fields.insertElementAt(field, index);
		return this;
	}

	/**
	 * Removes the field at position <code>index</code>. The fields whose
	 * position is greater than <code>index</code> are shifted upwards, i.e.,
	 * their index is decreased by one.
	 * 
	 * @return the resulting tuple.
	 */
	public ITuple removeAt(int index) {
		_fields.removeElementAt(index);
		return this;
	}

	/**
	 * Returns all the fields in this tuple.
	 * 
	 * @return an array containing the fields of this tuple.
	 */
	public IField[] getFields() {
		IField[] ret = new FieldAdapter[_fields.size()];
		_fields.copyInto(ret);
		return ret;
	}

	/** Returns the number of fields in the tuple. */
	public int length() {
		return _fields.size();
	}

	/**
	 * Determines the rule used for pattern matching between tuples.
	 * <code>this.matches(t)</code> returns <code>true</code> if:
	 * <OL>
	 * 
	 * <LI><code>this</code> and <code>t</code> have the same number of
	 * fields;
	 * 
	 * <LI>all the fields of <code>this</code> match the corresponding field
	 * of <code>t</code>.
	 * </OL>
	 * 
	 * or <code>this</code> has no fields.
	 * 
	 * @return <code>true</code> if the tuple passed as a parameter matches
	 *            this tuple, <code>false</code> otherwise.
	 */
	public boolean matches(ITuple tuple) {
		boolean matching = (_fields.size() == tuple.length());
		int i = 0;
		while (matching && i < _fields.size()) {
			matching = matching
					&& ((IField) _fields.elementAt(i)).matches(tuple.get(i));
			i++;
		}
		return matching;
	}

	/** Returns a string representation of the field. */
	public String toString() {
		String result = null;
		for (int i = 0; i < length(); i++)
			result = (result == null) ? (get(i).toString())
					: (result + ", " + get(i).toString());
		return "<" + result + ">";
	}

	// TODO: scrivere documentazione
	protected ITuple getDeepCopy() {
		ITuple toWrite = new TupleAdapter();
		FieldAdapter[] fields = (FieldAdapter[]) getFields(); 
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].isFormal()) {
				toWrite.add(new FieldAdapter().setType(fields[i].getType()));
			} else {
				if (fields[i].getValue() instanceof String) {
					toWrite.add(new FieldAdapter().setValue(new String(
							((String) fields[i].getValue()))));
				} else if (fields[i].getValue() instanceof Integer) {
					toWrite.add(new FieldAdapter().setValue(new Integer(
							((Integer) fields[i].getValue()).intValue())));
				} else {
					throw new IllegalArgumentException(
							"In J2ME only String and Integer are supported");
				}
			}
		}
		
		return toWrite;
	}
}