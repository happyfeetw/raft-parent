import com.spike.raft.cluster.NodeEndpoint;
import com.spike.raft.core.NodeBuilder;
import com.spike.raft.core.NodeImpl;
import com.spike.raft.election.FollowerNodeRole;
import com.spike.raft.election.NodeId;
import com.spike.raft.process.DirectTaskExecutor;
import com.spike.raft.test.MockConnector;
import com.spike.raft.test.NullScheduler;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class NodeImplTest {
    private NodeBuilder newNodeBuilder(NodeId selfId, NodeEndpoint... nodeEndpoints) {
        return new NodeBuilder(Arrays.asList(nodeEndpoints), selfId)
                .setScheduler(new NullScheduler())
                .setConnector(new MockConnector())
                .setTaskExecutor(new DirectTaskExecutor());
    }

    @Test
    public void start() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"), new NodeEndpoint("A", "localhost", 10086)).build();
        node.start();
        FollowerNodeRole role = (FollowerNodeRole) node.getRole();
        Assert.assertEquals(0, role.getTerm());
        Assert.assertNull(role.getVotedFore());
    }
}
