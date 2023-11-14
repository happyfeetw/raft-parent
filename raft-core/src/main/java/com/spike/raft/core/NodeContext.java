package com.spike.raft.core;

import com.google.common.eventbus.EventBus;
import com.spike.raft.cluster.NodeGroup;
import com.spike.raft.election.NodeId;
import com.spike.raft.process.TaskExecutor;
import com.spike.raft.rpc.Connector;
import com.spike.raft.schedule.Scheduler;

/**
 * 该类的作用是持有核心组件依赖的其他组件，
 * 使核心组件通过该类调用所依赖的其他组件，
 * 从而达到将核心组件与其他依赖组件解耦的目的
 */
public class NodeContext {
    private NodeId selfId;
    private NodeGroup group;

    // todo
    //private Log log;

    private Connector connector;

    private Scheduler scheduler;

    private EventBus eventBus;
    /**
     * 主线程执行器
     */
    private TaskExecutor taskExecutor;
    /**
     * 部分角色状态数据存储
     */
    private NodeStore store;

    public NodeId selfId () {
        return selfId;
    }

    public void setSelfId(NodeId selfId) {
        this.selfId = selfId;
    }

    public NodeId getSelfId () {
        return selfId;
    }

    public NodeGroup getGroup () {
        return group;
    }

    public void setGroup (NodeGroup group) {
        this.group = group;
    }

    public Connector getConnector () {
        return connector;
    }

    public void setConnector (Connector connector) {
        this.connector = connector;
    }

    public Scheduler getScheduler () {
        return scheduler;
    }

    public void setScheduler (Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public EventBus getEventBus () {
        return eventBus;
    }

    public void setEventBus (EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public TaskExecutor getTaskExecutor () {
        return taskExecutor;
    }

    public void setTaskExecutor (TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public NodeStore getStore () {
        return store;
    }

    public void setStore (NodeStore store) {
        this.store = store;
    }
}
