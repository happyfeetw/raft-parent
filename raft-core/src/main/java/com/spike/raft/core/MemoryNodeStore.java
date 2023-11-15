package com.spike.raft.core;

import com.spike.raft.election.NodeId;

/**
 * in-memory implementation
 */
public class MemoryNodeStore implements NodeStore{
    private int term;
    private NodeId votedFor;

    /**
     * 初始状态
     */
    public MemoryNodeStore () {
        this(0, null);
    }

    /**
     * 变更
     * @param term
     * @param votedFor
     */
    public MemoryNodeStore (int term, NodeId votedFor) {
        this.term = term;
        this.votedFor = votedFor;
    }

    @Override
    public int getTerm () {
        return this.term;
    }

    @Override
    public void setTerm (int term) {
        this.term = term;
    }

    @Override
    public NodeId getVotedFor () {
        return null;
    }

    @Override
    public void setVotedFor (NodeId votedFor) {
        this.votedFor = votedFor;
    }

    @Override
    public void close () {
        // leave it empty.
    }
}
