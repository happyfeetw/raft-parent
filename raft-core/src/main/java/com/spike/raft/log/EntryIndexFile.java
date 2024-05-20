package com.spike.raft.log;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EntryIndexFile implements Iterable<EntryIndexItem> {
    // 最大条目索引的偏移量
    private static final long OFFSET_MAX_ENTRY_INDEX = Integer.BYTES;
    // 单条日志条目元信息的长度
    private static final int LENGTH_ENTRY_INDEX_ITEM = 16;

    private final SeekableFile seekableFile;
    private int entryIndexCount; // 日志条目个数
    private int minEntryIndex; // 最小日志条目的索引
    private int maxEntryIndex; // 最大日志条目的索引
    // 索引缓存
    private final Map<Integer, EntryIndexItem> entryIndexMap = new HashMap<>();

    public EntryIndexFile(File file) throws IOException {
        this(new RandomAccessFileAdapter(file));
    }

    public EntryIndexFile(SeekableFile seekableFile) throws IOException{
        this.seekableFile = seekableFile;
        this.load();
    }

    // 初始化, 加载所有日志元信息
    private void load() throws IOException{
        if (seekableFile.size() == 0L) {
            entryIndexCount = 0;
            return;
        }
        this.minEntryIndex = seekableFile.readInt();
        this.maxEntryIndex = seekableFile.readInt();
        this.updateEntryIndexCount();
        // 逐条加载
        long offset;
        int kind,  term;
        for (int i = minEntryIndex; i <= maxEntryIndex; i++) {
            offset = seekableFile.readLong();
            kind = seekableFile.readInt();
            term = seekableFile.readInt();
            entryIndexMap.put(i, new EntryIndexItem(i, offset, term, kind));
        }
    }

    private void updateEntryIndexCount() {
        this.entryIndexCount = maxEntryIndex - minEntryIndex + 1;
    }

    // 追加条目元信息数据
    public void appendEntryIndex(int index, long offset, int kind, int term) throws IOException{
        if (seekableFile.size() == 0) {
            // 文件为空 直接定位到最小索引位置准备写入
            seekableFile.writeInt(index);
            minEntryIndex = index;
        } else {
            // 索引检查
            if (index != maxEntryIndex + 1) {
                // 不等于，说明索引维护错误。正常情况下每次写开始时，当前索引都会位于最大位置处.
                throw new IllegalArgumentException(String.format("Index must be %s, but was %s", maxEntryIndex + 1, index));
            }
            // 文件不为空，跳过最小，直接定位到最大索引位置(文件末尾),准备写入新数据
            seekableFile.seek(OFFSET_MAX_ENTRY_INDEX);
        }
        // 写入索引
        seekableFile.writeInt(index);
        maxEntryIndex = index;
        this.updateEntryIndexCount();
        // 移动到文件最后,写入其他信息
        seekableFile.seek(getOffsetOfEntryIndexItem(index));
        seekableFile.writeLong(offset);
        seekableFile.writeInt(kind);
        seekableFile.writeInt(term);
        entryIndexMap.put(index, new EntryIndexItem(index, offset, term, kind));
    }

    // 获取指定索引位置的日志的偏移量
    private long getOffsetOfEntryIndexItem(int index) {
         return (long) (index - minEntryIndex) * LENGTH_ENTRY_INDEX_ITEM + Integer.BYTES * 2;
    }

    // 清空某个index后的所有数据

    public void removeAfter(int newMaxEntryIndex) throws IOException {
        if (isEmpty() || newMaxEntryIndex > maxEntryIndex) {
            return;
        }

        if (newMaxEntryIndex < minEntryIndex) {
            clear();
            return;
        }

        seekableFile.seek(OFFSET_MAX_ENTRY_INDEX);
        seekableFile.writeInt(newMaxEntryIndex);
        seekableFile.truncate(getOffsetOfEntryIndexItem(newMaxEntryIndex + 1));

        for (int i = newMaxEntryIndex + 1; i <= maxEntryIndex; i++) {
            entryIndexMap.remove(i);
        }
        maxEntryIndex = newMaxEntryIndex;
        entryIndexCount = newMaxEntryIndex - minEntryIndex + 1;
    }
    private boolean isEmpty() {
        return entryIndexCount == 0;
    }

    // 全部清空
    public void clear() throws IOException {
        seekableFile.truncate(0L);
        entryIndexCount = 0;
        entryIndexMap.clear();
    }

    @Nonnull
    @Override
    public Iterator<EntryIndexItem> iterator() {
        if (isEmpty()) {
            return Collections.emptyIterator();
        }
        return new EntryIndexIterator(entryIndexCount, minEntryIndex);
    }

    private class EntryIndexIterator implements Iterator<EntryIndexItem> {
        private final int entryIndexCount;
        private int currentEntryIndex;

        public EntryIndexIterator(int entryIndexCount, int minEntryIndex) {
            this.entryIndexCount = entryIndexCount;
            this.currentEntryIndex = minEntryIndex;
        }

        @Override
        public boolean hasNext() {
            checkModification();
            return currentEntryIndex <= maxEntryIndex;
        }

        @Override
        public EntryIndexItem next() {
            checkModification();
            return entryIndexMap.get(currentEntryIndex++);
        }

        // todo 是否需要同步锁
        private void checkModification() {
            // 迭代器的indexCount与外部类的indexCount必须一致,不一致就报错
            if (this.entryIndexCount != EntryIndexFile.this.entryIndexCount) {
                throw new IllegalStateException("Entry index count changed.");
            }
        }

        public int getEntryIndexCount() {
            return entryIndexCount;
        }

        public int getCurrentEntryIndex() {
            return currentEntryIndex;
        }
    }
}
