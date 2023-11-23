package com.spike.raft.core;

import com.google.common.eventbus.EventBus;
import com.spike.raft.cluster.NodeEndpoint;
import com.spike.raft.cluster.NodeGroup;
import com.spike.raft.election.NodeId;
import com.spike.raft.process.SingleThreadTaskExecutor;
import com.spike.raft.process.TaskExecutor;
import com.spike.raft.rpc.Connector;
import com.spike.raft.schedule.DefaultScheduler;
import com.spike.raft.schedule.Scheduler;

import java.util.Collection;
import java.util.Collections;

/**
 * builder for NodeImpl instance.
 */
public class NodeBuilder {

    private final NodeGroup group;
    private final NodeId selfId;
    private final EventBus eventBus;
    private Scheduler scheduler = null;
    private Connector connector = null;
    private TaskExecutor taskExecutor = null;

    public NodeBuilder(NodeEndpoint endpoint) {
        this(Collections.singletonList(endpoint), endpoint.getId());
    }
    public NodeBuilder(Collection<NodeEndpoint> endpoints, NodeId selfId) {
        this.selfId = selfId;
        this.group = new NodeGroup(endpoints, selfId);
        this.eventBus = new EventBus(selfId.getValue());
    }

    public NodeBuilder setConnector(Connector connector) {
        this.connector = connector;
        return this;
    }
    public NodeBuilder setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public NodeBuilder setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
        return this;
    }

    public Node build() {
        return new NodeImpl(buildContext());
    }

    private NodeContext buildContext() {
        NodeContext ctx = new NodeContext();
        ctx.setGroup(group);
        ctx.setSelfId(selfId);
        ctx.setEventBus(eventBus);
        ctx.setStore(new MemoryNodeStore());

        //ctx.setScheduler(scheduler != null ? scheduler : new DefaultScheduler(config));
        ctx.setScheduler(scheduler);
        ctx.setConnector(connector);
        ctx.setTaskExecutor(taskExecutor != null ? taskExecutor : new SingleThreadTaskExecutor("node"));
        return ctx;
    }



}
