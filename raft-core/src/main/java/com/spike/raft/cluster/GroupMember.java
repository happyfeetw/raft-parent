package com.spike.raft.cluster;

import com.spike.raft.election.NodeId;

/**
 * 集群成员类
 */
public class GroupMember {

    /**
     * 成员的节点信息
     */
    private final NodeEndpoint endPoint;

    /**
     * 成员的日志复制进度
     * 默认为null，只有当节点成为leader节点时才会初始化。
     * 同时，单机模式下不需要同步日志，因此也为null。
     */
    private ReplicatingState replicatingState;

    public GroupMember (NodeEndpoint endPoint) {
        this.endPoint = endPoint;
    }

    int getNextIndex() {
        return ensureReplicatingState().nextIndex();
    }

    int getMatchINdex() {
        return ensureReplicatingState().matchIndex();
    }

    public GroupMember (NodeEndpoint endPoint, ReplicatingState replicatingState) {
        this.endPoint = endPoint;
        this.replicatingState = replicatingState;
    }

    /**
     * 同步日志复制进度
     * 只有当前节点成为leader节点后才会调用该方法。
     * @return
     */
    private ReplicatingState ensureReplicatingState() {
        if (replicatingState == null) throw new IllegalArgumentException("Replication state is not set.");
        return replicatingState;
    }

    public NodeEndpoint getEndPoint () {
        return endPoint;
    }

    // todo rename this method.
    public boolean idEquals (NodeId id) {
        return getEndPoint().getId().equals(id);
    }
}
