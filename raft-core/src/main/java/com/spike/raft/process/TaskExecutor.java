package com.spike.raft.process;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 将核心组件的处理模式分离出来，方便快速修改
 * 因为同步处理会阻塞执行线程，因此设计上直接使用异步实现。
 */
public interface TaskExecutor {

    Future<?> submit(Runnable task);

    <V> Future<V> submit(Callable<V> task);

    void shutdown() throws InterruptedException;
}
