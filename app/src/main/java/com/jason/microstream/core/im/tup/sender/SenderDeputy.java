package com.jason.microstream.core.im.tup.sender;

import com.jason.microstream.core.im.tup.Coder;
import com.jason.microstream.core.im.tup.Demultiplexer;
import com.jason.microstream.core.im.tup.channelcontext.ChannelContext;
import com.jason.microstream.core.im.tup.data.SendNode;
import com.jason.microstream.core.im.tup.reader.RawData;
import com.jason.microstream.tool.log.LogTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SenderDeputy {
    private static final String TAG = SenderDeputy.class.getSimpleName();
    public static final int ACKED_GAP = 1 * 1000;

    private final int workerCount;
    private final List<SenderDeputyWorker> workers;
    private final List<BlockingQueue<SenderDeputyWorker.Task>> workQueues;
    private final Map<Long, Integer> workerIndexMap;
    private final AtomicInteger counter = new AtomicInteger(0);
    private final ExecutorService executor;

    public SenderDeputy(int workerCount, int workerCapacity, Demultiplexer demultiplexer) {
        this.workerCount = workerCount;
        this.workers = new ArrayList<>(workerCount);
        this.workQueues = new ArrayList<>(workerCount);
        this.workerIndexMap = new ConcurrentHashMap<>();
        for (int i = 0; i < workerCount; i++) {
            BlockingQueue<SenderDeputyWorker.Task> workQueue = new LinkedBlockingQueue<>(workerCapacity);
            workQueues.add(workQueue);
            SenderDeputyWorker worker = new SenderDeputyWorker(i, workQueue, demultiplexer);
            workers.add(worker);
        }

        this.executor = Executors.newFixedThreadPool(workerCount);
    }

    public void start() {
        for (SenderDeputyWorker worker : workers) {
            executor.submit(worker);
        }
        LogTool.e(TAG,"Started SenderDeputy system with {} workers.size():" + workers.size());
    }

    /**
     * 停止处理系统
     */
    public void stop() {
        // 停止所有工作线程
        for (SenderDeputyWorker worker : workers) {
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
        LogTool.e(TAG,"SenderDeputy system stopped");
    }

    /**
     * 将数据分发到相应的队列
     */
    public void putCandidateNode(SendNode sendNode, ChannelContext channelContext) {
        if (sendNode.isAcked == 1) {
            return;
        }
        LogTool.e(TAG, "putCandidateNode:"
                + "-sendNode.stubId:" + sendNode.stubId
                + "-sendNode.seqId:" + sendNode.seqId
                + "-sendNode.isAcked:" + sendNode.isAcked
        );
        long sessionId = channelContext.getSessionId();

        int queueIndex = workerIndexMap.computeIfAbsent(sessionId, id -> {
            // 新会话：使用轮询策略分配队列
            return counter.getAndIncrement() % workerCount;
        });
        //或者如下：（只要保证同一个 待读的channel放到同一个que当中就行）
        // int queueIndex = sessionId % queueCount;
        SenderDeputyWorker.Task task = new SenderDeputyWorker.Task(sendNode, System.currentTimeMillis() + ACKED_GAP);
        workers.get(queueIndex).addTask(task);
        channelContext.waitAkcMap.put(sendNode.stubId, task);


//        // 将数据放入对应队列
//        BlockingQueue<SenderDeputyWorker.Task> targetQueue = workQueues.get(queueIndex);
//
//        // 如果队列已满，实现背压策略
//        if (targetQueue.remainingCapacity() == 0) {
//            handleBackpressure(sessionId, sendNode);
//        } else {
//            SenderDeputyWorker.Task task = new SenderDeputyWorker.Task(sendNode, System.currentTimeMillis() + ACKED_GAP);
//            targetQueue.offer(task);
//            channelContext.waitAkcMap.put(sendNode.seqId, task);
//        }
    }

    /**
     * 这个方法是 ，在send过程中失败，而不是waitAck超时 产生的失败
     * send过程中失败 的处理流程：找到work，移除waitAck的Node，添加send失败的Node
     * @param e
     * @param failedPacket
     * @param channelContext
     */
    public void sendFail(IOException e, SendNode failedPacket, ChannelContext channelContext) {
        LogTool.e(TAG, "sendFail:"
                + "-sendNode.stubId:" + failedPacket.stubId
                + "-sendNode.seqId:" + failedPacket.seqId
                + "-sendNode.isAcked:" + failedPacket.isAcked
        );
        if (failedPacket.isAcked == 1) { //ack 消息失败了，直接放弃？？？非超时失败也直接放弃？是不是可以优化
            return;
        }

        //TODO:sendFail后，在此处直接调用callback.onSendFailed会影响send时的IO效率，onSendFailed业务代码执行时间会阻塞io的send线程的发送
        // 所以此处应该加入fail的队列，让SenderDeputy的工作线程去处理：新建一个fail标记的Node，并加入队列
        //// failedPacket.callback.onSendFailed(e, failedPacket);


        long sessionId = channelContext.getSessionId();
        int queueIndex = workerIndexMap.computeIfAbsent(sessionId, id -> {
            // 新会话：使用轮询策略分配队列
            return counter.getAndIncrement() % workerCount;
        });
        //或者如下：（只要保证同一个 待读的channel放到同一个que当中就行）
        // int queueIndex = sessionId % queueCount;


        SenderDeputyWorker worker = workers.get(queueIndex);
        SenderDeputyWorker.Task failTask = new SenderDeputyWorker.Task(failedPacket, System.currentTimeMillis());
        // 移除等待超时的Node，加入sendFail的Node。(node的fail 在worker的队列遍历的时候去处理)
        worker.removeTask(failTask); //???TODO: 是不是逻辑上 可不移除，因为当send产生的失败时，waitAck的队列中是没有这个Node的
        channelContext.waitAkcMap.remove(failTask.getNode().seqId);// 同上
        failTask.setFail(e);
        worker.addTask(failTask);
    }
    /**
     * TODO:根据read到的ack数据，找到发送完成后，在等待ack的数据包
     * 为了尽可能 快 和高效率找到对应队列里的sendNode:
     * 1、在每个SocketChannel的ChannelContext里，建立一个等待ack的消息Map集合在send和read-ack和超时 过程中 维护这个集合
     */
    public void ackedCandidateNode(Coder.CoderData coderData, ChannelContext channelContext) {
        LogTool.e(TAG, "ackedCandidateNode:"
                + "-coderData.stubId:" + coderData.stubId
                + "-coderData.seqId:" + coderData.seqId
                + "-coderData.ackFlag:" + coderData.ackFlag
        );
        if (coderData.ackFlag != 1) {
            return;
        }
        long sessionId = channelContext.getSessionId();

        int queueIndex = workerIndexMap.computeIfAbsent(sessionId, id -> {
            // 新会话：使用轮询策略分配队列
            return counter.getAndIncrement() % workerCount;
        });
        //或者如下：（只要保证同一个 待读的channel放到同一个que当中就行）
        // int queueIndex = sessionId % queueCount;

        SenderDeputyWorker worker = workers.get(queueIndex);
        SenderDeputyWorker.Task task = channelContext.waitAkcMap.get(coderData.stubId);
        //TODO:没有waitAkcMap的写法，是remove用seqId构造出来的新SendNode
        if (task != null && worker.removeTask(task)) {
            task.setSuccess(String.valueOf(coderData.seqId)); //收到ack消息后，ack消息所带的额外信息seqId
            task.resetExecuteTime(System.currentTimeMillis());
            worker.addTask(task);
            LogTool.e(TAG, "ackedCandidateNode:"
                    + "-task.seqId:" + task.getNode().seqId
                    + "-task.stubId:" + task.getNode().stubId
            );
        } else {
            LogTool.e(TAG, "ackedCandidateNode:"
                    + "-task.seqId:" + (task == null ? -1 : task.getNode().seqId)
                    + "-task.stubId:" + (task == null ? -1 : task.getNode().stubId)
                    + "-task != null:" + (task != null)
            );
        }
    }

    public void clearCache(RawData clearData) {

    }

    /**
     * 处理背压情况
     */
    private void handleBackpressure(long sessionId, SendNode sendNode) {
        // 策略1：丢弃最旧的数据
//        BlockingQueue<SendNode> queue = workQueues.get(workerIndexMap.get(sessionId));
//        if (queue.remainingCapacity() == 0) {
//            queue.poll(); // 移除最旧的数据
//        }
//        queue.offer(sendNode);

        // 策略2：暂时拒绝数据并记录日志
        // logger.warn("Queue full for session {}, dropping packet", sessionId);

        // 策略3：动态调整队列分配
        // reassignSessionToNewQueue(sessionId);
    }

    // 需要实现获取会话最后活动时间的方法
    private long getLastActivityTime(int sessionId) {
        // 从会话状态管理器中获取
        return 0; // 伪代码
    }

    public void onConnectionClosed(ChannelContext channelContext) {
        //TODO:不需要做任何处理，可在此处简单记录下，数据处理工作线程SessionAwareWorker会自行判断连接的关闭状态
        // 补充：也可 加锁 遍历和替换队列，让 对应channel的wait-ack数据包立刻上报 fail
    }

}
