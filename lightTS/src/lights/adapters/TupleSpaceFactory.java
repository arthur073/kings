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
import lights.interfaces.IValuedField;

public abstract class TupleSpaceFactory {
  private static String factoryName = null;
  private static TupleSpaceFactory factory = null;
  public static void setFactory(String factoryName)
    throws ClassNotFoundException {
    Class fClass = Class.forName(factoryName);
    try {
      factory = (TupleSpaceFactory) fClass.newInstance();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }
  }
  public static TupleSpaceFactory getFactory() {
    return factory;
  }
  public abstract IValuedField createFieldAdapter();
  public abstract ITuple createTupleAdapter();
  public abstract ITupleSpace createTupleSpaceAdapter(String name);
  //    public final Field createField(IField fieldAdapter) {
  //      return new Field(fieldAdapter);
  //    }
  //    public final Tuple createTuple(ITuple tupleAdapter) {
  //      return new Tuple(tupleAdapter);
  //    }
  //    public final TupleSpace createTupleSpace(ITupleSpace tsAdapter) {
  //      return new TupleSpace(tsAdapter);
  //    }
}
