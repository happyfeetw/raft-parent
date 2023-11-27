import com.spike.raft.cluster.NodeEndpoint;
import com.spike.raft.core.Node;
import com.spike.raft.core.NodeBuilder;
import com.spike.raft.core.NodeImpl;
import com.spike.raft.election.*;
import com.spike.raft.process.DirectTaskExecutor;
import com.spike.raft.rpc.*;
import com.spike.raft.test.MockConnector;
import com.spike.raft.test.NullScheduler;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NodeImplTest {

    /**
     * 三节点系统中A为leader，向其他节点发送消息并收到回复
     * 该场景为一般场景的测试
     */
    @Test
    public void testOnReceiveAppendEntriesNormal() {
        NodeImpl node = prepare3Nodes();
        node.start();
        node.electionTimeout(); // become candidate
        node.onReceiveRequestVoteResult(new RequestVoteResult(1, true)); // become leader
        node.replicateLog();
        node.onReceiveAppendEntriesResult(new AppendEntriesResultMessage(
                NodeId.of("B"),
                new AppendEntriesResult(1, true)));
    }

    /**
     * 三节点系统中，节点A作为follower，收到作为leader的B发来的心跳（日志复制消息）
     * 并设置自己的term和leaderId并回复ok
     */
    @Test
    public void testOnReceiveAppendEntriesRpcFollower() {
        NodeImpl node = prepare3Nodes();
        node.start();
        AppendEntriesRpc rpc = new AppendEntriesRpc();
        rpc.setTerm(1);
        rpc.setLeaderId(NodeId.of("B"));

        node.onReceiveAppendEntriesRpc(new AppendEntriesRpcMessage(rpc, NodeId.of("B")));
        MockConnector mockConnector = (MockConnector) node.getContext().getConnector();
        AppendEntriesResult result = (AppendEntriesResult) mockConnector.getResult();
        Assert.assertEquals(1, result.getTerm());
        Assert.assertTrue(result.isSuccess());
        FollowerNodeRole role = (FollowerNodeRole) node.getRole();
        Assert.assertEquals(NodeId.of("B"), role.getLeaderId());
    }

    /**
     * 在三节点系统中，A变成leader后向B和C节点发送心跳消息
     */
    @Test
    public void testReplicateLog() {
        NodeImpl node = prepare3Nodes();
        node.start();
        node.electionTimeout(); //  发送拉票消息（requestVote）
        node.onReceiveRequestVoteResult(new RequestVoteResult(1, true));
        node.replicateLog(); // 给其他节点发送日志复制的消息
        MockConnector mockConnector = (MockConnector) node.getContext().getConnector();

        // 总共三条消息，一条是拉票消息，两条是日志复制消息
        Assert.assertEquals(3, mockConnector.getMessageCount());
        // 检查目标节点
        List<MockConnector.Message> messages = mockConnector.getMessages();
        Set<NodeId> destinationNodeIds = messages
                .subList(1, messages.size())
                .stream()
                .map(MockConnector.Message::getDestinationNodeId)
                .collect(Collectors.toSet());
        Assert.assertEquals(2, destinationNodeIds.size());
        Assert.assertTrue(destinationNodeIds.contains(NodeId.of("B")));
        Assert.assertTrue(destinationNodeIds.contains(NodeId.of("C")));
        AppendEntriesRpc rpc1 = (AppendEntriesRpc) messages.get(1).getRpc();
        AppendEntriesRpc rpc2 = (AppendEntriesRpc) messages.get(2).getRpc();
        Assert.assertEquals(1, rpc1.getTerm());
        Assert.assertEquals(1, rpc2.getTerm());
    }

    /**
     * 在ABC三个节点中，A变成candidate后收到拉票响应结果信息，并变成leader
     * 仅模拟获得一票的情况
     */
    @Test
    public void testOnReceiveRequestVoteResult() {
        NodeImpl node = prepare3Nodes();
        node.start();
        node.electionTimeout(); // 发起选举，同时当前节点变成candidate
        node.onReceiveRequestVoteResult(new RequestVoteResult(1, true));
        LeaderNodeRole role = (LeaderNodeRole) node.getRole();
        Assert.assertEquals(1, role.getTerm());
    }

    /**
     * 测试follower节点收到 RequestVote 消息后的处理逻辑
     * 要求follower收到其他节点的 RequestVote 消息后，给消息来源的节点投票
     */
    @Test
    public void testOnReceiveRequestVoteRpcFollower() {
        NodeImpl node = prepare3Nodes();
        node.start();
        // 构建一个从C节点发来的消息
        RequestVoteRpc rpc = new RequestVoteRpc();
        rpc.setTerm(1);
        rpc.setCandidateId(NodeId.of("C"));
        rpc.setLastLogIndex(0);
        rpc.setLastLogTerm(0);

        // 模拟当前节点收到上面构建的消息
        node.onReceiveRequestVoteRpc(new RequestVoteRpcMessage(rpc, NodeId.of("C")));

        // 校验当前节点回复的消息是否符合预期
        MockConnector connector = (MockConnector) node.getContext().getConnector();
        RequestVoteResult result = (RequestVoteResult) connector.getResult();
        Assert.assertEquals(1, result.getTerm());
        Assert.assertTrue(result.isVoteGranted()); // 校验当前节点是否给C投票
        Assert.assertEquals(NodeId.of("C"), ((FollowerNodeRole) node.getRole()).getVotedFore());
    }


    /**
     * 选举超时测试，要求Follower选举超时后变成Candidate角色，并给其他节点发送RequestVote消息
     */
    @Test
    public void testElectionTimeoutWhenFollower() {
        NodeImpl node = prepare3Nodes();
        node.start();
        node.electionTimeout();

        // 选举开始后，初始term为1
        CandidateNodeRole role = (CandidateNodeRole) node.getRole();
        // 校验选举超时后的自身数据
        Assert.assertEquals(1, role.getTerm());
        Assert.assertEquals(1, role.getVotesCount());
        MockConnector mockConnector = (MockConnector) node.getContext().getConnector();
        // 校验向其他节点发送RequestVote消息
        RequestVoteRpc rpc = (RequestVoteRpc) mockConnector.getRPC();
        Assert.assertEquals(1, rpc.getTerm());
        Assert.assertEquals(NodeId.of("A"), rpc.getCandidateId());
        Assert.assertEquals(0, rpc.getLastLogIndex());
        Assert.assertEquals(0, rpc.getLastLogTerm());

    }

    /**
     * 启动测试，要求启动后角色为follower，term为0
     */
    @Test
    public void start() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"), new NodeEndpoint("A", "localhost", 10086)).build();
        node.start();
        FollowerNodeRole role = (FollowerNodeRole) node.getRole();
        Assert.assertEquals(0, role.getTerm());
        Assert.assertNull(role.getVotedFore());
    }

    /**
     * 创建构建器
     * @param selfId
     * @param nodeEndpoints
     * @return
     */
    private NodeBuilder newNodeBuilder(NodeId selfId, NodeEndpoint... nodeEndpoints) {
        return new NodeBuilder(Arrays.asList(nodeEndpoints), selfId)
                .setScheduler(new NullScheduler())
                .setConnector(new MockConnector())
                .setTaskExecutor(new DirectTaskExecutor());
    }

    private NodeImpl prepare3Nodes() {
        return (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", "localhost", 2333),
                new NodeEndpoint("B", "localhost", 2334),
                new NodeEndpoint("C", "localhost", 2335)
        ).build();
    }

    private NodeImpl prepare4Nodes() {
        return (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", "localhost", 2333),
                new NodeEndpoint("B", "localhost", 2334),
                new NodeEndpoint("C", "localhost", 2335),
                new NodeEndpoint("D", "localhost", 2336)
        ).build();
    }
}
