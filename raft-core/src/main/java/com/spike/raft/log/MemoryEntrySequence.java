package com.spike.raft.log;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于内存的日志条目序列实现
 * 由于对日志条目序列的访问存在随机性,
 * 因此本实现采用可变数组而不是链表来构造日志条目,
 * 以提高访问效率.
 */
public class MemoryEntrySequence extends AbstractEntrySequence{
    private static final int INITIAL_LOG_INDEX_OFFSET = 1;
    private final List<Entry> entries = new ArrayList<>();
    private int commitIndex = 0;

    public MemoryEntrySequence() {
        this(INITIAL_LOG_INDEX_OFFSET);
    }

    public MemoryEntrySequence(int logIndexOffset) {
        super(logIndexOffset);
    }

    @Override
    protected List<Entry> doSubList(int fromIndex, int toIndex) {
        return entries.subList(fromIndex - super.logIndexOffset, toIndex - super.logIndexOffset);
    }

    @Override
    protected Entry doGetEntry(int index) {
        return entries.get(index - super.logIndexOffset);
    }

    @Override
    protected void doAppend(Entry entry) {
        entries.add(entry);
    }

    @Override
    public void commit(int index) {
        commitIndex = index;
    }

    @Override
    public int getCommitIndex() {
        return commitIndex;
    }

    @Override
    public void close() {
        // do nothing because of in-memory operations.
    }

    @Override
    protected void doRemoveAfter(int index) {
        if (index < doGetFirstLogIndex()) {
            entries.clear();
            nextLogIndex = logIndexOffset;
        } else {
            entries.subList(index - logIndexOffset + 1, entries.size())
                    .clear();
            nextLogIndex = index + 1;
        }
    }
}
