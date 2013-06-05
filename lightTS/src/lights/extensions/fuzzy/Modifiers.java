/*
 * Created on Jan 28, 2004
 *
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Paolo Costa <paolo.costa@polimi.it>
 *
 * To change the templates for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Modifiers {

	static class Not implements IModifier {
		public float getModifiedValue(float value) {
			return (1 - value);
		}
	}

	static class MoreOrLess implements IModifier {
		public float getModifiedValue(float value) {
			return (float) (Math.pow(value, 0.33f));
		}
	}

	static class Somewhat implements IModifier {
		public float getModifiedValue(float value) {
			return (float) (Math.pow(value, 0.5f));
		}
	}

	static class Plus implements IModifier {
		public float getModifiedValue(float value) {
			return (float) (Math.pow(value, 1.25f));
		}
	}

	static class Very implements IModifier {
		//		public Very() {
		//		}
		public float getModifiedValue(float value) {
			return (float) (Math.pow(value, 2f));
		}
	}

	static class Extremely implements IModifier {
		public float getModifiedValue(float value) {
			return (float) (Math.pow(value, 3f));
		}
	}

	static class Intensify implements IModifier {
		public float getModifiedValue(float value) {
			if ((value >= 0.0f) && (value <= 0.5f)) {
				return (2 * value * value);
			} else if ((value > 0.5f) && (value <= 1f)) {
				return (1 - 2 * (1 - value) * (1 - value));
			}
			throw new IllegalArgumentException();
		}
	}

	/**
	 * 
	 */

	private static Map _modifiersMap = new HashMap();

	public Modifiers() {
		super();
	}

	static {
		try {
			setEquivalence("very", "Very");
			setEquivalence("extremely", "Extremely");
			setEquivalence("+/-", "MoreOrLess");
			setEquivalence("not", "Not");
			setEquivalence("plus", "Plus");
			setEquivalence("somewhat", "Somewhat");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static IModifier getModifier(String name) throws IllegalArgumentException {
		Class c;
		if (_modifiersMap.containsKey(name)) {
			c = (Class) _modifiersMap.get(name);
		} else {
			try {
				c = Class.forName("lights.extensions.fuzzy.Modifiers$" + name);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(
					"Modifier " + name + " not found");
			}
		}
		try {
			//Constructor ctor[] = c.getConstructor(new Class[] {
			//return (IModifier) ctor[0].newInstance(new Object[] {
			return (IModifier) c.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException("Default constructor not found");
		}
	}

	public static void setEquivalence(String newName, String className)
		throws ClassNotFoundException {
		Class c = Class.forName("lights.extensions.fuzzy.Modifiers$" + className);
		_modifiersMap.put(newName, c);
	}

	public static void setModifier(String newName, Class newModifier) {
		_modifiersMap.put(newName, newModifier);
	}

}
