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

import java.io.Serializable;

import lights.interfaces.IField;
import lights.interfaces.IValuedField;
import lights.utils.SerializeTuple;

/**
 * This a convenience abstract class that serves the only purpose of
 * implementing the <code>IField</code> interface.
 * 
 * @author costa
 * 
 * No longer "implements" IField (ILabelledField is now an extension of IField)
 * @author babak 
 * 
 */
public abstract class TypedField implements ILabelledField, Serializable {

	protected Class<?> type = null;
	protected String label;
	
	public TypedField() {
		super();
	}

	public TypedField(String label) {
		super();
		this.label = label;
	}
	
	/**
	 * static method to determine if two classes "match" one another 
	 * @param c1
	 * @param c2
	 * @return
	 */
	protected static boolean classcompatible (Class<?> c1, Class<?> c2){
		if(c1 == null || c2 ==null)
			return false;
		if (c1.isAssignableFrom(c2) || c2.isAssignableFrom(c1))
			return true;
		else 
			return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see lights.IField#matches(lights.IField)
	 */
	public abstract boolean matches(IField field);

	/*
	 * (non-Javadoc)
	 * 
	 * @see lights.IField#getType()
	 */
	public Class<?> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lights.IField#setType(java.lang.Class)
	 */
	public IField setType(Class<?> classObj) {
		if (classObj == null) {
			throw new IllegalArgumentException(
					"Type must refer to a non-null object");
		}

		type = classObj;
		return this;
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

	@Override
	public Object clone()  {
		return SerializeTuple.getDeepCopy(this);
	}

}