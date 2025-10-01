package com.jason.microstream.core.im.tup.sender;


import com.jason.microstream.core.im.tup.Demultiplexer;
import com.jason.microstream.core.im.tup.data.SendNode;
import com.jason.microstream.core.im.tup.reader.SessionAwareWorker;
import com.jason.microstream.core.im.tup.reader.SessionState;
import com.jason.microstream.tool.log.LogTool;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SenderDeputyWorker implements Runnable {
    private final static String TAG = SessionAwareWorker.class.getSimpleName();

    private final int workerId;
    private final BlockingQueue<Task> queue;
    private volatile boolean running = true;

    private final Demultiplexer demultiplexer;

    public SenderDeputyWorker(int workerId, BlockingQueue<Task> queue, Demultiplexer demultiplexer) {
        this.workerId = workerId;
        this.queue = queue;
        this.demultiplexer = demultiplexer;
    }

    private void handleNode(Task task) {
        LogTool.e(TAG, "handleNode:"
                + "-task.node.seqId:" + task.node.seqId
                + "-task.isFail:" + task.isFail
                + "-task.isSuccess:" + task.isSuccess
                + "-task.successExtra:" + task.successExtra
        );

        if (task.isFail) { // task.isFail是send过程中的失败，而不思waitAck产生的失败
            task.node.callback.onSendFailed(task.ioe, task.node);
        } else if (task.isSuccess) {
            task.node.seqId = Long.parseLong(task.successExtra);
            task.node.callback.onSendSuccess(task.node);
        } else { // waitAck超时产生的失败
            task.node.callback.onSendFailed(new IOException("send wait ack timeout!"), task.node);
        }
    }

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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private final PriorityQueue<Task> taskQueue = new PriorityQueue<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition newTaskOrChange = lock.newCondition();

    public void addTask(Task task) {
        lock.lock();
        try {
            boolean isNewHead = taskQueue.isEmpty() || task.executeTime < taskQueue.peek().executeTime;
            taskQueue.offer(task);
            if (isNewHead) {
                newTaskOrChange.signal(); // 新任务成为队首时唤醒线程
            }
        } finally {
            lock.unlock();
        }
    }


    public boolean removeTask(Task task) {
        lock.lock();
        try {
//            boolean wasHead = !taskQueue.isEmpty() && taskQueue.peek() == task;
            boolean wasHead = !taskQueue.isEmpty() && taskQueue.peek().equals(task); //????TODO:此处是不是可以.equals代替==

            boolean removed = taskQueue.remove(task);
            if (removed && wasHead) {
                newTaskOrChange.signal(); // 删除队首任务时唤醒线程
            }
            return removed;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && running) {
            lock.lock();
            try {
                // 等待队列非空
                while (taskQueue.isEmpty()) {
                    newTaskOrChange.await();
                }

                Task nextTask = taskQueue.peek();
                long currentTime = System.currentTimeMillis();
                long delay = nextTask.executeTime - currentTime;

                if (delay <= 0) { // 任务已到期
                    taskQueue.poll(); // 移除队首
                    lock.unlock(); // 释放锁执行任务
                    try {
                        handleNode(nextTask);
                    } finally {
                        lock.lock(); // 重新获取锁继续循环
                    }
                } else {
                    // 等待直到任务时间到或被唤醒
                    newTaskOrChange.await(delay, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LogTool.e(TAG,"SenderDeputyWorker_Thread_InterruptedException");
                break;
            } finally {
                lock.unlock();
            }
        }
    }


    public static class Task implements Comparable<Task> {
        private final SendNode node;
        private long executeTime; // 执行时间（毫秒）
        private boolean isFail = false;
        private IOException ioe;
        private boolean isSuccess = false;
        private String successExtra;


        public Task(SendNode node, long executeTime) {
            this.node = node;
            this.executeTime = executeTime;
        }

        public SendNode getNode() {
            return node;
        }

        public long getExecuteTime() {
            return executeTime;
        }

        public String getSuccessExtra() {
            return successExtra;
        }

        /**
         * 设置在send过程中产生的失败，而不是wait ack超时产生的失败
         * @param exception
         */
        public void setFail(IOException exception) {
            this.isFail = true;
            this.ioe = exception;
        }

        public void setSuccess(String successExtra) {
            this.isSuccess = true;
            this.successExtra = successExtra;
        }

        public void resetExecuteTime(long executeTime) {
            this.executeTime = executeTime;
        }

        @Override
        public boolean equals(Object other) {
            return this.node.seqId == ((Task)other).node.seqId;
        }

        @Override
        public int hashCode() {
            return (int) (node.seqId & 0x0000_0000_7fff_ffffL);
        }

        @Override
        public int compareTo(Task other) {
            return Long.compare(this.executeTime, other.executeTime);
        }

    }


}
