package com.jason.microstream.core.im.tup;

import com.jason.microstream.core.im.tup.channelcontext.ChannelContext;
import com.jason.microstream.tool.log.LogTool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class ChannelHolder implements Callable<Integer> {
    private final String TAG = ChannelHolder.class.getSimpleName();

    private final ExecutorService executor;
    SocketChannel mSocketChannel;
    private final Selector selector;

    private Demultiplexer demultiplexer;

    private final String host, port;
    private ChannelContext channelContext;

    public ChannelHolder(String host, String port) {
        this.host = host;
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
        try {
            selector = Selector.open();
        } catch (IOException e) {
            LogTool.e(TAG, "ChannelHolder connect Selector.open() fail!");
            throw new RuntimeException(e);
        }
    }


    //?????
    public void nioDisconnect() {
//        host = null;
//        port = null;
        if (mSocketChannel != null) {
            if (mSocketChannel.isConnected()) {
                try {
                    mSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    mSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        channelContext = null;
        isConnected = false;
    }

    public void setDemultiplexer(Demultiplexer demultiplexer) {
        this.demultiplexer = demultiplexer;
    }

    public void setContext(ChannelContext channelContext) {
        this.channelContext = channelContext;
    }

    public ChannelContext getContext() {
        return channelContext;
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public int connectSync() {
        if (host == null || port == null) {
             throw new RuntimeException("MSIM connect without host!!!");
        }

        int ret = registerNioSelector();
        if (ret != 0) return ret;
        ret = nioConnect();
        return ret;
    }

    //step1
    private int registerNioSelector() {
        if (mSocketChannel == null || !mSocketChannel.isOpen()) {
            synchronized (this) {
                if (mSocketChannel == null || !mSocketChannel.isOpen()) {
                    int[] gaps = new int[]{0, 50, 200};
                    int openRet = 1;
                    for (int i = 0; i < 3; i++) {
                        int gap = gaps[i];
                        if (gap != 0) {
                            try {
                                Thread.sleep(gap);
                            } catch (InterruptedException ignored) {
                            }
                        }
                        try {
                            mSocketChannel = SocketChannel.open();
                            //用channel.finishConnect();才能完成连接
                            mSocketChannel.configureBlocking(false);
                            mSocketChannel.register(selector, SelectionKey.OP_CONNECT);
                            openRet = 0;
                        } catch (IOException e) {
                            LogTool.e(TAG,"registerNioImp IOException!");
                        }
                        if (openRet == 0) break;
                    }

                    return openRet;
                }
            }
        }
        return 0;
    }

    //step3
    private int nioConnect() {
        int ret = 1;
        if (!isConnected) {
            synchronized (executor) {
                if (!isConnected) {
                    Future<Integer> result = executor.submit(this);
                    try {
                        ret = result.get();
                        if (ret == 0) {
                            isConnected = true;
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        LogTool.e(TAG, "mSocketChannel.connect callable thread submit fail!!!e:" + e);
                        throw new RuntimeException(e);
                    } finally {
                        return ret;
                    }
                }
            }
        }
        return ret;
    }
    @Override
    public Integer call() {
        if (mSocketChannel == null || !mSocketChannel.isOpen()) {
//            dispatcher.senderException(Dispatcher.SendCmd.SOCKET_CONNECT_FAIL);
        }

        int ret = 1;
        if (!mSocketChannel.isConnected()) {
            synchronized (this) {
                if (!mSocketChannel.isConnected()) {
                    int[] gaps = new int[]{0, 50, 200};
                    boolean connectResult = false;
                    for (int i = 0; i < 3; i++) {
                        connectResult = connectNioImp(gaps[i]);
                        if (connectResult) break;
                    }
                    if (connectResult) {
                        ret = demultiplexer.handleConnectingChannel(mSocketChannel, null, this);
                    }
                }
            }
        }
        return ret;
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
                        mSocketChannel.close();
                        return false;
                    }
                    // 可以在这里做一些其他的操作，比如取消连接，或者处理其他的I/O事件
                }
            }
            LogTool.e(TAG, "mSocketChannel.connect success");
            ret = true;
        } catch (IOException e) {
            LogTool.e(TAG, "connectNioImp IOException!e:" + e);
        }
        return ret;
    }

    private volatile boolean isConnected = false;

}
