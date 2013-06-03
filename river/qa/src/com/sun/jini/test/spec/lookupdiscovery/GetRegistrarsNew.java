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

package com.sun.jini.test.spec.lookupdiscovery;
import com.sun.jini.qa.harness.QAConfig;

import java.util.logging.Level;

import com.sun.jini.qa.harness.TestException;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;

/**
 * This class verifies that the <code>LookupDiscovery</code> utility
 * operates in a manner consistent with the specification. In particular,
 * this class verifies that the method <code>getRegistrars</code> returns
 * a new array upon each invocation.
 *
 * The environment in which this class expects to operate is as follows:
 * <p><ul>
 *    <li> one or more lookup services, each belonging to a finite set of
 *         member groups
 *    <li> one instance of LookupDiscovery configured to discover the union
 *         of the member groups to which each lookup belongs 
 *    <li> one instance of DiscoveryListener registered with that
 *         LookupDiscovery utility
 * </ul><p>
 * 
 * If the lookup discovery utility functions as specified, then on each
 * separate invocation of the <code>getRegistrars</code> method, a new array
 * containing the <code>ServiceRegistrar</code> instances that are currently
 * discovered will be returned.
 */
public class GetRegistrarsNew extends GetRegistrars {

    /** Executes the current test by doing the following:
     * <p><ul>
     *     <li> verifies that getRegistrars returns the registrars that have
     *          been discovered by the lookup discovery utility
     *     <li> invokes getRegistrars two more times and verifies 
     *          that each invocation returns different arrays having the
     *          same contents
     * </ul>
     */
    public void run() throws Exception {
        super.run();
        logger.log(Level.FINE, "1st call to getRegistrars ...");
        ServiceRegistrar[] regs0 = ldToUse.getRegistrars(); // prepared by lds
        logger.log(Level.FINE, "2nd call to getRegistrars ...");
        ServiceRegistrar[] regs1 = ldToUse.getRegistrars(); // prepared by lds
        if(regs0 == regs1) {
            throw new TestException(
                                 "same array returned on different calls");
        }//endif
        logger.log(Level.FINE, "comparing regs from 1st call "
                          +"with regs from 2nd call ...");

        /* Compare the registrars from the two calls to getRegistrars */
        if(regs0.length != regs1.length) {
            throw new TestException(
                                 "# of registrars from 1st ("
                                 +regs0.length+") != # of registrars from 2nd "
                                 +"call ("+regs1.length+")");
        }//endif
        iLoop:
        for(int i=0;i<regs0.length;i++) {
            LookupLocator curLoc0 = QAConfig.getConstrainedLocator(regs0[i].getLocator());
            for(int j=0;j<regs1.length;j++) {
                if(regs0[i].equals(regs1[j])) {
                    logger.log(Level.FINE, "  OK -- "+curLoc0);
                    continue iLoop;
                }//endif
            }//end loop
            throw new TestException(
                                 "registrar from 1st call NOT in set of "
                                 +"registrars from 2nd call -- "+curLoc0);
        }//end loop
    }//end run

}//end class GetRegistrarsNew

