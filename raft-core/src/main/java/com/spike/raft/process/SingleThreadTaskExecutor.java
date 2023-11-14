package com.spike.raft.process;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SingleThreadTaskExecutor implements TaskExecutor {

    private ExecutorService executor;

    public SingleThreadTaskExecutor () {
        this(Executors.defaultThreadFactory());
    }

    public SingleThreadTaskExecutor(String name) {
        this(r -> new Thread(name));
    }

    public SingleThreadTaskExecutor (ThreadFactory threadFactory) {
        // 简化实现采用了Executors类
        // 也可以自定义实现
        executor = Executors.newSingleThreadScheduledExecutor(threadFactory);
    }


    @Override
    public Future<?> submit (Runnable task) {
        return executor.submit(task);
    }

    @Override
    public <V> Future<V> submit (Callable<V> task) {
        return executor.submit(task);
    }

    @Override
    public void shutdown () throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);
    }
}
