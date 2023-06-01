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
            if(view!=null) view.showError("发送线程被终止");
        }
    }

    private void sendNormalMsg(String userId, String sendMsg) {
        if (mSocketChannel == null) {
            if(view!=null) view.showError("当前未连接");
            return;
        }
        if (sendHandle != null) {
            sendHandle.post(new Runnable() {
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
            if(view!=null) view.showError("发送线程被终止");
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
            if (view != null) view.showError("发送失败：连接已断开");
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
            if (view != null) view.showError("发送失败：" + e.getCause() + e.getMessage());
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

    private boolean isConnected() {
        return isAuth;
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
                                if (socketChannel.finishConnect()) {
                                    socketChannel.configureBlocking(false);
                                    socketChannel.register(NioPeriodChronicService.this.selector, SelectionKey.OP_READ);
                                    registerUser(socketChannel, uid,token);
                                }
//                                if (socketChannel.isConnectionPending()) {
////                                    socketChannel.finishConnect();
//                                    continue;
//                                }
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
            byte[] data = getData(socketChannel);

            int action = getAction(data);
            byte[] msgModeData = new byte[data.length - 4];
            System.arraycopy(data, 4, msgModeData, 0, data.length - 4);
            data = msgModeData;
            if (action == 1) { // auth
                String authRet;
                try {
                    authRet = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                if (authRet == null || !authRet.equals("success")) {
                    //TODO: dispose and disconnect
                    if(view!=null) view.showError("校验用户失败：" +authRet);
                    return;
                }
                isAuth = true;
                this.mSocketChannel = socketChannel;
                if(view!=null) view.showConnection(host,port,uid,token);
                LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_LOGIN, null);
            } else if (action == Integer.MAX_VALUE) { //heart
                System.out.println("heart beat");
            } else if (action == -1) { //disconnect

            } else if (action == 0) { //data
                //TODO:
                // msgCache.add(content);
                byte[] sendModeData = new byte[4];
                byte[] formIdData = new byte[32];
                byte[] msgData = new byte[msgModeData.length-32-4];
                System.arraycopy(msgModeData, 0, sendModeData, 0, 4);
                System.arraycopy(msgModeData, 4, formIdData,0, 32);
                System.arraycopy(msgModeData, 4+32, msgData, 0, msgModeData.length-32-4);
                int sendMode = Tool.byte4ToInt(sendModeData, 0);
                if (sendMode == 1) {
                    String msgContent = new String(msgData, "UTF-8");
                    String formId = new String(formIdData, "UTF-8");
                    LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_MSG_RECEIVE, userMap.get(formId) + msgContent);
                }


            }

        } catch (Exception e) {
            //ignore
        } finally {
            //关闭socket
        }
    }



    private int getAction(byte[] data) {
        byte[] actionData = new byte[4];
        System.arraycopy(data, 0, actionData, 0, 4);
        return Tool.byte4ToInt(actionData, 0);
    }

    private byte[] getData(SocketChannel socketChannel) {
        byte[] bytes = parseBatch(socketChannel);
        if (bytes == null || bytes.length == 0) {
            //TODO: dispose and disconnect
            return new byte[0];
        }
//        int protocolVersion = parseProtocolVersion(bytes);
//        if (protocolVersion != 1) {
//
//        }
//        int serviceType = parseServiceType(bytes);
//        if (serviceType != 1) {
//
//        }
//        int redundancyProcess = parseRedundancyProcess(bytes);
//        if (redundancyProcess != 1) {
//
//        }

        byte[] data = new byte[bytes.length - 4 * 3];
        System.arraycopy(bytes, 4 * 3, data, 0, bytes.length - 4 * 3);
        return data;
    }

    public static final long MAX_BATCH = 1048576000;//1024*1024*100=100MB
    private byte[] parseBatch(SocketChannel socketChannel) {
        int batchLength = 0;
        byte[] cacheBatch;

        for (; ; ) {
            try {
                if (batchLength == 0) {
                    ByteBuffer buffer = ByteBuffer.allocate(4);
                    int readedCount = socketChannel.read(buffer);
                    if (readedCount != 4) {
                        //TODO: dispose and disconnect
                        return new byte[0];
                    }

//            long batchLength = Tool.byte8ToLong(buffer.array(), 0);
                    int tempBatchLength =  Tool.byte4ToInt(buffer.array(), 0);
                    if (batchLength > MAX_BATCH) {
                        //TODO: dispose and disconnect
                        return new byte[0];
                    }
                    batchLength = tempBatchLength;
                } else {
                    ByteBuffer buffer = ByteBuffer.allocate(batchLength);
                    int readedByteCount = socketChannel.read(buffer);
                    cacheBatch = buffer.array();
                    break;
                }
            } catch (IOException e) {
                if (e.getMessage() != null && e.getMessage().contains("closed")
                        && e.getMessage().contains("Broken")) {
                    //TODO: dispose and disconnect
                }
                e.printStackTrace();
            }
        }
        return cacheBatch;
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
            int action = 1;
            byte[] batchData = getBatchData(action, (uid + token).getBytes(StandardCharsets.UTF_8));
            byte[] data = pkgServiceData(batchData);

            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            socketChannel.write(byteBuffer);
            return "";
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
