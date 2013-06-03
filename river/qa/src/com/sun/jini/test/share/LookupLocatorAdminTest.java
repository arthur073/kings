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

package com.sun.jini.test.share;

import java.util.logging.Level;

// Test harness specific classes
import com.sun.jini.qa.harness.QAConfig;
import com.sun.jini.qa.harness.TestException;

// All other imports
import java.rmi.*;
import net.jini.admin.JoinAdmin;
import java.util.Set;
import net.jini.core.discovery.LookupLocator;

public class LookupLocatorAdminTest extends LookupTestBase {
    private JoinAdmin joinAdmin;
    static final private LookupLocator[] LLarray = new LookupLocator[0];
    private LookupLocator[] originalState;

    public void setup(QAConfig config) throws Exception {
	super.setup(config);
	this.parse();
    }

    protected void checkLocators(Set locators, String op)
	throws RemoteException, TestException
    {
	LookupLocator[] fromService = joinAdmin.getLookupLocators();
	LookupLocator[] shouldBe = (LookupLocator[])locators.toArray(LLarray);
	if (!arraysEqual(shouldBe, fromService)) {
	    logger.log(Level.INFO, op + " did not work");
	    logger.log(Level.INFO, "Should have:");
	    dumpArray(shouldBe, ",");
	    logger.log(Level.INFO, "has:");
	    dumpArray(fromService, ",");
	    throw new TestException(op + " did not work");
	}
    }


    public void run() throws Exception {
	init();
	joinAdmin = (JoinAdmin)admin;

	if (!noCleanup) {
	    try {
		originalState = joinAdmin.getLookupLocators();
		logger.log(Level.INFO, "Storing inital LookupLocator state:\n\t");
		dumpArray(originalState, ",");
	    } catch (Throwable t) {
		setupFailure(
		    "Could not get inital state for future restoration", t);
	    }
	}

	Set locators = new java.util.HashSet();

	locators.add(QAConfig.getConstrainedLocator("jini://recycle"));
	locators.add(QAConfig.getConstrainedLocator("jini://recycle:5008"));
	locators.add(QAConfig.getConstrainedLocator("jini://sugarloaf"));

	// Put the locators in a known state
	joinAdmin.setLookupLocators((LookupLocator[])
				locators.toArray(LLarray));
	checkLocators(locators, "Set");

	joinAdmin.setLookupLocators((LookupLocator[])
				    locators.toArray(LLarray));
	checkLocators(locators, "Set Two");

	// Add
	locators.add(QAConfig.getConstrainedLocator("jini://oreo"));
	locators.add(QAConfig.getConstrainedLocator("jini://photoplod"));
	locators.add(QAConfig.getConstrainedLocator("jini://elemendorf"));
	joinAdmin.addLookupLocators(new LookupLocator[]
				    {QAConfig.getConstrainedLocator("jini://oreo")});
	joinAdmin.addLookupLocators((LookupLocator[])
				    locators.toArray(LLarray));
	checkLocators(locators, "Add");

	// Remove
	locators.remove(QAConfig.getConstrainedLocator("jini://photoplod"));
	locators.remove(QAConfig.getConstrainedLocator("jini://elemendorf"));
	joinAdmin.removeLookupLocators(new LookupLocator[] {
	    QAConfig.getConstrainedLocator("jini://photoplod"),
	    QAConfig.getConstrainedLocator("jini://elemendorf")
	});
	checkLocators(locators, "Remove");

	if (tryShutdown) {
	    shutdown();

	    if (activatable) {
		joinAdmin.getLookupLocators();
		logger.log(Level.INFO, "Checking locators");
		checkLocators(locators, "Restart");
	    } else {
		try {
		    joinAdmin.getLookupLocators();
		    throw new TestException("Sevice did not go away");
		} catch (RemoteException e) {
		    // This is what we are looking for
		}
	    }
	}
    }

    /** Performs cleanup actions necessary to achieve a graceful exit of
     *  the current QA test.
     *  @exception TestException will usually indicate an "unresolved"
     *  condition because at this point the test has completed.
     */
    public void tearDown() {
	try {
	    if (!noCleanup) {
		try {
		    logger.log(Level.INFO, "Restoring inital LookupLocator state:\n\t");
		    dumpArray(originalState, ",");
		    ((JoinAdmin)admin).setLookupLocators(originalState);
		    logger.log(Level.INFO, "success");
		} catch (Throwable t) {
		    cleanupFailure("Could not restore inital state", t);
		}

	    }
	} catch (Exception ex) {
	    String message = "Warning: Test LookupLocatorAdminTest did not shutdown " +
			     "cleanly!\n" + "Reason: " + ex.getMessage();
	    logger.log(Level.INFO, message);
	    ex.printStackTrace();
	}
        super.tearDown();
    }

    /**
     * Return an array of String whose elements comprise the
     * categories to which this test belongs.
     */
    public String[] getCategories() {
	return new String[] { "admin" };
    }

    /**
     * Return a String which describes this test
     */
    public String getDescription() {
	return "Test Name = LookupLocatorAdminTest : \n" +
		"Test tests different operations with locators.";
    }

}



