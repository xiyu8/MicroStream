package com.jason.microstream.core.im.tup.channelcontext;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class BufferPool {
    private final Queue<ByteBuffer> directBuffers = new ConcurrentLinkedQueue<>();
    private final int bufferSize;
    private final int maxBuffers;
    private final AtomicInteger allocatedCount = new AtomicInteger(0);

    public BufferPool(int bufferSize, int maxBuffers) {
        this.bufferSize = bufferSize;
        this.maxBuffers = maxBuffers;
    }

    public BufferRef acquire(int minSize) {
        // 如果请求的缓冲区比池化的大小大，直接分配
        if (minSize > bufferSize) {
            return new BufferRef(ByteBuffer.allocateDirect(minSize), null);
        }

        // 尝试从池中获取
        ByteBuffer buffer = directBuffers.poll();
        if (buffer == null) {
            if (allocatedCount.get() < maxBuffers) {
                buffer = ByteBuffer.allocateDirect(bufferSize);
                allocatedCount.incrementAndGet();
            } else {
                // 超过最大限制，等待或抛出异常
                throw new IllegalStateException("Buffer pool exhausted");
            }
        }

        buffer.clear();
        return new BufferRef(buffer, this);
    }

    public void release(BufferRef ref) {
        if (ref.pool == this && ref.buffer.capacity() == bufferSize) {
            directBuffers.offer(ref.buffer);
        }
        // 如果不是池管理的缓冲区，由GC回收
    }

    // 缓冲区引用类
    public static class BufferRef {
        public final ByteBuffer buffer;
        public final BufferPool pool;

        public BufferRef(ByteBuffer buffer, BufferPool pool) {
            this.buffer = buffer;
            this.pool = pool;
        }
    }
}