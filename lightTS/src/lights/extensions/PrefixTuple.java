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
package lights.extensions;

import lights.Tuple;
import lights.interfaces.ITuple;

public class PrefixTuple extends LabelledTuple{

  public boolean matches(ITuple tuple) {
   boolean matching = true;
    int i = 0;
    while (matching && i < length()) {
      matching = matching && get(i).matches(tuple.get(i));
      i++;
    }
    return matching;
  }

}
