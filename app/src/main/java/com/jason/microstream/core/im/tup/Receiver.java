//package com.jason.microstream.core.im.tup;
//
//import static com.jason.microstream.MainActivity1_.TAGT;
//
//import android.util.Log;
//
//import com.google.gson.Gson;
//import com.jason.microstream.Tool;
//import com.jason.microstream.localbroadcast.Events;
//import com.jason.microstream.localbroadcast.LocBroadcast;
//import com.jason.microstream.tool.log.LogTool;
//
//import org.webrtc.IceCandidate;
//import org.webrtc.SessionDescription;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.nio.ByteBuffer;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.SocketChannel;
//import java.util.Iterator;
//
//import kotlin.Pair;
//
//public class Receiver extends Thread{
//    private final String TAG = Receiver.class.getSimpleName();
//
//    Dispatcher dispatcher;
//    SocketChannel socketChannel;
//    Selector selector;
//
//    public void setChannel(SocketChannel socketChannel,Selector selector) {
//        this.socketChannel = socketChannel;
//        this.selector = selector;
//
////        socketChannel.configureBlocking(false);
////        socketChannel.register(ChannelHolder.this.selector, SelectionKey.OP_READ);
//
//    }
//
//    public void interrupt() {
//        socketChannel = null;
//        selector = null;
//        super.interrupt();
//    }
//
//    @Override
//    public void run() {
//        while (true) {
//            if (isInterrupted()) {
//                return;
//            }
//            try {
//                selector.select();
//                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
//                while (iterator.hasNext()) {
//                    SelectionKey selectionKey = (SelectionKey) iterator.next();
//                    iterator.remove();
//                    if (selectionKey.isConnectable()) {
//                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
//                        if (socketChannel.finishConnect()) {
//                            socketChannel.configureBlocking(false);
//                            socketChannel.register(selector, SelectionKey.OP_READ);
//                        }
//                    } else if (selectionKey.isReadable()) {
//                        nioHandleReadChannel();
//                    }
//                }
//            } catch (IOException e) {
//                if (e.getMessage() != null && e.getMessage().contains("closed")
//                        && e.getMessage().contains("Broken")) {
//                    //TODO:
//                }
//                dispatcher.receiverException(Dispatcher.SendCmd.READ_IO_EXCEPTION);
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void nioHandleReadChannel() {
//        byte[] serviceData = cacheCapsule(socketChannel);
//        if (serviceData == null) {
//            dispatcher.receiverException(Dispatcher.SendCmd.READ_DATA_EXCEPTION);
//            return;
//        }
//        LogTool.e(TAG, "nioHandleReadChannel_read_packet_data:"
//                + "-serviceData.length:" + serviceData.length
//        );
//        dispatcher.dispatchData(serviceData);
//    }
//
//    public static final int MSG_ACTION_SIZE = 4;
//    private byte[] parseAction(byte[] batchData) {
//        byte[] actionData =new byte[MSG_ACTION_SIZE];
//        System.arraycopy(batchData, 0, actionData, 0, actionData.length);
//        return actionData;
//    }
//
//    public static final long MAX_CAPSULE = 1048576000;//1024*1024*100=100MB
//    private byte[] cacheCapsule(SocketChannel socketChannel) {
//        int capsuleLength = 0;
//        byte[] cacheCapsule = new byte[0];
//        int cacheCursor = 0;
//
//        byte[] cacheLength = new byte[4];
//        int cacheLengthCursor = 0;
//
//        for (; ; ) {
//            try {
//                if (capsuleLength == 0) {
//                    for (; ; ) {
//                        ByteBuffer buffer = ByteBuffer.allocate(4 - cacheLengthCursor);
//                        int readedCount = socketChannel.read(buffer);
//                        if (readedCount <= 0) {
//                            if (readedCount == 0) {
//                                continue;
//                            }
////                            showError("readedCount <= 0");
//                            return null;
//                        }
//                        System.arraycopy(buffer.array(), 0, cacheLength, cacheLengthCursor, readedCount);
//                        cacheLengthCursor += readedCount;
//                        if (cacheLengthCursor == 4) {
//                            int tempCapsuleLength = Tool.byte4ToInt(cacheLength, 0);
//                            if (tempCapsuleLength > MAX_CAPSULE) { //data exception
////                                showError("tempCapsuleLength > MAX_CAPSULE：readedCount"+readedCount+"tempCapsuleLength"+tempCapsuleLength);
//                                return null;
//                            }
//                            capsuleLength = tempCapsuleLength;
//                            cacheCapsule = new byte[capsuleLength];
//                            break;
//                        }
//                    }
//                } else {
//                    if (cacheCursor == 0) {
//                        ByteBuffer buffer = ByteBuffer.allocate(capsuleLength);
//                        int readedByteCount = socketChannel.read(buffer);
//                        if (readedByteCount <= 0) {
//                            if (readedByteCount == 0) {
//                                continue;
//                            }
////                            showError("readedByteCount <= 0");
//                            return null;
//                        }
//                        System.arraycopy(buffer.array(), 0, cacheCapsule, 0, readedByteCount);
//                        cacheCursor = readedByteCount;
//                        if (capsuleLength == readedByteCount) {
//                            break;
//                        }
//                    } else {
//                        ByteBuffer buffer = ByteBuffer.allocate(capsuleLength - cacheCursor);
//                        int readedByteCount = socketChannel.read(buffer);
//                        if (readedByteCount <= 0) {
//                            if (readedByteCount == 0) {
//                                continue;
//                            }
////                            showError("readedByteCount <= 0");
//                            return null;
//                        }
//                        System.arraycopy(buffer.array(), 0, cacheCapsule, cacheCursor, readedByteCount);
//                        cacheCursor = cacheCursor + readedByteCount;
//                        if (capsuleLength == cacheCursor) {
//                            break;
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                if (e.getMessage() != null && e.getMessage().contains("closed")
//                        && e.getMessage().contains("Broken")) {
////                    showError("read exception" + e.getMessage());
//                    return null;
//                }
////                showError("read exception");
//                e.printStackTrace();
//            }
//        }
//        return cacheCapsule;
//    }
//
//
//
//
//
//
//
//
//
//
//    /////////////////////////////////////////////////////////
//    public Dispatcher getDispatcher() {
//        return dispatcher;
//    }
//
//    public void setDispatcher(Dispatcher dispatcher) {
//        this.dispatcher = dispatcher;
//    }
//
//
//    public SocketChannel getSocketChannel() {
//        return socketChannel;
//    }
//
//    public void setSocketChannel(SocketChannel socketChannel) {
//        this.socketChannel = socketChannel;
//    }
//
//    public void setSelector(Selector selector) {
//        this.selector = selector;
//    }
//
//}
