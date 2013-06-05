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
package lights.extensions;

import lights.interfaces.IField;
import lights.interfaces.ITuple;


/**
 * @author balzarot
 */
public class BooleanTuple extends LabelledTuple {

    protected String _query = null;
	
	private boolean testExpression(String query)
	{
	    
		String formula = query.trim();
		String pred;
		boolean last = false;
		int start, end;

		while(true){
			end   = formula.indexOf(')');
			if (end == -1){
				start = formula.indexOf('(');
				if (start > 0) return false;
			}
			else
				start = formula.lastIndexOf('(',end);

			if (end > 0 && start == -1)
				return false;
			
			if (end == -1){
				pred = formula;
				last = true;
			}
			else{
				pred = formula.substring(start, end);
			}
			pred = pred.trim();
			
			String[] ops = pred.split(" ");
			if (ops.length != 3) return false;
			
			if (last) break;
			
			formula = formula.substring(0, start) + "atom" + formula.substring(end + 1);
			System.out.println(""+formula);
		}
	    
		return true;	
	}

    private boolean calc(String formula, ITuple tuple) {

        boolean result = false;

        formula = formula.trim();

		// If it's a complex formula, analyze it step by step
        int operatorIndex = formula.indexOf("&&");
        if (operatorIndex > -1) {
             return calc(formula.substring(0, operatorIndex),tuple) && 
					calc(formula.substring(operatorIndex + 2), tuple);
        }
		
        operatorIndex = formula.indexOf("||");
		if (operatorIndex > -1) {
			return calc(formula.substring(0, operatorIndex),tuple) ||
		 		   calc(formula.substring(operatorIndex + 2), tuple);
		}
		
		if (formula.equals("false")) return false;
		if (formula.equals("true")) return true;
		
		// If it is a simple one...
        boolean negate = false;
		int pos        = -1;
		boolean bypos  = false;
		
		// If formula starts whit an exclamation mark
		if (formula.startsWith("!")){
			negate  = true;
			formula = formula.substring(1);
			formula = formula.trim();
		}
		
		// If it is a number, it represents the position of the field in the tuple
		try{
			if (formula.startsWith("#")){
				formula = formula.substring(1);
				pos = Integer.parseInt(formula);
				bypos = true;
			}
		}catch (Exception e) {}
		
		if (bypos){
			if (pos >= _fields.size() || pos < 0)
				throw new IllegalArgumentException("There are no Field at position " + pos);

			IField f = (IField) _fields.elementAt(pos);
			result = f.matches(tuple.get(pos));
		}
		
		// If it is not a number, it must be a string containing 
		// the name of the field
		else{
			pos = getFieldPosition(formula);
			if (pos  == -1)
				throw new IllegalArgumentException("Field " + formula + " not defined");
			IField f = get(pos);
			result = f.matches(tuple.get(pos));
		}

		if (negate) return !result;
        else return result;
    }

    private boolean parse(String formula, ITuple tuple){
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
        boolean matching = false;
            if (_query != null) {
                matching = parse(_query, tuple);
            } else {
                if (_fields.size() != tuple.length()) return false;
                for (int i = 0; i < _fields.size(); i++) {
                    IField f = (IField) _fields.elementAt(i);
					matching = matching && f.matches(tuple.get(i));
                }
            }
        return matching;
    }

    public String getMatchingExpression() {
        return _query;
    }

    public void setMatchingExpression(String query) {
		if (testExpression(query))
			_query = query;
		else
			throw new IllegalArgumentException("The expression is not well-formed");
    }

} 
