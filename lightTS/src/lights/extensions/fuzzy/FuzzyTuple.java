/*
 * lighTS - An extensible and lightweight Linda-like tuplespace Copyright (C)
 * 2001, Gian Pietro Picco
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package lights.extensions.fuzzy;

import java.lang.reflect.Constructor;
import java.util.Iterator;

import lights.extensions.BooleanTuple;
import lights.extensions.fuzzy.operators.IOperator;
import lights.extensions.fuzzy.operators.Minimum;
import lights.interfaces.IField;
import lights.interfaces.ITuple;
import lights.interfaces.IValuedField;
import lights.Field;

/**
 * @author balzarot
 */
public class FuzzyTuple extends BooleanTuple {

    protected float _threshold = 0.6f;
    protected IOperator _and = new Minimum();
    protected IOperator _or = new Minimum();


    protected IOperator getOperatorByName(String term) throws Exception {
        Class c = Class.forName("lights.extensions.fuzzy.operators." + term);

        Constructor ctor = c.getConstructor(new Class[] {});
        return (IOperator) ctor.newInstance(new Object[] {});
    }

    private float calc(String formula, ITuple tuple) throws Exception {
	//        System.out.println("Valuto --> " + formula);

        float result = 0.0f;

        formula = formula.trim();

        int operatorIndex = formula.indexOf("&&");
        if (operatorIndex > -1) {
            IOperator operator = getAndOperator();
            return operator.operator(calc(formula.substring(0, operatorIndex),
                    tuple), calc(formula.substring(operatorIndex + 2), tuple));
        }
        operatorIndex = formula.indexOf("||");
        if (operatorIndex > -1) {
            IOperator operator = getOrOperator();
            return operator.operator(calc(formula.substring(0, operatorIndex),
                    tuple), calc(formula.substring(operatorIndex + 2), tuple));
        }
        operatorIndex = formula.indexOf(" -");
        if (operatorIndex > -1) {
            int operatorEndIndex = formula.indexOf("- ", operatorIndex);
            IOperator operator = getOperatorByName(formula.substring(
                    operatorIndex + 2, operatorEndIndex));
            return operator.operator(calc(formula.substring(0, operatorIndex),
                    tuple),
                    calc(formula.substring(operatorEndIndex + 1), tuple));
        }

        String variable = "";
        String operator = "";
        String term = "";
        String modifier = "";
        boolean isaString = false;

        int stringStart = formula.indexOf("\"");
        if (stringStart > -1) {
            int stringEnd = formula.indexOf("\"", stringStart + 1);
            term = formula.substring(stringStart + 1, stringEnd);
            String items[] = formula.substring(0, stringStart).split(" +");
            variable = items[0];
            operator = items[1];
            isaString = true;
        } else {
            String items[] = formula.split(" +");
            if (items.length == 1) return Float.parseFloat(items[0]);

            variable = items[0];
            operator = items[1];
            if (items.length == 3)
                term = items[2];
            else {
                modifier = items[2];
                term = items[3];
            }
        }

       	int fpos = getFieldPosition(variable);
		if (fpos == -1)
			throw new IllegalArgumentException("The variable " + variable
                + " has not been defined");
		Object crisp = ((Field) tuple.get(fpos)).getValue();
        float fcrisp = 0.0f;
		if (isaString == false){
			fcrisp = ((Float) crisp).floatValue();
		}
		IField field = get(fpos);
		boolean isafuzzyfield = false;
        if (field instanceof FuzzyField)
			isafuzzyfield = true; 
		
        if (operator.equals("IS") || operator.equals("is")) {
            if (isafuzzyfield == false)
				throw new IllegalArgumentException("The variable " + variable
                + " must be a fuzzy field");
			FuzzyType fuzzyVariable = ((FuzzyField) field).getFuzzyType();
            result = fuzzyVariable.getMembershipValue(term, new Float(fcrisp));
            if (modifier.equals("") == false)
                    result = Modifiers.getModifier(modifier).getModifiedValue(
                            result);
        } else if (operator.equals("~")) {
            if (isafuzzyfield == false)
				throw new IllegalArgumentException("The variable " + variable
                + " must be a fuzzy field");
			FuzzyType fuzzyVariable = ((FuzzyField) field).getFuzzyType();
			result = ((FloatFuzzyType) fuzzyVariable).isNearly(fcrisp, Float
                    .parseFloat(term));
            if (modifier.equals("") == false)
                    result = Modifiers.getModifier(modifier).getModifiedValue(
                            result);
        } else if (operator.equals(">>")) {
            if (isafuzzyfield == false)
				throw new IllegalArgumentException("The variable " + variable
                + " must be a fuzzy field");
			FuzzyType fuzzyVariable = ((FuzzyField) field).getFuzzyType();
			result = ((FloatFuzzyType) fuzzyVariable).isGreater(fcrisp, Float
                    .parseFloat(term));
            if (modifier.equals("") == false)
                    result = Modifiers.getModifier(modifier).getModifiedValue(
                            result);
        } else if (operator.equals("<<")) {
            if (isafuzzyfield == false)
				throw new IllegalArgumentException("The variable " + variable
                + " must be a fuzzy field");
			FuzzyType fuzzyVariable = ((FuzzyField) field).getFuzzyType();
            result = ((FloatFuzzyType) fuzzyVariable).isSmaller(fcrisp, Float
                    .parseFloat(term));
            if (modifier.equals("") == false)
                    result = Modifiers.getModifier(modifier).getModifiedValue(
                            result);
        } else if (operator.equals("==")) {
            if (isaString == true) {
                if (term.equals(crisp) == true)
                        result = 1.0f;
            } else if (Float.compare(fcrisp, Float.parseFloat(term)) == 0)
                    result = 1.0f;
        } else if (operator.equals("!=")) {
            if (isaString == true) {
                if (term.equals(crisp) == false)
                        result = 1.0f;
            } else if (Float.compare(fcrisp, Float.parseFloat(term)) != 0)
                    result = 1.0f;
        } else if (operator.equals("<")) {
            if (Float.compare(fcrisp, Float.parseFloat(term)) < 0)
                    result = 1.0f;
        } else if (operator.equals(">")) {
            if (Float.compare(fcrisp, Float.parseFloat(term)) > 0)
                    result = 1.0f;
        }

        return result;
    }

    private float parse(String formula, ITuple tuple) throws Exception {
        formula = formula.trim();

        int end = formula.indexOf(')');
        int start = formula.lastIndexOf('(', end);

        if (end == -1)
            return calc(formula, tuple);
        else {
            return parse(formula.substring(0, start)
                    + String.valueOf(calc(formula.substring(start + 1, end),
                            tuple)) + formula.substring(end + 1), tuple);
        }
    }

    public boolean matches(ITuple tuple) {
        float matching = 0;
        // TODO: sistemare la gestione delle eccezioni

        try {
            if (_query != null) {
                matching = parse(_query, tuple);
            } else {
                if (_fields.size() != tuple.length()) return false;
                for (int i = 0; i < _fields.size(); i++) {
                    float temp = 0.0f;
                    IField f = (IField) _fields.elementAt(i);
                    if (f instanceof FuzzyField)
                        matching = _and.operator(matching, ((FuzzyField) f)
                                .fuzzyMatches(tuple.get(i)));
                    else if (f.matches(tuple.get(i)) == true)
                        matching = _and.operator(matching, 1.0f);
                    else
                        matching = _and.operator(matching, 0.0f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (matching >= _threshold);
    }

    public void setThreshold(float threshold) {
        _threshold = threshold;
    }

    public float getThreshold() {
        return _threshold;
    }

    public IOperator getAndOperator() {
        return _and;
    }

    public IOperator getOrOperator() {
        return _or;
    }

    public FuzzyTuple setAndOperator(IOperator operator) {
        _and = operator;
        return this;
    }

    public FuzzyTuple setOrOperator(IOperator operator) {
        _or = operator;
        return this;
    }
}
