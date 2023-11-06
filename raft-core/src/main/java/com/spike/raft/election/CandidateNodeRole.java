package com.spike.raft.election;

public class CandidateNodeRole extends AbstractNodeRole{
    /**
     * 收到的票数
     */
    private final int votesCount;

    /**
     * 选举超时
     */
    private final ElectionTimeout electionTimeout;

    /**
     * 票数为1的构造
     * 在当前节点发起选举并变成candidate时调用
     * @param term
     * @param electionTimeout
     */
    public CandidateNodeRole (int term, ElectionTimeout electionTimeout) {
        this(term, 1, electionTimeout);
    }

    /**
     * 指定票数的构造
     * 用于当前节点收到其他节点的票数时调用
     * @param term
     * @param votesCount
     * @param electionTimeout
     */
    public CandidateNodeRole (int term, int votesCount, ElectionTimeout electionTimeout) {
        super(RoleName.CANDIDATE, term);
        this.votesCount = votesCount;
        this.electionTimeout = electionTimeout;
    }

    @Override
    public void cancelTimeoutOrTask () {
        electionTimeout.cancel();
    }

    @Override
    public String toString () {
        return "CandidateNodeRole{" +
                "term=" + term +
                ", votesCount=" + votesCount +
                ", electionTimeout=" + electionTimeout +
                '}';
    }
}
