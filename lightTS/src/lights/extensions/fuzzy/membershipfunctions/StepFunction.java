/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 20, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

/**
 * @author balzarot
 */
public class StepFunction extends FloatMembershipFunction {

	protected float _before;
	protected float _after;
	protected float _x;

	public StepFunction(float before, float after, float xstep )
	{
		if(before < 0) 	throw new IllegalArgumentException("before < 0");
		if(before > 1) 	throw new IllegalArgumentException("before > 1");
		if(after < 0) 	throw new IllegalArgumentException("after  < 0");
		if(after > 1) 	throw new IllegalArgumentException("after  > 1");

		_x      = xstep;
		_before = before;
		_after  = after;
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.FloatMembershipFunction#getMembershipValue(float)
	 */
	public float getMembershipValue(float crisp) {
		if(crisp < _x) return _before;
		else if(crisp > _x) return _after;
		else if(_after > _before) return _after;
		else return _before;
	}

	public void translate(float delta)
	{
		_x = _x + delta;
	}

	public float getBeforeValue()
	{
		return _before;
	}

	public float getAfterValue()
	{
		return _after;
	}

	public float getXStep()
	{
		return _x;
	}

}
