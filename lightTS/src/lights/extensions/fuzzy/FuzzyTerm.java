/*
 * Copyright (C) 2004 costa

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package lights.extensions.fuzzy;

import lights.extensions.fuzzy.membershipfunctions.IMembershipFunction;

/**
 * @author costa
 * 
 * TODO Insert comment
 */
public class FuzzyTerm {

	protected String term;

	protected IMembershipFunction membershipFunction;

	/**
	 * @param term
	 * @param membershipFunction
	 */
	public FuzzyTerm(String term, IMembershipFunction membershipFunction) {
		this.term = term;
		this.membershipFunction = membershipFunction;
	}

	/**
	 * @return Returns the membershipFunction.
	 */
	public IMembershipFunction getMembershipFunction() {
		return membershipFunction;
	}

	/**
	 * @param membershipFunction
	 *                 The membershipFunction to set.
	 */
	public void setMembershipFunction(IMembershipFunction membershipFunction) {
		this.membershipFunction = membershipFunction;
	}

	/**
	 * @return Returns the term.
	 */
	public String getTerm() {
		return term;
	}

	/**
	 * @param term
	 *                 The term to set.
	 */
	public void setTerm(String term) {
		this.term = term;
	}
}