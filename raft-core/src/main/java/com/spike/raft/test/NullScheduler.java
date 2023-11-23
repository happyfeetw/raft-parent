package com.spike.raft.test;

import com.spike.raft.election.ElectionTimeout;
import com.spike.raft.election.LogReplicationTask;
import com.spike.raft.schedule.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * tester for scheduler components
 */
public class NullScheduler implements Scheduler {
    private static final Logger logger = LoggerFactory.getLogger(NullScheduler.class);

    @Override
    public void stop() throws InterruptedException {
        // nothing to do.
    }

    /**
     * 日志复制定时器
     * @param task
     * @return
     */
    public LogReplicationTask scheduleLogReplicationTask(@Nonnull Runnable task) {
        logger.debug("schedule log replication task.");
        return LogReplicationTask.NONE;
    }

    public ElectionTimeout scheduleElectionTimeout(@Nonnull Runnable task) {
        logger.debug("schedule election timeout.");

        return ElectionTimeout.NONE;
    }

}
