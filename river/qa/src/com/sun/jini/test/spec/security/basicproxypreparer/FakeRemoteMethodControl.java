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
package com.sun.jini.test.spec.security.basicproxypreparer;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.constraint.MethodConstraints;

/**
 * Emulation proxy with RemoteMethodControl interface.
 */
public class FakeRemoteMethodControl implements RemoteMethodControl {

    MethodConstraints constraints = null;
    RuntimeException setConstraintsTrap = null;
    public FakeRemoteMethodControl setConstraintsReturn = this;

    public RemoteMethodControl setConstraints(MethodConstraints constraints){
        this.constraints = constraints;
        if (setConstraintsTrap != null) {
            throw setConstraintsTrap;
        }
        return setConstraintsReturn;
    };

    public MethodConstraints getConstraints(){
        return constraints;
    };

    /**
     * Exception e will be thrown in next call of setConstraints
     */
    public void catchSetConstraints(RuntimeException e) {
	setConstraintsTrap = e;
    }
    
}
