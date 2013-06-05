/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 20, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

/**
 * @author balzarot
 */
public class PiFunction extends FloatMembershipFunction {

	private ZFunction _zfunction;
	private SFunction _sfunction;
	private float _center;
	private float _delta;

	public PiFunction(float center, float delta)
	{
		_zfunction = new ZFunction(center + (delta/2), delta/2);
		_sfunction = new SFunction(center - (delta/2), delta/2);

		_center = center;
		_delta  = delta;
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.FloatMembershipFunction#getMembershipValue(float)
	 */
	public float getMembershipValue(float crisp) {
		if(crisp < _center) return _sfunction.getMembershipValue(crisp);
		else return _zfunction.getMembershipValue(crisp);
	}

	public void translate(float delta)
	{
		_center    = _center + delta;
		_zfunction.translate(delta);
		_sfunction.translate(delta);
	}

	public float getDelta()
	{
		return _delta;
	}

	public float getCenter()
	{
		return _center;
	}

}
