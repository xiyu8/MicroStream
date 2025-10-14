package com.jason.microstream.core.im.tup.sender;

import com.jason.microstream.account.AccountManager;
import com.jason.microstream.core.im.tup.Coder;
import com.jason.microstream.core.im.tup.Demultiplexer;
import com.jason.microstream.core.im.tup.channelcontext.ChannelContext;
import com.jason.microstream.core.im.tup.channelcontext.WriteStatus;
import com.jason.microstream.core.im.tup.data.SendNode;
import com.jason.microstream.core.im.tup.data.msg.Msg;
import com.jason.microstream.tool.log.LogTool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Sender implements Callable<Integer> {
    private static final String TAG = Sender.class.getSimpleName();
    private int VICE_WORKER_COUNT = 1;
    private int PER_VICE_WORKER_CAP = 16;
    private final Coder coder;

    private ExecutorService executor;
    private Selector sendSelector;
    private SenderDeputy senderDeputy;

    private Demultiplexer demultiplexer;
    public Sender(Demultiplexer demultiplexer) {
        this.demultiplexer = demultiplexer;
        executor = Executors.newCachedThreadPool();
        coder = new Coder();
        init();
    }

    private boolean isInit = false;
    public void init() {
        int ret = 1;
        senderDeputy = new SenderDeputy(VICE_WORKER_COUNT,PER_VICE_WORKER_CAP,demultiplexer);
        senderDeputy.start();

        try {
            sendSelector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Future<Integer> result = executor.submit(this);
        isInit = true;
    }

    //TODO:BUG!!!服务端未改
    private final Queue<Sender.RegisterTask> registerTaskQueue = new ConcurrentLinkedQueue<>();
    public boolean registerSend(SocketChannel socketChannel,ChannelContext channelContext) {
        try {
            socketChannel.configureBlocking(false);
            Sender.RegisterTask registerTask = new Sender.RegisterTask(true, socketChannel, channelContext);
            registerTaskQueue.offer(registerTask);
            sendSelector.wakeup();
            return true;
        } catch (IOException e) {
//            throw new RuntimeException(e);
            LogTool.e(TAG, "registerSend_IOException:" + e.getMessage());
            handleConnectionClosed(null, socketChannel, null);
            return false;
        }
    }

    private void doRegisterSender() {
        Sender.RegisterTask task;
        while ((task = registerTaskQueue.poll()) != null) {
            if (task.isPreRegister) {
                try {
                        SelectionKey selectionKey = task.socketChannel.register(sendSelector, 0);
//                    SelectionKey selectionKey = task.socketChannel.register(sendSelector, SelectionKey.OP_WRITE);
                        selectionKey.attach(task.channelContext);
                } catch (IOException e) {
                    LogTool.e(TAG, "doRegisterSender_IOException:" + e.getMessage()
                            + "-task.isPreRegister:" + task.isPreRegister);
                    handleConnectionClosed(task.channelContext, task.socketChannel, null);
                }
            } else {
                SelectionKey key = task.socketChannel.keyFor(sendSelector);
                if (key == null || !key.isValid()) {
                    LogTool.e(TAG, "doRegisterSender_IOException1:key == null || !key.isValid()"
                            + "-task.isPreRegister:" + task.isPreRegister);
                    synchronized (task.channelContext.writeStatus.getSendQueue()) {
                        SendNode failedPacket;
                        while ((failedPacket = task.channelContext.writeStatus.getSendQueue().poll()) != null) {
                            senderDeputy.sendFail(new IOException("send_with_register_fail!!!"), failedPacket, task.channelContext);
                        }
                    }
                    handleConnectionClosed(task.channelContext, task.socketChannel, null);
                } else {
                    key = key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                    if (key == null || !key.isValid()) {
                        LogTool.e(TAG, "doRegisterSender_IOException2:key == null || !key.isValid()"
                                + "-task.isPreRegister:" + task.isPreRegister);
                        synchronized (task.channelContext.writeStatus.getSendQueue()) {
                            SendNode failedPacket;
                            while ((failedPacket = task.channelContext.writeStatus.getSendQueue().poll()) != null) {
                                senderDeputy.sendFail(new IOException("send_with_register_fail!!!"), failedPacket, task.channelContext);
                            }
                        }
                        handleConnectionClosed(task.channelContext, task.socketChannel, null);
                    }
                }
            }


            try {
                if (task.isPreRegister) {
                    SelectionKey selectionKey = task.socketChannel.register(sendSelector, 0);
//                    SelectionKey selectionKey = task.socketChannel.register(sendSelector, SelectionKey.OP_WRITE);
                    selectionKey.attach(task.channelContext);
                } else {
                    SelectionKey key = task.socketChannel.keyFor(sendSelector);
                    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                }
            } catch (IOException e) {
                LogTool.e(TAG, "doRegisterSender_IOException:" + e.getMessage()
                        + "-task.isPreRegister:" + task.isPreRegister);
                if (!task.isPreRegister) {
                    synchronized (task.channelContext.writeStatus.getSendQueue()) {
                        SendNode failedPacket;
                        while ((failedPacket = task.channelContext.writeStatus.getSendQueue().poll()) != null) {
                            senderDeputy.sendFail(e, failedPacket, task.channelContext);
                        }
                    }
                }
                handleConnectionClosed(task.channelContext, task.socketChannel, null);
            }
        }
    }



    private void handleConnectionClosed(ChannelContext channelContext,SocketChannel channel, SelectionKey key) { //？？？key参数是否需要
        if (channelContext == null) {
            LogTool.e(TAG, "handleConnectionClosed-:channelContext == null");
            demultiplexer.handleCloseConnect(null, channel); //通知上层关闭
        } else {
            LogTool.e(TAG, "handleConnectionClosed-:"
                    + "-channelContext.channelAuth:" + channelContext.channelAuth);

            if (key != null) key.cancel(); //取消注册OP_WRITE}
            if (channelContext.markInactive()) { //markInactive是原子操作，返回值可用来标识 第一次检测到 连接关闭
                // channelContext.markInactive();
                demultiplexer.handleCloseConnect(channelContext, channelContext.getSocket()); //通知上层关闭
                senderDeputy.onConnectionClosed(channelContext); //通知下层关闭
            }
        }
    }


    public void unregister(SocketChannel socketChannel) {
        SelectionKey key = socketChannel.keyFor(sendSelector);
        if (key != null && key.isValid()) {
//            // 只移除 OP_READ
//            int currentOps = key.interestOps();
//            key.interestOps(currentOps & ~SelectionKey.OP_READ);

            // 直接取消整个注册
            key.cancel();
        }
    }

////////////////////////////////////////////////////send Procedure///////////////////////////////////////////////////////////////////////////////

    public long sendImp(ChannelContext channelContext, Object ob, int action, int msgType, SendNode.SendCallback callback) {
        preSend(ob);
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

        SendNode sendNode = new SendNode(codedData, ((Msg) ob).seqId, callback);
        sendNode.data = codedData;
        if (ob instanceof Msg) {
            sendNode.stubId = ((Msg) ob).stubId;
            sendNode.seqId = ((Msg) ob).seqId;
        }

        sendNode.original = ob;
        putSendQueue(channelContext, sendNode);
        return sendNode.stubId;
    }
    private void preSend(Object ob) {
        if(ob instanceof Msg){
            Msg msg = (Msg) ob;
            long stubId = getStubId();
            if (msg.getStubId() == 0) {
                msg.setStubId(stubId = genStubId(stubId));
                msg.setStubId(stubId);
            }
            msg._state = Msg.STATE.SENDING;
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

////////////////////////////////////////////////////send Procedure(end line)///////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////send ack Procedure///////////////////////////////////////////////////////////////////////////////

    public void sendAckImp(ChannelContext channelContext, Coder.CoderData coderData) {
        //添加ack标识、置空原数据、node透出字段
        coderData.ackFlag = 1;
        coderData.msgData = null;
        SendNode sendNode = new SendNode(coder.encode(coderData), coderData.seqId, null);
        sendNode.stubId = coderData.stubId;
        sendNode.seqId = coderData.seqId;
        sendNode.isAcked = 1; //important，node中透出的的ack信息！！！
        putSendQueue(channelContext, sendNode);
        LogTool.e(TAG, "sendAckImp:"
                + "-sendNode.stubId:" + sendNode.stubId
                + "-sendNode.seqId:" + sendNode.seqId
        );
    }

////////////////////////////////////////////////////send ack Procedure(end line)///////////////////////////////////////////////////////////////////////////////


    private void putSendQueue(ChannelContext channelContext, SendNode sendNode) {
        //TODO:send时的同步问题未考虑清楚：channelContext.isActive、SendQueue().add、registerSend
        if (!channelContext.isActive()) { //?????
            channelContext.getChannelHolder().connectSync();  //???????
            if (!channelContext.isActive()) {
                senderDeputy.sendFail(new IOException("channel is closed"), sendNode, channelContext);
                return;
            }
        }


        synchronized (channelContext.writeStatus.getSendQueue()) {
            channelContext.writeStatus.getSendQueue().add(sendNode);
        }
        if (!channelContext.isActive()) {
            synchronized (channelContext.writeStatus.getSendQueue()) {
                channelContext.writeStatus.getSendQueue().remove(sendNode);
            }
            senderDeputy.sendFail(new IOException("channel is closed"), sendNode, channelContext);
            return;
        }
        try {
            RegisterTask registerTask = new RegisterTask(false, channelContext.getSocket(), channelContext);
            registerTaskQueue.offer(registerTask);
            sendSelector.wakeup();
        } catch (CancelledKeyException e) {
            // 在wakeup之前key被取消
            handleConnectionClosed(channelContext,channelContext.getSocket(),null);
        }
    }

    public void ackedCandidateNode(ChannelContext channelContext, Coder.CoderData coderData) {
        senderDeputy.ackedCandidateNode(coderData,channelContext);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Integer call() {
        if (sendSelector == null || !sendSelector.isOpen()) {
//            dispatcher.senderException(Dispatcher.SendCmd.SOCKET_CONNECT_FAIL);
        }
        while (true) {
            try {
                doRegisterSender();
                sendSelector.select();//当注册的事件到达时，方法返回；否则,该方法会一直阻塞
                Iterator<SelectionKey> iterator = sendSelector.selectedKeys().iterator();// 获得selector中选中的项的迭代器，选中的项为注册的事件
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();// 删除已选的key,以防重复处理
                    if (selectionKey.isWritable()) { // 获得了可读的事件
                        writeChannel((SocketChannel) selectionKey.channel(),selectionKey);
                    }
                }
            } catch (IOException e) {
                if (e.getMessage() != null && e.getMessage().contains("closed")
                        && e.getMessage().contains("Broken")) {
                }
                e.printStackTrace();
            }
        }
    }

    private int MAX_WRITE_SIZE = 8192;
    private void writeChannel(SocketChannel channel, SelectionKey selectionKey) {
        if (selectionKey.attachment() == null) {
            LogTool.e(TAG,"writeChannel:selectionKey.attachment() == null");
            throw new RuntimeException("send without auth!");
        }
        ChannelContext channelContext = (ChannelContext) selectionKey.attachment();
        LogTool.e(TAG, "writeChannel:"
                + "-channelContext.writeStatus.getSendQueue().size():" + channelContext.writeStatus.getSendQueue().size()
        );
        WriteStatus writeStatus = channelContext.writeStatus;

        int totalWritten = 0;
        boolean writtenZero = false;
        while (!writtenZero && totalWritten < MAX_WRITE_SIZE) { // 检查是否达到限制：总字节数超过MAX_WRITE_SIZE，或者writtenZero为true（表示TCP缓冲区已满）
            SendNode currentPacket;
            if ((currentPacket = writeStatus.currentNode) == null) {
                synchronized (writeStatus.getSendQueue()) {
                    currentPacket = writeStatus.getSendQueue().poll();
                }
                if (currentPacket == null) {
                    // 没有数据可写，取消写事件
                    selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
//                        selectionKey.cancel();
                    break;
                }
                writeStatus.currentNode = currentPacket;
            }

            ByteBuffer currentBuffer = currentPacket.dataWrap;
            // 确保缓冲区处于读模式？？？是否有必要，缓冲区一直是出于读状态，没有切换过
            if (currentBuffer.position() > 0 && currentBuffer.limit() == currentBuffer.capacity()) {
                currentBuffer.flip();
            }
            int written = 0;
            try {
                if (currentBuffer.hasRemaining()) {
                    int toWrite = Math.min(currentBuffer.remaining(), MAX_WRITE_SIZE - totalWritten);

//                    //这段代码是通过 slice将原有大包 限定 一段数据，写入channel，并挪动原大包的position指针
//                    ByteBuffer slice = currentBuffer.slice();
//                    slice.limit(slice.position() + toWrite);
//                    written = channel.write(slice);
//                    if (written < 0) {
//                        throw new IOException("Channel closed");
//                    }
//                    currentBuffer.position(currentBuffer.position() + written);


                    //代替上面那段代码，通过slice限制大包写范围，改用临时limit来限制写范围
                    int oldPosition = currentBuffer.position();// 记录原始位置和限制
                    int oldLimit = currentBuffer.limit();
                    // 设置新的限制，限制本次写入的大小
                    currentBuffer.limit(oldPosition + toWrite);
                    written = channel.write(currentBuffer);
                    if (written < 0) {
                        throw new IOException("Channel closed");
                    }
                    // 恢复原始限制
                    currentBuffer.limit(oldLimit);


                    totalWritten += written;

                    if (!currentBuffer.hasRemaining()) { //已写完一个currentBuffer
                        currentBuffer.clear(); // 切换回写模式???是否有必要
                        writeStatus.currentNode = null;
                        senderDeputy.putCandidateNode(currentPacket,channelContext);
//                        currentPacket.callback.onSendSuccess(currentPacket);
                    } else {
                        if (totalWritten >= MAX_WRITE_SIZE) {
                            return; // 额度用尽，退出
                        }
                    }
                }

            } catch (IOException e) {
                // 异常处理
                senderDeputy.sendFail(e, currentPacket, channelContext);
//                currentPacket.callback.onSendFailed(e, currentPacket);
                // 处理队列中所有剩余数据包的异常
                synchronized (writeStatus.getSendQueue()) {
                    SendNode failedPacket;
                    while ((failedPacket = writeStatus.getSendQueue().poll()) != null) {
                        senderDeputy.sendFail(e, failedPacket, channelContext);
//                        failedPacket.callback.onSendFailed(e, failedPacket);
                    }
                }
                handleConnectionClosed(channelContext, channel, selectionKey);
                break;
            }

            if (written == 0) writtenZero = true;
        }
    }

    private static class RegisterTask{
        public final boolean isPreRegister;
        public final SocketChannel socketChannel;
        public final ChannelContext channelContext;

        public RegisterTask(boolean isPreRegister, SocketChannel socketChannel, ChannelContext channelContext) {
            this.isPreRegister = isPreRegister;
            this.socketChannel = socketChannel;
            this.channelContext = channelContext;
        }
    }
}
