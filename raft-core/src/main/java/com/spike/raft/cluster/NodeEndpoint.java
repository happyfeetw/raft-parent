package com.spike.raft.cluster;

import com.spike.raft.election.NodeId;
import com.spike.raft.net.Address;

import java.util.Objects;

/**
 * 成员节点类
 * 主要包括节点id和地址
 */
public class NodeEndpoint {
    private final NodeId id;
    private final Address address;

    public NodeEndpoint (String id, String host, int port) {
        this(new NodeId(id), new Address(host, port));
    }
    public NodeEndpoint (NodeId id, Address address) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(address);
        this.id = id;
        this.address = address;
    }

    public NodeId getId () {
        return id;
    }

    public Address getAddress () {
        return address;
    }
}
