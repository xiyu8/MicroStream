package com.jason.microstream.core.im.tup;

import com.jason.microstream.account.AccountManager;
import com.jason.microstream.core.im.tup.data.SendNode;
import com.jason.microstream.core.im.tup.data.msg.Msg;
import com.jason.microstream.tool.log.LogTool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Sender {
    private final String TAG = Sender.class.getSimpleName();


    Dispatcher dispatcher;
    Coder coder;

    SocketChannel socketChannel;
    ChannelHolder channelHolder;

    private ArrayBlockingQueue<SendNode> queue = new ArrayBlockingQueue<>(128);
    SenderQueueTimer queueTimer;

    public void sendShutdownImp() {
        //TODO: opposite of register
    }

    private class SendThread extends Thread{
        boolean isTerminate = false;
        public void safeTerminate() {
            isTerminate = true;
        }

        @Override
        public void run() {
            super.run();
            boolean checked;
            while (!isTerminate) {
                checked = false;
                SendNode node;
                try {
                    node = queue.take(); //取出、移除、阻塞；poll取出、移除、不阻塞(无异常取null)；peek取出、不移除、不阻塞(无异常取null)
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
//                node = queue.peek();
                if (node == null) {
                    continue;
                }
                if (node.retryCount >= 3) {
                    queueTimer.putFailNode(node);
                    continue;
                }
                if (socketChannel == null) {
                    dispatcher.senderException(Dispatcher.SendCmd.SEND_CHANNEL_NULL);
                } else if (!socketChannel.isConnected()) {
                    dispatcher.senderException(Dispatcher.SendCmd.SEND_CHANNEL_UNCONNECTED);
                }/* else if (!channelHolder.isCheckChannel) {
                    dispatcher.senderException(Dispatcher.SendCmd.SEND_CHANNEL_UNCHECK);
                } else if (!channelHolder.isAuth) {
                    dispatcher.senderException(Dispatcher.SendCmd.SEND_CHANNEL_UNAUTH);
                } */ else {
                    checked = true;
                }
                if (!checked) {
                    try {
                        node.retryCount++;
                        if (node.retryCount >= 3) {
                            queueTimer.putFailNode(node);
                        } else {
                            queue.put(node); //添加、阻塞；offer添加、不阻塞(无异常返false)；
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        ByteBuffer byteBuffer = ByteBuffer.wrap(node.data);
                        socketChannel.write(byteBuffer);
                    } catch (IOException e) {
                        dispatcher.senderException(Dispatcher.SendCmd.SEND_IO_EXCEPTION);
                        e.printStackTrace();
                        try {
                            node.retryCount++;
                            if (node.retryCount >= 3) {
                                queueTimer.putFailNode(node);
                            } else {
                                queue.put(node);
                            }
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        continue;
                    }
                    if (node.acked != 1) {
                        queueTimer.putCandidateNode(node);
                    }
                    queueTimer.resetBeat();
                }

            }

        }
    }
    SendThread sendThread = new SendThread() ;



    public void start() {
        if (!sendThread.isAlive()) {
            sendThread.start();
        }
    }


//    protected void sendAckImp(SendNode sendNode) {
//        Coder.CoderData coderData = coder.decode(sendNode.data);
//        coderData.ackFlag = 1;
//        coderData.msgData = null;
//        sendNode.data = coder.encode(coderData);
//        sendLocker.lock();
//        try {
////            queue.offer(sendNode);
//            queue.put(sendNode);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } finally {
//            sendLocker.unlock();
//        }
//    }
    protected void sendAckImp(Coder.CoderData coderData) {
        LogTool.e(TAG, "sendAckImp__coderData.msgAction:" + coderData.msgAction
                + "-coderData.ackFlag:" + coderData.ackFlag
                + "-coderData.seqId:" + coderData.seqId
                + "-coderData.stubId:" + coderData.stubId
        );
        SendNode sendNode = new SendNode();
        sendNode.stubId = coderData.stubId;
        sendNode.seqId = coderData.seqId;
        sendNode.acked = 1;
        coderData.ackFlag = 1;
        coderData.msgData = null;
        sendNode.data = coder.encode(coderData);
        sendLocker.lock();
        try {
//            queue.offer(sendNode);
            queue.put(sendNode);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            sendLocker.unlock();
        }
    }

    public void sendRegister(Object ob) {
        preSend(ob);
        sendImp(ACTION_AUTH, ob, Coder.MSG_TYPE_REGISTER);
        if (ob instanceof Msg) {
            LogTool.e(TAG, "sendRegister__:"
                    + "-((Msg) ob).seqId:" + ((Msg) ob).seqId
                    + "-((Msg) ob).stubId:" + ((Msg) ob).stubId
            );
        }
    }

    public void sendTest(Object ob) {
        preSend(ob);
        sendImp(ACTION_DATA, ob, Coder.MSG_TYPE_TEST);
        LogTool.e(TAG, "sendTest:"
                + "-((Msg) ob).seqId:" + ((Msg) ob).seqId
                + "-((Msg) ob).seqId:" + ((Msg) ob).stubId
        );
    }

    public void sendVideoCmd(Object ob,int cmd) {
        preSend(ob);
        sendImp(ACTION_DATA, ob, cmd);
        LogTool.e(TAG, "sendVideoCmd:"
                + "-((Msg) ob).seqId:" + ((Msg) ob).seqId
                + "-((Msg) ob).seqId:" + ((Msg) ob).stubId
        );
    }

    public void sendBeat() {
        LogTool.e(TAG,"sendBeat__");
        SendNode sendNode = new SendNode();
        Coder.CoderData coderData = new Coder.CoderData();
        coderData.version = Coder.IM_VERSION;
        coderData.service = Coder.IM_SERVICE;
        coderData.redundancy = Coder.IM_SERVICE_SIZE;

        coderData.seqId = 0;
        coderData.stubId = 0;
        coderData.ackFlag = 1;
        coderData.msgAction = ACTION_BEAT;

        coderData.msgType = 0;
        coderData.msgData = new byte[0];


        sendNode.stubId = coderData.stubId;
        sendNode.seqId = coderData.seqId;
        sendNode.acked = 1;
        sendNode.data = coder.encode(coderData);
        sendLocker.lock();
        try {
//            queue.offer(sendNode);
            queue.put(sendNode);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            sendLocker.unlock();
        }

        LogTool.e(TAG, "sendBeat:"
                + "-sendNode.seqId:" + sendNode.seqId
                + "-sendNode.stubId:" + sendNode.stubId
        );
    }

    private void preSend(Object ob) {
        if(ob instanceof Msg){
            Msg msg = (Msg) ob;
            long stubId = getStubId();
            if (msg.getStubId() == 0) {
                msg.setStubId(stubId = genStubId(stubId));
                msg.setSeqId(stubId);
            }
            msg.state = Msg.STATE.SENDING;
        }
    }

    ReentrantLock sendLocker = new ReentrantLock();
    private void sendImp(int action, Object ob,int msgType) {
        Coder.CoderData coderData = new Coder.CoderData();
        if(ob instanceof Msg) {
            Msg msg = (Msg) ob;
            // coderData.length = msg.;
            coderData.version = Coder.IM_VERSION;
            coderData.service = Coder.IM_SERVICE;
            coderData.redundancy = Coder.IM_SERVICE_SIZE;
            coderData.seqId = msg.seqId;
            coderData.stubId = msg.stubId;
            // coderData.ackFlag = msg.;
        }
        coderData.msgAction = action;
        coderData.msgType = msgType;
        try {
            coderData.msgData = coder.flatObject(ob).getBytes(("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        byte[] codedData = coder.encode(coderData);

        SendNode sendNode = new SendNode();
        sendNode.data = codedData;
        if (ob instanceof Msg) {
            sendNode.stubId = ((Msg) ob).stubId;
            sendNode.seqId = ((Msg) ob).seqId;
        }
        sendLocker.lock();
        try {
//            queue.offer(sendNode);
            queue.put(sendNode);//添加、阻塞；offer添加、不阻塞(无异常返false)；
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            sendLocker.unlock();
        }
//        if (!sendThread.isAlive()) {
//            sendThread.start();
//        }
    }




    public void stop() {
        if (sendThread.isAlive()) {
            //先进行 正在排队的 SendNode 处理：将队列中的数据 交给queueTimer处理
            //TimerQueue 的处理
//            sendThread.safeTerminate();
            queueTimer.reset();
        }
    }

    private Set<Long> idsSet = new HashSet<>();
    private long getStubId() {
        long id = AccountManager.get().getServerTime();
        while (idsSet.contains(id)) {
            id++;
        }
        idsSet.add(id);
        return id;
    }

    private long genStubId(long sendTime) {
//        sendTime = sendTime / 1000;
//        long msgId = (sendTime << 31);
//        while (idsSet.contains(msgId)) {
//            msgId++;
//        }
//        idsSet.add(msgId);
        return sendTime;
    }

    ///////////////////////////////////////////////////

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Coder getCoder() {
        return coder;
    }

    public void setCoder(Coder coder) {
        this.coder = coder;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public ChannelHolder getChannelHolder() {
        return channelHolder;
    }

    public void setChannelHolder(ChannelHolder channelHolder) {
        this.channelHolder = channelHolder;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(ArrayBlockingQueue queue) {
        this.queue = queue;
    }

    public SenderQueueTimer getQueueTimer() {
        return queueTimer;
    }

    public void setQueueTimer(SenderQueueTimer queueTimer) {
        this.queueTimer = queueTimer;
    }


    public static final int ACTION_DATA = 0;
    public static final int ACTION_AUTH = 1;
    public static final int ACTION_BEAT = -1;

}
