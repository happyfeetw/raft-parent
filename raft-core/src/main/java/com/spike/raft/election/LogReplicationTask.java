package com.spike.raft.election;

import com.spike.raft.test.NullScheduledFuture;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Leader节点日志复制定时器
 */
public class LogReplicationTask {
    /**
     * for test only
     */
    public static final LogReplicationTask NONE = new LogReplicationTask(new NullScheduledFuture());
    private final ScheduledFuture<?> scheduledFuture;
    public LogReplicationTask (ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public void cancel() {
        this.scheduledFuture.cancel(false);
    }

    @Override
    public String toString () {
        return "LogReplicationTask{delay="+ scheduledFuture.getDelay(TimeUnit.MILLISECONDS) +"}";
    }
}
