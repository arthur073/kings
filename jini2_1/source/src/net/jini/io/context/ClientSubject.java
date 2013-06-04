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

package net.jini.io.context;

import javax.security.auth.Subject;

/**
 * A server context element that can supply the client subject for the
 * remote call.
 *
 * @author Sun Microsystems, Inc.
 * @see net.jini.export.ServerContext#getServerContextElement
 * @since 2.0
 **/
public interface ClientSubject {

    /**
     * Returns the authenticated identity of the client as a read-only
     * instance, or <code>null</code> if the client was not authenticated.
     *
     * <p>If the client delegated to the server, then the returned subject
     * contains any derived delegation credentials; the server can then
     * impersonate the client by performing outbound secure calls (or by
     * receiving incoming secure calls) in the context of a
     * <code>Subject.doAs</code> with the client subject.
     *
     * @return  the authenticated identity of the client as a read-only
     *	        instance, or <code>null</code> if the client was not
     *		authenticated
     *
     * @throws SecurityException if a security manager exists and its
     * <code>checkPermission</code> method invoked with the permission
     * <code>{@link
     * ContextPermission}("net.jini.io.context.ClientSubject.getClientSubject")</code>
     * throws a <code>SecurityException</code>
     **/
    public Subject getClientSubject();
}

