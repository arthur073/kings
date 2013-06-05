/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 20, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

/**
 * @author balzarot
 */
public class ConstantFunction extends FloatMembershipFunction {

	protected float _value;

	public ConstantFunction(float value)
	{
		if(value < 0) 	throw new IllegalArgumentException("value < 0");
		if(value > 1) 	throw new IllegalArgumentException("value > 1");
		_value = value;
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.FloatMembershipFunction#getMembershipValue(float)
	 */
	public float getMembershipValue(float crisp) {
		return _value;
	}

	public float getValue()
	{
		return _value;
	}

	public void translate(float delta){}
}
