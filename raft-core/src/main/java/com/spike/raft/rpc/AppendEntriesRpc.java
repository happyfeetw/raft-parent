package com.spike.raft.rpc;

import com.spike.raft.election.NodeId;

import java.util.Collections;
import java.util.List;

/**
 * leader->follower的心跳消息、日志复制通信
 */
public class AppendEntriesRpc {
    private int term;
    private NodeId leaderId;
    private int prevLogIndex = 0;
    private int prevLogTerm;

    // todo replace Object with Entry class.
    private List<Object> entries = Collections.emptyList();
    private int leaderCommit;

    public int getTerm () {
        return term;
    }

    public void setTerm (int term) {
        this.term = term;
    }

    public NodeId getLeaderId () {
        return leaderId;
    }

    public void setLeaderId (NodeId leaderId) {
        this.leaderId = leaderId;
    }

    public int getPrevLogIndex () {
        return prevLogIndex;
    }

    public void setPrevLogIndex (int prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
    }

    public int getPrevLogTerm () {
        return prevLogTerm;
    }

    public void setPrevLogTerm (int prevLogTerm) {
        this.prevLogTerm = prevLogTerm;
    }

    public List<Object> getEntries () {
        return entries;
    }

    public void setEntries (List<Object> entries) {
        this.entries = entries;
    }

    public int getLeaderCommit () {
        return leaderCommit;
    }

    public void setLeaderCommit (int leaderCommit) {
        this.leaderCommit = leaderCommit;
    }

    @Override
    public String toString () {
        return "AppendEntries{" +
                "term=" + term +
                ", leaderId=" + leaderId +
                ", prevLogIndex=" + prevLogIndex +
                ", prevLogTerm=" + prevLogTerm +
                ", entries.size()=" + entries.size() +
                ", leaderCommit=" + leaderCommit +
                '}';
    }
}
