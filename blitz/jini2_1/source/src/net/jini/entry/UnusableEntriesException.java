/*
 * 
 * Copyright 2005 Sun Microsystems, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package net.jini.entry;

import java.io.InvalidObjectException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;

/**
 * Thrown from methods that normally return a collection of {@link
 * Entry} instances when one or more of the entries can't be
 * unmarshalled.  Contains copies of any entries that could be
 * unmarshalled and an {@link UnusableEntryException} for each
 * <code>Entry</code> that could not be.
 *
 * @since 2.1
 * @see UnusableEntryException 
 */
public class UnusableEntriesException extends Exception {
    private static final long serialVersionUID = 1L;

    /** 
     * The entries that could be unmarshalled 
     * @serial
     */
    private final Collection entries;

    /**
     * Exceptions detailing why certain entries could not
     * be unmarshalled.
     * @serial
     */
    private final Collection exceptions;

    /**
     * Constructs an <code>UnusableEntriesException</code> with
     * the given message, {@link Collection} of entries that could
     * be unmarshalled, and <code>Collection</code> of {@link
     * UnusableEntryException}s, detailing for each unusable
     * {@link Entry} why it could not be unmarshalled. <p>
     * 
     * @param s the detail message, may be <code>null</code>
     * @param entries <code>Collection</code> of {@link Entry}
     *                instances that could be unmarshalled, may be
     *                empty or <code>null</code>. Note, if 
     *                non-<code>null</code>, the passed instance
     *                will be returned by the {@link #getEntries}
     *                method
     * @param exceptions <code>Collection</code> of {@link
     *                UnusableEntryException} instances. Note, the
     *                passed instance will be returned by the {@link
     *                #getUnusableEntryExceptions} method
     * @throws IllegalArgumentException if <code>entries</code>
     *                contains an element that is not an
     *                <code>Entry</code>, if
     *                <code>exceptions</code> contains an element
     *                that is not an
     *                <code>UnusableEntryException</code>, or if
     *                <code>exceptions</code> is empty
     * @throws NullPointerException if <code>exceptions</code> is
     *                <code>null</code>, any element of 
     *                <code>exceptions</code> is <code>null</code>, 
     *                or any element of <code>entries</code> is
     *                <code>null</code> 
     */
    public UnusableEntriesException(String     s, 
				    Collection entries, 
				    Collection exceptions)
    { 
	super(s);

	if (entries == null) {
	    this.entries = Collections.EMPTY_SET;
	} else {
	    for (Iterator i=entries.iterator(); i.hasNext();) {
		final Object e = i.next();
		if (e == null)
		    throw new 
			NullPointerException("entries contains a null value");

		if (!(e instanceof Entry)) {
		    throw new IllegalArgumentException(
                        "entries contains a value which is not an entry");
		}
	    }

	    this.entries = entries;
	}

	for (Iterator i=exceptions.iterator(); i.hasNext();) {
	    if (i == null)
		throw new 
		    NullPointerException("exceptions contains a null value");

	    if (!(i.next() instanceof UnusableEntryException)) {
		throw new IllegalArgumentException("exceptions contains " +
		    "a value which is not an UnusableEntryException");
	    }
	}

	this.exceptions = exceptions;
    }

    /**
     * @throws InvalidObjectException if the <code>entries</code>
     * field is <code>null</code>, or contains an element which is not
     * an {@link Entry}. Also throws an
     * <code>InvalidObjectException</code> if the
     * <code>exceptions</code> field is <code>null</code>, empty, or contains
     * an element which is not an {@link UnusableEntryException}
     */
    private void readObject(ObjectInputStream in) 
	throws IOException, ClassNotFoundException
    {
	in.defaultReadObject();

	if (exceptions == null)
	    throw new InvalidObjectException("exceptions field is null");

	if (exceptions.isEmpty())
	    throw new InvalidObjectException(
		"exceptions field contains an empty collection");

 	for (Iterator i=exceptions.iterator(); i.hasNext();) {
	    final Object e = i.next();
	    if (e == null)
		throw new InvalidObjectException(
                    "The Collection referenced by the exceptions field " +
		    "contains a null element");

	    if (!(e instanceof UnusableEntryException)) {
		throw new InvalidObjectException(
                    "The Collection referenced by the exceptions field " +
		    "contains an element which is not an UnusableEntryException");
	    }         
	}	    
	
	if (entries == null)
	    throw new InvalidObjectException("entries field is null");

 	for (Iterator i=entries.iterator(); i.hasNext();) {
	    final Object e = i.next();
	    if (e == null)
		throw new InvalidObjectException(
                    "The Collection referenced by the entries field " +
		    "contains a null element");


	    if (!(e instanceof Entry)) {
		throw new InvalidObjectException(
                    "The Collection referenced by the entries field " +
		    "contains an element which is not an Entry");
	    }         
	}	    
    }

    /** 
     * @throws InvalidObjectException if called
     */
    private void readObjectNoData() throws InvalidObjectException {
	throw new InvalidObjectException(
	    "UnusableEntriesException should always have data");
    }

    /**
     * Returns a {@link Collection} of the entries that could be
     * unmarshalled by the operation. If no entries could be
     * unmarshalled, an empty <code>Collection</code> will be returned. 
     * Result may be immutable.
     * @return a {@link Collection} of {@link Entry}
     *         instances that could be unmarshalled 
     */
    public Collection getEntries() {
	return entries;
    }

    /**
     * Returns a {@link Collection} of {@link UnusableEntryException}s
     * with one element for each {@link Entry} that could not be
     * unmarshalled by the operation.  Will be non-<code>null</code>
     * and non-empty. Result may be immutable.
     * @return a {@link Collection} of {@link UnusableEntryException} 
     *         instances with one element for each {@link Entry} that
     *         that could not be unmarshalled 
     */
    public Collection getUnusableEntryExceptions() {
	return exceptions;
    }
}

