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
package lights.extensions;

import lights.Field;
import lights.interfaces.IField;
import lights.interfaces.IValuedField;

/**
 * @author costa
 * 
 * TODO Insert comment
 */
public class NotEqualField extends LabelledField {

	/**
	 *  
	 */
	public NotEqualField() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lights.IField#matches(lights.IField)
	 */
	public boolean matches(IField field) {
		if (type == null) {
			// i.e. "this" is a wildcard (any field is matched)
			return true;
		} else if (formal || (field instanceof IValuedField) == false)
			// i.e. either "this" or field is formal
			return !(this.type.equals(field.getType()));
		else
			// i.e. both of them are actual
			return !(this.type.equals(field.getType()))
					|| !(this.value.equals(((IValuedField) field).getValue()));
	}

}