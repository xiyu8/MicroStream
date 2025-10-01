package com.jason.microstream.core.im.tup.reader;


import com.jason.microstream.core.im.tup.ChannelHolder;
import com.jason.microstream.core.im.tup.Demultiplexer;
import com.jason.microstream.core.im.tup.channelcontext.ChannelContext;
import com.jason.microstream.tool.log.LogTool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Reader implements Callable<Integer> {
    private static final String TAG = Reader.class.getSimpleName();

    private ExecutorService executor;
    private Selector readSelector;


    private Demultiplexer demultiplexer;
    public Reader(Demultiplexer demultiplexer) {
        this.demultiplexer = demultiplexer;
        executor = Executors.newCachedThreadPool();
        init();
    }

    private boolean isInit = false;
    public void init() {
        int ret = 1;
        processingSystem = new ProcessingSystem(ProtocolParserFactory.getParser(ProtocolParser.ProtocolType.LENGTH_FIELD),demultiplexer);
        processingSystem.start();

        try {
            readSelector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Future<Integer> result = executor.submit(this);
        isInit = true;
    }


    private final Queue<RegisterTask> registerTaskQueue = new ConcurrentLinkedQueue<>();
    public boolean registerRead(SocketChannel socketChannel,ChannelContext channelContext) {
        try {
            socketChannel.configureBlocking(false);
            RegisterTask registerTask = new RegisterTask(socketChannel, channelContext);
            registerTaskQueue.offer(registerTask);
            readSelector.wakeup();
            return true;
        } catch (IOException e) {
            LogTool.e(TAG, "registerRead_IOException:" + e.getMessage());
            handleConnectionClosed(channelContext, socketChannel, null);
            return false;
        }
    }

    private void doRegisterRead() {
        RegisterTask task;
        while ((task = registerTaskQueue.poll()) != null) {
            try {
                SelectionKey selectionKey = task.socketChannel.register(readSelector, SelectionKey.OP_READ);
                selectionKey.attach(task.channelContext);
            } catch (IOException e) {
                LogTool.e(TAG, "doRegisterRead_IOException:" + e.getMessage());
                handleConnectionClosed(task.channelContext, task.socketChannel, null);
            }
        }
    }

    public void unregister(SocketChannel socketChannel) {
        SelectionKey key = socketChannel.keyFor(readSelector);
        if (key != null && key.isValid()) {
//            // 只移除 OP_READ
//            int currentOps = key.interestOps();
//            key.interestOps(currentOps & ~SelectionKey.OP_READ);

            // 直接取消整个注册
            key.cancel();
        }
    }

    @Override
    public Integer call() {
        if (readSelector == null || !readSelector.isOpen()) {
//            dispatcher.senderException(Dispatcher.SendCmd.SOCKET_CONNECT_FAIL);
        }
        while (true) {
            try {
                doRegisterRead();
                readSelector.select();//当注册的事件到达时，方法返回；否则,该方法会一直阻塞
                Iterator<SelectionKey> iterator = readSelector.selectedKeys().iterator();// 获得selector中选中的项的迭代器，选中的项为注册的事件
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();// 删除已选的key,以防重复处理
                    if (selectionKey.isReadable()) { // 获得了可读的事件
                        readChannel((SocketChannel) selectionKey.channel(),selectionKey);
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

    private ProcessingSystem processingSystem;

    private void readChannel(SocketChannel channel, SelectionKey selectionKey) {
        // 使用直接ByteBuffer减少内存拷贝
        ByteBuffer buffer = ByteBuffer.allocateDirect(8192);
        int bytesRead = 0;
        try {
            bytesRead = channel.read(buffer);
        } catch (IOException e) {
            handleConnectionClosed((ChannelContext) selectionKey.attachment(), channel, selectionKey);
            return;
        }
        if (bytesRead > 0) {
            buffer.flip();
            // 将数据放入队列
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
//            queue.offer(data);
            if (selectionKey.attachment() == null) { //？？？是否有必要
//                demultiplexer.genContext(channel, selectionKey);
                throw new RuntimeException("error: read selector do not match read!");
            }
            processingSystem.processData(new RawData(channel, data, (ChannelContext) selectionKey.attachment()));

        } else if (bytesRead < 0) { //channel异常关闭
            handleConnectionClosed((ChannelContext) selectionKey.attachment(), channel, selectionKey);
        }
        buffer.clear();
    }

    private void handleConnectionClosed(ChannelContext channelContext,SocketChannel channel, SelectionKey key) { //？？？key参数是否需要
        if (channelContext == null) {
            LogTool.e(TAG, "handleConnectionClosed-:channelContext == null");
            demultiplexer.handleCloseConnect(null, channel); //通知上层关闭
        } else {
            LogTool.e(TAG, "handleConnectionClosed-:"
                    + "-channelContext.channelAuth:" + channelContext.channelAuth);

            key.cancel(); //取消注册OP_READ
            if (channelContext.markInactive()) { //markInactive是原子操作，返回值可用来标识 第一次检测到 连接关闭
                // channelContext.markInactive();
                demultiplexer.handleCloseConnect(channelContext, channelContext.getSocket()); //通知上层关闭
                processingSystem.onConnectionClosed(channelContext); //通知下层关闭
            }
        }
    }

    private static class RegisterTask{
        public final SocketChannel socketChannel;
        public final ChannelContext channelContext;

        public RegisterTask(SocketChannel socketChannel, ChannelContext channelContext) {
            this.socketChannel = socketChannel;
            this.channelContext = channelContext;
        }
    }

}
