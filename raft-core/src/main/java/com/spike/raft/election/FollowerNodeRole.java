package com.spike.raft.election;

public class FollowerNodeRole extends AbstractNodeRole{
    private final NodeId votedFore;
    private final NodeId leaderId;
    private final ElectionTimeout electionTimeout;

    public FollowerNodeRole (int term, NodeId votedFore, NodeId leaderId, ElectionTimeout electionTimeout) {
        super(RoleName.FOLLOWER, term);
        this.votedFore = votedFore;
        this.leaderId = leaderId;
        this.electionTimeout = electionTimeout;
    }
    public NodeId getVotedFore() {
        return votedFore;
    }

    public NodeId getLeaderId () {
        return leaderId;
    }

    @Override
    public void cancelTimeoutOrTask () {
        electionTimeout.cancel();
    }

    @Override
    public NodeId getLeaderId (NodeId nodeId) {
        return getLeaderId();
    }

    @Override
    public String toString () {
        return "FollowerNodeRole{" +
                "term=" + term +
                ", votedFore=" + votedFore +
                ", leaderId=" + leaderId +
                ", electionTimeout=" + electionTimeout +
                '}';
    }
}
