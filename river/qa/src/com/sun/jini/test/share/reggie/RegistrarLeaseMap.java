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
package com.sun.jini.test.share.reggie;

import net.jini.core.lease.*;
import net.jini.core.lookup.ServiceID;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * The LeaseMap implementation class for registrar leases.  Clients only see
 * instances via the LeaseMap interface.
 *
 * 
 *
 */
class RegistrarLeaseMap extends com.sun.jini.lease.AbstractLeaseMap {

    private static final long serialVersionUID = 1840099461579576431L;

    /**
     * The registrar
     *
     * @serial
     */
    private final Registrar server;

    /** Simple constructor */
    public RegistrarLeaseMap(RegistrarLease lease, long duration) {
	super(lease, duration);
	this.server = lease.getRegistrar();
    }

    /** Any RegistrarLease from the same server can be in the map */
    public boolean canContainKey(Object key) {
	return (key instanceof RegistrarLease &&
		server.equals(((RegistrarLease)key).getRegistrar()));
    }

    // This method's javadoc is inherited from an interface of this class
    public void renewAll() throws LeaseMapException, RemoteException {
	int size = map.size();
	if (size == 0)
	    return;
	Object[] regIDs = new Object[size];
	long[] leaseIDs = new long[size];
	long[] durations = new long[size];
	int i = 0;
	for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); i++) {
	    Map.Entry e = (Map.Entry)iter.next();
	    RegistrarLease ls = (RegistrarLease)e.getKey();
	    regIDs[i] = ls.getRegID();
	    leaseIDs[i] = ls.getLeaseID();
	    durations[i] = ((Long)e.getValue()).longValue();
	}
	RenewResults results = server.renewLeases(regIDs, leaseIDs, durations);
	long now = System.currentTimeMillis();
	HashMap emap = (results.exceptions != null) ?
	    	       new HashMap(2 * results.exceptions.length + 1) : null;
	i = 0;
	int j = 0;
	for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); i++) {
	    Map.Entry e = (Map.Entry)iter.next();
	    long duration = results.durations[i];
	    if (duration >= 0) {
		((RegistrarLease)e.getKey()).setExpiration(duration + now);
	    } else {
		emap.put(e.getKey(), results.exceptions[j++]);
		iter.remove();
	    }
	}
	if (emap != null)
	    throw new LeaseMapException("lease renewal failures", emap);
    }

    // This method's javadoc is inherited from an interface of this class
    public void cancelAll() throws LeaseMapException, RemoteException {
	int size = map.size();
	if (size == 0)
	    return;
	Object[] regIDs = new Object[size];
	long[] leaseIDs = new long[size];
	int i = 0;
	for (Iterator iter = map.keySet().iterator(); iter.hasNext(); i++) {
	    RegistrarLease ls = (RegistrarLease)iter.next();
	    regIDs[i] = ls.getRegID();
	    leaseIDs[i] = ls.getLeaseID();
	}
	Exception[] exceptions = server.cancelLeases(regIDs, leaseIDs);
	if (exceptions == null)
	    return;
	i = 0;
	HashMap emap = new HashMap(13);
	for (Iterator iter = map.keySet().iterator(); iter.hasNext(); i++) {
	    RegistrarLease ls = (RegistrarLease)iter.next();
	    Exception ex = exceptions[i];
	    if (ex != null) {
		emap.put(ls, ex);
		iter.remove();
	    }
	}
	throw new LeaseMapException("lease cancellation failures", emap);
    }
}
