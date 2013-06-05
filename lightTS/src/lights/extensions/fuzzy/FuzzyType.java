/*
 * Project : lights.extensions.fuzzy.types
 * Created on Dec 21, 2003
 */
package lights.extensions.fuzzy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lights.extensions.fuzzy.membershipfunctions.IMembershipFunction;

/**
 * @author balzarot
 */
public class FuzzyType {

	protected String _name;

	protected List _terms;

	public FuzzyType(String name) {
		_name = name;
		_terms = new LinkedList();
	}

	public String getName() {
		return _name;
	}

	public void addTerm(FuzzyTerm fuzzyTerm) {
		_terms.add(fuzzyTerm);
	}

	public FuzzyTerm getTerm(String name) {
		for (Iterator iter = _terms.iterator(); iter.hasNext();) {
			FuzzyTerm element = (FuzzyTerm) iter.next();
			if (element.getTerm().equals(name)) {
				return element;
			}
		}
		return null;
	}

	public Iterator getTerms() {
		return _terms.iterator();
	}

	public float getMembershipValue(String term, Object crispObject) {
		FuzzyTerm t = getTerm(term);
		IMembershipFunction membershipFunction = (IMembershipFunction) t
				.getMembershipFunction();

		if (membershipFunction == null)
			throw new IllegalArgumentException("The term " + term
					+ " is not defined");

		return membershipFunction.getMembershipValue(crispObject);

	}

	public FuzzyTerm getTerm(Object crispObject) {
		FuzzyTerm result = null;
		float maxValue = 0.0f;
		float temp;

		Iterator terms = _terms.iterator();
		while (terms.hasNext()) {
			FuzzyTerm term = (FuzzyTerm)terms.next();
			temp = getMembershipValue(term.getTerm(), crispObject);
			if (temp > maxValue) {
				maxValue = temp;
				result = term;
			}
		}
		return result;
	}

	public int getTermsNumber() {
		return _terms.size();
	}

}