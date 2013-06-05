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
import lights.utils.Tuplable;
import lights.utils.ObjectTuple;
import lights.Field;

/**
 * This class is used for unit testing
 *
 * @author balzarot
 *  
 */
public class Foobar implements Tuplable {
	private int x;
	private float f;
	
	public Foobar()
	{
		this.x = 0;
		this.f = 0;
	}
	
	public Foobar(int x, float f)
	{
		this.x = x;
		this.f = f;
	}
	
	public ITuple toTuple(){
		ObjectTuple t = new ObjectTuple(Foobar.class);
		return t.add(new Field().setValue(new Integer(x)))
				.add(new Field().setValue(new Float(f)));		
	}

	public void setFromTuple(ITuple tuple){
		x = ((Integer)((Field)tuple.get(0)).getValue()).intValue();
		f = ((Float)((Field)tuple.get(1)).getValue()).floatValue();
	}
	
	public String toString(){
		return ""+x+" - "+f;
	}
}



