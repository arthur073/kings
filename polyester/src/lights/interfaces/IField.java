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
 * Represents a single typed field of a tuple. Usually you should refer to
 * {@link  lights.interfaces.IValuedField  IValueField} or to one of its concrete
 * implementations <Field>that support full Linda semantics (actual and formal
 * fields are allowed).
 * 
 * @author <a href="mailto:picco@elet.polimi.it">Gian Pietro Picco </a>
 * @version 1.0
 */

public interface IField extends Cloneable{

	public Object clone();
	
	/**
	 * Returns the type of this field.
	 */
	public Class<?> getType();

	/**
	 * Sets the type of this Field.
	 * 
	 * @param classObj
	 *                 the new type of this field.
	 * @return the resulting field.
	 */

	
	
	public IField setType(Class<?> classObj);

	/**
	 * Determines the rule used for pattern matching between fields. Classes
	 * implementing this interface may specify different policies for matching.
	 * 
	 * @return <code>true</code> if the field passed as a parameter matches
	 *            this field, <code>false</code> otherwise.
	 */

	public boolean matches(IField field);
	
	public boolean equals(Object o);

}