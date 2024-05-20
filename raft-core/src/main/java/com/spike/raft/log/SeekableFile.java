package com.spike.raft.log;

import java.io.IOException;
import java.io.InputStream;

public interface SeekableFile {
    // 获取当前位置
    long position() throws IOException;
    // 将文件指针移动到指定位置
    void seek(long position) throws IOException;
    // 写入整数
    void writeInt(int i) throws IOException;
    // 写入长整数
    void writeLong(long l) throws IOException;
    // 写入字节数组
    void write(byte[] b) throws IOException;
    // 读取整数(1个)
    int readInt() throws IOException;
    // 读取长整数
    long readLong() throws IOException;
    // 读取字节数组，返回读取的长度
    int read(byte[] b) throws IOException;
    // 获取文件的大小
    long size() throws IOException;
    // 将文件裁剪到指定大小
    void truncate(long size) throws IOException;
    // 从指定位置创建输入流
    InputStream inputStream(long start) throws IOException;
    // 强制刷盘
    void flush() throws IOException;
    // 关闭文件
    void close() throws IOException;

}
