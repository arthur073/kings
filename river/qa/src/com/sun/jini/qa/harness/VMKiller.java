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

package com.sun.jini.qa.harness;

import java.rmi.RemoteException;
import java.rmi.Remote;
import com.sun.jini.admin.DestroyAdmin;
import net.jini.admin.Administrable;

/**
 * An interface for a service which kills its own VM
 */

public interface VMKiller extends Remote, Administrable, DestroyAdmin {

    /**
     * Kill the VM.
     *
     * @throws RemoteException if a communications error occurs when
     *         calling the backend service
     */
    public void killVM() throws RemoteException;
}
