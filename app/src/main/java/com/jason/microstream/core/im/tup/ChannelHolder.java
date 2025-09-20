package com.jason.microstream.core.im.tup;

import com.jason.microstream.core.im.imconpenent.ImService;
import com.jason.microstream.tool.log.LogTool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.netty.channel.ChannelPromise;


public class ChannelHolder implements Callable<Integer> {
    private final String TAG = ChannelHolder.class.getSimpleName();

    ExecutorService executor;
    SocketChannel mSocketChannel;
    Selector selector;
    Dispatcher dispatcher;
    Receiver receiver;

    boolean isConnected = false;
    boolean isCheckChannel = false;
    boolean isAuth = false;

    public ChannelHolder() {
        this.executor = Executors.newCachedThreadPool();
    }

    String host, port, uid, token;
    public void connect() {
        host = dispatcher.getCore().getHost();
        port = dispatcher.getCore().getPort();
        uid = ImService.getIm().getUid();
        token = ImService.getIm().getToken();

        int ret = registerNioSelector();
        if (ret != 0) {
            dispatcher.senderException(Dispatcher.SendCmd.SOCKET_OPEN_FAIL);
            return;
        }
        ret = nioConnect();
        if (ret != 0) {
            dispatcher.senderException(Dispatcher.SendCmd.SOCKET_CONNECT_FAIL);
        } else {
            dispatcher.connectReseted(mSocketChannel, selector);
        }

    }

    //step1
    private int registerNioSelector() {
        if (mSocketChannel == null || !mSocketChannel.isOpen()) {
            synchronized (this) {
                if (mSocketChannel == null || !mSocketChannel.isOpen()) {
                    int[] gaps = new int[]{0, 50, 200};
                    boolean ret = false;
                    for (int i = 0; i < 3; i++) {
                        ret = registerNioImp(gaps[i]);
                        if (ret) break;
                    }
                    if (!ret) {
                        return 1;
                    }

                }
            }
        }
        return 0;
    }

    private boolean registerNioImp(int gap) {
        boolean ret = false;
        if (gap != 0) {
            try {
                Thread.sleep(gap);
            } catch (InterruptedException ignored) {
            }
        }
        try {
            mSocketChannel = SocketChannel.open();
            LogTool.e(TAG, "mSocketChannel = SocketChannel.open()");
            mSocketChannel.configureBlocking(false);
            selector = Selector.open();
            //用channel.finishConnect();才能完成连接
            mSocketChannel.register(selector, SelectionKey.OP_CONNECT);
            ret = true;
        } catch (IOException e) {
            LogTool.e(TAG,"registerNioImp IOException!");
        }
        return ret;
    }

    public void shutdown() {
        if (mSocketChannel == null) {
            return;
        }
        synchronized (this) {
            try {
                mSocketChannel.close();
            } catch (IOException e) {

            }
            mSocketChannel = null;
        }
    }

    @Override
    public Integer call() {
        if (mSocketChannel == null || !mSocketChannel.isOpen()) {
            dispatcher.senderException(Dispatcher.SendCmd.SOCKET_CONNECT_FAIL);
        }

        if (!mSocketChannel.isConnected()) {
            synchronized (this) {
                if (!mSocketChannel.isConnected()) {
                    int[] gaps = new int[]{0, 50, 200};
                    boolean ret = false;
                    for (int i = 0; i < 3; i++) {
                        ret = connectNioImp(gaps[i]);
                        if (ret) break;
                    }
                    if (!ret) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }


    private boolean connectNioImp(int gap) {
        boolean ret = false;
        if (gap != 0) {
            try {
                Thread.sleep(gap);
            } catch (InterruptedException ignored) {
            }
        }
        try {
            boolean connected = mSocketChannel.connect(new InetSocketAddress(host, Integer.parseInt(port)));
            // 如果没有立即连接成功，等待连接完成或者超时
            if (!connected) {
                // 设置一个超时时间，比如10秒
                long timeout = 5000;
                long start = System.currentTimeMillis();
                // 循环调用finishConnect()方法，直到连接成功，或者超时，或者出现异常
                while (!mSocketChannel.finishConnect()) {
                    // 检查是否超时
                    if (System.currentTimeMillis() - start > timeout) {
//                                    throw new SocketTimeoutException("Connect timed out");
                        LogTool.e(TAG,"connectNioImp__SocketTimeoutException!");
                        return false;
                    }
                    // 可以在这里做一些其他的操作，比如取消连接，或者处理其他的I/O事件
                }
            }
            LogTool.e(TAG, "mSocketChannel.connect success");
            mSocketChannel.configureBlocking(false);
            mSocketChannel.register(selector, SelectionKey.OP_READ);

            receiver.setChannel(mSocketChannel, selector);
            //TODO:receiver的start不应该在此处
            receiver.start();
            ret = true;
        } catch (IOException e) {
            LogTool.e(TAG,"connectNioImp IOException!");
        }
        return ret;
    }

    //step3
    private int nioConnect() {
        int ret = 1;
        Future<Integer> result = executor.submit(this);
        try {
            ret = result.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            return ret;
        }
    }

    //?????
    public void nioDisconnect() {
//        host = null;
//        port = null;
        isAuth = false;
        if (mSocketChannel == null) {
            return;
        }
        if (mSocketChannel.isConnected()) {
            try {
                mSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            mSocketChannel.close();
        } catch (IOException e) {
//            showError("关闭连接失败：" + e.getCause() + e.getMessage());
            e.printStackTrace();
        }
    }


    private void listenNioConnect() {
        while (true) {
            try {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = (SelectionKey) iterator.next();
                    iterator.remove();
                    if (selectionKey.isConnectable()) {
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        if (socketChannel.finishConnect()) {
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);

                            receiver.setChannel(socketChannel,selector);
                            receiver.start();
                            return;
                        }
                    } else if (selectionKey.isReadable()) {
//                                break;
                    }
                }
            } catch (IOException e) {
                if (e.getMessage() != null && e.getMessage().contains("closed")
                        && e.getMessage().contains("Broken")) {
                    nioDisconnect();
                }
                receiver.interrupt();
//                        showError("连接失败：" + e.getCause() + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public boolean haveChannel() {
        if (mSocketChannel == null) {
            registerNioSelector();
        }
        return false;
    }


//    public SocketChannel getSocketChannel() {
//        if (mSocketChannel == null) {
//            registerNioSelector();
//        }
//        if (mSocketChannel.isConnected())
//            return mSocketChannel;
//    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

}
