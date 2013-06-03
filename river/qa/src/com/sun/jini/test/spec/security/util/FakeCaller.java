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

// java
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;

// net.jini
import net.jini.security.Security;


/**
 * Fake class to which tested permission is granted.
 */
public class FakeCaller {

    /**
     * Invokes 'Security.doPrivileged(PrivilegedAction)' method with given
     * argument. Rethrows any exception thrown by this method.
     *
     * @param act PrivilegedAction for 'doPrivileged' method
     * @return result of this call
     */
    public static Object callDoPrivileged(PrivilegedAction act) {
        return Security.doPrivileged(act);
    }

    /**
     * Invokes 'Security.doPrivileged(PrivilegedExceptionAction)' method with
     * given argument. Rethrows any exception thrown by this method.
     *
     * @param act PrivilegedExceptionAction for 'doPrivileged' method
     * @return result of this call
     */
    public static Object callDoPrivileged(PrivilegedExceptionAction act)
            throws PrivilegedActionException {
        return Security.doPrivileged(act);
    }
}
