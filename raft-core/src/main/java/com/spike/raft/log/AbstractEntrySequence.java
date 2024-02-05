package com.spike.raft.log;

import com.spike.raft.exception.EmptySequenceException;

import java.util.Collections;
import java.util.List;

public abstract class AbstractEntrySequence implements EntrySequence{

    private int logIndexOffset;
    private int nextLogIndex;

    public AbstractEntrySequence(int logIndexOffset) {
        this.logIndexOffset = logIndexOffset;
        this.nextLogIndex = logIndexOffset;
    }



    @Override
    public boolean isEmpty() {
        return logIndexOffset == nextLogIndex;
    }

    @Override
    public int getFirstLogIndex() {
        if (isEmpty()) {
            throw new EmptySequenceException();
        }
        return doGetFirstLogIndex();
    }

    private int doGetFirstLogIndex() {
        return logIndexOffset;
    }

    @Override
    public int getLastLogIndex() {
        if (isEmpty()) {
            throw new EmptySequenceException();
        }
        return doGetLastLogIndex();
    }

    private int doGetLastLogIndex() {
        return nextLogIndex - 1;
    }

    @Override
    public int getNextLogIndex() {
        return nextLogIndex;
    }

    @Override
    public List<Entry> subList(int fromIndex) throws IllegalArgumentException {
        if (isEmpty() || fromIndex > doGetLastLogIndex()) {
            return Collections.emptyList();
        }
        //防止越界 取大的一个
        return subList(Math.max(fromIndex, doGetFirstLogIndex()), nextLogIndex);
    }

    @Override
    public List<Entry> subList(int fromIndex, int toIndex) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (isEmpty()) {
            throw new EmptySequenceException();
        }
        // 检查索引
        if (fromIndex < doGetFirstLogIndex()
                || toIndex > doGetLastLogIndex() + 1
                || fromIndex > toIndex) {
            throw new IllegalArgumentException("Illegal from index " + fromIndex + " or to index " + toIndex + ".");
        }

        return doSubList(fromIndex, toIndex);
    }

    /**
     * 获取一个子视图
     * 需要子类根据情况各自实现
     * @param fromIndex
     * @param toIndex
     * @return
     */
    protected abstract List<Entry> doSubList(int fromIndex, int toIndex);

    @Override
    public boolean isEntryPresent(int index) {
        return !isEmpty() && index >= doGetFirstLogIndex() && index <= doGetLastLogIndex();
    }

    @Override
    public EntryMeta getEntryMeta(int index) {
        Entry entry = getEntry(index);
        return entry != null ? entry.getMeta() : null;
    }

    @Override
    public Entry getEntry(int index) {
        if (!isEntryPresent(index)) {
            return null;
        }
        return doGetEntry(index);
    }

    @Override
    public Entry getLastEntry() {
        return isEmpty() ? null : doGetEntry(doGetLastLogIndex());
    }

    /**
     * 核心方法 委派给不同的子类各自实现
     * @param index
     * @return
     */
    protected abstract Entry doGetEntry(int index);

    @Override
    public void append(Entry entry) {
        if (entry.getIndex() != nextLogIndex) {
            throw new IllegalArgumentException("Entry index must be "
                    + nextLogIndex + ", but actually it is " + entry.getIndex() + ".");
        }
        doAppend(entry);
        nextLogIndex ++;
    }

    @Override
    public void append(List<Entry> entries) {
        for (Entry entry : entries) {
            append(entry);
        }
    }

    /**
     * 追加日志
     * 由子类实现
     * @param entry
     */
    protected abstract void doAppend(Entry entry);

    @Override
    public void commit(int index) {

    }

    @Override
    public int getCommitIndex() {
        return 0;
    }

    @Override
    public void removeAfter(int index) {
        if (isEmpty() || index >= doGetLastLogIndex()) {
            return;
        }
        doRemoveAfter(index);
    }

    /**
     * 移除指定索引后的日志条目
     * 由子类实现
     * @param index
     */
    protected abstract void doRemoveAfter(int index);

    @Override
    public void close() {

    }
}
