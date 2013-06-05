/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 20, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

/**
 * @author balzarot
 */
public class ZFunction extends FloatMembershipFunction {

	private SFunction _sfunction;

	public ZFunction(float center, float delta)
	{
		_sfunction = new SFunction(center,delta);
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.FloatMembershipFunction#getMembershipValue(float)
	 */
	public float getMembershipValue(float crisp) {
		return 1-_sfunction.getMembershipValue(crisp);
	}

	public void translate(float delta)
	{
		_sfunction.translate(delta);
	}

	public float getDelta()
	{
		return _sfunction.getDelta();
	}

	public float getCenter()
	{
		return _sfunction.getCenter();
	}

}
