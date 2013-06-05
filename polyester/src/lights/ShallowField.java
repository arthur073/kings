/*
 * Copyright (C) 2006 costa

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
package lights;

import java.io.Serializable;

import lights.interfaces.IValuedField;

public class ShallowField extends Field {

	@Override
	public Object clone() {
return this;
	}

	public IValuedField setValue(Object value) {
		if (value == null) {
			throw new IllegalArgumentException(
					"Value must refer to a non-null object");
		}		
		this.value =  value;
		this.type = value.getClass();
		formal = false;
		return this;
	}
	
}
