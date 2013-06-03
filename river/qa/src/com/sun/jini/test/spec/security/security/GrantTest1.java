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
package com.sun.jini.test.spec.security.security;

import java.util.logging.Level;

// java
import java.util.PropertyPermission;
import java.security.Policy;
import java.security.Permission;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;
import javax.security.auth.Subject;

// net.jini
import net.jini.security.Security;

// com.sun.jini
import com.sun.jini.qa.harness.TestException;
import com.sun.jini.qa.harness.QATest;
import com.sun.jini.test.spec.security.util.Util;
import com.sun.jini.test.spec.security.util.BasePolicyProvider;
import com.sun.jini.test.spec.security.util.BaseDynamicPolicyProvider;
import com.sun.jini.test.spec.security.util.DynamicPolicyProviderThrowingUOE;
import com.sun.jini.test.spec.security.util.DynamicPolicyProviderThrowingSE;
import com.sun.jini.test.spec.security.util.DynamicPolicyProviderThrowingNPE;
import com.sun.jini.test.spec.security.util.TrueDynamicPolicyProvider;
import com.sun.jini.test.spec.security.util.FakePrincipal;


/**
 * <pre>
 * Purpose
 *   This test verifies the following:
 *     'grant(Class, Permission[])' static method of Security class does the
 *     following: if the installed security policy provider implements the
 *     DynamicPolicy interface, delegates to it to grant the specified
 *     permissions to all protection domains (including ones not yet created)
 *     which are associated with the class loader of the given class and
 *     possess at least the principals of the current subject (if any).
 *     The current subject is determined by calling Subject.getSubject method
 *     on the context returned by AccessController.getContext method.
 *     It throws UnsupportedOperationException if the installed security policy
 *     provider does not support dynamic permission grants. It rethrows
 *     exceptions thrown by grant method of installed policy provider.
 *
 * Infrastructure
 *   This test requires the following infrastructure:
 *     NonDynamicPolicyProvider - policy provider which does not implement
 *             DynamicPolicy interface
 *     TestDynamicPolicyProvider - test policy provider implementing
 *             DynamicPolicy interface
 *     DynamicPolicyProviderThrowingSE - policy provider whose
 *             'grant(Class, Principal[], Permission[])' method always throws
 *             SecurityException
 *     DynamicPolicyProviderThrowingUOE - policy provider whose
 *             'grant(Class, Principal[], Permission[])' method always throws
 *             UnsupportedOperationException
 *     DynamicPolicyProviderThrowingNPE - policy provider whose
 *             'grant(Class, Principal[], Permission[])' method always throws
 *             NullPointerException
 *
 * Action
 *   The test performs the following steps:
 *     1) set current policy provider to NonDynamicPolicyProvider
 *     2) invoke 'grant(Class, Permission[])' static method of Security class
 *     3) assert that UnsupportedOperationException will be thrown
 *     4) set current policy provider to DynamicPolicyProviderThrowingUOE
 *     5) invoke 'grant(Class, Permission[])' static method of Security class
 *     6) assert that 'grant(Class, Principal[], Permission[])' method of
 *        installed DynamicPolicyProviderThrowingUOE will be invoked with
 *        the same Class and Permission[] parameters, and Principal[] parameter
 *        containing the same set of principals as Subject.getSubject method
 *        on the context returned by AccessController.getContext method
 *     7) assert that UnsupportedOperationException will be thrown
 *     8) set current policy provider to DynamicPolicyProviderThrowingSE
 *     9) invoke 'grant(Class, Permission[])' static method of Security class
 *     10) assert that 'grant(Class, Principal[], Permission[])' method of
 *         installed DynamicPolicyProviderThrowingSE will be invoked with
 *         the same Class and Permission[] parameters, and Principal[] parameter
 *         containing the same set of principals as Subject.getSubject method
 *         on the context returned by AccessController.getContext method
 *     11) assert that SecurityException will be thrown
 *     12) set current policy provider to DynamicPolicyProviderThrowingSE
 *     13) invoke 'grant(Class, Permission[])' static method of Security class
 *     14) assert that 'grant(Class, Principal[], Permission[])' method of
 *         installed DynamicPolicyProviderThrowingNPE will be invoked with
 *         the same Class and Permission[] parameters, and Principal[] parameter
 *         containing the same set of principals as Subject.getSubject method
 *         on the context returned by AccessController.getContext method
 *     15) assert that NullPointerException will be thrown
 *     16) set current policy provider to TestDynamicPolicyProvider
 *     17) invoke 'grant(Class, Permission[])' static method of Security class
 *     18) assert that 'grant(Class, Principal[], Permission[])' method of
 *         installed TestDynamicPolicyProvider will be invoked with
 *         the same Class and Permission[] parameters, and Principal[] parameter
 *         containing the same set of principals as Subject.getSubject method
 *         on the context returned by AccessController.getContext method
 *     19) assert that method will return normally
 * </pre>
 */
public class GrantTest1 extends QATest {

    /**
     * This method performs all actions mentioned in class description.
     *
     */
    public void run() throws Exception {
        Class testClass = Class.class;
        Principal testPrin = new FakePrincipal();
        Permission[] testPerm = new Permission[] {
            new PropertyPermission("*", "read") };
        Subject testSubj = new Subject();
        testSubj.getPrincipals().add(testPrin);
        Policy[] testPolicy = new Policy[] {
            new BasePolicyProvider(),
            new DynamicPolicyProviderThrowingUOE(),
            new DynamicPolicyProviderThrowingSE(),
            new DynamicPolicyProviderThrowingNPE(),
            new TrueDynamicPolicyProvider() };
        Class[] expRes = new Class[] {
            UnsupportedOperationException.class,
            UnsupportedOperationException.class,
            SecurityException.class,
            NullPointerException.class,
            null };

        for (int i = 0; i < testPolicy.length; ++i) {
            logger.fine("Policy provider is " + testPolicy[i]);
            Policy.setPolicy(testPolicy[i]);

            try {
                try {
                    Subject.doAs(testSubj,
                            new FakePrivilegedExceptionAction(testClass,
                                    testPerm));
                } catch (PrivilegedActionException pae) {
                    throw pae.getException();
                }

                if (expRes[i] != null) {
                    // FAIL
                    throw new TestException(
                            "Method did not throw any exception while "
                            + expRes[i] + " was expected to be thrown.");
                }

                // PASS
                logger.fine("Method returned normally as expected.");
            } catch (Exception ex) {
                if (expRes[i] == null) {
                    // FAIL
                    throw new TestException(
                            "Method threw " + ex + " while normal return "
                            + "was expected.");
                }

                if (ex.getClass() != expRes[i]) {
                    // FAIL
                    throw new TestException(
                            "Method threw " + ex + " while " + expRes[i]
                            + "was expected.");
                }

                // PASS
                logger.fine(ex.toString() + " was thrown as expected.");
            }

            if (!(testPolicy[i] instanceof BaseDynamicPolicyProvider)) {
                continue;
            }
            Object[] params = ((BaseDynamicPolicyProvider)
                    testPolicy[i]).getGrantParams();

            if (params == null || params.length == 0) {
                // FAIL
                throw new TestException(
                        "'grant' method of " + testPolicy[i]
                        + " was not called.");
            }

            if (!params[0].equals(testClass)) {
                // FAIL
                throw new TestException(
                        "1-st parameter was " + params[0]
                        + " while " + testClass + " was expected.");
            }

            if (params[1] == null || ((Object []) params[1]).length != 1
                    || !((Object []) params[1])[0].equals(testPrin)) {
                // FAIL
                throw new TestException(
                        "2-nd parameter was "
                        + Util.arrayToString((Object []) params[1])
                        + " while " + testPrin + " was expected.");
            }

            if (params[2] == null || !params[2].equals(testPerm)) {
                // FAIL
                throw new TestException(
                        "3-rd parameter was "
                        + Util.arrayToString((Object []) params[2])
                        + " while " + Util.arrayToString(testPerm)
                        + " was expected.");
            }

            // PASS
            logger.fine("'grant' method of installed policy provider "
                    + " was called with correct parameters.");
        }
    }


    /**
     * PrivilegedExceptionAction implementation whose purpose just call
     * 'Security.grant' method with parameters specified in constructor.
     */
    class FakePrivilegedExceptionAction implements PrivilegedExceptionAction {

        /** Stored test class */
        private Class testClass;

        /** Stored array of permissions */
        private Permission[] testPerm;

        /**
         * Constructor whose parameters will be used in 'Security.grant' call.
         *
         * @param testClass test class
         * @param testPerm array of permissions
         */
        public FakePrivilegedExceptionAction(Class testClass,
                Permission[] testPerm) {
            this.testClass = testClass;
            this.testPerm = testPerm;
        }

        /**
         * Call 'Security.grant' with stored parameters.
         *
         * @return null in case of success
         */
        public Object run() throws Exception {
            Security.grant(testClass, testPerm);
            return null;
        }
    }
}
