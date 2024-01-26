package com.spike.raft.log;

/**
 * 状态机日志条目
 */
public interface Entry {
    /**
     * NO_OP日志条目
     * 记录选举产生的新的LEADER节点增加的第一条空日志
     * 不在上层服务的状态机中应用
     */
    int KIND_NO_OP = 0;

    /**
     * 普通日志条目
     * 记录上层服务的操作
     * 主要在上层服务的状态机中应用
     */
    int KIND_GENERAL = 1;

    int getKind();
    int getIndex();
    int getTerm();

    /**
     * 获取元信息(kind, index, term)
     * 与获取负载的方法做分离, 是因为获取日志负载是一项IO操作, 应该尽量避免频繁访问
     * @return 元信息对象
     */
    EntryMeta getMeta();

    /**
     * 获取日志负载的二进制内容
     * @return 日志负载的二进制内容
     */
    byte[] getCommandBytes();

}
