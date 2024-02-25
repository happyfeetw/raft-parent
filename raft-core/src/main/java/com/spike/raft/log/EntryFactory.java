package com.spike.raft.log;

public class EntryFactory {
    public Entry create(int kind, int index, int term, byte[] commandBytes) {
        switch (kind) {
            case Entry.KIND_NO_OP:
                return new NoOpEntry(index, term);
            case Entry.KIND_GENERAL:
                return new GeneralEntry(kind, index, term, commandBytes);
            default:
                throw new IllegalArgumentException("Unexpected entry kind: " + kind);
        }
    }
}
