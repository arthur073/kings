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
package com.sun.jini.test.spec.javaspace.conformance;

// java.io
import java.io.ObjectStreamException;
import java.io.Serializable;

// java.rmi
import java.rmi.RemoteException;

// net.jini
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.EventRegistration;

import net.jini.export.Exporter;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.security.TrustVerifier;
import net.jini.security.proxytrust.ServerProxyTrust;

import com.sun.jini.proxy.BasicProxyTrustVerifier;

import com.sun.jini.qa.harness.QAConfig;

/**
 * This auxilary class listens for notify events and counts them.
 *
 * 
 */
public class NotifyCounter
    implements RemoteEventListener, ServerProxyTrust, Serializable
{

    /** Template for which this class counts events */
    protected Entry template;

    /** Time for which this listener will count events */
    protected long leaseTime;

    /** number of events */
    protected long maxEvNum;

    /** the proxy */
    private Object proxy;

    private static Configuration configuration;

    public static void setConfiguration(Configuration configuration) {
	NotifyCounter.configuration = configuration;
    }

    public static Configuration getConfiguration() {
	if (NotifyCounter.configuration == null) {
	    throw new IllegalStateException("Configuration not set");
	}
	return NotifyCounter.configuration;
    }

    /**
     * Constructor with no arguments, set template to null, and lease time to 0.
     *
     * @exception RemoteException
     *         If an error occured while trying to export this object.
     */
    public NotifyCounter() throws RemoteException {
        this(null, 0);
    }

    /**
     * Constructor to init fields of the class and register class itself.
     *
     * @param template Template for which this class counts events.
     * @param leaseTime Time for which this listener will cout events.
     *
     * @exception RemoteException
     *         If an error occured while trying to export this object.
     */
    public NotifyCounter(Entry template, long leaseTime)
            throws RemoteException {
	Configuration c = getConfiguration();
	Exporter exporter = QAConfig.getDefaultExporter();
	if (c instanceof com.sun.jini.qa.harness.QAConfiguration) {
	    try {
		exporter = (Exporter) c.getEntry("test",
						 "outriggerListenerExporter",
						 Exporter.class);
	    } catch (ConfigurationException e) {
		throw new RemoteException("Configuration Error", e);
	    }
	}
	proxy = exporter.export(this);
        maxEvNum = 0;
        this.template = template;
        this.leaseTime = leaseTime;
    }

    public Object writeReplace() throws ObjectStreamException {
	return proxy;
    }

    public TrustVerifier getProxyVerifier() {
	return new BasicProxyTrustVerifier(proxy);
    }

    /**
     * Method which counts events.
     *
     * @param ev RemoteEvent received.
     */
    public void notify(RemoteEvent ev) {
        maxEvNum = Math.max(ev.getSequenceNumber(), maxEvNum);
    }

    /**
     * Returns current event's number.
     *
     * @return Current event's number.
     */
    public long getEventsNum(EventRegistration reg) {
        return (maxEvNum - reg.getSequenceNumber());
    }

    /**
     * Returns template for which this class counts events.
     *
     * @return Template.
     */
    public Entry getTemplate() {
        return template;
    }

    /**
     * Returns time for which this listener will count events
     *
     * @return Time in ms.
     */
    public long getLeaseTime() {
        return leaseTime;
    }

    /**
     * Creates the string representation of this counter.
     *
     * @return The string representation.
     */
    public String toString() {
        String leaseStr;

        if (leaseTime == Lease.FOREVER) {
            leaseStr = "Lease.FOREVER";
        } else if (leaseTime == Lease.ANY) {
            leaseStr = "Lease.ANY";
        } else {
            leaseStr = "" + leaseTime;
        }
        return "NotifyCounter: (template = " + template + ", lease time = "
                + leaseStr + ")";
    }
}
