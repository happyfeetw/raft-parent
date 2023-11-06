package com.spike.raft.schedule;

import com.spike.raft.election.ElectionTimeout;
import com.spike.raft.election.LogReplicationTask;

public interface Scheduler {
    void stop() throws InterruptedException;

    LogReplicationTask scheduleLogReplicationTask(Runnable task);

    ElectionTimeout scheduleElectionTimeout(Runnable task);
}
