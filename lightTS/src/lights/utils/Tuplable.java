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
package lights.utils;

import lights.interfaces.ITuple;

/**
 * The <code>toTuple</code> method is responsible for the unrolling of the
 * object; in such a way it is possible to add an object in a tuple space
 * transparently: the tuple space will handle the conversion from object to
 * tuple by invoking that method on the object. Instead when a tuple is
 * retrieved from tuple space, it may be easily translated into its original
 * form by creating a new object and invoking \texttt{setFromTuple} passing that
 * tuple as parameter.
 * 
 * @author costa
 *  
 */
public interface Tuplable {
	ITuple toTuple();

	void setFromTuple(ITuple tuple);
}



