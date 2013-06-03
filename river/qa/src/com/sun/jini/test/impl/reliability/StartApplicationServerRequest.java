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
package com.sun.jini.test.impl.reliability;

import java.rmi.MarshalledObject;
import java.io.Serializable;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.InetAddress;
import java.lang.reflect.Constructor;

import com.sun.jini.qa.harness.SlaveRequest;
import com.sun.jini.qa.harness.SlaveTest;

/**
 * A <code>SlaveRequest</code> to start an ApplicationServer.
 */
class StartApplicationServerRequest implements SlaveRequest {

    private String registryHost;
    private transient Thread server;
    private static final Logger logger = Logger.getLogger("com.sun.jini.qa.harness");

    /**
     * Construct the request.
     *
     * @param registryHost the host on which the rmiregistry is
     *        running. This is needed by the constructor of the
     *        ApplicationServer.
     */
    StartApplicationServerRequest(String registryHost) {
	this.registryHost = registryHost;
    }

    /**
     * Called by the <code>SlaveTest</code> after unmarshalling this object.
     * <code>com.sun.jini.test.impl.reliability.ApplicationServer</code>
     * is started in a new thread.
     *
     * @param slaveTest a reference to the <code>SlaveTest</code>
     * @return null
     * @throws Exception if an error occurs starting the service
     */
    public Object doSlaveRequest(SlaveTest slaveTest) throws Exception {
        server = new Thread(new ApplicationServer(registryHost));
        logger.log(Level.INFO, "Starting application server " +
            "on host " + InetAddress.getLocalHost().getHostName());
        server.start();
	return null;
    }
}
