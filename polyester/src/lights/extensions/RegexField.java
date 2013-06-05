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

import java.util.regex.Pattern;

import lights.interfaces.IField;
import lights.interfaces.IValuedField;

/**
 * This class is an implementation of <code>IField</code> interface that
 * allows matching of string fields using regular expressions. The matching is
 * implemented by leveraging off the java.util.regex.Pattern.
 * 
 * @author costa
 * 
 * Added methods that implement IValuedField methods (due to change made to TypedField)
 * 
 * @author Babak
 */
public class RegexField extends TypedField {

	protected Pattern pattern = null;

	/**
	 * Modified this from the original version to add setting the type
	 * (in reality I wish one could also match Regex types against String types,
	 * knowing that it would break the symmetry of the "matches" relation)
	 * @param pattern
	 * @author babak
	 */
	public RegexField(String pattern) {
		this.setType(this.getClass());
		this.pattern = Pattern.compile(pattern);
	}

	/**
	 * It sets the pattern used for regular expression matching
	 * 
	 * @param pattern
	 * @return
	 */
	public RegexField setPattern(String pattern) {
		this.pattern = Pattern.compile(pattern);
		return this;
	}

	/**
	 * It sets the pattern used for regular expression matching with relative
	 * flags.
	 * 
	 * @param pattern
	 * @param flags
	 * @return
	 */
	public RegexField setPattern(String pattern, int flags) {
		this.pattern = Pattern.compile(pattern, flags);
		return this;
	}

	/**
	 * It returns the compiled representation of a regular expression used.
	 * 
	 * @return the pattern used
	 */
	public Pattern getPattern() {
		return pattern;
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
		boolean valueMatch = true;

		boolean result = f.getType().isAssignableFrom(String.class);
		if (result) {
			result = pattern.matcher((String) f.getValue()).matches();
		}
		return result;
	}

	/**
	 * Modified this from the original version:
	 * added rudimentary string representation
	 * @author babak
	 */
	public String toString() {
		//return new String("Rappresentazione di un RegexField non implementata");
		return pattern + "";
	}

	
	/*
	 * the methods below should never be called
	 */
	public Object getValue() {
		throw new IllegalArgumentException();
	}

	public boolean isFormal() {
		return true;
	}

	public IValuedField setValue(Object obj) {
		throw new IllegalArgumentException();
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
}