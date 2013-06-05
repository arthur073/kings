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

import lights.interfaces.ITuple;
import lights.interfaces.ITupleSpace;
import lights.interfaces.IValuedField;
import lights.interfaces.TupleSpaceException;

public class TupleSpaceFactory extends lights.adapters.TupleSpaceFactory {

  public IValuedField createFieldAdapter() { 

    return new lights.adapters.tspaces.FieldAdapter(); 

  }  

  public ITuple createTupleAdapter() { 

    return new lights.adapters.tspaces.TupleAdapter(); 

  }

  

  public ITupleSpace createTupleSpaceAdapter(String name) { 

    ITupleSpace a = null;

    try {

      a = new lights.adapters.tspaces.TupleSpaceAdapter(name); 

    } catch(TupleSpaceException e) { e.printStackTrace(); }

    return a;

  }

}

