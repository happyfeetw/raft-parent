package com.spike.raft.log;

import com.spike.raft.election.NodeId;
import com.spike.raft.rpc.AppendEntriesRpc;

import java.util.List;

/**
 * 日志组件顶级接口
 * getNextIndex和getCommitIndex是向外部提供日志组件的nextIndex和当前commitIndex的接口;
 * 在当前节点成为leader时, 调用getNextIndex, 并重置follower节点的日志复制进度, 此时所有follower节点的初始nextLogIndex
 * 都是当前节点的下一条日志的索引.
 *
 * isNewerTo方法用于收到的其他节点的RequestVote消息时用来判断是否投票. 使用getLastEntryMeta同样可以达到.
 *
 * close用于安全关闭日志组件
 *
 * setStateMachine是上层服务提供的应用日志回调. 需要该回调的原因是, 在raft算法中, 更新日志的commitIndex
 * 会间接更新上层服务写入日志时的哪些操作. 因为更新commitIndex是在日志组件的内部, 所以如果要通知上层应用, 必须通过回调完成.
 *
 */
public interface Log {
    int ALL_ENTRIES = -1;
    EntryMeta getLastEntryMeta();
    AppendEntriesRpc createAppendEntriesRpc(
            int term, NodeId selfId, int nextIndex, int maxEntries);

    /**
     * 获取下一条日志的索引
     * @return
     */
    int getNextIndex();

    /**
     *获取当前的commitIndex
     * @return
     */
    int getCommitIndex();

    /**
     * 判断收到的对象的lastLogIndex和LastLogTerm是否比自己新
     * @param lastLogIndex 自身节点的lastLogIndex
     * @param lastLogTerm 自身节点的lastLogTerm
     * @return
     */
    boolean isNewerTo(int lastLogIndex, int lastLogTerm);

    NoOpEntry appendEntry(int term);

    GeneralEntry appendEntry(int index, byte[] command);

    boolean appendEntriesFromLeader(int prevLogIndex, int prevLogTerm, List<Entry> entries);

    void advanceCommitIndex(int newCommitIndex, int currentTerm);

    //void setStateMachine(StateMachine stateMachine);

    void close();
}
