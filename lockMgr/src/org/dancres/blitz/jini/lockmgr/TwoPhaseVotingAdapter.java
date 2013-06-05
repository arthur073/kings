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

import org.jgroups.ChannelException;
import org.jgroups.MessageListener;

import org.jgroups.blocks.VotingListener;
import org.jgroups.blocks.TwoPhaseVotingListener;
import org.jgroups.blocks.VoteException;


/**
 * This adapter introduces simple two-phase voting on a specified decree. All
 * nodes in the group receive a decree in "prepare" phase where they expres
 * their opinion on the decree. If all nodes voted positively on decree, next
 * phase "commit" fixes changes that were made in "prepare" phase, otherwise 
 * changes are canceled in "abort" phase. 
 * 
 * <p>Modified to fit with VoteAdapter changes</p>
 *
 * @author Roman Rokytskyy (rrokytskyy@acm.org)
 * @author Dan Creswell (dan@dancres.org)
 */
public class TwoPhaseVotingAdapter {

    private VotingAdapter voteChannel;

    /**
     * Creats an instance of the class.
     * @param voteChannel the channel that will be used for voting.
     */
    public TwoPhaseVotingAdapter(VotingAdapter voteChannel) {
        this.voteChannel = voteChannel;
    }

    public void setDelegate(MessageListener aListener) {
        this.voteChannel.setDelegate(aListener);
    }

    /**
     * Wraps actual listener with the VoteChannelListener and adds to the
     * voteChannel
     */
    public void addListener(TwoPhaseVotingListener listener) {
        voteChannel.addVoteListener(new TwoPhaseVoteWrapper(listener));
    }

    /**
     * Removes the listener from the voteChannel
     */
    public void removeListener(TwoPhaseVotingListener listener) {
        voteChannel.removeVoteListener(new TwoPhaseVoteWrapper(listener));
    }

    /**
     * Performs the two-phase voting on the decree. After the voting each
     * group member remains in the same state as others.
     */
    public boolean vote(Object decree, long timeout) throws ChannelException {
        // wrap real decree
        TwoPhaseWrapper wrappedDecree = new TwoPhaseWrapper(decree);

        // check the decree acceptance
        try {
            if (voteChannel.vote(wrappedDecree, timeout / 3)) {
                wrappedDecree.commit();

                // try to commit decree
                if (!voteChannel.vote(wrappedDecree, timeout / 3)) {
                    // strange, should fail during prepare... abort all
                    wrappedDecree.abort();
                    voteChannel.vote(wrappedDecree, timeout / 3);
                    return false;
                } else
                        return true;

            } else {
                // somebody is not accepting the decree... abort
                wrappedDecree.abort();
                voteChannel.vote(wrappedDecree, timeout / 3);
                return false;
            }
        } catch(ChannelException chex) {
            wrappedDecree.abort();
            voteChannel.vote(wrappedDecree, timeout / 3 );
            throw chex;
        }
    }
 
    public static class TwoPhaseVoteWrapper implements VotingListener {
    
        private TwoPhaseVotingListener listener;
    
        public TwoPhaseVoteWrapper(TwoPhaseVotingListener listener) {
            this.listener = listener;
        }
    
        public boolean vote(Object decree) throws VoteException {
            if (!(decree instanceof TwoPhaseWrapper))
                throw new VoteException("Not my type of decree. Ignore me.");
    
            TwoPhaseWrapper wrapper = (TwoPhaseWrapper)decree;
    
            // invoke the corresponding operation
            if (wrapper.isPrepare())
                return listener.prepare(wrapper.getDecree());
            else
            if (wrapper.isCommit())
                return listener.commit(wrapper.getDecree());
            else {
                listener.abort(wrapper.getDecree());
                return false;
            }
        }
    
        /*
    
         This wrapper is completely equal to the object it wraps.
    
         Therefore the hashCode():int and equals(Object):boolean are
         simply delegated to the wrapped code.
    
         */
    
        public int hashCode() {	return listener.hashCode(); }
        public boolean equals(Object other) { return listener.equals(other); }
    }

    /**
     * Wrapper of the decree to voting decree.
     */
    public static class TwoPhaseWrapper implements java.io.Serializable {
        private static final int PREPARE = 0;
        private static final int COMMIT = 1;
        private static final int ABORT = 2;
    
        public TwoPhaseWrapper(Object decree) {
            setDecree(decree);
            setType(PREPARE);
        }
    
        private Object decree;
        private int type;
    
        public Object getDecree(){ return decree; }
        public void setDecree(Object decree){ this.decree = decree; }
    
        private int getType() { return type; }
        private void setType(int type) { this.type = type; }
        private boolean isType(int type) { return this.type == type; }
    
        public boolean isPrepare() { return isType(PREPARE); }
        public boolean isCommit() { return isType(COMMIT); }
        public boolean isAbort() { return isType(ABORT); }
    
        public void commit() { setType(COMMIT); }
        public void abort() { setType(ABORT); }
    
        public String toString() { return decree.toString(); }
    }
}