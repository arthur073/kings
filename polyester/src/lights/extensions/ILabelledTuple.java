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
 * This interface allows to retrive a specific field from a tuple through a
 * metadata
 * 
 * @author costa
 * 
 *  
 */
public interface ILabelledTuple {

	/**
	 * Returns the field associated with <code>label</code>. If multiple
	 * occurences of label are present, the first one is returned. If none
	 * is found, <code>null</code> is returned.
	 */
	public IField get(String label);
	
	/**
	 * Returns the position of the field associated with <code>label</code>. 
	 * If multiple occurences of label are present, the first one is returned. 
	 * If the field is not found, <code>-1</code> is returned.
	 */
	public int getFieldPosition(String label);
	
}
