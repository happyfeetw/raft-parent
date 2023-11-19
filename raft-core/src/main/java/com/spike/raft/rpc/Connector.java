package com.spike.raft.rpc;

import com.spike.raft.cluster.NodeEndpoint;

import java.util.Collection;

/**
 * rpc组件接口
 */
public interface Connector {

    void initialize();

    void close();

    /**
     * 发送征求投票消息，群发
     * @param rpc
     * @param destEndpoints
     */
    void sendRequestVote(RequestVoteRpc rpc,
                         Collection<NodeEndpoint> destEndpoints);

    void replyRequestVote(RequestVoteResult result,
                          NodeEndpoint destEndpoint);

    /**
     * 发送心跳消息，单点发送
     * @param rpc
     * @param destEndpoint
     */
    void sendAppendEntries(AppendEntriesRpc rpc,
                           NodeEndpoint destEndpoint);

    void replyAppendEntries (AppendEntriesResult result,
                             NodeEndpoint destEndpoint);

}
