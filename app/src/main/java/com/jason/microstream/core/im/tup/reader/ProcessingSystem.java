package com.jason.microstream.core.im.tup.reader;


import com.jason.microstream.core.im.tup.Demultiplexer;
import com.jason.microstream.core.im.tup.channelcontext.ChannelContext;
import com.jason.microstream.tool.log.LogTool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProcessingSystem {
    private static final String TAG = ProcessingSystem.class.getSimpleName();

    private final SessionAwareDispatcher awareDispatcher;
    private final List<SessionAwareWorker> workers;
    private final ExecutorService executor;

    private Demultiplexer demultiplexer;//？？？可能不需要

    private final int QUEUE_COUNT = 1, QUEUE_CAP = 18;
    public ProcessingSystem(ProtocolParser protocolParser,Demultiplexer demultiplexer) {
        // 创建分发器
        this.awareDispatcher = new SessionAwareDispatcher(QUEUE_COUNT, QUEUE_CAP);

        // 创建工作线程
        this.workers = new ArrayList<>(QUEUE_COUNT);
        for (int i = 0; i < QUEUE_COUNT; i++) {
            SessionAwareWorker worker = new SessionAwareWorker(
                    i, awareDispatcher.getQueue(i), protocolParser,demultiplexer);
            workers.add(worker);
        }

        // 创建线程池
        this.executor = Executors.newFixedThreadPool(QUEUE_COUNT);
    }

    /**
     * 启动处理系统
     */
    public void start() {
        for (SessionAwareWorker worker : workers) {
            executor.submit(worker);
        }
        LogTool.e(TAG,"Started processing system with {} workers.size():" + workers.size());
    }

    /**
     * 停止处理系统
     */
    public void stop() {
        // 停止所有工作线程
        for (SessionAwareWorker worker : workers) {
            worker.stop();
        }

        // 关闭线程池
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        LogTool.e(TAG,"Processing system stopped");
    }

    /**
     * 处理传入数据
     */
    public void processData(RawData data) {
        awareDispatcher.dispatch(data);
    }

    public void clearCache(RawData clearData) {
        awareDispatcher.clearCache(clearData);
    }

    public void onConnectionClosed(ChannelContext channelContext) {
        //在已读数据处理系统中，无需关心 连接是否关闭，只需在读到队列数据时，判断连接是否关闭，如果关闭 则将丢弃队列数据
        awareDispatcher.onConnectionClosed(channelContext);

    }


//    /**
//     * 获取系统指标
//     */
//    public SystemMetrics getMetrics() {
//        SystemMetrics metrics = new SystemMetrics();
//
//        // 计算队列使用情况
//        int totalSize = 0;
//        for (int i = 0; i < dispatcher.getQueueCount(); i++) {
//            totalSize += dispatcher.getQueue(i).size();
//        }
//        metrics.setTotalQueueSize(totalSize);
//
//        // 计算活跃会话数
//        int activeSessions = 0;
//        for (SessionAwareWorker worker : workers) {
//            // 这里需要添加获取活跃会话数的方法
//            // activeSessions += worker.getActiveSessionCount();
//        }
//        metrics.setActiveSessions(activeSessions);
//
//        return metrics;
//    }
}