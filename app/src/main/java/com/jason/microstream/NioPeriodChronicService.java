package com.jason.microstream;

import static com.jason.microstream.MainActivity1_.userMap;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.jason.microstream.localbroadcast.Events;
import com.jason.microstream.localbroadcast.LocBroadcast;
import com.jason.microstream.model.msg.DisplayMsg;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class NioPeriodChronicService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    NioBinder mBinder = new NioBinder();

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
    public void registerNioSelector() {
        try {
            mSocketChannel = SocketChannel.open();
            mSocketChannel.configureBlocking(false);
            this.selector = Selector.open();
            //用channel.finishConnect();才能完成连接
            mSocketChannel.register(selector, SelectionKey.OP_CONNECT);
        } catch (IOException e) {
            showError("初始化nio失败");
            e.printStackTrace();
        }
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
                    showError("连接失败：" + e.getCause() + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Thread sendThread;
    Handler sendHandler;
    private void initWriteThread() {
        sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                sendHandler = new Handler();
                Looper.loop();
            }
        });
        sendThread.start();
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
                                if (socketChannel.finishConnect()) {
                                    socketChannel.configureBlocking(false);
                                    socketChannel.register(NioPeriodChronicService.this.selector, SelectionKey.OP_READ);
                                    boolean ret = registerUserLaunch(socketChannel, uid, token);
                                    if (!ret) {
                                        nioDisconnect();
                                    }
                                }
                            } else if (selectionKey.isReadable()) {
                                nioHandleReadChannel(selectionKey, (SocketChannel) selectionKey.channel());
                            }
                        }
                    } catch (IOException e) {
                        if (e.getMessage() != null && e.getMessage().contains("closed")
                                && e.getMessage().contains("Broken")) {
                            nioDisconnect();
                        }
                        showError("连接失败：" + e.getCause() + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    private  void nioWriteString(final String ss) {
        if (mSocketChannel == null) {
            showError("当前未连接");
            return;
        }
        if (sendHandler != null) {
            sendHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        int action = 0;
                        byte[] batchData = getBatchData(action, ss.getBytes(("UTF-8")));
                        nioWriteStringImp(batchData);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            showError("发送线程被终止");
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void sendNormalMsg(String userId, String sendMsg) {
        if (mSocketChannel == null) {
            showError("当前未连接");
            return;
        }
        if (sendHandler != null) {
            sendHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        int sendType = 1;
                        String data = userId + sendMsg;
                        byte[] dataBytes = data.getBytes(("UTF-8"));
                        byte[] targetDataBytes = new byte[4 + dataBytes.length];
                        System.arraycopy(Tool.intToByte4(sendType),0,targetDataBytes,0,4);
                        System.arraycopy(dataBytes,0,targetDataBytes,4,dataBytes.length);
                        int action = 0;
                        byte[] batchData = getBatchData(action, targetDataBytes);
                        nioWriteStringImp(batchData);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            showError("发送线程被终止");
        }
    }
    private byte[] getBatchData(int actionRet, byte[] bytes) {
        byte[] cb = new byte[4 + bytes.length];
        System.arraycopy(Tool.intToByte4(actionRet), 0, cb, 0, 4);
        System.arraycopy(bytes, 0, cb, 4, bytes.length);
        return cb;
    }
    private synchronized void nioWriteStringImp(byte[] batchBytes) {
        if (mSocketChannel == null) {
            showError("发送失败：连接已断开");
        }
        try {
            byte[] data = pkgServiceData(batchBytes);
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            mSocketChannel.write(byteBuffer);
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("closed")
                    && e.getMessage().contains("Broken")) {
                nioDisconnect();
            }
            showError("发送失败：" + e.getCause() + e.getMessage());
            e.printStackTrace();
        }
    }

//    public static final String imService = "TEST";
    public static final int IM_VERSION = 1;
    public static final int IM_VERSION_SIZE = 4;
    public static final int IM_SERVICE = 1;
    public static final int IM_SERVICE_SIZE = 4;
    public static final int IM_BK = 0;
    public static final int IM_BK_SIZE = 4;
    public static final String imService = "MICRO_STREAM";
    public static final Map<String, Integer> imServiceMap = new HashMap<String, Integer>() {{
        put("TEST", 0);
        put(imService, 1);
    }};

    public int getImServiceSize() {
        return IM_VERSION_SIZE + IM_BK_SIZE + IM_SERVICE_SIZE;
    }

    private byte[] pkgServiceData(byte[] bytes) {
        int serviceDataLength = bytes.length + getImServiceSize();
        byte[] serviceData = new byte[serviceDataLength];
        System.arraycopy(Tool.intToByte4(IM_VERSION), 0, serviceData, 0, IM_VERSION_SIZE);
        System.arraycopy(Tool.intToByte4(IM_SERVICE), 0, serviceData, IM_VERSION_SIZE, IM_SERVICE_SIZE);
        System.arraycopy(Tool.intToByte4(IM_BK), 0, serviceData, IM_VERSION_SIZE + IM_SERVICE_SIZE, IM_BK_SIZE);
        System.arraycopy(bytes, 0, serviceData, IM_VERSION_SIZE + IM_SERVICE_SIZE + IM_BK_SIZE, bytes.length);
        return encapsulate(serviceData);
    }


    private byte[] encapsulate(byte[] bytes) {
        byte[] cellData = new byte[bytes.length + 4];
        System.arraycopy(Tool.intToByte4(bytes.length), 0, cellData, 0, 4);
        System.arraycopy(bytes, 0, cellData, 4, bytes.length);
        return cellData;
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
            showError("关闭连接失败：" + e.getCause() + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isConnected() {
        return isAuth;
    }



    public static final int SEND_TYPE_SIZE = 4;
    public static final int SEND_TYPE_SINGLE = 1;

    public static final int FROM_UID_SIZE = 32;

    public void nioHandleReadChannel(SelectionKey selectionKey, SocketChannel socketChannel) {
        try {
            byte[] batchData = parseBatchData(socketChannel);
            if (batchData == null) {
                nioDisconnect();
                return;
            }

            byte[] actionData = parseAction(batchData);
            int action = Tool.byte4ToInt(actionData, 0);
            byte[] msgModeData = new byte[batchData.length - actionData.length];
            System.arraycopy(batchData, 4, msgModeData, 0, msgModeData.length);
            if (action == 1) { // auth
                String authRet;
                try {
                    authRet = new String(msgModeData, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    nioDisconnect();
                    throw new RuntimeException(e);
                }
                if (authRet == null || !authRet.equals("success")) {
                    nioDisconnect();
                    showError("校验用户失败：" +authRet);
                    return;
                }
                isAuth = true;
                this.mSocketChannel = socketChannel;
                LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_LOGIN, null);
            } else if (action == Integer.MAX_VALUE) { //heart
                System.out.println("heart beat");
            } else if (action == -1) { //disconnect

            } else if (action == 0) { //data
                //TODO:
                // msgCache.add(content);
                int sendType = Tool.byte4ToInt(msgModeData, 0);
                if (sendType == SEND_TYPE_SINGLE) {

                    byte[] formIdData = new byte[FROM_UID_SIZE];
                    byte[] msgData = new byte[msgModeData.length - FROM_UID_SIZE - SEND_TYPE_SIZE];
                    System.arraycopy(msgModeData, SEND_TYPE_SIZE, formIdData,0, formIdData.length);
                    System.arraycopy(msgModeData, FROM_UID_SIZE + SEND_TYPE_SIZE, msgData, 0, msgData.length);

                    String formId = new String(formIdData, "UTF-8");
                    String msgContent = new String(msgData, "UTF-8");

                    // msgCache.add(msgContent);
                    LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_MSG_RECEIVE, userMap.get(formId) + msgContent);
                }
            }

        } catch (Exception e) {
            //ignore
        } finally {
            //关闭socket
        }
    }


    public static final int MSG_ACTION_SIZE = 4;
    private byte[] parseAction(byte[] batchData) {
        byte[] actionData =new byte[MSG_ACTION_SIZE];
        System.arraycopy(batchData, 0, actionData, 0, actionData.length);
        return actionData;
    }

    private byte[] parseBatchData(SocketChannel socketChannel) {
        byte[]  serviceData= cacheCapsule(socketChannel);
        if (serviceData == null || serviceData.length == 0) {
            //TODO: dispose and disconnect
            return null;
        }
        int protocolVersion = Tool.byte4ToInt(serviceData, 0);
        if (protocolVersion != IM_VERSION) {

        }
        int serviceType = Tool.byte4ToInt(serviceData, IM_VERSION_SIZE);
        if (serviceType != IM_SERVICE) {

        }
        int redundancyProcess = Tool.byte4ToInt(serviceData, IM_VERSION_SIZE + IM_SERVICE_SIZE);
        if (redundancyProcess != 1) {

        }

        byte[] batchData = new byte[serviceData.length - getImServiceSize()];
        System.arraycopy(serviceData, 4 * 3, batchData, 0, batchData.length);
        return batchData;
    }

    public static final long MAX_CAPSULE = 1048576000;//1024*1024*100=100MB
    private byte[] cacheCapsule(SocketChannel socketChannel) {
        int capsuleLength = 0;
        byte[] cacheCapsule;

        for (; ; ) {
            try {
                if (capsuleLength == 0) {
                    ByteBuffer buffer = ByteBuffer.allocate(4);
                    int readedCount = socketChannel.read(buffer);
                    if (readedCount != 4) { //data exception
                        //TODO: use read consor,for cache read,to get integrale data
                        return null;
                    }
                    int tempCapsuleLength =  Tool.byte4ToInt(buffer.array(), 0);
                    if (tempCapsuleLength > MAX_CAPSULE) { //data exception
                        return null;
                    }
                    capsuleLength = tempCapsuleLength;
                } else {
                    ByteBuffer buffer = ByteBuffer.allocate(capsuleLength);
                    int readedByteCount = socketChannel.read(buffer);
                    cacheCapsule = buffer.array();
                    break;
                }
            } catch (IOException e) {
                if (e.getMessage() != null && e.getMessage().contains("closed")
                        && e.getMessage().contains("Broken")) {
                    return null;
                }
                e.printStackTrace();
            }
        }
        return cacheCapsule;
    }

    private boolean registerUserLaunch(SocketChannel socketChannel, String uid,String token) {
        try {
            int action = 1;
            byte[] batchData = getBatchData(action, (uid + token).getBytes(StandardCharsets.UTF_8));
            byte[] data = pkgServiceData(batchData);
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            socketChannel.write(byteBuffer);
            return true;
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("closed")
                    && e.getMessage().contains("Broken")) {
                nioDisconnect();
            }
            showError("连接失败：" + e.getCause() + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public class NioBinder extends Binder {
        public void registerNioSelector(){
            NioPeriodChronicService.this.registerNioSelector();
        }
        public void initWriteThread() {
            NioPeriodChronicService.this.initWriteThread();
        }
        public void nioWriteString(String sendMsg) {
            NioPeriodChronicService.this.nioWriteString(sendMsg);
        }
        public void sendNormalMsg(String user, String sendMsg) {
            NioPeriodChronicService.this.sendNormalMsg(user,sendMsg);
        }
        public void nioConnect(String host,String port,String uid,String token) {
            NioPeriodChronicService.this.nioConnect( host, port,uid,token);
        }
        public void nioDisconnect() {
            NioPeriodChronicService.this.nioDisconnect();
        }
        public boolean isConnected() {
            return NioPeriodChronicService.this.isConnected();
        }
    }
    private void showError(String err) {

    }

}
