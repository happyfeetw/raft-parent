package com.spike.raft.rpc;

import com.spike.raft.election.NodeId;

/**
 * 拉票消息rpc的包装类
 */
public class RequestVoteRpcMessage {
    private final RequestVoteRpc rpc;
    private final NodeId sourceNodeId;

    public RequestVoteRpcMessage (RequestVoteRpc rpc, NodeId sourceNodeId) {
        this.rpc = rpc;
        this.sourceNodeId = sourceNodeId;
    }

    public NodeId getSourceNodeId () {
        return sourceNodeId;
    }

    public RequestVoteRpc get () {
        return rpc;
    }
}
