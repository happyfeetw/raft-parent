package com.spike.raft.log;

public class NoOpEntry extends AbstractEntry{

    public NoOpEntry(int kind, int index, int term) {
        super(kind, index, term);
    }

    @Override
    public byte[] getCommandBytes() {
        return new byte[0];
    }

    @Override
    public String toString() {
        return "NoOpEntry{" +
                "index=" + index +
                ", term=" + term +
                '}';
    }
}
