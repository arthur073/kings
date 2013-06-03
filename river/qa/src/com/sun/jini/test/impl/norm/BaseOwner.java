/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sun.jini.test.impl.norm;

import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.UnknownLeaseException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Base class for owners that don't cause failures. Does basic management
 * of the expiration and interaction with the test.  Relies on 
 * abstract methods to validate extension and create/register leases.
 * <p>
 * Renews the lease when asked. Ensures that the lease has not expired
 * and that each time it is renewed it has be asked for the correct
 * duration 
 */
abstract public class BaseOwner extends LeaseOwner {

    protected static Logger logger = Logger.getLogger("com.sun.jini.qa.harness.test");

    /** String to be returned by <code>didPass</code> */
    protected String rslt = null;

    /** Max time we are will to grant on a renewal request */
    final private long maxExtension;

    /** Desired expiration renewal service should be renewing towards */
    final protected long desiredExpiration;

    /** The max value we expect for each renewal request */
    final protected long desiredRenewal;

    /** Max acceptable varaion from desiredExpiration for a renewal request */
    final protected long slop;

    /** Current expiration time of lease */
    protected long expiration;

    /** Time of last renewal */
    private long lastRenewal;

    /** Object to notify if we fail */
    private Object notifyOnFailure;

    /** In the context of a batch renew call the current time */
    protected long now;

    /** 
     * Simple constructor 
     * @param initialExpiration Initial expiration time for lease.
     * @param maxExtension Maximum time this owner will be willing to extend 
     *                     the lease
     * @param desiredExpiration Every lease a renewal service is renewing
     *              has a "membership expiration" associated with it.  The
     *              renewal service should all-ways pick a renewal duration
     *              that when added to the current time equals this membership
     *              expiration time.
     * @param slop Allowable variance from desired expiration when making a
     *             renewal request.
     * @param desiredRenewal
     *             Expect value of the renewDuration parameter
     * @param notifyOnFailure
     *             Object to notify if there is a failure
     */
    public BaseOwner(long initialExpiration, long maxExtension,
			long desiredExpiration, long slop,
			long desiredRenewal, Object notifyOnFailure) 
    {
	this.desiredRenewal = desiredRenewal;
	this.maxExtension = maxExtension;
	this.desiredExpiration = desiredExpiration;
	this.slop = slop;
	expiration = initialExpiration;
	this.notifyOnFailure = notifyOnFailure;
    }

    /**
     * Retrun true if the lease has not expired
     */
    private boolean ensureCurrent(long now) {
	return now < expiration;
    }

    /**
     * Return null if we dected no error, and a disciptive string otherwise
     */
    String didPass() {
	if (rslt != null) 
	    return rslt;

	// Make sure the lease has not expired (unless it should have)
	synchronized (this) {
	    final long now = System.currentTimeMillis();
	    final long diff = Math.abs(desiredExpiration - expiration);

	    if (!ensureCurrent(now) && diff > slop)
		rslt = "Lease expired";
	}

	return rslt;	    
    }
    
    /**
     * Set rslt string if it is not already set and set renwals to zero
     */
    protected void setRsltIfNeeded(String newResult) {
	if (rslt == null) {	    
	    rslt = newResult;
	}
    }

    /**
     * Return true if the passed extension is valid for this lease
     * called from batchRenew() so the now is valid.  If the 
     * extension is invalid should return false and set rslt
     */
    abstract boolean isValidExtension(long extension);

    private synchronized long batchRenewSync(long extension) 
	throws UnknownLeaseException, LeaseDeniedException 
    {
	now = System.currentTimeMillis();
	lastRenewal = now;

	// Has the lease expired?
	if (!ensureCurrent(now)) {
	    setRsltIfNeeded("Renwal service let lease expire, " +
			    (now - expiration) + " ms late");
	    throw new UnknownLeaseException("Lease expired");
	}

	// Have the asked for the right renewal?
	if (!isValidExtension(extension))
	    throw new LeaseDeniedException("Did not ask for right extension");
		
	// Seem to ask for the right extension
	// Calculate the renewal grant __DURATION__
	
	long grant;
	if (extension == Lease.ANY) {
	    grant = maxExtension;
	} else {
	    grant = Math.min(maxExtension, extension);
	}
	
	expiration = now + grant;
	if (expiration < 0) {
	    // Overflow
	    expiration = Long.MAX_VALUE;
	    grant = Long.MAX_VALUE - now;
	}
	
	return grant;
    }

    // Inherit java doc from super type
    public long batchRenew(long extension) 
	     throws UnknownLeaseException, LeaseDeniedException 
    {
	try {
	    return batchRenewSync(extension);
	} finally {
	    // If after all this rslt is non null we have to notify 
	    // notifyOnFailure
	    if (rslt != null) {
		synchronized (notifyOnFailure) {
		    notifyOnFailure.notifyAll();
		} 
	    }
	}
    }

    /**
     * Return the lease desired duration we are shotting for
     */
    long getDesiredDuration() {
	if (desiredExpiration  == Lease.FOREVER) 
	    return Lease.FOREVER;
	else
	    return desiredExpiration - System.currentTimeMillis();
    }

    /**
     * Return true if the lease assocated with this owner should
     * be registered using the two arg form
     */
    abstract boolean isTwoArg();

    /**
     * Return the desired renewal request
     */
    long getDesiredRenewal() {
	return desiredRenewal;
    }

    // Inherit java doc from super type
    public Object renew(long extension) 
	throws UnknownLeaseException, LeaseDeniedException
    {
	return new Long(batchRenew(extension));
    }

    // Inherit java doc from super type
    public void batchCancel() {
	setRsltIfNeeded("Renewal Service canceld lease!");
    }

    // Inherit java doc from super type
    public Throwable cancel() {
	batchCancel();
	return null;
    }

    /** 
     * Returns the expiration time of the lease
     */
    public synchronized long getExpiration() {
	return expiration;
    }

    /** 
     * Returns the time of the last renewal call
     */
    public synchronized long getLastRenewTime() {
	return lastRenewal;
    }    
}
