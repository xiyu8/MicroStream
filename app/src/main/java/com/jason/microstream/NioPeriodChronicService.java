package com.jason.microstream;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class NioPeriodChronicService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    NioBinder mBinder = new NioBinder(){
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    Selector selector;
    SocketChannel mSocketChannel;
    public void registerNIoSelector(View view) {
        this.view = view;
        try {
            mSocketChannel = SocketChannel.open();
            mSocketChannel.configureBlocking(false);
            this.selector = Selector.open();
            //用channel.finishConnect();才能完成连接
            mSocketChannel.register(selector, SelectionKey.OP_CONNECT);
        } catch (IOException e) {
            if(view!=null) view.showError("初始化nio失败");
            e.printStackTrace();
        }
    }

    private  void nioWriteString(final String ss) {
        if (mSocketChannel == null) {
            if(view!=null) view.showError("当前未连接");
            return;
        }
        if (sendHandle != null) {
            sendHandle.post(new Runnable() {
                @Override
                public void run() {
                    nioWriteStringImp(ss);
                }
            });
        } else {
            if(view!=null) view.showError("发送线程被终止");
        }
    }
    private synchronized void nioWriteStringImp(String ss) {
        if (mSocketChannel == null) {
            if(view!=null) view.showError("发送失败：连接已断开");
        }
        try {
            byte[] contentBytes = ss.getBytes(("UTF-8"));
            int contentLength = contentBytes.length;
            byte[] contentLengthBytes = Tool.intToByte4(contentLength);
            byte[] totalBytes = Tool.combineBytes(new byte[4 + contentLength], contentLengthBytes, contentBytes);
            ByteBuffer byteBuffer = ByteBuffer.wrap(totalBytes);
            mSocketChannel.write(byteBuffer);
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("closed")
                    && e.getMessage().contains("Broken")) {
                nioDisconnect();
            }
            if(view!=null) view.showError("发送失败：" + e.getCause() + e.getMessage());
            e.printStackTrace();
        }
    }

    Thread sendThread;
    Handler sendHandle;
    private void initWriteThread() {
        sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                sendHandle = new Handler();
                Looper.loop();
            }
        });
        sendThread.start();
    }


    String port = null;
    String host = null;
    String uid = null, token = null;
    boolean isAuth = false;

    private void nioDisconnect() {
        host = null;
        port = null;
        isAuth = false;
        if (mSocketChannel == null) {
            return;
        }
        if (!(mSocketChannel.isConnected())) {
            try {
                mSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(view!=null) view.showConnection(null, null,null, null);
            return;
        }
        try {
            mSocketChannel.close();
        } catch (IOException e) {
            if(view!=null) view.showError("关闭连接失败：" + e.getCause() + e.getMessage());
            e.printStackTrace();
        }
        if(view!=null) view.showConnection(null, null, null,null);
    }


    private void nioConnect(String host, String port,String uid,String token) {
        this.host = host;
        this.port = port;
        this.uid = uid;
        this.token = token;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocketChannel.connect(new InetSocketAddress(host, Integer.parseInt(port)));
                    listenNioConnect(uid,token);
                } catch (IOException e) {
                    if (e.getMessage() != null && e.getMessage().contains("closed")
                            && e.getMessage().contains("Broken")) {
                        nioDisconnect();
                    }
                    if(view!=null) view.showError("连接失败：" + e.getCause() + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void listenNioConnect(String uid,String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        selector.select();
                        Iterator iterator = NioPeriodChronicService.this.selector.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey selectionKey = (SelectionKey) iterator.next();
                            iterator.remove();
                            if (selectionKey.isConnectable()) {
                                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                                if (socketChannel.isConnectionPending()) {
                                    socketChannel.finishConnect();
                                }
                                socketChannel.configureBlocking(false);
                                socketChannel.register(NioPeriodChronicService.this.selector, SelectionKey.OP_READ);
                                registerUser(socketChannel, uid,token);
                            } else if (selectionKey.isReadable()) {
                                nioHandleReadChannel(selectionKey, (SocketChannel) selectionKey.channel());
                            }
                        }
                    } catch (IOException e) {
                        if (e.getMessage() != null && e.getMessage().contains("closed")
                                && e.getMessage().contains("Broken")) {
                            nioDisconnect();
                        }
                        if(view!=null) view.showError("连接失败：" + e.getCause() + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void nioHandleReadChannel(SelectionKey selectionKey, SocketChannel socketChannel) {
        try {
            if (!isAuth) {
                isAuth = checkRegister(socketChannel);
                if (!isAuth) {
                    if(view!=null) view.showError("验证用户失败");
                    return;
                }
                if(view!=null) view.showConnection(host,port,uid,token);
            } else {
                handleNioChannelDataRead(selectionKey, socketChannel);
            }
        } catch (Exception e) {
            //ignore
        } finally {
            //关闭socket
        }
    }

    private synchronized void handleNioChannelDataRead(SelectionKey selectionKey, SocketChannel socketChannel) {
        int contentLength = 0;
        for (; ; ) {
            try {
                if (contentLength == 0) {
                    ByteBuffer buffer = ByteBuffer.allocate(4);
                    long readedByteCount = socketChannel.read(buffer);
                    if (readedByteCount == -1) { //客户端关闭了连接(=0未判断)
                        nioDisconnect();
                        break;
                    } else {
                        contentLength = Tool.byte4ToInt(buffer.array(), 0);
                    }
                } else {
                    ByteBuffer buffer = ByteBuffer.allocate(contentLength);
                    int readedByteCount = socketChannel.read(buffer);
                    if (readedByteCount != -1) {
                        String ss = new String(buffer.array(), "UTF-8");
                        if(view!=null) view.showData(ss);
                    }
                    contentLength = 0;
                    break;
                }
            } catch (IOException e) {
                if (e.getMessage() != null && e.getMessage().contains("closed")
                        && e.getMessage().contains("Broken")) {
                    nioDisconnect();
                }
                e.printStackTrace();
            }
        }
    }

    private boolean checkRegister(SocketChannel socketChannel) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            int readedCount = socketChannel.read(buffer);
            if (readedCount == -1 || readedCount == 0) {
                return false;
            }
            byte[] tempBytes = buffer.array();
            byte[] targetBytes = new byte[readedCount];
            for (int i = 0; i < readedCount; i++) {
                targetBytes[i] = tempBytes[i];
            }
            String ret = new String(targetBytes, "UTF-8");
            if (ret.contains("success")) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e.getMessage() != null && e.getMessage().contains("closed")
                    && e.getMessage().contains("Broken")) {
                nioDisconnect();
            }
            if(view!=null) view.showError("校验用户失败：" + e.getCause() + e.getMessage());
            return false;
        }
        return false;
    }

    private String registerUser(SocketChannel socketChannel, String uid,String token) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap((uid + token + "\n\r").getBytes(StandardCharsets.UTF_8));
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("closed")
                    && e.getMessage().contains("Broken")) {
                nioDisconnect();
            }
            if(view!=null) view.showError("连接失败：" + e.getCause() + e.getMessage());
            e.printStackTrace();
        }
        return "verify fail";
    }



    public class NioBinder extends Binder {
        public void registerNIoSelector(View view){
            NioPeriodChronicService.this.registerNIoSelector(view);
        }
        public void initWriteThread() {
            NioPeriodChronicService.this.initWriteThread();
        }
        public void nioWriteString(String sendMsg) {
            NioPeriodChronicService.this.nioWriteString(sendMsg);
        }
        public void nioConnect(String host,String port,String uid,String token) {
            NioPeriodChronicService.this.nioConnect( host, port,uid,token);
        }
        public void nioDisconnect() {
            NioPeriodChronicService.this.nioDisconnect();
        }
        public void setView(View view) {
            NioPeriodChronicService.this.setView(view);
        }
    }

    private void setView(View view) {
        this.view = view;
    }

    View view;
    public interface View {
        void showError(String ss);

        void showData(final String ss);

        void showConnection(final String ip, final String port, final String uid,String token);
    }


}
