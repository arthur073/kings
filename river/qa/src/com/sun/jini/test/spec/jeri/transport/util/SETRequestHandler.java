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
package com.sun.jini.test.spec.jeri.transport.util;

//jeri imports
import net.jini.jeri.InboundRequest;
import net.jini.jeri.RequestDispatcher;


//java.util
import java.util.ArrayList;
import java.util.logging.Logger;

//java.io
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

public class SETRequestHandler implements RequestDispatcher {

    ArrayList requestList = new ArrayList();

    public synchronized void dispatch(InboundRequest request) {
        Logger log = AbstractEndpointTest.getLogger();
        log.finest("Received InboundRequest " + request);
        try {
            ObjectInputStream ois = new ObjectInputStream(
                request.getRequestInputStream());
            int value = ois.readInt();
            requestList.add(new Integer(value));
            ObjectOutputStream oos = new ObjectOutputStream(
                request.getResponseOutputStream());
            oos.writeInt(this.hashCode());
            oos.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public synchronized ArrayList getRequests() {
        return requestList;
    }

}
