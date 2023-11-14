package com.spike.raft.process;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 直接执行的任务执行器
 */
public class DirectTaskExecutor implements TaskExecutor{
    @Override
    public Future<?> submit (Runnable task) {
        FutureTask<?> futureTask = new FutureTask<>(task, null);
        futureTask.run();
        return futureTask;
    }

    @Override
    public <V> Future<V> submit (Callable<V> task) {
        FutureTask<V> futureTask = new FutureTask<>(task);
        futureTask.run();
        return futureTask;
    }

    @Override
    public void shutdown () throws InterruptedException {
        // no need to implement.
    }
}
