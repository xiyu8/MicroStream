package com.jason.microstream.core.im.tup.reader;

import com.jason.microstream.core.im.tup.channelcontext.ChannelContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionAwareDispatcher {
    private final int queueCount;
    private final List<BlockingQueue<RawData>> queues;
    private final Map<Long, Integer> sessionToQueueMap;
    private final AtomicInteger counter = new AtomicInteger(0);

    public SessionAwareDispatcher(int queueCount, int queueCapacity) {
        this.queueCount = queueCount;
        this.queues = new ArrayList<>(queueCount);
        this.sessionToQueueMap = new ConcurrentHashMap<>();

        // 初始化队列
        for (int i = 0; i < queueCount; i++) {
            queues.add(new LinkedBlockingQueue<>(queueCapacity));
        }
    }

    /**
     * 将数据分发到相应的队列
     */
    public void dispatch(RawData data) {
        long sessionId = data.getChannelContext().readStatus.getSessionId();

        // 确定会话对应的队列索引
        int queueIndex = sessionToQueueMap.computeIfAbsent(sessionId, id -> {
            // 新会话：使用轮询策略分配队列
            return counter.getAndIncrement() % queueCount;
        });
        //或者如下：（只要保证同一个 待读的channel放到同一个que当中就行）
        // int queueIndex = sessionId % queueCount;

        // 将数据放入对应队列
        BlockingQueue<RawData> targetQueue = queues.get(queueIndex);

        // 如果队列已满，实现背压策略
        if (targetQueue.remainingCapacity() == 0) {
            handleBackpressure(sessionId, data);
        } else {
            targetQueue.offer(data);
        }
    }

    public void clearCache(RawData clearData) {
        // 确定会话对应的队列索引
        long clearSessionId = clearData.getChannelContext().readStatus.getSessionId();
        Integer index = sessionToQueueMap.get(clearSessionId);
        if (index == null) {
            return;
        }

        BlockingQueue<RawData> targetQueue = getQueue(index);
        // 创建一个临时队列，只保留非该会话的数据
        BlockingQueue<RawData> tempQueue = new LinkedBlockingQueue<>();
        int removedCount = 0;
        while (!targetQueue.isEmpty()) {
            RawData data = targetQueue.poll();
            if (data != null && data.getChannelContext().readStatus.getSessionId() != clearSessionId) {
                tempQueue.offer(data);
            } else if (data != null) {
                removedCount++;
            }
        }

        // 将过滤后的数据放回原队列
        targetQueue.addAll(tempQueue);

        if (removedCount > 0) {
//            logger.debug("Removed {} items for session {} from queue {}",
//                    removedCount, sessionId, queueIndex);
        }


    }

    /**
     * 处理背压情况
     */
    private void handleBackpressure(long sessionId, RawData data) {
        // 策略1：丢弃最旧的数据
        BlockingQueue<RawData> queue = queues.get(sessionToQueueMap.get(sessionId));
        if (queue.remainingCapacity() == 0) {
            queue.poll(); // 移除最旧的数据
        }
        queue.offer(data);

        // 策略2：暂时拒绝数据并记录日志
        // logger.warn("Queue full for session {}, dropping packet", sessionId);

        // 策略3：动态调整队列分配
        // reassignSessionToNewQueue(sessionId);
    }

    /**
     * 获取所有队列（供工作线程使用）
     */
    public BlockingQueue<RawData> getQueue(int index) {
        return queues.get(index);
    }

    /**
     * 获取队列数量
     */
    public int getQueueCount() {
        return queueCount;
    }

    /**
     * 清理不再活跃的会话
     */
    public void cleanupInactiveSessions(long timeoutMs) {
        long currentTime = System.currentTimeMillis();
//        sessionToQueueMap.entrySet().removeIf(entry -> {
//            // 这里需要访问会话的最后活动时间
//            // 实际实现中可能需要维护额外的会话状态信息
//            return currentTime - getLastActivityTime(entry.getKey()) > timeoutMs;
//        });
    }

    // 需要实现获取会话最后活动时间的方法
    private long getLastActivityTime(int sessionId) {
        // 从会话状态管理器中获取
        return 0; // 伪代码
    }

    public void onConnectionClosed(ChannelContext channelContext) {
        //TODO:不需要做任何处理，可在此处简单记录下，数据处理工作线程SessionAwareWorker会自行判断连接的关闭状态
    }
}