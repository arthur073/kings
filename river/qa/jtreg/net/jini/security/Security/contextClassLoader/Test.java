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
/* @test 
 * @summary 5105843: Security class missing doPrivileged around getContextClassLoader
 * @run main/othervm/policy=policy Test
 */

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlException;
import java.util.Collections;
import net.jini.security.Security;

public class Test {
    public static void main(String[] args) throws Exception {
	if (System.getSecurityManager() == null) {
	    System.setSecurityManager(new SecurityManager());
	}
	ClassLoader cl = URLClassLoader.newInstance(new URL[0], null);
	Thread.currentThread().setContextClassLoader(cl);
	Security.verifyCodebaseIntegrity("", null);
	try {
	    Security.verifyObjectTrust("", null, Collections.EMPTY_SET);
	} catch (AccessControlException e) {
	    throw e;
	} catch (SecurityException e) {
	}
    }
}
