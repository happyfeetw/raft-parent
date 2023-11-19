package com.spike.raft.rpc;

import com.spike.raft.election.NodeId;

public class AppendEntriesRpcMessage extends RpcMessage{
    private final AppendEntriesRpc rpc;
    public AppendEntriesRpcMessage (AppendEntriesRpc rpc, NodeId sourceNodeId) {
        super(sourceNodeId);
        this.rpc = rpc;
    }

    public AppendEntriesRpc get () {
        return rpc;
    }
}
