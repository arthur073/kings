/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 20, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

/**
 * @author balzarot
 */
public class IntersectionFunction extends FloatMembershipFunction {

	private FloatMembershipFunction _functionA;
	private FloatMembershipFunction _functionB;

	public IntersectionFunction(FloatMembershipFunction functionA, FloatMembershipFunction functionB)
	{
		_functionA = functionA;
		_functionB = functionB;
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.FloatMembershipFunction#getMembershipValue(float)
	 */
	public float getMembershipValue(float crisp) {
		float membershipValueA = _functionA.getMembershipValue(crisp);
		float membershipValueB = _functionB.getMembershipValue(crisp);
		if(membershipValueA < membershipValueB) return membershipValueA;
		else return membershipValueB;
	}

	public FloatMembershipFunction getFunctionA()
	{
		return _functionA;
	}

	public FloatMembershipFunction getFunctionB()
	{
		return _functionB;
	}

	public void translate(float delta)
	{
		_functionA.translate(delta);
		_functionB.translate(delta);
	}

}
