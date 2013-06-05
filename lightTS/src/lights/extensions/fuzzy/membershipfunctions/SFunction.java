/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 20, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

/**
 * @author balzarot
 */
public class SFunction extends FloatMembershipFunction {
	private float _center;
	private float _delta;

	public SFunction(float center, float delta)
	{
		if(delta < 0.0001f) throw new IllegalArgumentException("delta");

		_center = center;
		_delta  = delta;
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.FloatMembershipFunction#getMembershipValue(float)
	 */
	public float getMembershipValue(float crisp) {
		float start = _center - _delta;
		float end   = _center + _delta;

		if(crisp <= start) return 0;
		else if(crisp <= _center)
		{
			float temp = (crisp - _center + _delta)/(2 * _delta);
			temp = temp * temp;
			return 2*temp;
		}
		else if(crisp <= end)
		{
			float temp = (_center - crisp + _delta)/(2 * _delta);
			temp = temp * temp;
			return 1- (2 * temp);
		}
		else return 1;
	}

	public void translate(float delta)
	{
		_center = _center + delta;
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
