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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jgroups.*;

import org.jgroups.blocks.VoteException;
import org.jgroups.blocks.VotingListener;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.blocks.PullPushAdapter;

import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Voting adapter provides a voting functionality for an application. There 
 * should be at most one {@link VotingAdapter} listening on one {@link Channel}
 * instance. Each adapter can have zero or more registered {@link VotingListener} 
 * instances that will be called during voting process. 
 * <p>
 * Decree is an object that has some semantic meaning within the application. 
 * Each voting listener receives a decree and can respond with either 
 * <code>true</code> or false. If the decree has no meaning for the voting
 * listener, it is required to throw {@link VotingException}. In this case
 * this specific listener will be excluded from the voting on the specified
 * decree. After performing local voting, this voting adapter sends the request
 * back to the originator of the voting process. Originator receives results
 * from each node and decides if all voting process succeeded or not depending 
 * on the consensus type specified during voting.
 * 
 * <p>Modified to dispatch state updates and state requests to a separate
 * listener</p>
 *
 * @author Roman Rokytskyy (rrokytskyy@acm.org)
 * @author Dan Creswell (dan@dancres.org)
 */
public class VotingAdapter implements MessageListener, MembershipListener {
    
    /**
     * This consensus type means that at least one positive vote is required
     * for the voting to succeed.
     */
    public static final	int VOTE_ANY = 0;
    
    /**
     * This consensus type means that at least one positive vote and no negative
     * votes are required for the voting to succeed.
     */
    public static final int VOTE_ALL = 1;
    
    /**
     * This consensus type means that number of positive votes should be greater
     * than number of negative votes.
     */
    public static final int VOTE_MAJORITY = 2;
    
    
    private static final int PROCESS_CONTINUE = 0;
    private static final int PROCESS_SKIP = 1;
    private static final int PROCESS_BREAK = 2;
    

    private RpcDispatcher rpcDispatcher;

    protected Log log=LogFactory.getLog(getClass());

    private HashSet suspectedNodes = new HashSet();
    private boolean blocked = false;
    private boolean closed;

    private MessageListener theDelegate;

    /**
     * Creates an instance of the VoteChannel that uses JGroups
     * for communication between group members.
     * @param channel JGroups channel.
     */
    public VotingAdapter(Channel channel) {
        rpcDispatcher = new RpcDispatcher(channel, this, this, this);
    }

    public VotingAdapter(PullPushAdapter adapter, Serializable id) {
        rpcDispatcher = new RpcDispatcher(adapter, id, this, this, this);
    }

    public void setDelegate(MessageListener aListener) {
        synchronized(this) {
            theDelegate = aListener;
        }
    }

    /**
     * Performs actual voting on the VoteChannel using the JGroups
     * facilities for communication.
     */
    public boolean vote(Object decree, int consensusType, long timeout)
	throws ChannelException
    {
        if (closed)
            throw new ChannelException("Channel was closed.");
            

            if(log.isDebugEnabled()) log.debug("Conducting voting on decree " + decree + ", consensus type " +
			getConsensusStr(consensusType) + ", timeout " + timeout);

        int mode = GroupRequest.GET_ALL;

        // perform the consensus mapping
        switch (consensusType) {
	case VotingAdapter.VOTE_ALL : mode = GroupRequest.GET_ALL; break;
	case VotingAdapter.VOTE_ANY : mode = GroupRequest.GET_FIRST; break;
	case VotingAdapter.VOTE_MAJORITY : mode = GroupRequest.GET_MAJORITY; break;
	default : mode = GroupRequest.GET_ALL;
        }

        try {
            java.lang.reflect.Method method = this.getClass().getMethod(
									"localVote", new Class[] { Object.class });

            MethodCall methodCall = new MethodCall(method, new Object[] {decree});


                if(log.isDebugEnabled()) log.debug("Calling remote methods...");
    
            // vote
            RspList responses = rpcDispatcher.callRemoteMethods(
									 null, methodCall, mode, timeout);
                

                if(log.isDebugEnabled()) log.debug("Checking responses.");


            return processResponses(responses, consensusType);
            
        } catch(NoSuchMethodException nsmex) {
            
            // UPS!!! How can this happen?!
            
            if(log.isErrorEnabled()) log.error("Could not find method localVote(Object). " +
			nsmex.toString());

            throw new UnsupportedOperationException(
						    "Cannot execute voting because of absence of " + 
						    this.getClass().getName() + ".localVote(Object) method.");
        } 
    }

    /**
     * Processes the response list and makes a decision according to the
     * type of the consensus for current voting.
     * <p>
     * Note: we do not support voting in case of Byzantine failures, i.e.
     * when the node responds with the fault message.
     */
    private boolean processResponses(RspList responses, int consensusType)
	throws ChannelException 
    {
        if (responses == null) {
            return false;
        }

        boolean voteResult = false;
        int totalPositiveVotes = 0;
        int totalNegativeVotes = 0;

        for(int i = 0; i < responses.size(); i++) {
            Rsp response = (Rsp)responses.elementAt(i);

            switch(checkResponse(response)) {
	    case PROCESS_SKIP : continue;
	    case PROCESS_BREAK : return false;
            }

            VoteResult result = (VoteResult)response.getValue();
            
            totalPositiveVotes += result.getPositiveVotes();
            totalNegativeVotes += result.getNegativeVotes();
        }

        switch(consensusType) {
	case VotingAdapter.VOTE_ALL :
	    voteResult = (totalNegativeVotes == 0 && totalPositiveVotes > 0);
	    break;
	case VotingAdapter.VOTE_ANY :
	    voteResult = (totalPositiveVotes > 0);
	    break;
	case VotingAdapter.VOTE_MAJORITY :
	    voteResult = (totalPositiveVotes > totalNegativeVotes);
        }

        return voteResult;
    }

    /**
     * This method checks the response and says the processResponses() method
     * what to do.
     * @returns PROCESS_CONTINUE to continue calculating votes,
     * PROCESS_BREAK to stop calculating votes from the nodes,
     * PROCESS_SKIP to skip current response.
     * @throws ChannelException when the response is fatal to the
     * current voting process.
     */
    private int checkResponse(Rsp response) throws ChannelException {

        if (!response.wasReceived()) {
            

                if(log.isDebugEnabled()) log.debug("Response from node " + response.getSender() +
			    " was not received.");
            
            // what do we do when one node failed to respond?
            //throw new ChannelException("Node " + response.GetSender() +
            //	" failed to respond.");
            return PROCESS_BREAK ;
        }

        /**@todo check what to do here */
        if (response.wasSuspected()) {
            

                if(log.isDebugEnabled()) log.debug("Node " + response.getSender() + " was suspected.");
            
            // wat do we do when one node is suspected?
            return PROCESS_SKIP ;
        }

        Object object = response.getValue();

        // we received exception/error, something went wrong
        // on one of the nodes... and we do not handle such faults
        if (object instanceof Throwable) {
            System.err.println("Got exception");
            ((Throwable) object).printStackTrace(System.err);
            throw new ChannelException("Node " + response.getSender() +
				       " is faulty.");
        }

        if (object == null) {
            return PROCESS_SKIP;
        }

        // it is always interesting to know the class that caused failure...
        if (!(object instanceof VoteResult)) {
            String faultClass = object.getClass().getName();

            // ...but we do not handle byzantine faults
            throw new ChannelException("Node " + response.getSender() +
				       " generated fault (class " + faultClass + ')');
        }

        // what if we received the response from faulty node?
        if (object instanceof FailureVoteResult) {
            
            if(log.isErrorEnabled()) log.error(((FailureVoteResult)object).getReason());
            
            return PROCESS_BREAK;
        }

        // everything is fine :)
        return PROCESS_CONTINUE;
    }

    /**
     * Callback for notification about the new view of the group.
     */
    public void viewAccepted(View newView) {

        // clean nodes that were suspected but still exist in new view
        Iterator iterator = suspectedNodes.iterator();
        while(iterator.hasNext()) {
            Address suspectedNode = (Address)iterator.next();
            if (newView.containsMember(suspectedNode))
                iterator.remove();
        }

        blocked = false;
    }

    /**
     * Callback for notification that one node is suspected
     */
    public void suspect(Address suspected) {
        suspectedNodes.add(suspected);
    }

    /**
     * Blocks the channel until the ViewAccepted is invoked.
     */
    public void block() {
        blocked = true;
    }

    /**
     * Get the channel state.
     *
     * @return always <code>null</code>, we do not have any group-shared
     * state.
     */
    public byte[] getState() {
        MessageListener myDelegate = getDelegate();

        if (myDelegate != null)
            return myDelegate.getState();
        else
            return null;
    }

    /**
     * Receive the message. All messages are ignored.
     *
     * @param msg message to check.
     */
    public void receive(org.jgroups.Message msg) {
        MessageListener myDelegate = getDelegate();

        if (myDelegate != null)
            myDelegate.receive(msg);
    }

    /**
     * Set the channel state. We do nothing here.
     */
    public void setState(byte[] state) {
        MessageListener myDelegate = getDelegate();

        if (myDelegate != null)
            myDelegate.setState(state);
    }
            
    private MessageListener getDelegate() {
        synchronized(this) {
            return theDelegate;
        }
    }

    private Set voteListeners = new HashSet();
    private VotingListener[] listeners;

    /**
     * Vote on the specified decree requiring all nodes to vote.
     * 
     * @param decree decree on which nodes should vote.
     * @param timeout time during which nodes can vote.
     * 
     * @return <code>true</code> if nodes agreed on a decree, otherwise 
     * <code>false</code>
     * 
     * @throws ChannelException if something went wrong.
     */
    public boolean vote(Object decree, long timeout) throws ChannelException {
        return vote(decree, VOTE_ALL, timeout);
    }

    /**
     * Adds voting listener.
     */
    public void addVoteListener(VotingListener listener) {
        voteListeners.add(listener);
        listeners = (VotingListener[])voteListeners.toArray(
							    new VotingListener[voteListeners.size()]);
    }

    /**
     * Removes voting listener.
     */
    public void removeVoteListener(VotingListener listener) {
        voteListeners.remove(listener);

        listeners = (VotingListener[])voteListeners.toArray(
							    new VotingListener[voteListeners.size()]);
    }

    /**
     * This method performs voting on the specific decree between all
     * local voteListeners.
     */
    public VoteResult localVote(Object decree) {

        VoteResult voteResult = new VoteResult();
    
        for(int i = 0; i < listeners.length; i++) {
            VotingListener listener = listeners[i];

            try {
                voteResult.addVote(listener.vote(decree));
            } catch (VoteException vex) {
                // do nothing here.
            } catch(RuntimeException ex) {
                
                if(log.isErrorEnabled()) log.error(ex.toString());
                
                // if we are here, then listener 
                // had thrown a RuntimeException
                return new FailureVoteResult(ex.getMessage());
            }
        }


            if(log.isDebugEnabled()) log.debug("Voting on decree " + decree.toString() + " : " +
			voteResult.toString());

        return voteResult;
    }

    /**
     * Convert consensus type into string representation. This method is 
     * useful for debugginf.
     * 
     * @param consensusType type of the consensus.
     * 
     * @return string representation of the consensus type.
     */
    public static String getConsensusStr(int consensusType) {
        switch(consensusType) {
	case VotingAdapter.VOTE_ALL : return "VOTE_ALL";
	case VotingAdapter.VOTE_ANY : return "VOTE_ANY";
	case VotingAdapter.VOTE_MAJORITY : return "VOTE_MAJORITY";
	default : return "UNKNOWN";
        }
    }


    /**
     * This class represents the result of local voting. It contains a 
     * number of positive and negative votes collected during local voting.
     */
    public static class VoteResult implements Serializable {
        private int positiveVotes = 0;
        private int negativeVotes = 0;

        public void addVote(boolean vote) {
            if (vote)
                positiveVotes++;
            else
                negativeVotes++;
        }

        public int getPositiveVotes() { return positiveVotes; }

        public int getNegativeVotes() { return negativeVotes; }

        public String toString() {
            return "VoteResult: up=" + positiveVotes +
                ", down=" + negativeVotes;
        }
    }

    /**
     * Class that represents a result of local voting on the failed node.
     */
    public static class FailureVoteResult extends VoteResult {
        private String reason;
        
        public FailureVoteResult(String reason) {
            this.reason = reason;
        }
        
        public String getReason() {
            return reason;
        }
    }
    
}
