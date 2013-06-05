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
 * Represents a single typed field of a tuple. A field can either contain a
 * value, or be characterized just by its type. In Linda jargon, the former is
 * called an <I>actual </I> field, while the latter is called a <I>formal </I>
 * field. Tuples with formals are typically used as a <I>templates </I> to query
 * the tuple space by pattern matching. Different matching rules can be
 * implemented by redefining the <code>matches</code> method.
 * 
 * <P>
 * Formals and actuals are set by using two separate methods,
 * <code>setType</code> and <code>setActual</code>. 
 * 
 * @author <a href="mailto:picco@elet.polimi.it">Gian Pietro Picco </a>
 * @version 1.0
 */

public interface IValuedField extends IField {

	/**
	 * Returns <code>true</code> if the field is a formal, <code>false</code>
	 * otherwise.
	 */
	public boolean isFormal();

	/**
	 * Returns the value of this field.
	 */

	public Object getValue();

	/**
	 * Sets the value of this field to the object passed as a parameter. The
	 * type of the field is set automatically to the one of the object.
	 * 
	 * @param obj
	 *                 the new value of the field.
	 * @return the resulting field.
	 */
	public IValuedField setValue(Object obj);
	
	

}