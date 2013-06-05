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

// TODO: sistemare equals per le tuple
package lights;

/*import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
*/
import java.util.Iterator;
import java.util.Vector;

import lights.interfaces.IField;
import lights.interfaces.ITuple;

/**
 * This is the default implementation of ITuple interface provided by LighTS.
 * 
 * Matching is determined by the <code>matches</code> method.
 * <P>
 * <code>this.matches(t)</code> returns <code>true</code> if:
 * <OL>
 * <LI><code>this</code> and <code>t</code> have the same number of fields;
 * <LI>all the fields of <code>this</code> match the corresponding field of
 * <code>t</code>.
 * </OL>
 * 
 * <P>
 * Notably, the matching rules above do not depend on the type of the tuple.
 * Thus, for instance, if <code>this</code> is of class <code>Tuple</code>,
 * and <code>t</code> is of type <code>MyTuple</code>, extending
 * <code>Tuple</code>, as long as they comply with the two rules above they
 * still match, even though their types are different.
 * 
 * <BR>
 * Different matching rules, e.g., taking into account subtyping, or allowing
 * matching between tuples with different arity, can be provided by overriding
 * the method <code>matches</code> (examples may be found in
 * {@link lights.extensions lights.extension}).
 * 
 * @author <a href="mailto:picco@elet.polimi.it">Gian Pietro Picco </a>
 * @version 1.0
 */
public class Tuple implements ITuple {
	/** Holds the tuple fields. */
	protected Vector<IField> _fields = null;

	/** Creates an uninitialized tuple. */
	public Tuple() {
		_fields = new Vector<IField>();
	}

	/**
	 * Change made to the original version:
	 * replaced casting to (Field) by a cast to (IField)
	 * to make this work with fields that extend IField
	 * but do not inherit from Field (e.g. RegexField)
	 * 
	 * @author babak
	 */
	@Override
	public Object clone() {
		Tuple tuple = new Tuple();
		for (Iterator<IField> iter = _fields.iterator(); iter.hasNext();) {
			IField field = (IField) iter.next();
			tuple.add((IField) field.clone());
		}
		
		return tuple;
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

	public int hashCode(){
		int hash = 3;
		for (int i=0; i<this.length();i++){
			IField f = this.get(i);
			hash = hash * 31 + f.hashCode(); 
		}
		return hash;
	}
	/**
	 * Removes the field at position <code>index</code>. The fields whose
	 * position is greater than <code>index</code> are shifted upwards, i.e.,
	 * their index is decreased by one.
	 * 
	 * @return the resulting tuple.
	 */
	public ITuple removeAt(int index) {
		_fields.remove(index);
		return this;
	}

	/**
	 * Returns all the fields in this tuple.
	 * 
	 * @return an array containing the fields of this tuple.
	 */
	public IField[] getFields() {
		IField[] ret = new Field[_fields.size()];
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
		if (_fields.size() == 0)
			return true;
		if (this.equals(tuple))
			return true;
		boolean matching = (_fields.size() == tuple.length());
		int i = 0;
		while (matching && i < _fields.size()) {
			matching = matching
					&& ((IField) _fields.elementAt(i)).matches(tuple.get(i));
			i++;
		}
		return matching;
	}
	
	/**
	 * Determines whether two tuples are exactly identical (stronger than "matching")
	 * Relies on Fields being "equal"
	 * @param tuple
	 * @return
	 */
	public boolean equals(Object tuple) {
		
		if (tuple instanceof Tuple){
			Tuple thetuple = (Tuple)tuple;
			boolean matching = (_fields.size() == thetuple.length());
			int i = 0;
			while (matching && i < _fields.size()) {
				matching = matching
				&& ((IField) _fields.elementAt(i)).equals(thetuple.get(i));
				i++;
			}
			return matching;
		}
		else
			return false;
		
	}

	/** Returns a string representation of the tuple. */
	public String toString() {
		String result = null;
		for (int i = 0; i < length(); i++)
			result = (result == null) ? (get(i).toString())
					: (result + ", " + get(i).toString());
		return "[" + result + "]";
	}
}