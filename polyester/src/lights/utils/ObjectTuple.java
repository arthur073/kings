/*
 * Created on Oct 26, 2003
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.utils;

import java.lang.reflect.Constructor;
import java.util.Iterator;

import lights.Field;
import lights.Tuple;
import lights.interfaces.IField;

public class ObjectTuple extends Tuple {
	private Class<?> sourceType;

	public ObjectTuple(Class<?> source) {
		super();
		sourceType = source;
	}

	public String getClassName() {
		return sourceType.getName();
	}

	public Tuplable getObject() {
		try {
			Constructor<?> ctor = sourceType.getConstructor(new Class[] {});

			Tuplable t = (Tuplable) ctor.newInstance(new Object[] {});
			t.setFromTuple(this);
			return t;
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object clone() {
		ObjectTuple tuple = new ObjectTuple((Class<?>) SerializeTuple
				.getDeepCopy(sourceType));
		for (Iterator<IField> iter = _fields.iterator(); iter.hasNext();) {
			Field field = (Field) iter.next();
			tuple.add((IField) field.clone());
		}
		return tuple;
	}
}
