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

/**
 * Extension to <code>Field</code> to support labelling
 * 
 * @author costa
 *  
 */
public class LabelledField extends Field implements ILabelledField {

	protected String label;

	/**
	 * It creates an empty field
	 */
	public LabelledField() {
		super();
	}

	/**
	 * It creates an empy field with <code>label</code> as label
	 * 
	 * @param label
	 */
	public LabelledField(String label) {
		super();
		this.label = label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lights.extensions.IFieldLabel#setLabel(java.lang.String)
	 */
	public IField setLabel(String label) {
		this.label = label;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lights.extensions.IFieldLabel#getLabel()
	 */
	public String getLabel() {
		return label;
	}

}