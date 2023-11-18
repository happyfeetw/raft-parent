package com.spike.raft.election;

public abstract class AbstractNodeRole {
    private final RoleName name;
    protected  final int term;

    AbstractNodeRole (RoleName name, int term) {
        this.name = name;
        this.term = term;
    }

    public RoleName getName () {
        return name;
    }

    /**
     * 取消选举超时或日志复制的定时任务
     * 在角色发生转换时必须调用该方法，然后创建新的定时任务或者超时时间
     */
    public abstract void cancelTimeoutOrTask();

    // todo why nodeId as param?
    public NodeId getLeaderId(NodeId nodeId) {
        return null;
    }

    public int getTerm () {
        return term;
    }

}
