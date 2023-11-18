package com.spike.raft.core;

import com.google.common.eventbus.Subscribe;
import com.spike.raft.cluster.GroupMember;
import com.spike.raft.election.AbstractNodeRole;
import com.spike.raft.election.CandidateNodeRole;
import com.spike.raft.election.ElectionTimeout;
import com.spike.raft.election.FollowerNodeRole;
import com.spike.raft.election.LeaderNodeRole;
import com.spike.raft.election.LogReplicationTask;
import com.spike.raft.election.NodeId;
import com.spike.raft.election.RoleName;
import com.spike.raft.rpc.AppendEntriesRpc;
import com.spike.raft.rpc.RequestVoteResult;
import com.spike.raft.rpc.RequestVoteRpc;
import com.spike.raft.rpc.RequestVoteRpcMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

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

    @Subscribe
    public void onReceiveRequestVoteRpc (RequestVoteRpcMessage rpcMsg) {
        context.getTaskExecutor().submit(() -> context.getConnector()
                .replyRequestVote(
                        doProcessRequestVoteRpc(rpcMsg),
                        context.getGroup().findMember(rpcMsg.getSourceNodeId()).getEndpoint()
                ));
    }

    private RequestVoteResult doProcessRequestVoteRpc (RequestVoteRpcMessage rpcMsg) {
        RequestVoteRpc rpc = rpcMsg.get();
        // 收到消息中的term比自己的term小，不投票，不变换身份
        if (rpc.getTerm() < role.getTerm()) {
            logger.debug("term from rpc < current term, don't vote ({} < {})",
                    rpc.getTerm(), role.getTerm());
            return new RequestVoteResult(role.getTerm(), false);
        }

        // todo 暂时设置为无条件投票
        boolean voteForCandidate = true;
        // 收到消息中的term比自己的term大，投票，并将自己变为follower
        if (rpc.getTerm() > role.getTerm()) {
            // 将自己变为follower
            becomeFollower(rpc.getTerm(), (voteForCandidate ? rpc.getCandidateId() : null), null, true);
            return new RequestVoteResult(rpc.getTerm(), voteForCandidate);
        }

        // 收到消息中的term与自己的term一致，意味着两种情况：
        // 1. 存在多个节点以不同的term启动，选举超时后，candidate节点碰巧将拉票消息发送到了比自己term大的follower节点上，
        //    此时应该根据日志决定是否投票。对应下面的 case 1；
        // 2. 集群中出现了两个以上的candidate节点，部分已投过票的follower可能收到了其他candidate节点的拉票消息，
        //    这种情况由于一票制的机制，不应该再投票。对应下面的 case 2；
        switch (role.getName()) {
            // 自己是candidate时，只为自己投票
            // 自己为leader时，是否投票没有意义
            // 所以只有自己是follower时需要投票
            case FOLLOWER:
                FollowerNodeRole follower = (FollowerNodeRole) role;
                NodeId votedFor = follower.getVotedFore();
                // 两种情况需要投票
                // case 1. 自己未投过票, 并且对方的日志比自己的新
                // case 2. 自己已经给对方投过票
                // 其他情况不需要投票(一票制)
                // 投票后需要切换为follower角色 todo 为什么已经是follower的情况下还要切换成follower？
                if (votedFor == null && voteForCandidate /* case 1 */
                        || Objects.equals(votedFor, rpc.getCandidateId()) /* case 2 */) {
                    becomeFollower(role.getTerm(), rpc.getCandidateId(), null, true);
                    return new RequestVoteResult(rpc.getTerm(), true);
                }
            case CANDIDATE:
            case LEADER:
                return new RequestVoteResult(role.getTerm(), false);
            default:
                throw new IllegalStateException("Unexpected node role [" + role.getName() + "]");
        }
    }

    /**
     *
     * @param term 选举任期
     * @param votedFor candidate节点的id
     * @param leaderId leader节点id
     * @param scheduleElectionTimeout 是否设置选举超时
     */
    private void becomeFollower (int term, NodeId votedFor, NodeId leaderId, boolean scheduleElectionTimeout) {
        role.cancelTimeoutOrTask(); // 角色变换前先取消选举超时
        if (leaderId != null && !leaderId.equals(role.getLeaderId(context.selfId()))) { //todo  why need selfId
            logger.info("current leader is {}, term {}", leaderId, term);
        }

        ElectionTimeout electionTimeout = scheduleElectionTimeout ? scheduleElectionTimeout() : ElectionTimeout.NONE;
        changeToRole(new FollowerNodeRole(term, votedFor, leaderId, electionTimeout));
    }

    @Subscribe
    public void onReceiveRequestVoteResult (RequestVoteResult result) {
        context.getTaskExecutor().submit(() -> doProcessRequestVoteResult(result));
    }

    private void doProcessRequestVoteResult (RequestVoteResult result) {
        // 收到的term比自己大，则自己退化成follower节点
        if (result.getTerm() > role.getTerm()) {
            becomeFollower(result.getTerm(), null,null, true);
            return;
        }

        // 若自己不是candidate，忽略投票结果消息
        if (!role.getName().equals(RoleName.CANDIDATE)) {
            logger.warn("receive request vote result but current role is not candidate, ignore.");
            return;
        }

        // 收到的term比自己小，并且对方未给自己投票
        if (result.getTerm() < role.getTerm() || !result.isVoteGranted()) {
            // 直接返回的目的是希望系统在一个选举超时时间内能够收集到足够的票数，否则需要重开选举，增加时间消耗。
            return;
        }

        // 当前票数
        int currentVoteCount = ((CandidateNodeRole) role).getVotesCount() + 1;
        // 节点总数
        int countOfMajor = context.getGroup().getCount();
        logger.debug("votes count {}, node count {}", currentVoteCount, countOfMajor);

        // 取消选举超时定时器
        role.cancelTimeoutOrTask();
        if (currentVoteCount > countOfMajor / 2) { // 票数过半
            // become leader
            logger.info("become leader, term {}", role.getTerm());
            // resetReplicatingStates(); // todo implement after log replication implementation been done.
            scheduleElectionTimeout();
            changeToRole(new LeaderNodeRole(role.getTerm(), scheduleLogReplicationTask()));
            // context.log().appendEntry(role.getTerm()); // no-op log
        } else {
            // 修改收到的投票数，重新创建选举超时定时器
            changeToRole(new CandidateNodeRole(role.getTerm(), currentVoteCount, scheduleElectionTimeout()));
        }

    }

    private LogReplicationTask scheduleLogReplicationTask () {
        return context.getScheduler().scheduleLogReplicationTask(this::replicateLog);
    }

    private void replicateLog () {
        context.getTaskExecutor().submit(this::doReplicateLog);
    }

    private void doReplicateLog () {
        logger.debug("replicate log");
        // 给目标节点发送日志复制的AppendEntries消息
        context.getGroup().listReplicationTarget().forEach(this::doLogReplication);
    }

    private void doLogReplication (GroupMember member) {
        AppendEntriesRpc rpc = new AppendEntriesRpc();
        rpc.setTerm(role.getTerm());
        rpc.setLeaderId(context.selfId());
        rpc.setPrevLogIndex(0);
        rpc.setPrevLogTerm(0);
        rpc.setLeaderCommit(0);
        // rpc.entries 为空，待日志部分实现后再修改
        context.getConnector().sendAppendEntries(rpc, member.getEndpoint());
    }
}
