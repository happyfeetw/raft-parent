package com.spike.raft.log;

import java.util.List;

public class FileEntrySequence implements EntrySequence{
    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getFirstLogIndex() {
        return 0;
    }

    @Override
    public int getLastLogIndex() {
        return 0;
    }

    @Override
    public int getNextLogIndex() {
        return 0;
    }

    @Override
    public List<Entry> subList(int fromIndex) throws IllegalArgumentException {
        return null;
    }

    @Override
    public List<Entry> subList(int fromIndex, int toIndex) throws IllegalArgumentException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public boolean isEntryPresent(int index) {
        return false;
    }

    @Override
    public EntryMeta getEntryMeta(int index) {
        return null;
    }

    @Override
    public Entry getEntry(int index) {
        return null;
    }

    @Override
    public Entry getLastEntry() {
        return null;
    }

    @Override
    public void append(Entry entry) {

    }

    @Override
    public void append(List<Entry> entries) {

    }

    @Override
    public void commit(int index) {

    }

    @Override
    public int getCommitIndex() {
        return 0;
    }

    @Override
    public void removeAfter(int index) {

    }

    @Override
    public void close() {

    }
}
