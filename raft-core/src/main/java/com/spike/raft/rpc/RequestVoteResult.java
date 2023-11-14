package com.spike.raft.rpc;

/**
 * 投票响应的数据类
 */
public class RequestVoteResult {
    private int term;
    /**
     * 是否投票
     */
    private final boolean voteGranted;

    public RequestVoteResult (int term, boolean voteGranted) {
        this.term = term;
        this.voteGranted = voteGranted;
    }

    public int getTerm () {
        return term;
    }

    public boolean isVoteGranted () {
        return voteGranted;
    }

    @Override
    public String toString () {
        return "RequestVoteResult{" +
                "term=" + term +
                ", voteGranted=" + voteGranted +
                '}';
    }
}
