/* lime - A middleware for coordination of mobile agents and mobile hosts
 * Copyright (C) 2000,
 * Amy L. Murphy, Gian Pietro Picco, and Gruia-Catalin Roman
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

import lights.interfaces.*;
import lights.adapters.*;
import java.net.*;
import java.io.*;
import java.util.*;
import lime.*;

/**
 * <p> This class provides an extension to the regular LimeTupleSpace which
 * provides weak, ubiquitous operations on the federated tuple space.  These
 * operations are not performed atomically over a snapshot of the federated
 * tuple space, but instead are performed individually and incrementally over
 * the hosts which are present when the operation is invoked.  For example, if
 * hosts A, B and C have agents with transiently shared tuple spaces of the
 * same name, if an agent on host A issues one of these weak operations, the
 * result returned will be the cumulative result of probing operation
 * performed first on A, then on B, then on C.  If B or C disconnect before
 * the operation is complete, it is as if the result of the probe of that host
 * returned <code>null</code>.  If host D connects during the operation, D is
 * ignored for this operation.
 *
 * <p>To use this class, an agent should create an instance of a
 * <code>WeakProbeLimeTupleSpace</code> instead of a
 * <code>LimeTupleSpace</code>.  All normal operations of a
 * <code>LimeTupleSpace</code> are available through a
 * <code>WeakProbeLimeTupleSpace</code>, and it is possible for an agent with
 * an instance of this class to issue one of the specific operations of this
 * class on tuple spaces which are not of this type.  In other words, the
 * agents on host B and C of the example above need not have tuple spaces of
 * type <code>WeakProbeLimeTupleSpace</code>...only the agent issuing the
 * operation must have a tuple space of this type.
 *
 * <p>It should be noted that none of the weak ubiquitous operations should be
 * called from within a reaction as an exception will be thrown.
 *
 * @author <a href="mailto:murphy@cs.rochester.edu">Amy L. Murphy</a>
 * @version 1.0 
 * @see LimeTupleSpace */
public class WeakProbeLimeTupleSpace extends LimeTupleSpace {

  // a vector containing the set of hosts in the current Lime configuration.
  // Each entry of the vector is a HostLocation
  Vector hostsInSystem = new Vector(10);

  HostLocation thisHostLocation = new HostLocation(LimeServer.getServerID());
  ITuple hostPattern = new Tuple().addActual("_host").addFormal(LimeServerID.class); 

  static final short WEAKRDP = 1;
  static final short WEAKINP = 2;
  static final short WEAKING = 3;
  static final short WEAKRDG = 4;

  // This is done to allow the WeakProbeLimeTupleSpace to be used inside a
  // mobile agent.  A mobile agent does not migrate with its LSTS, but instead
  // rebinds to the new one at each destination.

  private transient LimeSystemTupleSpace tlsts = new LimeSystemTupleSpace();

  LimeSystemTupleSpace lsts() {
    if(tlsts==null) tlsts = new LimeSystemTupleSpace();
    if(tlsts==null) 
      throw new NullPointerException("Problems in the system tuple space");
    return tlsts;
  }

  /**
   * Creates a new <code>WeakProbeLimeTupleSpace</code> instance.  Note, all
   * the usual LimeTupleSpace operations can be called on an object of this
   * type.
   *
   * @param name a <code>String</code> for the name of the tuple space
   * @exception TupleSpaceEngineException if an error occurs */
  public WeakProbeLimeTupleSpace (String name) throws TupleSpaceEngineException {
    super (name);
  }

  /**
   * Creates a new <code>WeakProbeLimeTupleSpace</code> instance with the
   * default name. Note, all the usual LimeTupleSpace operations can be called
   * on an object of this type.
   *
   * @exception TupleSpaceEngineException if an error occurs */
  public WeakProbeLimeTupleSpace () throws TupleSpaceEngineException {
    super();
  }


  ///////////////////////////////////////////
  // operations without destination specified
  ///////////////////////////////////////////


  /**
   * Retrieves a copy of a tuple that matches a given template, or returns
   * <code>null</code> if no matching tuple exists.  The whole transiently
   * shared tuple space is searched, but the search is NOT done on a snapshot
   * of the federated space.  Instead, the operation sequentially searches the
   * hosts which are connected when the operation is issued.  If multiple
   * tuples match the template, one is selected non-deterministically.  If no
   * matching tuple is present, <code>null</code> is returned.
   *
   * @param template the template the return tuple must be matched against.
   * @return a copy of a tuple that matches the template, or <code>null</code>
   *   if the tuple cannot be found.
   * @exception TupleSpaceEngineException if a problem is encountered in the
   * processing. */
  public ITuple weakRdp(ITuple template) 
    throws TupleSpaceEngineException {
    return weakRdp(AgentLocation.UNSPECIFIED, template);
  }

  /**
   * Retrieves a tuple that matches a given template and removes it from the
   * tuple space, or returns <code>null</code> if no matching tuple exists.
   * The whole transiently shared tuple space is searched, but the search is
   * NOT done on a snapshot of the federated space.  Instead, the operation
   * sequentially searches the hosts which are connected when the operation is
   * issued.  If multiple tuples match the template, one is selected
   * non-deterministically.  If no matching tuple is present,
   * <code>null</code> is returned.
   *
   * @param template the template the return tuple must be matched against.
   * @return a copy of a tuple that matches the template, or <code>null</code>
   *   if the tuple cannot be found.
   * @exception TupleSpaceEngineException if a problem is encountered in the
   * processing. */
  public ITuple weakInp (ITuple template) 
    throws TupleSpaceEngineException {
    return weakInp(AgentLocation.UNSPECIFIED, template);
  }

  /**
   * Retrieves all tuples that match a given template and removes them from
   * the tuple space, or returns <code>null</code> if no matching tuples
   * exist.  The whole transiently shared tuple space is searched, but the
   * search is NOT done on a snapshot of the federated space.  Instead, the
   * operation sequentially searches the hosts which are connected when the
   * operation is issued.  If no matching tuples are present,
   * <code>null</code> is returned.
   *
   * @param template the template the return tuples must be matched against.
   * @return a copy of a tuples that match the template, or <code>null</code>
   *   if no tuples can be found.
   * @exception TupleSpaceEngineException if a problem is encountered in the
   * processing. */
  public ITuple[] weakIng (ITuple template) 
    throws TupleSpaceEngineException {
    return weakIng(AgentLocation.UNSPECIFIED, template);
  }

  /**
   * Retrieves all tuples that match a given template, or returns
   * <code>null</code> if no matching tuples exist.  The whole transiently
   * shared tuple space is searched, but the search is NOT done on a snapshot
   * of the federated space.  Instead, the operation sequentially searches the
   * hosts which are connected when the operation is issued.  If no
   * matching tuples are present, <code>null</code> is returned.
   *
   * @param template the template the return tuples must  match against.
   * @return a copy of a tuples that match the template, or <code>null</code>
   *   if no tuples can be found.
   * @exception TupleSpaceEngineException if a problem is encountered in the
   * processing. */
  public ITuple[] weakRdg (ITuple template) 
    throws TupleSpaceEngineException {
    return weakRdg(AgentLocation.UNSPECIFIED, template);
  }



  // operations with a specified destination

  /**
   * Retrieves a copy of a tuple that matches a given template, or returns
   * <code>null</code> if no matching tuple exists.  The whole transiently
   * shared tuple space is searched, but the search is NOT done on a snapshot
   * of the federated space.  Instead, the operation sequentially searches the
   * hosts which are connected when the operation is issued.  If multiple
   * tuples match the template, one is selected non-deterministically.  If no
   * matching tuple is present, <code>null</code> is returned.
   *
   * @param destination the destination location of the tuple that can be
   *   searched. The value <code>AgentLocation.UNSPECIFIED</code> is allowed, in
   *   which case only the current location will matter.
   * @param template the template the return tuple must be matched against.
   * @return a copy of a tuple that matches the template, or <code>null</code>
   *   if the tuple cannot be found.
   * @exception TupleSpaceEngineException if a problem is encountered in the
   * processing. */
  public ITuple weakRdp (AgentLocation destination, ITuple template) 
    throws TupleSpaceEngineException {
    return doWeak(destination, template, WEAKRDP);
  }

  /**
   * Retrieves a tuple that matches a given template and removes it from the
   * tuple space, or returns <code>null</code> if no matching tuple exists.
   * The whole transiently shared tuple space is searched, but the search is
   * NOT done on a snapshot of the federated space.  Instead, the operation
   * sequentially searches the hosts which are connected when the operation is
   * issued.  If multiple tuples match the template, one is selected
   * non-deterministically.  If no matching tuple is present,
   * <code>null</code> is returned.
   *
   * @param destination the destination location of the tuple that can be
   *   searched. The value <code>AgentLocation.UNSPECIFIED</code> is allowed, in
   *   which case only the current location will matter.
   * @param template the template the return tuple must be matched against.
   * @return a copy of a tuple that matches the template, or <code>null</code>
   *   if the tuple cannot be found.
   * @exception TupleSpaceEngineException if a problem is encountered in the
   * processing. */
  public ITuple weakInp (AgentLocation destination, ITuple template) 
    throws TupleSpaceEngineException {
    return doWeak(destination, template, WEAKINP);
  }

  /**
   * Retrieves all tuples that match a given template and removes them from
   * the tuple space, or returns <code>null</code> if no matching tuples
   * exist.  The whole transiently shared tuple space is searched, but the
   * search is NOT done on a snapshot of the federated space.  Instead, the
   * operation sequentially searches the hosts which are connected when the
   * operation is issued.  If no matching tuples are present,
   * <code>null</code> is returned.
   *
   * @param destination the destination location of the tuple that can be
   *   searched. The value <code>AgentLocation.UNSPECIFIED</code> is allowed, in
   *   which case only the current location will matter.
   * @param template the template the return tuple must be matched against.
   * @return a copy of a tuples that match the template, or <code>null</code>
   *   if no tuples can be found.
   * @exception TupleSpaceEngineException if a problem is encountered in the
   * processing. */
  public ITuple[] weakIng (AgentLocation destination, ITuple template) 
    throws TupleSpaceEngineException {
    ITuple t = doWeak(destination, template, WEAKING);
    if (t==null)  return null;
    else return (ITuple[]) t.get(0).getValue();
  }

  /**
   * Retrieves all tuples that match a given template, or returns
   * <code>null</code> if no matching tuples exist.  The whole transiently
   * shared tuple space is searched, but the search is NOT done on a snapshot
   * of the federated space.  Instead, the operation sequentially searches the
   * hosts which are connected when the operation is issued.  If no matching
   * tuples are present, <code>null</code> is returned.
   *
   * @param destination the destination location of the tuple that can be
   *   searched. The value <code>AgentLocation.UNSPECIFIED</code> is allowed, in
   *   which case only the current location will matter.
   * @param template the template the return tuple must be matched against.
   * @return a copy of a tuples that match the template, or <code>null</code>
   *   if the tuple cannot be found.
   * @exception TupleSpaceEngineException if a problem is encountered in the
   * processing. */
  public ITuple[] weakRdg (AgentLocation destination, ITuple template)
    throws TupleSpaceEngineException {
    ITuple t = doWeak(destination, template, WEAKRDG);
    if (t==null) return null;
    else return  (ITuple[]) t.get(0).getValue();
  }



  // returns either the tuple returned, or an array of tuples as the first
  // field of the returned tuple
  ITuple doWeak(AgentLocation destination, ITuple template, short type)
    throws TupleSpaceEngineException {
    ITuple result = null;
    
    ITuple [] matchingTuples = null;

    // always look locally
    switch(type){
    case WEAKRDP:
      result = rdp(thisHostLocation, destination, template);
      break;
    case WEAKINP:
      result = inp(thisHostLocation, destination, template);
      break;
    case WEAKRDG:
      matchingTuples = rdg(thisHostLocation, destination, template);
      break;
    case WEAKING:
      matchingTuples = ing(thisHostLocation, destination, template);
      break;
    }


    // if the ts is public, then cycle through all connected hosts.  If one is
    // found (for inp and rdp), stop cycling.  Note, in the case of group
    // operations, the result variable will remain null until all hosts have
    // been proved (keeping the loop from exiting early without checking all
    // of the connected hosts).
    if (isShared() && result == null) {
      ITuple[] hosts = lsts().rdg(hostPattern);
      int i = 0;
      while (result == null && i<hosts.length) {
        HostLocation aHost = new HostLocation((LimeServerID)hosts[i].get(1).getValue());
        //System.out.println("aHost = "+aHost+"     thisHostLocation = "+thisHostLocation);
        if (!aHost.equals(thisHostLocation)) { // don't look locally again
          switch(type) {
          case WEAKRDP: 
            result = rdp(aHost, destination, template);
            break;
          case WEAKINP:
            result = inp(aHost, destination, template);
            break;
          case WEAKRDG:
            ITuple[] tmpResultRdg = rdg(aHost, destination, template);
            matchingTuples = appendArrays(matchingTuples, tmpResultRdg);
            break;
          case WEAKING:
            ITuple[] tmpResultIng = ing(aHost, destination, template);
            matchingTuples = appendArrays(matchingTuples, tmpResultIng);
            break;
          }
        } // end if
        i++; // go to the next host in the list retrieved from the LSTS
      }  // end while
    }  // end if 

    if (matchingTuples != null) 
      result = new Tuple().addActual(matchingTuples);

    return result;
  }
  

  ITuple [] appendArrays (ITuple[] a1, ITuple[] a2) {
    int a1Length = 0;
    int a2Length = 0;

    if (a1!=null) a1Length = a1.length;
    if (a2!=null) a2Length = a2.length;

    ITuple[] retArray = null;
    if (a1Length+a2Length > 0)
      retArray = new ITuple[a1Length+a2Length];
    
    if (a1!=null) 
      System.arraycopy(a1,0,retArray,0,a1Length);
    if (a2!=null)
      System.arraycopy(a2,0,retArray,a1Length,a2Length);

    return retArray;
  }
  
}
