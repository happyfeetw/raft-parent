package com.spike.raft.test;

import com.spike.raft.election.LogReplicationTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * tester for scheduler components
 */
public class NullScheduler {
    private static final Logger logger = LoggerFactory.getLogger(NullScheduler.class);

    public LogReplicationTask scheduleLogReplicationTask(@Nonnull Runnable task) {
        logger.debug("schedule log replication task.");
        return LogReplicationTask.NONE;
    }
}
