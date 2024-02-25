package com.spike.raft.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 文件内容格式
 *      +---------+-----------+----------+------------+-----------------+
 *      |  int(4) |  int(4)   |  int(4)  |   int(4)   |     bytes       |
 *      +---------+-----------+----------+------------+-----------------+
 *      |  kind   |  index    |  term    |   length   |   command bytes |
 *      +---------+-----------+----------+------------+-----------------+
 */
public class EntriesFile {

    private final SeekableFile seekableFile;

    public EntriesFile(File file) throws FileNotFoundException {
        this(new RandomAccessFileAdapter(file));
    }

    public EntriesFile(SeekableFile seekableFile) {
        this.seekableFile = seekableFile;
    }

    /**
     * 写入日志条目内容
     * @param entry
     * @return
     * @throws IOException
     */
    public long appendEntry(Entry entry) throws IOException {
        long offset = seekableFile.size();
        seekableFile.seek(offset);
        seekableFile.writeInt(entry.getKind());
        seekableFile.writeInt(entry.getIndex());
        seekableFile.writeInt(entry.getTerm());
        byte[] commandBytes = entry.getCommandBytes();
        seekableFile.writeInt(commandBytes.length);
        seekableFile.write(commandBytes);
        // todo 是否需要考虑换行符?
        return offset;
    }

    /**
     * 从指定偏移量加载日志条目
     * @param offset
     * @param factory
     * @return
     * @throws IOException
     */
    public Entry loadEntry(long offset, EntryFactory factory) throws IOException {
        if (offset > seekableFile.size()) {
            throw new IllegalArgumentException("Offset: " + offset + " is greater than file size: " + seekableFile.size());
        }
        seekableFile.seek(offset);
        // 依次读字节
        int kind = seekableFile.readInt();
        int index = seekableFile.readInt();
        int term = seekableFile.readInt();
        int length = seekableFile.readInt();
        byte[] bytes = new byte[length];
        seekableFile.read(bytes);
        return factory.create(kind, index, term, bytes);
    }

    public long size() throws IOException {
        return seekableFile.size();
    }

    public void clear() throws IOException {
        truncate(0L);
    }

    // 将文件裁剪到指定大小
    public void truncate(long offset) throws IOException{
        seekableFile.truncate(offset);
    }

    public void close() throws IOException{
        seekableFile.close();
    }

}
