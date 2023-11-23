package com.spike.raft.election;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Leader节点日志复制定时器
 */
public class LogReplicationTask {
    public static final LogReplicationTask NONE = null;
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
