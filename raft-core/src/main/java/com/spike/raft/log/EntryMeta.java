package com.spike.raft.log;

public class EntryMeta {
    private final int kind;
    private final int index;
    private final int term;

    public EntryMeta(int kind, int index, int term) {
        this.kind = kind;
        this.index = index;
        this.term = term;
    }

    public int getKind() {
        return this.kind;
    }

    public int getIndex() {
        return this.index;
    }

    public int getTerm() {
        return this.term;
    }

    @Override
    public String toString() {
        return "EntryMeta{" +
                "kind=" + kind +
                ", index=" + index +
                ", term=" + term +
                '}';
    }
}
