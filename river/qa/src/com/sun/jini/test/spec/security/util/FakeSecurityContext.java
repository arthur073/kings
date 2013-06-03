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
package com.sun.jini.test.spec.security.util;

// java.security
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.AccessControlContext;

//net.jini
import net.jini.security.SecurityContext;


/**
 * Empty SecurityContext.
 */
public class FakeSecurityContext implements SecurityContext {

    /**
     * Method from SecurityContext. Does nothing.
     *
     * @return null
     */
    public PrivilegedAction wrap(PrivilegedAction a) {
        return null;
    }

    /**
     * Method from SecurityContext. Does nothing.
     *
     * @return null
     */
    public PrivilegedExceptionAction wrap(PrivilegedExceptionAction a) {
        return null;
    }

    /**
     * Method from SecurityContext. Does nothing.
     *
     * @return null
     */
    public AccessControlContext getAccessControlContext() {
        return null;
    }
}
