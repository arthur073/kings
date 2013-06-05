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

package lights.interfaces;

/**
 * The definition of a tuple, i.e., an ordered sequence of fields. A field
 * can either contain a value, or be characterized just by its type. In Linda
 * jargon, the former is called an <I>actual </I> field, while the latter is
 * called a <I>formal </I> field. Tuples with formals are typically used as a
 * <I>templates </I> to query the tuple space by pattern matching. Different
 * matching rules can be implemented by redefining the <code>matches</code>
 * method in <code>ITuple</code> or <code>IField</code>. Tuple fields are
 * accessed by their index, with the first element conventionally set to index
 * <code>0</code>.
 * <P>
 * This interface provides methods to manipulate the fields of a tuple in various
 * ways. All methods return the tuple resulting from the manipulation, so that
 * cascaded operations are possible, e.g.,
 * 
 * <PRE>
 * 
 * System.out.println(t.add(obj1).add(obj2).add(obj3).toString());
 * 
 * </PRE>
 * 
 * @author <a href="mailto:picco@elet.polimi.it">Gian Pietro Picco </a>
 * @version 1.0
 */

public interface ITuple extends Cloneable {

	public Object clone();
	
	/**
	 * Adds a field at the end of the tuple.
	 * 
	 * @return the resulting tuple.
	 */
	public ITuple add(IField field);

	/**
	 * Replaces the field at position <code>index</code> with the given one.
	 * 
	 * @return the resulting tuple.
	 */
	public ITuple set(IField field, int index);

	/** Returns the field at position <code>index</code>. */
	public IField get(int index);

	/**
	 * Inserts the given field at position <code>index</code>. All the fields
	 * whose position is greater than <code>index</code> are shifted
	 * downwards, i.e., their index is increased by one.
	 * 
	 * @return the resulting tuple.
	 */
	public ITuple insertAt(IField field, int index);

	/**
	 * Removes the field at position <code>index</code>. The fields whose
	 * position is greater than <code>index</code> are shifted upwards, i.e.,
	 * their index is decreased by one.
	 * 
	 * @return the resulting tuple.
	 */
	public ITuple removeAt(int index);

	/**
	 * Returns all the fields in this tuple.
	 * 
	 * @return an array containing the fields of this tuple.
	 */
	public IField[] getFields();

	/** Returns the number of fields in the tuple. */
	public int length();

	/**
	 * Determines the rule used for pattern matching between tuples. Classes
	 * implementing this interface may specify different policies for matching.
	 * 
	 * @return <code>true</code> if the tuple passed as a parameter matches
	 *            this tuple, <code>false</code> otherwise.
	 */
	public boolean matches(ITuple tuple);
	
	public boolean equals(Object tuple);
	
	public String toString();

}