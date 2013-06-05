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


package lights.adapters.tspaces;



import java.util.Vector;

import lights.interfaces.IField;
import lights.interfaces.ITuple;
import lights.interfaces.IValuedField;
import lights.interfaces.TupleSpaceError;

import com.ibm.tspaces.Field;
import com.ibm.tspaces.Tuple;



/** Implements the functionality described for

    <code>lights.adapters.Tuple</code> using TSpaces.

    

    @see lights.adapters.Tuple

    @see <a href="http://www.alphaworks.ibm.com/tech/tspaces">TSpaces Web page</A> 



    @author <a href="mailto:picco@elet.polimi.it">Gian Pietro Picco</a>

    @version 1.0 */

public class TupleAdapter implements ITuple, java.io.Serializable {

  private Tuple impl = null;

  

  public TupleAdapter() { impl = new Tuple(); }

  public TupleAdapter(Tuple tuple) { impl = tuple; }

  

  static Tuple unwrap(ITuple tuple) { 

    return (((TupleAdapter) tuple).impl);

  }

  

  static ITuple wrap(Tuple tuple) {

    TupleAdapter ret = null;

    if (tuple!=null)

      ret = new TupleAdapter(tuple); 

    return ret;

  }

  

  public ITuple add(IField field) { 

    impl.add(FieldAdapter.unwrap((IValuedField) field)); 

    return wrap(impl);

  }

  

  public ITuple addActual(java.io.Serializable obj) { 

    try {

      impl.add(obj); 

    } catch(com.ibm.tspaces.TupleSpaceException e) {

      throw new TupleSpaceError("Exception in the underlying"+

				"tuple space engine.");

    }

    return wrap(impl);

  }

  

  public ITuple addFormal(Class classObj) { 

    try {

      impl.add(new Field(classObj)); 

    } catch(com.ibm.tspaces.TupleSpaceException e) {

      throw new TupleSpaceError("Exception in the underlying"+

				"tuple space engine.");

    }

    return wrap(impl);

  }

  

  public ITuple set(IField field, int index) { 

    try {

      impl.putField(index, FieldAdapter.unwrap((IValuedField) field)); 

    } catch(com.ibm.tspaces.TupleSpaceException e) {

      throw new TupleSpaceError("Exception in the underlying"+

				"tuple space engine.");

    }

    return wrap(impl);

  }

  

  public IField get(int index) { 

    IField result = null;

    try {  

      result = FieldAdapter.wrap(impl.getField(index)); 

    } catch(com.ibm.tspaces.TupleSpaceException e) {

      throw new TupleSpaceError("Exception in the underlying"+

				"tuple space engine.");

    }

    return result;

  }



  public ITuple insertAt(IField field, int index) { return null; }

  public ITuple removeAt(int index) { return null; }

  

  public IField[] getFields() { 

    java.util.Enumeration e = impl.fields();

    Vector v = new Vector(100,10);

    while(e.hasMoreElements())

      v.addElement(FieldAdapter.wrap((Field) e.nextElement()));

    IField[] ret = new FieldAdapter[v.size()];

    v.copyInto(ret);

    return ret;

  }



  public int length() { return impl.numberOfFields(); }



  public boolean matches(ITuple tuple) { 

    return impl.matches(unwrap(tuple)); 

  }



  public String toString() {

    return "[TSpaces: " + impl.toString() + "]";

  }

}





















