package com.jason.microstream.core.im.tup.channelcontext;


import com.jason.microstream.core.im.tup.data.SendNode;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

public class WriteStatus {


    private final long sessionId;// 会话唯一标识
    private SocketChannel channel;// 对应的通道
    private ByteBuffer cacheBuffer;// 累积缓冲区
    public SendNode currentNode;// 累积缓冲区
    private Queue<SendNode> sendQueue;
    private long lastActivityTime;
    private long processedCount;
    private Object protocolState; // 协议特定状态 ///////ProtocolState protocolState; // 协议解析状态

    public WriteStatus(SocketChannel channel, long sessionId) {
        this.sessionId = sessionId;
        this.channel = channel;
        this.lastActivityTime = System.currentTimeMillis();
        this.cacheBuffer = ByteBuffer.allocate(8192);
        this.processedCount = 0;
        this.sendQueue = new ArrayDeque<>();
    }

    // 获取累积缓冲区（用于处理半包）
    public ByteBuffer getAccumulateBuffer() {
        return cacheBuffer;
    }

    // 确保缓冲区有足够空间
    public void ensureCapacity(int requiredCapacity) {
        if (cacheBuffer.capacity() < requiredCapacity) {
            ByteBuffer newBuffer = ByteBuffer.allocate(
                    Math.max(cacheBuffer.capacity() * 2, requiredCapacity));
            cacheBuffer.flip();
            newBuffer.put(cacheBuffer);
            cacheBuffer = newBuffer;
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

    public long getSessionId() {
        return sessionId;
    }

    public void setAccumulateBuffer(ByteBuffer cacheBuffer) {
        this.cacheBuffer = cacheBuffer;
    }



    // 资源引用
    private BufferPool.BufferRef bufferRef;

    // 缓冲区管理
    public ByteBuffer getOrCreateBuffer(BufferPool pool, int size) {
        if (bufferRef == null) {
            bufferRef = pool.acquire(size);
        } else if (bufferRef.buffer.capacity() < size) {
            pool.release(bufferRef);
            bufferRef = pool.acquire(size);
        }

        bufferRef.buffer.clear();
        return bufferRef.buffer;
    }

    public void releaseBuffer(BufferPool pool) {
        if (bufferRef != null) {
            pool.release(bufferRef);
            bufferRef = null;
        }
    }

    public Queue<SendNode> getSendQueue() {
        return sendQueue;
    }
}
