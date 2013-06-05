/*
 * Project : FuzzyLibrary
 * Created on Nov 16, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

/**
 * @author balzarot
 */
public abstract class FloatMembershipFunction implements IMembershipFunction {

	/* (non-Javadoc)
	 * @see lights.extensions.fuzzy.types.IMembershipFunction#getMembershipValue(java.lang.Object)
	 */
	public float getMembershipValue(Object object) {
			if(object.getClass().equals(Float.class))
				return getMembershipValue( ((Float)object).floatValue() );
			return 0;
	}
	
	abstract public float getMembershipValue(float crisp);

	abstract public void translate(float delta);

}
