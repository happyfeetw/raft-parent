package com.spike.raft.core;

import com.google.common.io.Files;
import com.spike.raft.election.NodeId;
import com.spike.raft.exception.NodeStoreException;
import com.spike.raft.log.RandomAccessFileAdapter;
import com.spike.raft.log.SeekableFile;

import java.io.File;
import java.io.IOException;

/**
 * file-oriented implementation
 * based on binary format.
 * currentTerm length: 4 bytes;
 * votedFor(nodeId) length: 4 bytes;
 * votedFor(content) length: variant.
 */
public class FileNodeStore implements NodeStore {

    public static final String FILE_NAME = "node.bin";
    private static final long OFFSET_TERM = 0;
    private static final long OFFSET_VOTED_FOR = 4;
    private final SeekableFile seekableFile;
    private int term = 0;
    private NodeId votedFor = null;

    /**
     * read from file
     * @param file
     */
    public FileNodeStore (File file) {
        try {
            if (!file.exists()) {
                Files.touch(file);
            }
            seekableFile = new RandomAccessFileAdapter(file);
            initializeOrLoad();
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
    }


    /**
     * for test
     * @param seekableFile
     */
    public FileNodeStore (SeekableFile seekableFile) {
        this.seekableFile = seekableFile;
        try {
            initializeOrLoad();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeOrLoad () throws IOException{
        if (seekableFile.size() == 0) {
            // initialize
            // (term, 4) + (votedFor length, 4) = 8
            seekableFile.truncate(8L);
            seekableFile.seek(0);
            seekableFile.writeInt(0); // term
            seekableFile.writeInt(0); // votedFor's length
        } else {
            // load
            // read term
            term = seekableFile.readInt();
            // read votedFor
            int length = seekableFile.readInt();
            if (length > 0) {
                byte[] bytes = new byte[length];
                seekableFile.read(bytes);
                votedFor = new NodeId(new String(bytes));
            }
        }
    }

    @Override
    public int getTerm () {
        return this.term;
    }

    @Override
    public void setTerm (int term) {
        try {
            seekableFile.seek(OFFSET_TERM);
            seekableFile.writeInt(term);
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
        this.term = term;
    }

    @Override
    public NodeId getVotedFor () {
        return this.votedFor;
    }

    @Override
    public void setVotedFor (NodeId votedFor) {
        try {
            seekableFile.seek(OFFSET_VOTED_FOR);
            // 初始
            if (votedFor == null) {
                seekableFile.writeInt(0);
                seekableFile.truncate(8L);
            } else {
                byte[] bytes = votedFor.getValue().getBytes();
                seekableFile.writeInt(bytes.length); // 长度
                seekableFile.write(bytes); // 内容
            }
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
        this.votedFor = votedFor;
    }

    @Override
    public void close () {
        try {
            seekableFile.close();
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
    }
}
