/*
 * Project : lights.extensions.fuzzy.types
 * Created on Dec 21, 2003
 */
package lights.extensions.fuzzy;


/**
 * @author balzarot
 */
public class FuzzyValue {

	protected String _value;
	protected String _modifier = null;

	public FuzzyValue(String value) {
		_value = value;
	}

	public void setValue(String value){
		_value = value;
	}

	public String getValue() {
		return _value;
	}

	public void setModifier(String modifier){
		_modifier = modifier;
	}

	public String getModifier() {
		return _modifier;
	}

}
