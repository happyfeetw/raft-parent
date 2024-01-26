package com.spike.raft.log;

public class GeneralEntry extends AbstractEntry{
    private final byte[] commandBytes;

    public GeneralEntry(int kind, int index, int term, byte[] commandBytes) {
        super(kind, index, term);
        this.commandBytes = commandBytes;
    }

    @Override
    public byte[] getCommandBytes() {
        return this.commandBytes;
    }

    @Override
    public String toString() {
        return "GeneralEntry{" +
                "index=" + index +
                ", term=" + term +
                '}';
    }
}
