package com.jason.microstream.core.im.reqresp;

import com.google.gson.Gson;
import com.jason.microstream.tool.log.LogTool;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RequestMonitor implements Runnable {
    private final static String TAG = RequestMonitor.class.getSimpleName();
    private Gson gson;
    RequestTimeoutI requestTimeout;
    public RequestMonitor(RequestTimeoutI requestTimeout) {
        gson = new Gson();
        this.requestTimeout = requestTimeout;
        new Thread(this).start();
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void dispatchTask(Task task) {
        LogTool.e(TAG, "handleNode:"
                + "-task.req.reqId:" + task.reqWrapper.reqId
        );

//        if (task.isFail) { // task.isFail是send过程中的失败，而不思waitAck产生的失败
//            task.parser.onFail(task.exception);
//        } else if (task.isSuccess) {
//            task.parser.parseData(task.respWrapper);
//        } else { // waitAck超时产生的失败
//            task.parser.onFail(new IOException("send wait ack timeout!"));
//        }

        waitingRespMap.remove(task.reqWrapper.reqId);
        requestTimeout.handleTimeout(task);

    }

    int TIME_OUT = 10000;

    private final PriorityQueue<Task> taskQueue = new PriorityQueue<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition newTaskOrChange = lock.newCondition();
    public final Map<Long, Task> waitingRespMap = new ConcurrentHashMap<>();

///////////////////////////////////////////////////////////////////////////////////////////////////////////

    void addTask(Task task) {
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

    boolean removeTask(Task task) {
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

    private volatile boolean running = true;
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
                        dispatchTask(nextTask);
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


    static final class Task implements Comparable<Task> {
        public ReqWrapper reqWrapper;
        public RespWrapper respWrapper;
        DataParser parser;
        public long executeTime;

        private boolean isFail = true;
        private Exception exception;
        private boolean isSuccess = false;

        public Task(ReqWrapper reqWrapper, DataParser parser, long executeTime) {
            this.reqWrapper = reqWrapper;
            this.executeTime = executeTime;
            this.parser = parser;
        }

        public void setResp(RespWrapper respWrapper) {
            this.respWrapper = respWrapper;
            this.isSuccess = true;
            this.isFail = false;
        }
        public void setFail(Exception exception) {
            this.isFail = true;
            this.exception = exception;
        }

        public void resetExecuteTime(long executeTime) {
            this.executeTime = executeTime;
        }



        @Override
        public boolean equals(Object other) {
            return this.reqWrapper.reqId == ((Task)other).reqWrapper.reqId;
        }

        @Override
        public int hashCode() {
            return (int) (reqWrapper.reqId & 0x0000_0000_7fff_ffffL);
        }

        @Override
        public int compareTo(Task other) {
            return Long.compare(this.executeTime, other.executeTime);
        }
    }


}
