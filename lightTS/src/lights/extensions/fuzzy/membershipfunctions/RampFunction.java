/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 20, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

/**
 * @author balzarot
 */
public class RampFunction extends FloatMembershipFunction {

	protected float _x1;
	protected float _x2;
	protected float _y1;
	protected float _y2;

	public RampFunction(float x1, float x2)
	{
		this(x1,x2,0.0f,1.0f);
	}

	public RampFunction(float x1, float x2, float y1, float y2)
	{
		if(y1 < 0) 		throw new IllegalArgumentException("y1 < 0");
		if(y1 > 1) 		throw new IllegalArgumentException("y1 > 1");
		if(y2 < 0) 		throw new IllegalArgumentException("y2 < 0");
		if(y2 > 1) 		throw new IllegalArgumentException("y2 > 1");
		if(x1 >= x2) 	throw new IllegalArgumentException("x2 >= x1");

		_x1      = x1;
		_x2      = x2;
		_y1      = y1;
		_y2      = y2;
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.FloatMembershipFunction#getMembershipValue(float)
	 */
	public float getMembershipValue(float crisp) {
		if(crisp < _x1) return _y1;
		else if(crisp > _x2) return _y2;
		else{
			if(_y2 > _y1) return _y1 + (crisp - _x1)*((_y2 - _y1) / (_x2 - _x1));
			else return _y1 - (crisp - _x1)*((_y1 - _y2) / (_x2 - _x1));
		}
	}

	public void translate(float delta)
	{
		_x1 = _x1 + delta;
		_x2 = _x2 + delta;
	}

	public float getX1()
	{
		return _x1;
	}

	public float getX2()
	{
		return _x2;
	}

	public float getY1()
	{
		return _y1;
	}

	public float getY2()
	{
		return _y2;
	}

}
