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
package lights.adapters.j2me;

import java.util.Vector;

import lights.interfaces.ITuple;
import lights.interfaces.ITupleSpace;
import lights.interfaces.TupleSpaceError;
import lights.interfaces.TupleSpaceException;

/**
 * @author costa
 * 
 * TODO Insert comment
 */
public class TupleSpaceAdapter implements ITupleSpace {

	/** Used to specify that tuple space access should be optimized for speed. */
	public static final int SPEED = 0;

	/** Used to specify that tuple space access should be optimized for space. */
	public static final int SPACE = 1;

	/** The name of the tuple space. */
	private String name = null;

	/** The actual implementation of the tuple space data structure. */
	protected Vector ts = null;

	/** The default for optimization is speed. */
	protected int optimizationFor = SPACE;

	/**
	 * Creates a tuple space with a default name, and optimization for speed.
	 */
	public TupleSpaceAdapter() {
		this(DEFAULT_NAME, SPACE);
	}

	/**
	 * Creates a tuple space with the name specified by the user, and
	 * optimization for speed.
	 * 
	 * @param name
	 *                 the tuple space name.
	 * @exception IllegalArgumentException
	 *                     if the name is <code>null</code>.
	 */
	public TupleSpaceAdapter(String name) {
		this(name, SPACE);
	}

	/**
	 * Creates a tuple space with the name and optimization specified by the
	 * user.
	 * 
	 * @param name
	 *                 the tuple space name.
	 * @param optimizeFor
	 *                 either of the values <code>SPACE</code> and
	 *                 <code>SPEED</code>.
	 * @exception IllegalArgumentException
	 *                     if the optimization parameter is not valid, or the name is
	 *                     <code>null</code>.
	 */
	public TupleSpaceAdapter(String name, int optimizeFor) {
		if (name == null)
			throw new IllegalArgumentException("Must specify a non-null name.");
		this.name = name;
		ts = new Vector(100, 100);
		if (optimizeFor != SPACE && optimizeFor != SPEED)
			throw new IllegalArgumentException(
					"Optimization must be for SPACE or SPEED.");
		optimizationFor = optimizeFor;
	}

	/**
	 * Returns the name of the tuple space.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Inserts a tuple in the tuple space. The operation is synchronous, i.e.,
	 * the tuple is guaranteed to be available in the tuple space after this
	 * method successfully completes execution.
	 * 
	 * @param tuple
	 *                 The tuple to be inserted.
	 * @exception TupleSpaceException
	 *                     if an error occurs in the implementation.
	 * @exception IllegalArgumentException
	 *                     if the tuple has no fields.
	 */
	public void out(ITuple tuple) throws TupleSpaceException {
		insertTuple(tuple);
		synchronized (this) {
			notifyAll();
		}
	}

	/**
	 * Inserts multiple tuples in the tuple space. The operation is performed
	 * atomically, i.e., each tuple is not available until all the tuples have
	 * been inserted.
	 * 
	 * @param tuples
	 *                 An array containing the tuples to be inserted.
	 * @exception TupleSpaceException
	 *                     if an error occurs in the implementation.
	 */
	public void outg(ITuple[] tuples) throws TupleSpaceException {
		synchronized (this) {
			for (int i = 0; i < tuples.length; i++)
				insertTuple(tuples[i]);
			notifyAll();
		}
	}

	/**
	 * Withdraws from the tuple space a tuple matching the templates specified;
	 * if no tuple is found, the caller is suspended until such a tuple shows up
	 * in the tuple space. If multiple matching tuples are found, the one
	 * returned is selected non-deterministically.
	 * 
	 * @param templates
	 *                 the templates used for matching.
	 * @return a tuple matching the templates.
	 * @exception TupleSpaceException
	 *                     if an error in the implementation.
	 */
	public ITuple in(ITuple template) throws TupleSpaceException {
		ITuple result = null;
		while (result == null) {
			result = inp(template);
			if (result == null)
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					throw new TupleSpaceError("Internal Error. Halting...");
				}
		}
		return result;
	}

	/**
	 * Withdraws from the tuple space a tuple matching the templates specified;
	 * if no tuple is found, <code>null</code> is returned. If multiple
	 * matching tuples are found, the one returned is selected
	 * non-deterministically.
	 * 
	 * @param templates
	 *                 the templates used for matching.
	 * @return a tuple matching the templates, or <code>null</code> if none is
	 *            found.
	 * @exception TupleSpaceException
	 *                     if an error in the implementation.
	 */
	public ITuple inp(ITuple template) throws TupleSpaceException {
		ITuple result = null;
		synchronized (this) {
			if (ts.size() != 0)
				result = lookupTuple(template, false);
		}
		return result;
	}

	/**
	 * Withdraws from the tuple space <i>all </I> the tuple matching the
	 * templates specified. If no tuple is found, <code>null</code> is
	 * returned.
	 * 
	 * @param templates
	 *                 the templates used for matching.
	 * @return a tuple matching the templates, or <code>null</code> if none is
	 *            found.
	 * @exception TupleSpaceException
	 *                     if an error in the implementation.
	 */
	public ITuple[] ing(ITuple template) throws TupleSpaceException {
		ITuple[] result = null;
		synchronized (this) {
			if (ts.size() != 0)
				result = lookupTuples(template, false);
		}
		return result;
	}

	/**
	 * Reads from the tuple space a copy of a tuple matching the templates
	 * specified. If no tuple is found, the caller is suspended until such a
	 * tuple shows up in the tuple space. If multiple matching tuples are found,
	 * the one returned is selected non-deterministically.
	 * 
	 * @param templates
	 *                 the templates used for matching.
	 * @return a copy of a tuple matching the templates.
	 * @exception TupleSpaceException
	 *                     if an error in the implementation.
	 */
	public ITuple rd(ITuple template) throws TupleSpaceException {
		ITuple result = null;
		while (result == null) {
			result = rdp(template);
			if (result == null)
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					throw new TupleSpaceError("Internal Error. Halting...");
				}
		}
		return result;
	}

	/** Attention to matching: it's templates.matches(tuple)!!! */
	/**
	 * Reads from the tuple space a copy of a tuple matching the templates
	 * specified. If no tuple is found, <code>null</code> is returned. If
	 * multiple matching tuples are found, the one returned is selected
	 * non-deterministically.
	 * 
	 * @param templates
	 *                 the templates used for matching.
	 * @return a copy of a tuple matching the templates, or <code>null</code>
	 *            if none is found.
	 * @exception TupleSpaceException
	 *                     if an error in the implementation.
	 */
	public ITuple rdp(ITuple template) throws TupleSpaceException {
		ITuple result = null;
		synchronized (this) {
			if (ts.size() != 0)
				result = lookupTuple(template, true);
		}
		return result;
	}

	/**
	 * Reads from the tuple space a copy of <I>all </I> the tuples matching the
	 * templates specified. If no tuple is found, <code>null</code> is
	 * returned. Conventionally, a tuple with no fields matches any other tuple.
	 * 
	 * @param templates
	 *                 the templates used for matching.
	 * @return a copy of a tuple matching the templates, or <code>null</code>
	 *            if none is found.
	 * @exception TupleSpaceException
	 *                     if an error in the implementation.
	 */
	public ITuple[] rdg(ITuple template) throws TupleSpaceException {
		ITuple[] result = null;
		synchronized (this) {
			if (ts.size() != 0)
				result = lookupTuples(template, true);
		}
		return result;
	}

	/**
	 * Returns a count of the tuples found in the tuple space that match the
	 * templates.
	 * 
	 * @param templates
	 *                 the templates used for matching.
	 * @return the number of tuples currently in the tuple space that match the
	 *            templates.
	 * @exception TupleSpaceException
	 *                     if an error in the implementation.
	 */
	public int count(ITuple template) throws TupleSpaceException {
		ITuple result = null;
		int c = 0;
		synchronized (this) {
			for (int i = 0; i < ts.size(); i++) {
				result = (ITuple) ts.elementAt(i);
				if (template.matches(result))
					c++;
			}
		}
		return c;
	}

	/** Returns a string representation of the tuple space. */
	public String toString() {
		return ts.toString();
	}

	////////////////////////////
	// protected methods //
	///////////////////////////
	protected void insertTuple(ITuple tuple) {
		if (tuple.length() == 0)
			throw new IllegalArgumentException(
					"Tuples with no fields can be used only as templates.");
		FieldAdapter[] fields = (FieldAdapter[]) tuple.getFields();
		ITuple toWrite = ((TupleAdapter) tuple).getDeepCopy();
		ts.addElement(toWrite);
	}

	protected ITuple lookupTuple(ITuple template, boolean isRead) {
		ITuple result = null;
		ITuple toReturn = null;
		boolean found = false;
		int i = 0;
		// Handles matching of any tuple.
		if (template.length() == 0) {
			result = (ITuple) ts.elementAt(0);
			if (!isRead)
				ts.removeElementAt(0);
		} else {
			while (!found && i < ts.size()) {
				result = (ITuple) ts.elementAt(i++);
				found = template.matches(result);
			}
			if (!found)
				result = null;
			else {
				if (isRead) {
					toReturn = ((TupleAdapter) result).getDeepCopy();

				} else
					toReturn = result;
				if (!isRead)
					ts.removeElementAt(--i);
			}
		}
		return toReturn;
	}

	protected ITuple[] lookupTuples(ITuple template, boolean isRead) {
		ITuple result = null;
		boolean found = false;
		Vector allResult = new Vector(100, 10);
		ITuple[] toReturn = null;
		// Handles matching of any tuple.
		if (template.length() == 0) {
			allResult = ts;
			found = true;
		} else {
			for (int i = 0; i < ts.size(); i++) {
				result = (ITuple) ts.elementAt(i);
				if (template.matches(result)) {
					if (isRead) {
						allResult.addElement(((TupleAdapter) result)
								.getDeepCopy());
					} else
						allResult.addElement(result);
					if (!isRead)
						ts.removeElementAt(i--);
					found = true;
				}
			}
		}
		if (!found)
			toReturn = null;
		else {
			toReturn = new ITuple[allResult.size()];
			allResult.copyInto(toReturn);
		}
		// Handles matching of any tuple.
		if (template.length() == 0 && !isRead)
			ts.removeAllElements();
		return toReturn;
	}

}