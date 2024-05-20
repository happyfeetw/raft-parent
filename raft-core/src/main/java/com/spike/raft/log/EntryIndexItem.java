package com.spike.raft.log;

/**
 * 日志条目索引项
 */
public class EntryIndexItem {

    private int index; // 索引号
    private long offset; // 偏移量
    private int term;
    private int kind;

    public EntryIndexItem(int index, long offset, int term, int kind) {
        this.index = index;
        this.offset = offset;
        this.term = term;
        this.kind = kind;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }
}
