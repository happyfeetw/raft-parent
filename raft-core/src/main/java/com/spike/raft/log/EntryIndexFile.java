package com.spike.raft.log;

import java.io.File;
import java.io.IOException;
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
    private Map<Integer, EntryIndexItem> entryIndexMap = new HashMap<>();

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

    @Override
    public Iterator<EntryIndexItem> iterator() {
        return null;
    }

}
