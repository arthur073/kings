/*
 * Project : lights.extensions.fuzzy.types
 * Created on Dec 21, 2003
 */
package lights.extensions.fuzzy;

import lights.extensions.fuzzy.membershipfunctions.ConstantFunction;
import lights.extensions.fuzzy.membershipfunctions.FloatMembershipFunction;
import lights.extensions.fuzzy.membershipfunctions.PiFunction;
import lights.extensions.fuzzy.membershipfunctions.RampFunction;
import lights.extensions.fuzzy.membershipfunctions.SFunction;
import lights.extensions.fuzzy.membershipfunctions.TriangleFunction;
import lights.extensions.fuzzy.membershipfunctions.ZFunction;

/**
 * @author balzarot
 */
public class FloatFuzzyType extends FuzzyType {

	protected String _units;

	protected float _max;

	protected float _min;

	protected FloatMembershipFunction _nearlyFunction;

	protected FloatMembershipFunction _greaterFunction;

	protected FloatMembershipFunction _smallerFunction;

	public FloatFuzzyType(String name, float min, float max) {
		this(name, min, max, "");
	}

	public FloatFuzzyType(String name, float min, float max, String units) {
		super(name);
		_units = units;
		_max = max;
		_min = min;

		_nearlyFunction = new PiFunction(0f, (_max - _min) / 10);
		_greaterFunction = new RampFunction(0f, (_max - _min) / 10);
		_smallerFunction = new RampFunction(0f - (_max - _min) / 10, 0f, 1f, 0f);
	}

	public void setNearlyFunction(FloatMembershipFunction function) {
		_nearlyFunction = function;
	}

	public void setSmallerFunction(FloatMembershipFunction function) {
		_smallerFunction = function;
	}

	public void setGreaterFunction(FloatMembershipFunction function) {
		_greaterFunction = function;
	}

	public String getUnits() {
		return _units;
	}

	public float getMax() {
		return _max;
	}

	public float getMin() {
		return _min;
	}

	public float getMembershipValue(String term, float crispValue) {
		return getMembershipValue(term, new Float(crispValue));
	}

	public FuzzyTerm getTerm(float crispValue) {
		return getTerm(new Float(crispValue));
	}

	public void generateTrianglePartition(String[] terms) {
		int termsNumber = terms.length;
		float step = (_max - _min);

		if (termsNumber < 1)
			return;
		if (termsNumber == 1) {
			addTerm(new FuzzyTerm(terms[0], new ConstantFunction(1.0f)));
			return;
		}

		step /= (termsNumber - 1);

		float alpha, beta, center;
		for (int i = 0; i < termsNumber; i++) {
			center = _min + i * step;
			alpha = step;
			beta = step;

			if (i == 0)
				alpha = 0;
			if (i == termsNumber - 1)
				beta = 0;

			addTerm(new FuzzyTerm(terms[i], new TriangleFunction(center, alpha,
					beta)));
		}

	}

	public void generatePiPartition(String[] terms) {
		int termsNumber = terms.length;
		float step = (_max - _min);

		if (termsNumber < 1)
			return;
		if (termsNumber == 1) {
			addTerm(new FuzzyTerm(terms[0], new ConstantFunction(1.0f)));
			return;
		}

		step /= (termsNumber - 1);

		float center;
		for (int i = 0; i < termsNumber; i++) {
			if (i == 0)
				addTerm(new FuzzyTerm(terms[i], new SFunction(_min + step / 2,
						step / 2)));
			else if (i == termsNumber - 1)
				addTerm(new FuzzyTerm(terms[i], new ZFunction(_max - step / 2,
						step / 2)));
			else
				addTerm(new FuzzyTerm(terms[i], new PiFunction(_min + i * step,
						step)));
		}

	}

	public float isNearly(float crisp, float reference) {
		_nearlyFunction.translate(reference);
		float result = _nearlyFunction.getMembershipValue(crisp);
		_nearlyFunction.translate(0 - reference);
		return result;
	}

	public float isGreater(float crisp, float reference) {
		_greaterFunction.translate(reference);
		float result = _greaterFunction.getMembershipValue(crisp);
		_greaterFunction.translate(0 - reference);
		return result;
	}

	public float isSmaller(float crisp, float reference) {
		_smallerFunction.translate(reference);
		float result = _smallerFunction.getMembershipValue(crisp);
		_smallerFunction.translate(0 - reference);
		return result;
	}

}