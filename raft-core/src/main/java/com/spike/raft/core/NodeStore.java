package com.spike.raft.core;

import com.spike.raft.election.NodeId;

/**
 * 保存节点的部分状态数据的类规范
 */
public interface NodeStore {
    int getTerm();
    void setTerm(int term);
    NodeId getVotedFor();
    void setVotedFor(NodeId votedFor);

    /**
     * 针对文件持久化的实现，需要关闭文件；
     * 内存方式不需要实现该方法；
     */
    void close();
}
