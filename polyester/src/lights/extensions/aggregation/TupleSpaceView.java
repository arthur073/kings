/*
 * Copyright (C) 2005 costa

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
package lights.extensions.aggregation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import lights.interfaces.ITuple;
import lights.interfaces.ITupleSpace;
import lights.interfaces.TupleSpaceException;

/**
 * @author costa TODO Insert comment
 */
public class TupleSpaceView {

	protected ITupleSpace tupleSpace;

	protected Aggregator[] aggregators;

	/**
	 * @param tupleSpace
	 * @param aggregators
	 */
	public TupleSpaceView(ITupleSpace tupleSpace, Aggregator[] aggregators) {
		this.tupleSpace = tupleSpace;
		this.aggregators = aggregators;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lights.interfaces.ITupleSpaceRead#rdp(lights.interfaces.ITuple)
	 */
	public ITuple rdp(ITuple template) throws TupleSpaceException {

		for (int i = 0; i < aggregators.length; i++) {
			if (aggregators[i].getTemplate() == null) {
				throw new IllegalArgumentException("Template not specified ");
			}
			ITuple[] concreteTuples = tupleSpace.rdg(aggregators[i]
					.getTemplate());
			ITuple[] virtualTuples = aggregators[i].aggregate(concreteTuples);

			for (int j = 0; j < virtualTuples.length; j++) {
				if (template.matches(virtualTuples[j])) {
					return virtualTuples[j];
				}
			}
		}
		// No tuple was found
		return null;
	}

	public ITuple[] rdg(ITuple template) throws TupleSpaceException {

		List viewTuples = new LinkedList();

		for (int i = 0; i < aggregators.length; i++) {
			if (aggregators[i].getTemplate() == null) {
				throw new IllegalArgumentException("Template not specified ");
			}

			ITuple[] concreteTuples = tupleSpace.rdg(aggregators[i]
					.getTemplate());
			ITuple[] virtualTuples = aggregators[i].aggregate(concreteTuples);

			for (int j = 0; j < virtualTuples.length; j++) {
				viewTuples.add(virtualTuples[j]);
			}
		}

		Vector returnTuples = new Vector();

		for (Iterator iter = viewTuples.iterator(); iter.hasNext();) {
			ITuple tuple = (ITuple) iter.next();
			if (template.matches(tuple)) {
				returnTuples.add(tuple);
			}
		}

		if (returnTuples.size() == 0) {
			return null;
		} else {
			ITuple tuples[] = new ITuple[returnTuples.size()];
			returnTuples.copyInto(tuples);
			return tuples;
		}
	}
}
