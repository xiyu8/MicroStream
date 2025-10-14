package com.jason.microstream.core.im.tup.reader;


import com.jason.microstream.core.im.tup.Demultiplexer;
import com.jason.microstream.core.im.tup.channelcontext.ChannelContext;
import com.jason.microstream.core.im.tup.channelcontext.ReadStatus;
import com.jason.microstream.tool.log.LogTool;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SessionAwareWorker implements Runnable {
    private final static String TAG = SessionAwareWorker.class.getSimpleName();

    private final int workerId;
    private final BlockingQueue<RawData> queue;
    private final ProtocolParser protocolParser;
    private volatile boolean running = true;

    private final ReadedDispatcher dataDispatcher;

    public SessionAwareWorker(int workerId, BlockingQueue<RawData> queue, ProtocolParser protocolParser, Demultiplexer demultiplexer) {
        this.workerId = workerId;
        this.queue = queue;
        this.protocolParser = protocolParser;

        this.dataDispatcher = new ReadedDispatcher(demultiplexer);
    }

    @Override
    public void run() {
        while (running) {
            try {
                // 从队列中获取数据（阻塞直到有数据可用）
                RawData data = queue.poll(100, TimeUnit.MILLISECONDS);
                if (data == null) {
                    // 定期清理不活跃的会话
//                    cleanupInactiveSessions(30 * 1000); // 30秒超时
                    continue;
                }

                // 处理数据
                processData(data);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // 记录错误但继续处理
//                LogTool.e(TAG, "Error processing data in worker {}");
            }
        }

//        // 清理资源
//        cleanup();
    }

    /**
     * 处理数据
     */
    private void processData(RawData data) {
//        int sessionId = data.getSessionId();
//        SocketChannel channel = data.getChannel();

//        // 获取或创建会话状态
//        SessionState state = sessionStates.computeIfAbsent(channel, key -> {
//            return new SessionState(channel, sessionId);
//        });
//
//        // 更新最后活动时间
//        state.setLastActivityTime(System.currentTimeMillis());
//        // 解析协议（使用会话特定的状态）
//        List<byte[]> packets = protocolParser.parse(ByteBuffer.wrap(data.getData()), state);
//        // 处理完整的数据包
//        for (byte[] packet : packets) {
//            processCompletePacket(channel,sessionId, packet, state);
//        }

        /**
         * 连接已经关闭时候，读到队列的数据：只需放弃数据即可，
         * ①放弃残缺的数据 对整体逻辑没影响，
         * ②从que中poll出来的rawData,已被移出队列，不被引用，会被系统回收；
         * rawData中放入context、channel，当不被其它引用后(其它:send的地方 连接管理处的缓存)，也会被系统回收
         * ③rawData中的读缓存数据，poll出来后 就没被引用了，也会被系统回收
         * 如果想要主动立刻执行回收，也可以在此手动置空 =null
         */
        if(!data.getChannelContext().isActive()) return;

        ReadStatus readStatus = data.getChannelContext().readStatus;
        readStatus.setLastActivityTime(System.currentTimeMillis());
        // 解析协议（使用会话特定的状态）
        List<byte[]> packets = protocolParser.parse(ByteBuffer.wrap(data.getData()), readStatus);
        // 处理完整的数据包
        for (byte[] packet : packets) {
            processCompletePacket(data.getChannelContext(), packet, data.getChannelContext().readStatus);
        }

    }

    /**
     * 处理完整的数据包
     */
    private void processCompletePacket(ChannelContext channelContext, byte[] packet, ReadStatus readStatus) {
        // 这里可以添加业务逻辑处理
        // 由于同一会话总是在同一线程处理，不需要同步

        // 示例：简单的回显处理
        // sendResponse(sessionId, packet.getData());

        // 记录处理指标
        readStatus.incrementProcessedCount();

        // 传递给业务处理器
//        BusinessProcessor.process(sessionId, packet.getData());
        dataDispatcher.dispatchData(packet,channelContext);
    }

//    /**
//     * 清理不活跃的会话
//     */
//    private void cleanupInactiveSessions(long timeoutMs) {
//        long currentTime = System.currentTimeMillis();
//        sessionStates.entrySet().removeIf(entry -> {
//            SessionState state = entry.getValue();
//            if (currentTime - state.getLastActivityTime() > timeoutMs) {
//                // 执行会话清理操作
//                cleanupSession(state);
//                return true;
//            }
//            return false;
//        });
//    }

    /**
     * 清理会话资源
     */
    private void cleanupSession(SessionState state) {
        // 释放与会话相关的资源
//        logger.info("Cleaning up inactive session: {}, processed {} packets",
//                state.getSessionId(), state.getProcessedCount());
        // 可以在这里通知业务层会话已关闭
    }

    /**
     * 停止工作线程
     */
    public void stop() {
        running = false;
    }

//    /**
//     * 彻底清理资源
//     */
//    private void cleanup() {
//        // 清理所有会话
//        for (SessionState state : sessionStates.values()) {
//            cleanupSession(state);
//        }
//        sessionStates.clear();
//    }

}