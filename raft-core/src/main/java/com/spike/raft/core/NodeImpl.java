package com.spike.raft.core;

import com.spike.raft.election.AbstractNodeRole;
import com.spike.raft.election.ElectionTimeout;
import com.spike.raft.election.FollowerNodeRole;
import com.spike.raft.election.RoleName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Node实现类
 * 由于处理逻辑使用单线程模型，因此本类仅使用final表示字段的不可变即可
 * 不需要考虑多线程访问的问题
 */
public class NodeImpl implements Node {
    private static final Logger logger = LoggerFactory.getLogger(NodeImpl.class);

    /**
     * 核心组件上下文
     */
    private final NodeContext context;

    private boolean started; // 是否已启动,防止重复调用start()
    private AbstractNodeRole role;

    public NodeImpl (NodeContext context) {
        this.context = context;
    }

    /**
     * 同步方法，防止同时调用
     */
    @Override
    public synchronized void start () {
        if (started) return;
        context.getEventBus().register(this);
        context.getConnector().initialize();

        NodeStore store = context.getStore();
        changeToRole(new FollowerNodeRole(
                store.getTerm(),
                store.getVotedFor(),
                null,
                scheduleElectionTimeout()));
        started = true;
    }


    /**
     * 同步方法
     * @throws InterruptedException
     */
    @Override
    public synchronized void stop () throws InterruptedException {
        if (!started) {
            throw new IllegalArgumentException("node not started yet.");
        }

        context.getScheduler().stop();
        context.getConnector().close();
        context.getTaskExecutor().shutdown();
        started = false;
    }

    private ElectionTimeout scheduleElectionTimeout() {
        return this.context.getScheduler().scheduleElectionTimeout(this::electionTimeout);
    }

    private void electionTimeout () {
        // todo
    }

    private void changeToRole (FollowerNodeRole newRole) {
        logger.debug("node{}, role state changed -> {}", context.selfId(), newRole);

        NodeStore store = context.getStore();
        store.setTerm(newRole.getTerm());

        if (newRole.getName().equals(RoleName.FOLLOWER)) {
            store.setVotedFor(newRole.getVotedFore());
        }

        role = newRole;
    }
}
