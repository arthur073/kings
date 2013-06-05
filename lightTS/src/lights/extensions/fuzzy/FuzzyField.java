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
package lights.extensions.fuzzy;

import java.io.Serializable;

import lights.extensions.TypedField;
import lights.interfaces.IField;
import lights.interfaces.IValuedField;

/**
 * @author balzarot
 */
public class FuzzyField extends TypedField {

	protected FuzzyValue _fuzzyValue = null;

	protected FuzzyType _fuzzyType = null;

	protected float _threshold = 0.6f;

	/**
	 * Creates an uninitialized field.
	 */
	public FuzzyField() {
		super();
	}

	public FuzzyField(String label) {
		super(label);
	}

	public FuzzyValue getFuzzyValue() {
		return _fuzzyValue;
	}

	public float getFuzzyThreshold() {
		return _threshold;
	}

	public FuzzyField setFuzzyValue(FuzzyValue fuzzyValue) {
		_fuzzyValue = fuzzyValue;
		return this;
	}

	public FuzzyField setFuzzyThreshold(float threshold) {
		_threshold = threshold;
		return this;
	}

	public FuzzyType getFuzzyType() {
		return _fuzzyType;
	}

	public IField setFuzzyType(FuzzyType fuzzyType) {
		_fuzzyType = fuzzyType;
		return this;
	}

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

		return (fuzzyMatches(field) >= _threshold);
	}

	public float fuzzyMatches(IField field) {
		if (type.equals(field.getType()) == false)
			return 0.0f;
		if (_fuzzyValue == null)
			return 0.0f;
		if (_fuzzyType == null)
			return 0.0f;
		if (field instanceof IValuedField) {
			Serializable value = (Serializable) ((IValuedField) field)
					.getValue();
			float result = _fuzzyType.getMembershipValue(
					_fuzzyValue.getValue(), value);
			// TODO : vanno applicati i modificatori
			return result;
		}
		return 0.0f;
	}

	/** Returns a string representation of the tuple. */
	public String toString() {
		return new String("Rappresentazione di un RangeField non implementata");
	}
}