package com.spike.raft.test;

import com.spike.raft.cluster.NodeEndpoint;
import com.spike.raft.election.NodeId;
import com.spike.raft.rpc.*;

import java.util.*;

/**
 * tester for rpc components.
 * can be substituted by JMock or other libs.
 */
public class MockConnector implements Connector {

    private LinkedList<Message> messages = new LinkedList<>();

    private Message getLastMessage() {
        return messages.isEmpty() ? null : messages.getLast();
    }

    private Message getLastMessageOrDefault() {
        return messages.isEmpty() ? new Message() : messages.getLast();
    }

    /**
     * 获得最后一条rpc消息
     * @return
     */
    public Object getRPC() {
        return getLastMessageOrDefault().rpc;
    }

    /**
     * 获得最后一条result消息
     * @return
     */
    public Object getResult() {
        return getLastMessageOrDefault().result;
    }

    /**
     * 获得最后一条消息的目标节点
     * @return
     */
    public NodeId getDestinationNodeId() {
        return getLastMessageOrDefault().destinationNodeId;
    }

    /**
     * 获取消息总数
     * @return
     */
    public int getMessageCount() {
        return messages.size();
    }

    /**
     * 获取所有消息
     * @return
     */
    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    @Override
    public void initialize() {
        // no need to do.
    }

    @Override
    public void close() {
        messages.clear();
    }

    @Override
    public void sendRequestVote(RequestVoteRpc rpc, Collection<NodeEndpoint> destEndpoints) {
        // todo 处理多目标节点的情况
        Message m = new Message();
        m.rpc = rpc;
        messages.add(m);
    }

    @Override
    public void replyRequestVote(RequestVoteResult result, NodeEndpoint destEndpoint) {
        Message m = new Message();
        m.result = result;
        m.destinationNodeId = destEndpoint.getId();
        messages.add(m);
    }

    @Override
    public void sendAppendEntries(AppendEntriesRpc rpc, NodeEndpoint destEndpoint) {
        Message m = new Message();
        m.rpc = rpc;
        m.destinationNodeId = destEndpoint.getId();
        messages.add(m);
    }

    @Override
    public void replyAppendEntries(AppendEntriesResult result, NodeEndpoint destEndpoint) {
        Message m = new Message();
        m.result = result;
        m.destinationNodeId = destEndpoint.getId();
        messages.add(m);
    }

    public static class Message {
        private Object rpc;
        private NodeId destinationNodeId;
        private Object result;

        public Object getRpc() {
            return rpc;
        }

        public NodeId getDestinationNodeId() {
            return destinationNodeId;
        }

        public Object getResult() {
            return result;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "rpc=" + rpc +
                    ", destinationNodeId=" + destinationNodeId +
                    ", result=" + result +
                    '}';
        }
    }
}
