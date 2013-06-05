/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 20, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

import java.util.*;

/**
 * @author balzarot
 */
public class ObjectListMembershipFunction implements IMembershipFunction {

	private HashMap _objectMap;

	public ObjectListMembershipFunction()
	{
		_objectMap = new HashMap();
	}

	public void addObject(Object object, float value)
	{
		if(value < 0) throw new IllegalArgumentException("value < 0");
		if(value > 1) throw new IllegalArgumentException("value > 1");
		_objectMap.put(object, new Float(value));
	}

	public Set getAllObject()
	{
		return _objectMap.keySet();
	}

	public int count()
	{
		return _objectMap.size();
	}

	public boolean isEmpty()
	{
		return (_objectMap.size()==0);
	}

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.IMembershipFunction#getMembershipValue(java.lang.Object)
	 */
	public float getMembershipValue(Object object) {
			Float temp = (Float) _objectMap.get(object);
			if(temp == null) return 0.0f;
			else return temp.floatValue();
	}

}
