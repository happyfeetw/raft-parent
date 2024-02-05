package com.spike.raft.log;

import java.util.List;

/**
 * 将日志组件中主要的操作对象独立出来, 主要是为了raft的日志快照功能
 */
public interface EntrySequence {

    boolean isEmpty();

    int getFirstLogIndex();
    int getLastLogIndex();
    int getNextLogIndex();

    /**
     * 获取日志序列从指定索引的日志到最后一条日志的子视图
     * 闭区间
     * 主要用于构造AppendEntries消息时获取指定区间的日志条目
     * @param fromIndex 开始的索引, 必须大于0, 否则抛出异常
     * @return 子视图
     * @throws IllegalArgumentException
     */
    List<Entry> subList(int fromIndex) throws IllegalArgumentException;

    /**
     * 获取指定范围内的日志序列子视图
     * 左闭右开区间
     * 主要用于构造AppendEntries消息时获取指定区间的日志条目
     * @param fromIndex 大于等于0
     * @param toIndex 小于 size-1
     * @return 子视图
     * @throws IllegalArgumentException,IndexOutOfBoundsException
     */
    List<Entry> subList(int fromIndex, int toIndex) throws IllegalArgumentException, IndexOutOfBoundsException;

    /**
     * 检查指定索引位置的日志条目是否存在
     * @param index
     * @return
     */
    boolean isEntryPresent(int index);
    EntryMeta getEntryMeta(int index);
    Entry getEntry(int index);
    Entry getLastEntry();
    void append(Entry entry);
    void append(List<Entry> entries);

    /**
     * 推进commitIndex
     * @param index
     */
    void commit(int index);

    /**
     * 获取当前的commitIndex
     * @return index
     */
    int getCommitIndex();

    /**
     * 移除指定索引后面的所有日志条目
     * 主要用于当追加来自leader节点的日志时出现日志冲突的情况下, 需要移除现有日志
     * @param index
     */
    void removeAfter(int index);

    void close();
}
