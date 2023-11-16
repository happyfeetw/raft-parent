package com.spike.raft.core;

/**
 * 核心组件接口
 *
 */
public interface Node {

    void start();

    void stop() throws InterruptedException;
}
