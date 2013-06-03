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
package com.sun.jini.test.spec.security.proxytrust.util;

import java.util.logging.Level;

// net.jini
import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.security.proxytrust.TrustEquivalence;


/**
 * Remote serializable class implementing RemoteMethodControl and
 * TrustEquivalence interfaces.
 */
public class RMCTEImpl extends RMCImpl implements TrustEquivalence {

    // number of 'setConstraints' method invocations
    protected int setConstraintsNum;

    // last called 'setConstraints' method's argument
    protected Object setConstraintsArg;

    /**
     * Default constructor.
     */
    public RMCTEImpl() {
        setConstraintsNum = 0;
        setConstraintsArg = null;
    }

    /**
     * Method from RemoteMethodControl interface.
     * Increase number of this method invocations by one.
     * Write argument to setConstraintsArg variable.
     *
     * @return this class
     */
    public RemoteMethodControl setConstraints(MethodConstraints constraints) {
        ++setConstraintsNum;
        setConstraintsArg = constraints;
        return this;
    }

    /**
     * Method from RemoteMethodControl interface. Does nothing.
     *
     * @return null
     */
    public MethodConstraints getConstraints() {
        return null;
    }

    /**
     * Returns number of 'setConstraints' method invocations.
     *
     * @return number of 'setConstraints' method invocations
     */
    public int getSetConstraintsNum() {
        return setConstraintsNum;
    }

    /**
     * Returns argument passed to last called 'setConstraints' method.
     *
     * @return argument passed to last called 'setConstraints' method
     */
    public Object getSetConstraintsArg() {
        return setConstraintsArg;
    }

    /**
     * Method from TrustEquivalence interface. Does nothing.
     *
     * @return false
     */
    public boolean checkTrustEquivalence(Object obj) {
        return false;
    }
}
