package com.spike.raft.log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class RandomAccessFileAdapter implements SeekableFile {
    public RandomAccessFileAdapter (File file) {
    }

    @Override
    public long position () throws IOException {
        return 0;
    }

    @Override
    public void seek (long position) throws IOException {

    }

    @Override
    public void writeInt (int i) throws IOException {

    }

    @Override
    public void writeLong (long l) throws IOException {

    }

    @Override
    public void write (byte[] b) throws IOException {

    }

    @Override
    public int readInt () throws IOException {
        return 0;
    }

    @Override
    public long readLong () throws IOException {
        return 0;
    }

    @Override
    public int read (byte[] b) throws IOException {
        return 0;
    }

    @Override
    public long size () throws IOException {
        return 0;
    }

    @Override
    public void truncate (long size) throws IOException {

    }

    @Override
    public InputStream inputStream (long start) throws IOException {
        return null;
    }

    @Override
    public void flush () throws IOException {

    }

    @Override
    public void close () throws IOException {

    }
}
