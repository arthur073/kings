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

import lights.interfaces.IField;

/**
 * This interface is implemented by all fields providing meta information
 * 
 * @author costa
 * 
 * Added "extends IField": one shouldn't need to "implement" both interfaces
 * @author babak
 */
public interface ILabelledField extends IField {

	/**
	 * Associates a label to this field
	 * 
	 * @param label
	 * @return itself
	 */
	IField setLabel(String label);

	/**
	 * 
	 * @return the metainformation associate to this field if any,
	 *            <code>null</code> otherwise
	 */
	String getLabel();
}