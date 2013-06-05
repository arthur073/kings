/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 20, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

import java.util.*;

/**
 * @author balzarot
 */
public class PolylineFunction extends FloatMembershipFunction {

	protected TreeMap _points;

	public PolylineFunction()
	{
		_points = new TreeMap();
	}

	public void addPoint(float xvalue, float yvalue)
	{
		if(yvalue < 0) 	throw new IllegalArgumentException("y < 0");
		if(yvalue > 1) 	throw new IllegalArgumentException("y > 1");
		_points.put(new Float(xvalue), new Float(yvalue));
	}

	public int count()
	{
		return _points.size();
	}

	public boolean isEmpty()
	{
		return (_points.size()==0);
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.FloatMembershipFunction#getMembershipValue(float)
	 */
	public float getMembershipValue(float crisp) {
		float x1 = Float.NaN;
		float x2 = Float.NaN;
		float y1, y2;

		Iterator iterator = _points.keySet().iterator();
		while(iterator.hasNext())
		{
			float x = ((Float)iterator.next()).floatValue();
			if(x<=crisp) x1 = x;
			else if(x>crisp){
				x2 = x;
				break;
			}
		}

		if((new Float(x1)).isNaN()) return 0;
		else{
			y1 = ((Float) _points.get(new Float(x1))).floatValue();
		}

		if((new Float(x2)).isNaN()){
			if(crisp > x1) return 0;
			else return y1;
		}
		else{
			y2 = ((Float) _points.get(new Float(x2))).floatValue();
		}

		return y1 + ( ( (y2-y1)*( crisp - x1 ) )  / (x2 - x1) );
	}

	public void translate(float delta)
	{
		TreeMap old = _points;
		_points = new TreeMap();

		Iterator iterator = old.keySet().iterator();
		while(iterator.hasNext())
		{
			Float x  = (Float)iterator.next();
			Float y  = (Float) _points.get(x);
			float x2 = x.floatValue() + delta;
			_points.put(new Float(x2), y);
		}

	}

}
