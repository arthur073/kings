/*
 * Project : FuzzyLibrary
 * Created on Nov 16, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

/**
 * @author balzarot
 */
public class TriangleFunction extends FloatMembershipFunction {

	private float _xmax;
	private float _xmin;
	private float _xtop;
	private float _ymax;
	private float _ymin;

	public TriangleFunction(float xtop, float base)
	{
		this(xtop,base/2,base/2,1,0);
	}

	public TriangleFunction(float xtop, float alpha, float beta)
	{
		this(xtop,alpha,beta,1,0);
	}

	public TriangleFunction(float xtop, float alpha, float beta, float ymax, float ymin)
	{
		if(alpha < 0) 		throw new IllegalArgumentException("alpha < 0");
		if(beta  < 0) 		throw new IllegalArgumentException("beta < 0");
		if(ymin  < 0)		throw new IllegalArgumentException("ymin < 0");
		if(ymin  > ymax) 	throw new IllegalArgumentException("ymin > ymax");
		if(ymax  > 1)		throw new IllegalArgumentException("ymax > 1");
		if(ymax  < 0) 		throw new IllegalArgumentException("ymax < 0");

		_xtop = xtop;
		_xmin = xtop-alpha;
		_xmax = xtop+beta;
		_ymin = ymin;
		_ymax = ymax;
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.FloatMembershipFunction#getMembershipValue(float)
	 */
	public float getMembershipValue(float crisp) {
		float result = 0.0f;

		if (crisp <= _xtop && crisp >= _xmin)
			result = _ymin + ( ( (_ymax-_ymin )*( crisp -_xmin ) )  / (_xtop - _xmin ) );
		else if(crisp <= _xmax && crisp > _xtop)
			result = _ymin + ( ( (_ymax-_ymin )*( _xmax - crisp) )  / (_xmax - _xtop ) );

		if((new Float(result)).isNaN()) return _ymax;
		else return result;
	}

	public void translate(float delta)
	{
		_xmax = _xmax + delta;
		_xmin = _xmin + delta;
		_xtop = _xtop + delta;
	}

	/**
	 * @return
	 */
	public float getXmax() {
		return _xmax;
	}

	/**
	 * @return
	 */
	public float getXmin() {
		return _xmin;
	}

	/**
	 * @return
	 */
	public float getXtop() {
		return _xtop;
	}

	/**
	 * @return
	 */
	public float getYmax() {
		return _ymax;
	}

	/**
	 * @return
	 */
	public float getYmin() {
		return _ymin;
	}
}
