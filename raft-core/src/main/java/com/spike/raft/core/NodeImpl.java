package com.spike.raft.core;

import com.spike.raft.election.AbstractNodeRole;
import com.spike.raft.election.CandidateNodeRole;
import com.spike.raft.election.ElectionTimeout;
import com.spike.raft.election.FollowerNodeRole;
import com.spike.raft.election.RoleName;
import com.spike.raft.rpc.RequestVoteRpc;
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
        context.getTaskExecutor().submit(this::doProcessElectionTimeout);
    }

    /**
     * 设定electionTimeout任务是在定时器线程中，而本类是在主线程中被调用的
     * 因此需要做任务转换
     */
    private void doProcessElectionTimeout () {
        // 当前节点Leader角色不存在选举超时任务
        if (role.getName().equals(RoleName.LEADER)) {
            logger.warn("node {}'s current role is leader, ignore electionTimeout.", context.getSelfId());
        }

        // 当前节点是follower时，发起选举
        // 当前节点时candidate时，再次发起选举
        int newTerm = role.getTerm() + 1;
        role.cancelTimeoutOrTask();
        logger.info("node {} start election.", context.getSelfId());

        // 自己投给自己一票，变成candidate角色
        changeToRole(new CandidateNodeRole(newTerm, scheduleElectionTimeout()));
        // 发送requestVote，拉票
        RequestVoteRpc rpc = new RequestVoteRpc();
        rpc.setTerm(newTerm);
        rpc.setCandidateId(context.selfId());
        rpc.setLastLogIndex(0);
        rpc.setLastLogTerm(0);

        // 发送消息
        context.getConnector().sendRequestVote(rpc, context.getGroup().listEndpointExceptSelf());
    }

    private void changeToRole (AbstractNodeRole newRole) {
        logger.debug("node{}, role state changed -> {}", context.selfId(), newRole);

        NodeStore store = context.getStore();
        store.setTerm(newRole.getTerm());

        if (newRole.getName().equals(RoleName.FOLLOWER)) {
            store.setVotedFor(((FollowerNodeRole)newRole).getVotedFore());
        }

        role = newRole;
    }
}
