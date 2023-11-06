package com.spike.raft.schedule;

import com.spike.raft.election.ElectionTimeout;
import com.spike.raft.election.LogReplicationTask;

import java.sql.Time;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DefaultScheduler implements Scheduler{

    private final int minElectionTimeout;
    private final int maxElectionTimeout;
    private final int logReplicationDelay;
    private final int logReplicationInterval;
    private final Random electionTimeoutRandom;
    private final ScheduledExecutorService scheduledExecutorService;

    public DefaultScheduler (int minElectionTimeout, int maxElectionTimeout,
                             int logReplicationDelay, int logReplicationInterval) {

        if (minElectionTimeout <= 0
                || maxElectionTimeout <= 0
                || minElectionTimeout > maxElectionTimeout) {
            throw new IllegalArgumentException("Election timeout should not be 0 or min > max.");
        }

        if (logReplicationDelay < 0 || logReplicationInterval <= 0) {
            throw new IllegalArgumentException("Log replication delay < 0 or log replication interval <= 0.");
        }

        this.minElectionTimeout = minElectionTimeout;
        this.maxElectionTimeout = maxElectionTimeout;
        this.logReplicationDelay = logReplicationDelay;
        this.logReplicationInterval = logReplicationInterval;
        this.electionTimeoutRandom = new Random();
        this.scheduledExecutorService = Executors
                .newSingleThreadScheduledExecutor(r -> new Thread(r, "default-scheduler"));
    }

    @Override
    public void stop () throws InterruptedException {
        // todo
    }

    @Override
    public LogReplicationTask scheduleLogReplicationTask (Runnable task) {
        ScheduledFuture<?> replicationTaskFuture = scheduledExecutorService
                .scheduleWithFixedDelay(task, logReplicationDelay, logReplicationInterval, TimeUnit.MILLISECONDS);

        return new LogReplicationTask(replicationTaskFuture);
    }

    @Override
    public ElectionTimeout scheduleElectionTimeout (Runnable task) {
        int timeout = minElectionTimeout
                + electionTimeoutRandom.nextInt(maxElectionTimeout - minElectionTimeout);
        ScheduledFuture<?> electionTimeoutFuture = scheduledExecutorService.schedule(task, timeout, TimeUnit.MILLISECONDS);
        return new ElectionTimeout(electionTimeoutFuture);
    }
}
