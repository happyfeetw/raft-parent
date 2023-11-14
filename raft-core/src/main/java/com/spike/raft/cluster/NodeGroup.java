package com.spike.raft.cluster;

import com.spike.raft.election.NodeId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 节点集群表
 */
public class NodeGroup {
    /**
     * 当前节点的id
     */
    private final NodeId selfId;

    /**
     * 集群成员表
     */
    private Map<NodeId, GroupMember> memberMap;

    NodeGroup (Collection<NodeEndpoint> endpoints, NodeId selfId) {
        this.memberMap = buildMemberMap(endpoints);
        this.selfId = selfId;
    }

    private Map<NodeId, GroupMember> buildMemberMap (Collection<NodeEndpoint> endpoints) {
        Map<NodeId, GroupMember> membermap = new HashMap<>();

        for (NodeEndpoint endpoint : endpoints) {
            NodeId endpointId = endpoint.getId();
            GroupMember groupMember = new GroupMember(endpoint);
            membermap.put(endpointId, groupMember);
        }

        if (membermap.isEmpty()) throw new IllegalArgumentException("Endpoints cannot be empty.");
        return membermap;
    }

    /**
     * 查找成员节点，找不到报错
     * @param nodeId
     * @return
     */
    GroupMember findMember(NodeId nodeId) {
        GroupMember member = getMember(nodeId);
        if (member == null) throw new IllegalArgumentException("No such node: " + nodeId.getValue());
        return member;
    }

    /**
     * 查找成员节点，找不到返回空
     * @param nodeId
     * @return
     */
    private GroupMember getMember(NodeId nodeId) {
        return memberMap.get(nodeId);
    }

    /**
     * 获取需要复制日志的节点列表，即除自身外的其他节点
     * 只有当前节点为leader节点时才会调用该方法
     * @return
     */
    Collection<GroupMember> listReplicationTarget() {
        return this.memberMap.values().stream()
                .filter(member -> !member.idEquals(selfId))
                .collect(Collectors.toList());
    }


}
