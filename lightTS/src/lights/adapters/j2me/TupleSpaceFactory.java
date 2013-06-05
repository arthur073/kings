/*
 * Copyright (C) 2004 costa

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package lights.adapters.j2me;

import lights.interfaces.ITuple;
import lights.interfaces.ITupleSpace;
import lights.interfaces.IValuedField;

/**
 * @author costa
 * 
 * TODO Insert comment
 */
public class TupleSpaceFactory extends lights.adapters.TupleSpaceFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see lights.adapters.TupleSpaceFactory#createFieldAdapter()
	 */
	public IValuedField createFieldAdapter() {
		return new FieldAdapter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lights.adapters.TupleSpaceFactory#createTupleAdapter()
	 */
	public ITuple createTupleAdapter() {
		return new TupleAdapter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lights.adapters.TupleSpaceFactory#createTupleSpaceAdapter(java.lang.String)
	 */
	public ITupleSpace createTupleSpaceAdapter(String name) {
		return new TupleSpaceAdapter();
	}

}