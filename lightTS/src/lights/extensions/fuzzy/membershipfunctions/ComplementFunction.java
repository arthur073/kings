/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 20, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

/**
 * @author balzarot
 */
public class ComplementFunction extends FloatMembershipFunction {

	private FloatMembershipFunction _function;

	public ComplementFunction(FloatMembershipFunction function)
	{
		_function = function;
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.FloatMembershipFunction#getMembershipValue(float)
	 */
	public float getMembershipValue(float crisp) {
		return (1 - _function.getMembershipValue(crisp));
	}

	public FloatMembershipFunction getFunction()
	{
		return _function;
	}

	public void translate(float delta)
	{
		_function.translate(delta);
	}
}
