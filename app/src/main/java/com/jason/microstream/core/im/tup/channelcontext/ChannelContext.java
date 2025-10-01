package com.jason.microstream.core.im.tup.channelcontext;


import com.jason.microstream.core.im.tup.ChannelHolder;
import com.jason.microstream.core.im.tup.sender.SenderDeputyWorker;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelContext {
    private String intrinsicMark;
    // 连接标识信息
    private final long sessionId;
    private final SocketChannel channel;
    private ChannelHolder channelHolder;

    public final ReadStatus readStatus;
    public final WriteStatus writeStatus;
    /**
     * TODO:此处可能可以优化，这个Map只是用户read了ack消息之后，在sender组件中提示send成功时，找不到原send的消息SendNode的问题
     *  (因为read的ack，只会携带seqId 不会携带原SendNode)
     */
    public final Map<Long, SenderDeputyWorker.Task> waitAkcMap;
    public final ChannelAuth channelAuth;

    public long accessTime;

    // 连接状态（原子操作保证线程安全）
    private final AtomicInteger status = new AtomicInteger(Status.ACTIVE); //触发close的源头进行实时改变，其它时候不操作这个变量

    private long lastActivityTime;


    public ChannelContext(long sessionId, SocketChannel channel) {
        this.sessionId = sessionId;
        this.channel = channel;
        this.lastActivityTime = System.currentTimeMillis();

        readStatus = new ReadStatus(channel, sessionId);
        writeStatus = new WriteStatus(channel, sessionId);
        waitAkcMap = new ConcurrentHashMap<>();

        channelAuth = new ChannelAuth();

    }


    // 状态检查方法（无锁快速路径）
    public boolean isActive() {
        return status.get() == Status.ACTIVE;
    }

    public boolean markInactive() {
        return status.compareAndSet(Status.ACTIVE, Status.INACTIVE);//触发close的源头进行实时改变，其它时候不操作这个变量
    }

    public boolean markClosed() {
        return status.compareAndSet(Status.ACTIVE, Status.CLOSED);
    }

    public SocketChannel getSocket() {
        return channel;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setAccessTime(long accessTime) {
        this.accessTime = accessTime;
    }

    public void setIntrinsicMark(String intrinsicMark) {
        this.intrinsicMark = intrinsicMark;
    }

    public String getIntrinsicMark() {
        return intrinsicMark;
    }

    public ChannelHolder getChannelHolder() {
        return channelHolder;
    }

    public void setChannelHolder(ChannelHolder channelHolder) {
        this.channelHolder = channelHolder;
    }

    // 静态状态常量
    public static class Status {
        public static final int ACTIVE = 0;
        public static final int INACTIVE = 1;
        public static final int CLOSING = 2;
        public static final int CLOSED = 3;
    }

}
