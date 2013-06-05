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
import lights.interfaces.ITuple;
import lights.interfaces.IValuedField;
/** The definition of a tuple, i.e., an ordered sequence of typed fields. The
    type of a field can be any class implementing the
    <code>Serializable</code> interface. A field can either contain a value,
    or be characterized just by its type. In Linda jargon, the former is
    called an <I>actual</I> field, while the latter is called a <I>formal</I>
    field. Tuples with formals are typically used as a <I>templates</I> to
    query the tuple space by pattern matching. Tuple fields are accessed by
    their index, with the first element conventionally set to index
    <code>0</code>.  
    <P> The interface, defined by <code>ITuple</code> provides methods to
    manipulate the fields of a tuple in various ways. All methods return the
    tuple resulting from the manipulation, so that cascaded operations are
    possible, e.g.,
    
    <PRE>System.out.println(t.add(obj1).add(obj2).add(obj3).toString());</PRE>
    Matching is determined by the <code>matches</code> method. 
    <P><code>this.matches(t)</code> returns <code>true</code> if:
    <OL>
    <LI><code>this</code> and <code>t</code> have the same number of fields;
    <LI>all the fields of <code>this</code> match the corresponding field of
    <code>t</code>.
    </OL>
    or 
    <OL>
    <LI><code>this</code> has no fields. 
    </OL>
    This latter rule expresses the convention adopted in this package to
    consider tuples with no fields as templates that match any tuple,
    independently from the type and number of fields. This is particularly
    useful when using group operations.
    <P> Notably, the matching rules above do not depend on the type of the
    tuple. Thus, for instance, if <code>this</code> is of class
    <code>Tuple</code>, and <code>t</code> is of type <code>MyTuple</code>,
    extending <code>Tuple</code>, as long as they comply with the two rules
    above they still match, even though their types are different.
    
    <BR> Different matching rules, e.g., taking into account subtyping, or
    allowing matching between tuples with different arity, can be provided by
    overriding the method <code>matches</code>.
    
    @author <a href="mailto:picco@elet.polimi.it">Gian Pietro Picco</a>
    @version 1.0 */
public class Tuple implements ITuple {
  ITuple adapter = null;
  Tuple(ITuple adapter) {
    this.adapter = adapter;
  }
  public Tuple() {
    adapter = TupleSpaceFactory.getFactory().createTupleAdapter();
  }
  /** Creates an uninitialized tuple. */
  public ITuple add(IField field) {
    adapter.add(((Field) field).adapter);
    return this;
  }

  /** Replaces the field at position <code>index</code> with the given one.
      @return the resulting tuple. */
  public ITuple set(IField field, int index) {
    adapter.set(((Field) field).adapter, index);
    return this;
  }

  /** Replaces the field at position <code>index</code> with the given one.
      @return the resulting tuple. */
  public IField get(int index) {
    return new Field((IValuedField) adapter.get(index));
  }

  /** Inserts the given field at position <code>index</code>. All the fields
      whose position is greater than <code>index</code> are shifted downwards,
      i.e., their index is increased by one.
      @return the resulting tuple. */
  public ITuple insertAt(IField field, int index) {
    adapter.insertAt(((Field) field).adapter, index);
    return this;
  }

  /** Removes the field at position <code>index</code>. The fields whose
      position is greater than <code>index</code> are shifted upwards, i.e.,
      their index is decreased by one.
      @return the resulting tuple. */
  public ITuple removeAt(int index) {
    adapter.removeAt(index);
    return this;
  }

  /** Returns all the fields in this tuple.
       @return an array containing the fields of this tuple. */
  public IField[] getFields() {
    IValuedField[] a = (IValuedField[]) adapter.getFields();
    IField[] ret = null;
    if (a != null) {
      ret = new Field[a.length];
      for (int i = 0; i < a.length; i++)
        ret[i] = new Field(a[i]);
    }
    return ret;
  }

  /** Returns the number of fields in the tuple. */
  public int length() {
    return adapter.length();
  }

  /** Determines the rule used for pattern matching between tuples.
     <code>this.matches(t)</code> returns <code>true</code> if:
    <OL>
    <LI><code>this</code> and <code>t</code> have the same number of fields;
    <LI>all the fields of <code>this</code> match the corresponding field of
    <code>t</code>.
    </OL>
    or <code>this</code> has no fields.
    @return <code>true</code> if the tuple passed as a parameter matches
    this tuple, <code>false</code> otherwise. */
  public boolean matches(ITuple tuple) {
    return adapter.matches(((Tuple) tuple).adapter);
  }

  /** Returns a string representation of the field.  */
  public String toString() {
    String result = null;
    for (int i = 0; i < length(); i++)
      result =
        (result == null)
          ? (get(i).toString())
          : (result + ", " + get(i).toString());
    return "<" + result + ">";
  }
  
	@Override
	public Object clone() {
		return adapter.clone();
	}

}
