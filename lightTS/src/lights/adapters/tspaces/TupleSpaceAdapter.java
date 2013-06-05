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



/** Implements the functionality described for

    <code>lights.adapters.TupleSpace</code> using TSpaces.

    

    @see lights.adapters.TupleSpace

    @see <a href="http://www.alphaworks.ibm.com/tech/tspaces">TSpaces Web page</A> 



    @author <a href="mailto:picco@elet.polimi.it">Gian Pietro Picco</a>

    @version 1.0 */



public class TupleSpaceAdapter implements ITupleSpace {

  private com.ibm.tspaces.TupleSpace impl = null;



  TupleSpaceAdapter(String name) throws lights.interfaces.TupleSpaceException { 

    try {

      if (name.equals(DEFAULT_NAME)) impl = new com.ibm.tspaces.TupleSpace(); 

      else impl = new com.ibm.tspaces.TupleSpace(name); 

    } catch(com.ibm.tspaces.TupleSpaceException e) {

      throw new lights.interfaces.TupleSpaceException(e);

    }

  }



  /** */

  public String getName() { return impl.getName(); }

  

  public void out(ITuple tuple) throws lights.interfaces.TupleSpaceException {

    try {

      impl.write(TupleAdapter.unwrap(tuple));

    } catch(com.ibm.tspaces.TupleSpaceException e) {

      throw new lights.interfaces.TupleSpaceException(e);

    }

  }



  public void outg(ITuple[] tuples) throws lights.interfaces.TupleSpaceException {

    try {

      com.ibm.tspaces.Tuple t = new com.ibm.tspaces.Tuple();

      for (int i=0; i < tuples.length; i++) 

	t.add(TupleAdapter.unwrap(tuples[i]));

      impl.multiWrite(t);

    } catch(com.ibm.tspaces.TupleSpaceException e) {

      throw new lights.interfaces.TupleSpaceException(e);

    }

  }



  public ITuple in(ITuple template) throws lights.interfaces.TupleSpaceException {

    com.ibm.tspaces.Tuple result = null;

    try {

      result = impl.waitToTake(TupleAdapter.unwrap(template));

    } catch(com.ibm.tspaces.TupleSpaceException e) {

      throw new lights.interfaces.TupleSpaceException(e);

    }

    return TupleAdapter.wrap(result);

  }

  

  public ITuple inp(ITuple template) throws lights.interfaces.TupleSpaceException {

    com.ibm.tspaces.Tuple result = null;

    try {

      result = impl.take(TupleAdapter.unwrap(template));

    } catch(com.ibm.tspaces.TupleSpaceException e) {

      throw new lights.interfaces.TupleSpaceException(e);

    }

    return TupleAdapter.wrap(result);

  }



  public ITuple[] ing(ITuple template) throws lights.interfaces.TupleSpaceException {

    com.ibm.tspaces.Tuple r = null;

    ITuple[] result = null;

    try {

      r = impl.consumingScan(TupleAdapter.unwrap(template));

      int l = r.numberOfFields();

      if (l > 0) {

	result = new ITuple[l];

	for (int i=0; i < result.length; i++) {

	  com.ibm.tspaces.Tuple aTuple =

	    (com.ibm.tspaces.Tuple) r.getField(i).getValue();

	  result[i] = TupleAdapter.wrap(aTuple);

	}

      }

    } catch(com.ibm.tspaces.TupleSpaceException e) {

      throw new lights.interfaces.TupleSpaceException(e);

    }

    return result;

  }



  public ITuple rd(ITuple template) throws lights.interfaces.TupleSpaceException {

    com.ibm.tspaces.Tuple result = null;

    try {

      result = impl.waitToRead(TupleAdapter.unwrap(template));

    } catch(com.ibm.tspaces.TupleSpaceException e) {

      throw new lights.interfaces.TupleSpaceException(e);

    }

    return TupleAdapter.wrap(result);

  }

  

  public ITuple rdp(ITuple template) throws lights.interfaces.TupleSpaceException {

    com.ibm.tspaces.Tuple result = null;

    try {

      result = impl.read(TupleAdapter.unwrap(template));

    } catch(com.ibm.tspaces.TupleSpaceException e) {

      throw new lights.interfaces.TupleSpaceException(e);

    }

    return TupleAdapter.wrap(result);

  }

  

  public ITuple[] rdg(ITuple template) throws lights.interfaces.TupleSpaceException {

    com.ibm.tspaces.Tuple r = null;

    ITuple[] result = null;

    try {

      r = impl.scan(TupleAdapter.unwrap(template));

      int l = r.numberOfFields();

      if (l > 0) {

	result = new ITuple[l];

	for (int i=0; i < result.length; i++) {

	  com.ibm.tspaces.Tuple aTuple = 

	    (com.ibm.tspaces.Tuple) r.getField(i).getValue();

	  result[i] = TupleAdapter.wrap(aTuple);

	}

      }

    } catch(com.ibm.tspaces.TupleSpaceException e) {

      throw new lights.interfaces.TupleSpaceException(e);

    }

    return result;

  }



  public int count(ITuple template) throws lights.interfaces.TupleSpaceException {

    int result = 0;

    try {

      result = impl.countN(TupleAdapter.unwrap(template));

    } catch(com.ibm.tspaces.TupleSpaceException e) {

      throw new lights.interfaces.TupleSpaceException(e);

    }

    return result;

  }

}

