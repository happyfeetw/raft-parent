package com.spike.raft.election;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 非leader节点的选举超时定时器
 */
public class ElectionTimeout {
    public static final ElectionTimeout NONE = null;
    private final ScheduledFuture<?> scheduledFuture;

    public ElectionTimeout (ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    /**
     * 取消的参数设置为false，避免线程中断
     */
    public void cancel() {
        this.scheduledFuture.cancel(false);
    }

    /**
     * 方便单测和调试
     * @return 状态
     */
    @Override
    public String toString () {

        // 选举超时已取消
        if (this.scheduledFuture.isCancelled()) {
            return "ElectionTimeout(state=cancelled)";
        }

        // 选举超时已设定
        if (this.scheduledFuture.isDone()) {
            return "ElectionTimeout(state=done)";
        }

        // 选举超时即将在一定延时后设定
        return "ElectionTimeout(state=delay for " + scheduledFuture.getDelay(TimeUnit.MILLISECONDS);
    }
}
