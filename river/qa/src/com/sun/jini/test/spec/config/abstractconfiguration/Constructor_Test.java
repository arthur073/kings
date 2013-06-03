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

package com.sun.jini.test.spec.config.abstractconfiguration;

import java.util.logging.Level;
import com.sun.jini.qa.harness.QATest;
import com.sun.jini.qa.harness.QAConfig;
import com.sun.jini.qa.harness.TestException;
import com.sun.jini.qa.harness.TestException;
import com.sun.jini.qa.harness.QAConfig;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * <pre>
 * Purpose:
 *   This test verifies the behavior of the constructor of
 *   AbstractConfiguration class.
 *
 * Infrastructure:
 *   This test requires the following infrastructure:
 *     1) FakeAbstractConfiguration class that implements
 *        AbstractConfiguration
 *
 * Actions:
 *   Test performs the following steps:
 *      construct a FakeAbstractConfiguration object
 *      this constructor calls AbstractConfiguration constructor
 *      assert the object is constructed and no exceptions are thrown;
 * </pre>
 */
public class Constructor_Test extends QATest {

    /**
     * This method performs all actions mentioned in class description.
     */
    public void run() throws Exception {
        new FakeAbstractConfiguration ();
    }
}
