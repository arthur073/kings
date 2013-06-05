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

import lights.Tuple;
import lights.interfaces.IField;

/**
 * @author costa
 * 
 * TODO Insert comment
 */
public class LabelledTuple extends Tuple implements ILabelledTuple {

	/**
	 *  
	 */
	public LabelledTuple() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lights.extensions.ILabelledTuple#get(java.lang.String)
	 */
	public IField get(String label) {
		for (int i = 0; i < _fields.size(); i++) {
			if (_fields.get(i) instanceof ILabelledField
					&& label.equals(((ILabelledField) _fields.get(i)).getLabel())) {
				return (IField) _fields.get(i);
			}
		}
		return null;
	}

	public int getFieldPosition(String label) {
		for (int i = 0; i < _fields.size(); i++) {
			if (_fields.get(i) instanceof ILabelledField
					&& label.equals(((ILabelledField) _fields.get(i)).getLabel())) {
				return i;
			}
		}
		return -1;
	}

}