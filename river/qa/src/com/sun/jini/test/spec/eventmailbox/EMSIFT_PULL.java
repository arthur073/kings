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
package com.sun.jini.test.spec.eventmailbox;

import java.util.logging.Level;

import com.sun.jini.qa.harness.TestException;
import com.sun.jini.qa.harness.QAConfig;

import java.rmi.RemoteException;

import com.sun.jini.constants.TimeConstants;

import net.jini.event.RemoteEventIterator;
import net.jini.event.PullEventMailbox;
import net.jini.event.MailboxPullRegistration;
import net.jini.core.lease.Lease;

import com.sun.jini.test.impl.mercury.MailboxTestBase;

/**
 * EventMailboxServiceInterfaceTest_Pull version
 * Tests the pullRegister() method with various in/valid lease durations.
 * Also checks to see that the regsitrations are unique.
 */

public class EMSIFT_PULL extends MailboxTestBase implements TimeConstants {

    private final long DURATION1 = 3*HOURS;
    private final long INVALID_DURATION_NEG = -DURATION1;
    private final long INVALID_DURATION_ZERO = 0;
    private final long VALID_DURATION_ANY = Lease.ANY;

    public void run() throws Exception {
	PullEventMailbox mb = getPullMailbox();        

	MailboxPullRegistration mr1 = getPullRegistration(mb, DURATION1);
	checkLease(getPullMailboxLease(mr1), DURATION1); 

	MailboxPullRegistration mr2 = getPullRegistration(mb, Lease.FOREVER);
	checkLease(getPullMailboxLease(mr2), Lease.FOREVER); 

	MailboxPullRegistration mr3 = getPullRegistration(mb, Lease.ANY);
	checkLease(getPullMailboxLease(mr3), Lease.ANY); 

	MailboxPullRegistration mr4 = null;
	try {
	    mr4 =  getPullRegistration(mb, INVALID_DURATION_NEG);
	    throw new TestException("Illegal negative duration value "
				  + "was accepted");
	} catch (IllegalArgumentException iae) {
	    logger.log(Level.INFO, 
		       "Caught expected invalid duration exception");
	}
	try {
	    mr4 =  getPullRegistration(mb, INVALID_DURATION_ZERO);
	    throw new TestException("Illegal (zero) duration value "
				  + "was accepted");
	} catch (IllegalArgumentException iae) {
	    logger.log(Level.INFO, 
		       "Caught expected invalid duration exception");
	}
	try {
	    mr4 =  getPullRegistration(mb, VALID_DURATION_ANY);
	    logger.log(Level.INFO, "Valid (any) duration value was accepted");
	} catch (IllegalArgumentException iae) {
	    throw new TestException("Valid (any) duration value was "
				  + "not accepted");
	}
	if ((mr1 == mr2) || (mr1 == mr3) || (mr2 == mr3)) 
	    throw new TestException("Service returned non-distinct "
				  + "objects in the \"==\" sense");
	else 
	    logger.log(Level.INFO, 
		       "Service returned distinct objects in the \"==\" sense");

	if (mr1.equals(mr2) || mr1.equals(mr3) || 
	    mr2.equals(mr1) || mr2.equals(mr3) ||
	    mr3.equals(mr1) || mr3.equals(mr2))
	    throw new TestException("Service returned non-distinct objects "
				  + "in the \"equals\" sense");
	else
	    logger.log(Level.INFO, "Service returned distinct objects in "
		       + "the \"equals\" sense");

	if (mr1.equals(mr1) && mr2.equals(mr2) && mr3.equals(mr3)) 
	    logger.log(Level.INFO, 
		       "Identity property holds for the registration objects");
	else
	    throw new TestException("Identity property doesn't hold "
				  + "for the registration objects");
        
        // Check secondary I/Fs
        RemoteEventIterator rei = mr1.getRemoteEvents();
        try {
            rei.next(-1L);
	    throw new TestException("Successfully called iterator "
				  + "with negative timeout.");
	} catch (IllegalArgumentException iae) {
	    logger.log(Level.INFO, 
		       "Caught expected IllegalArgumentException for next()");
	}
    }

    /**
     * Invoke parent's setup and parser
     * @exception TestException will usually indicate an "unresolved"
     *  condition because at this point the test has not yet begun.
     */
    public void setup(QAConfig config) throws Exception {
	super.setup(config);
	parse();
    }
}
