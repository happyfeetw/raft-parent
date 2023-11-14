package com.spike.raft.rpc;

import com.spike.raft.election.NodeId;

/**
 * 投票请求数据对象
 */
public class RequestVoteRpc {
    /**
     * 当前节点的选举任期
     */
    private int term;
    /**
     * 候选者节点的id，即当前节点自己
     */
    private NodeId candidateId;
    /**
     * 候选者最后一条日志的索引
     */
    private int lastLogIndex = 0;
    /**
     * 候选者最后一条日志的选举任期
     */
    private int lastLogTerm = 0;

    public int getTerm () {
        return term;
    }

    public void setTerm (int term) {
        this.term = term;
    }

    public NodeId getCandidateId () {
        return candidateId;
    }

    public void setCandidateId (NodeId candidateId) {
        this.candidateId = candidateId;
    }

    public int getLastLogIndex () {
        return lastLogIndex;
    }

    public void setLastLogIndex (int lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }

    public int getLastLogTerm () {
        return lastLogTerm;
    }

    public void setLastLogTerm (int lastLogTerm) {
        this.lastLogTerm = lastLogTerm;
    }

    @Override
    public String toString () {
        return "RequestVoteRpc{" +
                "term=" + term +
                ", candidateId=" + candidateId +
                ", lastLogIndex=" + lastLogIndex +
                ", lastLogTerm=" + lastLogTerm +
                '}';
    }
}
