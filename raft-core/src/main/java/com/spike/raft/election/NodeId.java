package com.spike.raft.election;

import java.io.Serializable;
import java.util.Objects;

/**
 * 节点id类
 * 需要在集合中作为key来查找对应的Node对象，因此需要重载hashCode方法和equals方法。
 */
public class NodeId implements Serializable {
    private final String value;

    public NodeId (String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static NodeId of(String value) {
        return new NodeId(value);
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeId nodeId = (NodeId) o;
        return Objects.equals(value, nodeId.value);
    }

    @Override
    public int hashCode () {
        return Objects.hash(value);
    }

    @Override
    public String toString () {
        return this.value;
    }
}
