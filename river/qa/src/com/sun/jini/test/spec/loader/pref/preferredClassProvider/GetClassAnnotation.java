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
package com.sun.jini.test.spec.loader.pref.preferredClassProvider;

import java.util.logging.Level;

// com.sun.jini.qa.harness
import com.sun.jini.qa.harness.TestException;
import com.sun.jini.qa.harness.QAConfig;

// com.sun.jini.qa
import com.sun.jini.qa.harness.QATest;
import com.sun.jini.qa.harness.QAConfig;

// java.io
import java.io.IOException;

// java.net
import java.net.URL;
import java.net.MalformedURLException;

// java.rmi.server
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIClassLoaderSpi;

// java.util.logging
import java.util.logging.Logger;
import java.util.logging.Level;

// java.util.Properties
import java.util.Properties;

// davis packages
import net.jini.loader.pref.PreferredClassLoader;
import net.jini.loader.pref.PreferredClassProvider;

// instrumented preferred class loader
import com.sun.jini.test.spec.loader.util.Item;
import com.sun.jini.test.spec.loader.util.Util;
import com.sun.jini.test.spec.loader.util.QATestPreferredClassProvider;

// test base class
import com.sun.jini.test.spec.loader.pref.AbstractTestBase;


/**
 * <b>Purpose</b><br><br>
 *
 * This test verifies the behavior of the<br>
 * <code>public String getClassAnnotation(Class cl)</code>
 * method of the<br>
 * <code>net.jini.loader.pref.PreferredClassProvider</code>
 * class.
 *
 * <b>Test Description</b><br><br>
 *
 *  This test uses {@link QATestPreferredClassProvider} that is created
 *  passing various parameters to parent PreferredClassProvider constructor.
 *  <br><br>
 *
 *  {@link QATestPreferredClassProvider} should be configured as
 *  java.rmi.server.RMIClassLoaderSpi, so that RMIClassLoader.getClassAnnotation
 *  calls QATestPreferredClassProvider.getClassAnnotation method.
 *  <br><br>
 *
 *  This test should be run with appropriate parameters.
 *  <br>
 *  All parameters should be set via config (properties) file.
 *  <ul><lh>Possible parameters are:</lh>
 *  <li>boolean isHttp: if <code>true</code> then will download classes via
 *                      http, otherwise will download classes via file based
 *                      url</li>
 *  <li>int httpPort: port do download classes via http</li>
 *  <li>boolean requireDlPerm: <code>false</code>, <code>true</code></li>
 *  </ul>
 *
 *  If requireDlPerm is equal to true then this test should be run
 *  with policy.loader policy file, otherwise with policy.loaderNoDlPerm file.
 *  <br>
 *  policy.loader policy file grants needed download permissions.
 *  <br>
 *  policy.loaderNoDlPerm file does not grant needed download permissions.
 *  <br><br>
 *
 *  This test iterates over a set of preferred/non-preferred classes.
 *  There are two sets of classes with the same names there. The first set of
 *  classes can be found in the executing VM's classpath. The second set of
 *  classes are placed in the qa1-loader-pref.jar file and can be downloaded
 *  using http or file based url.
 *  <br><br>
 *  Class {@link Util} has a statically defined lists of all resources
 *  placed in the qa1-loader-pref.jar file. {@link Util#listClasses},
 *  {@link Util#listResources}, {@link Util#listLocalClasses},
 *  {@link Util#listLocalResources} define names and preferred status of
 *  these resources.
 *  <br><br>
 *
 *  <br><br>
 *
 *  For each preferred/non-preferred class this test will try to execute
 *  RMIClassLoader.loadClass passing codebase to qa1-loader-pref.jar and
 *  RMIClassLoader.getClassAnnotation() passing returned class.
 *  <br>
 *  Then this test will verify that:
 *  <ol>
 *   <li>
 *    for non-preferred classes RMIClassLoader.getClassAnnotation() returns
 *    java.rmi.server.codebase property string.
 *   </li>
 *   <li>
 *    for preferred classes RMIClassLoader.getClassAnnotation() returns
 *    the same string as default RMIClassLoader provider
 *   </li>
 *  </ol>
 *
 *  <br><br>
 *
 * <b>Infrastructure</b><br><br>
 *
 * <ol><lh>This test requires the following infrastructure:</lh>
 *  <li> {@link QATestPreferredClassProvider} is an instrumented
 *       PreferredClassProvider using for davis.loader's and davis.loader.pref's
 *       testing.</li>
 *  <li> <code>META-INF/PREFERRED.LIST</code> with a set of
 *       preferred/non-preferred resources. <code>META-INF/PREFERRED.LIST</code>
 *       should be placed in the qa1-loader-pref.jar file.</li>
 *  <li> A first set of resources should be placed in the qa1.jar file, so these
 *       resource can be found in the executing VM's classpath.</li>
 *  <li> A second set of resources should be placed in the qa1-loader-pref.jar,
 *       so these resources can be downloaded.</li>
 * </ol>
 *
 * <br>
 *
 * <b>Actions</b><br><br>
 *
 * <ol>
 *  <li>
 *   run test passing {@link QATestPreferredClassProvider} as
 *   java.rmi.server.RMIClassLoaderSpi
 *  </li>
 *  <li>
 *   Get default RMIClassLoader provider.
 *  </li>
 *  <li>
 *   for each preferred/non-preferred class do the following:
 *   <ol>
 *    <li>
 *     invoke RMIClassLoader.loadClass method passing:
 *     <ul>
 *       <li>codebase - string representation of url to qa1-loader-pref.jar</li>
 *       <li>name - name of preferred/non-preferred class</li>
 *       <li>parent - ClassLoader.getSystemClassLoader()</li>
 *     </ul>
 *    </li>
 *    <li>
 *     call RMIClassLoader.getClassAnnotation() passing returned class
 *    </li>
 *    <li>
 *     for non-preferred classes obtain expected annotation string from
 *     java.rmi.server.codebase property;
 *     for preferred classes obtain expected annotation string
 *     from default RMIClassLoader provider.
 *    </li>
 *    <li>
 *     verify that RMIClassLoader.getClassAnnotation() returned
 *     expected string.
 *    </li>
 *   </ol>
 *  </li>
 * </ol>
 *
 */
public class GetClassAnnotation extends AbstractTestBase {

    /* String to format message string */
    static final String str1 = "preferred class";

    /* String to format message string */
    static final String str2 = "non-preferred class";

    /**
     *
     * run test passing {@link QATestPreferredClassProvider} as
     *     java.rmi.server.RMIClassLoaderSpi
     */
    public void run() throws Exception {
        String message = "";
        ClassLoader parent = Util.systemClassLoader();
        RMIClassLoaderSpi defaultProvider = null;
        String cb = null;

        /*
         * 2) Get default RMIClassLoader provider.
         */
        defaultProvider = RMIClassLoader.getDefaultProviderInstance();

        /*
         * Get codebase to qa1-loader-pref.jar.
         */
        cb = Util.getUrlAddr(isHttp, config, port) + Util.PREFERREDJarFile;

        /*
         * 3) for each preferred/non-preferred class do the following:
         *  - invoke RMIClassLoader.loadClass method passing:
         *    codebase - string representation of url to qa1-loader-pref.jar
         *    name - name of preferred/non-preferred class
         *    parent - ClassLoader.getSystemClassLoader()
         *  - call RMIClassLoader.getClassAnnotation() passing returned
         *    class
         *  - for non-preferred classes obtain expected annotation string
         *    from java.rmi.server.codebase property;
         *    for preferred classes obtain expected annotation string
         *    from default RMIClassLoader provider.
         *  - verify that RMIClassLoader.getClassAnnotation() returned
         *    expected string.
         */
        for (int item = 0; item < Util.listClasses.length; item++) {
            String name = Util.listClasses[item].name;
            boolean pref = Util.listClasses[item].pref;
            Class cl = null;

            try {
                cl = RMIClassLoader.loadClass(cb, name, parent);
            } catch (ClassNotFoundException e) {
                // Do not expect ClassNotFoundException.
                // Tests case with expected ClassNotFoundException
                // is LoadClassesClassNotFoundException
                message += "RMIClassLoader.loadClass(" + cb + ", "
                         + name + ", defaultLoader)\n"
                         + "  throws: " + e.toString() + "\n"
                         + "  expected: " + (pref ? str1 : str2);
                break;
            } catch (MalformedURLException me) {
                // Do not expect MalformedURLException.
                // Tests case with expected MalformedURLException
                // is LoadClassesMalformedURLException
                message += "RMIClassLoader.loadClass(" + cb + ", "
                         + name + ", defaultLoader)\n"
                         + "  throws: " + me.toString() + "\n"
                         + "  expected: " + (pref ? str1 : str2);
                break;
            } catch (SecurityException sex) {
                // Do not expect SecurityException.
                // Tests case with expected SecurityException
                // is LoadClassesSecurityException
                message += "RMIClassLoader.loadClass(" + cb + ", "
                         + name + ", false, defaultLoader)\n"
                         + "  throws: " + sex.toString() + "\n"
                         + "  expected: " + (pref ? str1 : str2);
                break;
            }
            String returned = RMIClassLoader.getClassAnnotation(cl);
            String expected = null;

            if (!pref) {
                // Non-preferred class.
                // Obtain expected annotation string from
                // java.rmi.server.codebase property.
                expected = System.getProperty(RMICODEBASE, null);
            } else {
                // Preferred class.
                // Obtain annotation string from default
                // RMIClassLoader provider.
                expected = defaultProvider.getClassAnnotation(cl);
            }

            if ((returned != null && expected == null) || (returned == null
                    && expected != null) || (returned != null
                    && !returned.equals(expected))) {
                message += "RMIClassLoader.getClassAnnotation("
                         + cl.toString() + ")\n"
                         + "  returned: " + returned + "\n"
                         + "  expected: " + expected + "\n";
                break;
            } else {
                String msg = ""
                           + "RMIClassLoader.getClassAnnotation("
                           + cl.toString() + ")\n"
                           + "  returned: " + returned
                           + "  as expected";
                logger.log(Level.FINE, msg);
            }
        }

        if (message.length() > 0) {
            throw new TestException(message);
        }
    }
}
