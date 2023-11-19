package com.spike.raft.rpc;

import com.spike.raft.election.NodeId;

/**
 * 拉票消息rpc的包装类
 */
public class RequestVoteRpcMessage extends RpcMessage{
    private final RequestVoteRpc rpc;

    public RequestVoteRpcMessage (RequestVoteRpc rpc, NodeId sourceNodeId) {
        super(sourceNodeId);
        this.rpc = rpc;
    }

    public RequestVoteRpc get () {
        return rpc;
    }
}
