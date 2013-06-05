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
import lights.interfaces.ITuple;
import lights.interfaces.ITupleSpace;
import lights.interfaces.TupleSpaceException;
/** Embodies the concept of a Linda-like tuple space. The traditional
    operations provided by Linda, namely the insertion of a tuple with
    <code>out</code> and pattern matching with the blocking operations
    <code>in</code> and <code>rd</code>, are supported. In addition, the
    following features are provided: <UL>
    
    <LI>non-blocking <I>probe</I> operations <code>inp</code> and
    <code>rdp</code>;
    
    <LI><I>group</I> operations <code>outg</code>, <code>ing</code>,
    <code>rdg</code>, that allow to write and retrieve multiple tuples at
    once;
    <LI>a <code>count</code> operation returning the number of tuples matching
    a given templates;
    
    <LI>a name associated to the tuple space.
    </ul>
    The semantics of the tuple space are such that a <I>copy</I> of the
    original tuple is inserted. In other words, modifications to the tuple
    object subsequent to insertion are not reflected into the tuple object
    stored into the tuple space. This semantics is ensured by serializing and
    deserializing the tuple object being inserted, in order to obtain a deep
    copy of the tuple that is the one actually inserted in the tuple space.
    
    <P> Tuple space access can be optimized for space or speed, by specifying
    the desired policy (<code>SPACE</code> or <code>SPEED</code>) at creation
    time. In the former case, each time a tuple is retrieved it is serialized
    and deserialized to ensure the copy semantics. In the latter case, a copy
    of the serialized form is stored with the tuple at insertion time. This
    way, a copy can be retrieved by performing the deserialization only. This
    is the default policy.
    @author <a href="mailto:picco@elet.polimi.it">Gian Pietro Picco</a>
    @version 1.0 */

public class TupleSpace implements ITupleSpace {
  ITupleSpace adapter = null;
  TupleSpace(ITupleSpace adapter) {
    this.adapter = adapter;    
  }
  /** Creates a tuple space with a default name. 
      @exception TupleSpaceException if there is an exception in the
      underlying implementation.  */
  public TupleSpace() throws TupleSpaceException {
    this(DEFAULT_NAME);
  }
  /** Creates a tuple space with the name specified by the user, and
      optimization for speed. 
      @param name the tuple space name.
      @exception IllegalArgumentException if the name is <code>null</code>.  
      @exception TupleSpaceException if there is an exception in the
      underlying implementation.  */
  public TupleSpace(String name) throws TupleSpaceException {
    if (name == null)
      throw new IllegalArgumentException("Must specify a non-null name.");
    adapter = TupleSpaceFactory.getFactory().createTupleSpaceAdapter(name);
  }
  /**
   * Returns the name of the tuple space. 
   */
  public String getName() {
    return adapter.getName();
  }
  /**
   * Inserts a tuple in the tuple space. The operation is synchronous, i.e.,
   * the tuple is guaranteed to be available in the tuple space after this
   * method successfully completes execution. 
   *
   * @param tuple The tuple to be inserted.
   * @exception TupleSpaceException if an error occurs in the implementation.
   * @exception IllegalArgumentException if the tuple has no fields.  
   * */
  public void out(ITuple tuple) throws TupleSpaceException {
    if (tuple.length() == 0)
      throw new IllegalArgumentException("Tuples with no fields can be used only as templates.");
    adapter.out(((Tuple) tuple).adapter);
  }
  /**
   * Inserts multiple tuples in the tuple space. The operation is not atomic,
   * i.e., probes and blocking operations may see the effects of an insertion
   * of a tuple even before this method completes execution.
   *
   * @param tuples An array containing the tuples to be inserted.
   * @exception TupleSpaceException if an error occurs in the
   * implementation. */
  public void outg(ITuple[] tuples) throws TupleSpaceException {
    ITuple[] aTuples = new ITuple[tuples.length];
    for (int i = 0; i < tuples.length; i++)
      aTuples[i] = ((Tuple) tuples[i]).adapter;
    adapter.outg(aTuples);
  }
  /**
   * Withdraws from the tuple space a tuple matching the templates
   * specified. If no tuple is found, the caller is suspended until such a
   * tuple shows up in the tuple space. If multiple matching tuples are found,
   * the one returned is selected non-deterministically.
   *
   * @param templates the templates used for matching. 
   * @return a tuple matching the templates. 
   * @exception TupleSpaceException if an error in the implementation. */
  public ITuple in(ITuple template) throws TupleSpaceException {
    ITuple t = adapter.in(((Tuple) template).adapter);
    ITuple ret = null;
    if (t != null)
      ret = new Tuple(t);
    return ret;
  }
  /**
   * Withdraws from the tuple space a tuple matching the templates
   * specified. If no tuple is found, <code>null</code> is returned. If
   * multiple matching tuples are found, the one returned is selected
   * non-deterministically.
   *
   * @param templates the templates used for matching. 
   * @return a tuple matching the templates, or <code>null</code> if none is
   * found.
   * @exception TupleSpaceException if an error in the implementation. */
  public ITuple inp(ITuple template) throws TupleSpaceException {
    ITuple t = adapter.inp(((Tuple) template).adapter);
    ITuple ret = null;
    if (t != null)
      ret = new Tuple(t);
    return ret;
  }
  /**
   * Withdraws from the tuple space <i>all</I> the tuple matching the templates
   * specified. If no tuple is found, <code>null</code> is returned. 
   *
   * @param templates the templates used for matching. 
   * @return a tuple matching the templates, or <code>null</code> if none is
   * found.
   * @exception TupleSpaceException if an error in the implementation. */
  public ITuple[] ing(ITuple template) throws TupleSpaceException {
    ITuple[] t = adapter.ing(((Tuple) template).adapter);
    ITuple[] ret = null;
    if (t != null) {
      ret = new Tuple[t.length];
      for (int i = 0; i < t.length; i++)
        ret[i] = new Tuple(t[i]);
    }
    return ret;
  }
  /**
   * Reads from the tuple space a copy of a tuple matching the templates
   * specified. If no tuple is found, the caller is suspended until such a
   * tuple shows up in the tuple space. If multiple matching tuples are found,
   * the one returned is selected non-deterministically.
   *
   * @param templates the templates used for matching. 
   * @return a copy of a tuple matching the templates.
   * @exception TupleSpaceException if an error in the implementation. */
  public ITuple rd(ITuple template) throws TupleSpaceException {
    ITuple t = adapter.rd(((Tuple) template).adapter);
    ITuple ret = null;
    if (t != null)
      ret = new Tuple(t);
    return ret;
  }
  /**
   * Reads from the tuple space a copy of a tuple matching the templates
   * specified. If no tuple is found, <code>null</code> is returned. If
   * multiple matching tuples are found, the one returned is selected
   * non-deterministically.
   *
   * @param templates the templates used for matching. 
   * @return a copy of a tuple matching the templates, or <code>null</code> if
   * none is found.
   * @exception TupleSpaceException if an error in the implementation. */
  public ITuple rdp(ITuple template) throws TupleSpaceException {
    ITuple t = adapter.rdp(((Tuple) template).adapter);
    ITuple ret = null;
    if (t != null)
      ret = new Tuple(t);
    return ret;
  }
  /**
   * Reads from the tuple space a copy of <I>all</I> the tuples matching the
   * templates specified. If no tuple is found, <code>null</code> is returned.
   *
   * @param templates the templates used for matching. 
   * @return a copy of a tuple matching the templates, or <code>null</code> if
   * none is found.
   * @exception TupleSpaceException if an error in the implementation. */
  public ITuple[] rdg(ITuple template) throws TupleSpaceException {
    ITuple[] t = adapter.rdg(((Tuple) template).adapter);
    ITuple[] ret = null;
    if (t != null) {
      ret = new Tuple[t.length];
      for (int i = 0; i < t.length; i++)
        ret[i] = new Tuple(t[i]);
    }
    return ret;
  }
  /**
   * Returns a count of the tuples found in the tuple space that match the
   * templates.
   *
   * @param templates the templates used for matching. 
   * @return the number of tuples currently in the tuple space that match the
   * templates.
   * @exception TupleSpaceException if an error in the implementation. */
  public int count(ITuple template) throws TupleSpaceException {
    return adapter.count(((Tuple) template).adapter);
  }
  /** Returns a string representation of the tuple space. */
  public String toString() {
    return adapter.toString();
  }
}
