/*
 * Project : FuzzyLibrary
 * Created on Nov 16, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

/**
 * @author balzarot
 */
public class TrapezFunction extends FloatMembershipFunction {

	private float _x1;
	private float _x2;
	private float _alpha;
	private float _beta;
	private float _ymax;
	private float _ymin;

	public TrapezFunction(float x1, float x2, float alpha, float beta){
		this(x1,x2,alpha,beta,1,0);
	}

	public TrapezFunction(float x1, float x2, float alpha, float beta, float ymax, float ymin)
	{
		if(alpha < 0) 		throw new IllegalArgumentException("alpha < 0");
		if(beta  < 0) 		throw new IllegalArgumentException("beta < 0");
		if(ymin  < 0)		throw new IllegalArgumentException("ymin < 0");
		if(ymin  > ymax) 	throw new IllegalArgumentException("ymin > ymax");
		if(ymax  > 1)		throw new IllegalArgumentException("ymax > 1");
		if(ymax  < 0) 		throw new IllegalArgumentException("ymax < 0");
		if(x2    < x1) 		throw new IllegalArgumentException("x2 < x1");

		_x1 = x1;
		_x2 = x2;
		_alpha = alpha;
		_beta  = beta;
		_ymin  = ymin;
		_ymax  = ymax;
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.FloatMembershipFunction#getMembershipValue(float)
	 */
	public float getMembershipValue(float crispValue) {
		if (crispValue < _x1 && crispValue >= (_x1-_alpha))
			return  _ymin + ( ( (_ymax-_ymin )*( crispValue - (_x1-_alpha) ) )  / _alpha );
		else if(crispValue <= (_x2 + _beta) && crispValue > _x2)
			return  _ymin + ( ( (_ymax-_ymin )*( _x2 + _beta - crispValue ) )  / _beta );
		else if(crispValue >= _x1 && crispValue <= _x2)
			return  _ymax;
		else return 0.0f;
	}

	public void translate(float delta)
	{
		_x1 = _x1 + delta;
		_x2 = _x2 + delta;
	}


	/**
	 * @return
	 */
	public float getAlpha() {
		return _alpha;
	}

	/**
	 * @return
	 */
	public float getBeta() {
		return _beta;
	}

	/**
	 * @return
	 */
	public float getX1() {
		return _x1;
	}

	/**
	 * @return
	 */
	public float getX2() {
		return _x2;
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
