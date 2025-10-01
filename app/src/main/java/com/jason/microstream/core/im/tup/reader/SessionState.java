package com.jason.microstream.core.im.tup.reader;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SessionState {
    private final int sessionId;// 会话唯一标识
    private SocketChannel channel;// 对应的通道
    private ByteBuffer accumulateBuffer;// 累积缓冲区
    private long lastActivityTime;
    private long processedCount;
    private Object protocolState; // 协议特定状态 ///////ProtocolState protocolState; // 协议解析状态

    public SessionState(SocketChannel channel, int sessionId) {
        this.sessionId = sessionId;
        this.channel = channel;
        this.lastActivityTime = System.currentTimeMillis();
        this.accumulateBuffer = ByteBuffer.allocate(8192);
        this.processedCount = 0;
    }

    // 获取累积缓冲区（用于处理半包）
    public ByteBuffer getAccumulateBuffer() {
        return accumulateBuffer;
    }

    // 确保缓冲区有足够空间
    public void ensureCapacity(int requiredCapacity) {
        if (accumulateBuffer.capacity() < requiredCapacity) {
            ByteBuffer newBuffer = ByteBuffer.allocate(
                    Math.max(accumulateBuffer.capacity() * 2, requiredCapacity));
            accumulateBuffer.flip();
            newBuffer.put(accumulateBuffer);
            accumulateBuffer = newBuffer;
        }
    }

    // 更新活动时间
    public void setLastActivityTime(long time) {
        this.lastActivityTime = time;
    }

    public long getLastActivityTime() {
        return lastActivityTime;
    }

    // 增加处理计数
    public void incrementProcessedCount() {
        processedCount++;
    }

    public long getProcessedCount() {
        return processedCount;
    }

    // 获取和设置协议特定状态
    public Object getProtocolState() {
        return protocolState;
    }

    public void setProtocolState(Object protocolState) {
        this.protocolState = protocolState;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setAccumulateBuffer(ByteBuffer accumulateBuffer) {
        this.accumulateBuffer = accumulateBuffer;
    }
}