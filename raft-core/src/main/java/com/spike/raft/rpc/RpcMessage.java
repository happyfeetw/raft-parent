package com.spike.raft.rpc;

import com.spike.raft.election.NodeId;

public class RpcMessage {
    private final NodeId sourceNodeId;

    public RpcMessage (NodeId sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }

    public NodeId getSourceNodeId () {
        return sourceNodeId;
    }
}
