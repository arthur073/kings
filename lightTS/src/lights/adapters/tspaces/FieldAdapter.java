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

import java.io.Serializable;

import lights.interfaces.IField;
import lights.interfaces.IValuedField;
import lights.interfaces.TupleSpaceError;

import com.ibm.tspaces.Field;

/**
 * Implements the functionality described for
 * 
 * <code>lights.adapters.Field</code> using TSpaces.
 * 
 * 
 * 
 * @see lights.adapters.Field
 * 
 * @see <a href="http://www.alphaworks.ibm.com/tech/tspaces">TSpaces Web page
 *         </A>
 * 
 * 
 * 
 * @author <a href="mailto:picco@elet.polimi.it">Gian Pietro Picco </a>
 * 
 * @version 1.0
 */

public class FieldAdapter implements IValuedField {

	private Field impl = null;

	FieldAdapter() {
		super();
	}

	FieldAdapter(Field field) {
		impl = field;
	}

	static Field unwrap(IValuedField field) {

		return (((FieldAdapter) field).impl);

	}

	static IValuedField wrap(Field field) {

		return new FieldAdapter(field);

	}
	
	public Object getValue() {
		return impl.getValue();
	}

	public Class getType() {
		return impl.getType();
	}

	public IValuedField setValue(Object obj) {

		try {

			if (!(obj instanceof Serializable)) {
				throw new IllegalArgumentException(
						"values of lights.adapters.Field must be Serializable.");
			}

			if (impl == null)
				impl = new Field((Serializable) obj);
			else
				impl.setValue((Serializable) obj);

		} catch (com.ibm.tspaces.TupleSpaceException e) {

			throw new TupleSpaceError("Exception in the underlying" +

			"tuple space engine.");

		}

		return this;

	}

	public IValuedField setToNullActual(Class classObj) {

		try {

			impl = new Field(classObj, null);

		} catch (com.ibm.tspaces.TupleSpaceException e) {

			throw new TupleSpaceError("Exception in the underlying" +

			"tuple space engine.");

		}

		return this;

	}

	public IField setType(Class classObj) {

		try {

			if (impl == null)
				impl = new Field(classObj);

			impl.setType(classObj);

			// TSpaces is BROKEN!!! So, we still need to do this hack.

			impl.setFormal(true);

		} catch (com.ibm.tspaces.TupleSpaceException e) {

			throw new TupleSpaceError("Exception in the underlying" +

			"tuple space engine.");

		}

		return this;

	}

	public boolean isFormal() {
		return impl.isFormal();
	}

	public boolean matches(IField field) {

		return impl.matches(unwrap((IValuedField) field));

	}

}

