package com.spike.raft.rpc;

import com.spike.raft.election.NodeId;

public class AppendEntriesResultMessage extends RpcMessage{

    private final AppendEntriesResult result;

    public AppendEntriesResultMessage (NodeId sourceNodeId, AppendEntriesResult result) {
        super(sourceNodeId);
        this.result = result;
    }

    public AppendEntriesResult get() {
        return this.result;
    }
}
