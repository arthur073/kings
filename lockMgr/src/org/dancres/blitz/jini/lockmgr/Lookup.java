/*
 Copyright 2005 Dan Creswell (dan@dancres.org)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 License for the specific language governing permissions and limitations under
 the License.
*/

package org.dancres.blitz.jini.lockmgr;

import java.io.IOException;

import java.rmi.RemoteException;

import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;

import net.jini.core.entry.Entry;

import net.jini.lookup.entry.Name;

import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;

/**
   A class which supports a simple JINI multicast lookup.  It doesn't register
   with any ServiceRegistrars it simply interrogates each one that's
   discovered for a ServiceItem associated with the passed interface class.
   i.e. The service needs to already have registered because we won't notice
   new arrivals. [ServiceRegistrar is the interface implemented by JINI
   lookup services].

   @todo Be more dynamic in our lookups - see above

   @author  Dan Creswell (dan@dancres.org)
   @version 1.00, 7/9/2003
 */
class Lookup implements DiscoveryListener {
    private ServiceTemplate theTemplate;
    private LookupDiscovery theDiscoverer;

    private Object theProxy;

    /**
       @param aServiceInterface the class of the type of service you are
       looking for.  Class is usually an interface class.
     */
    Lookup(Class aServiceInterface) {
        this(aServiceInterface, null);
    }

    Lookup(Class aServiceInterface, String aName) {
        Class[] myServiceTypes = new Class[] {aServiceInterface};

        Entry[] myAttrs = null;

        if (aName != null) {
            myAttrs = new Entry[] {new Name(aName)};
        }

        theTemplate = new ServiceTemplate(null, myServiceTypes, myAttrs);
    }

    /**
       Having created a Lookup (which means it now knows what type of service
       you require), invoke this method to attempt to locate a service
       of that type.  The result should be cast to the interface of the
       service you originally specified to the constructor.

       @return proxy for the service type you requested - could be an rmi
       stub or an intelligent proxy.
     */
    Object getService() {
        synchronized(this) {
            if (theDiscoverer == null) {

                try {
                    theDiscoverer =
                        new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
                    theDiscoverer.addDiscoveryListener(this);
                } catch (IOException anIOE) {
                    System.err.println("Failed to init lookup");
                    anIOE.printStackTrace(System.err);
                }
            }
        }

        return waitForProxy();
    }

    /**
       Location of a service causes the creation of some threads.  Call this
       method to shut those threads down either before exiting or after a
       proxy has been returned from getService().
     */
    void terminate() {
        synchronized(this) {
            if (theDiscoverer != null)
                theDiscoverer.terminate();
        }
    }

    /**
       Caller of getService ends up here, blocked until we find a proxy.

       @return the newly downloaded proxy
     */
    private Object waitForProxy() {
        synchronized(this) {
            while (theProxy == null) {

                try {
                    wait();
                } catch (InterruptedException anIE) {
                }
            }

            return theProxy;
        }
    }

    /**
       Invoked to inform a blocked client waiting in waitForProxy that
       one is now available.

       @param aProxy the newly downloaded proxy
     */
    private void signalGotProxy(Object aProxy) {
        synchronized(this) {
            if (theProxy == null) {
                theProxy = aProxy;
                notify();
            }
        }
    }

    /**
       Everytime a new ServiceRegistrar is found, we will be called back on
       this interface with a reference to it.  We then ask it for a service
       instance of the type specified in our constructor.
     */
    public void discovered(DiscoveryEvent anEvent) {
        synchronized(this) {
            if (theProxy != null)
                return;
        }

        ServiceRegistrar[] myRegs = anEvent.getRegistrars();

        for (int i = 0; i < myRegs.length; i++) {
            ServiceRegistrar myReg = myRegs[i];

            Object myProxy = null;

            try {
                myProxy = myReg.lookup(theTemplate);

                if (myProxy != null) {
                    signalGotProxy(myProxy);
                    break;
                }
            } catch (RemoteException anRE) {
                System.err.println("ServiceRegistrar barfed");
                anRE.printStackTrace(System.err);
            }
        }
    }

    /**
       When a ServiceRegistrar "disappears" due to network partition etc.
       we will be advised via a call to this method - as we only care about
       new ServiceRegistrars, we do nothing here.
     */
    public void discarded(DiscoveryEvent anEvent) {
    }
}
