//package com.jason.microstream.core.im.tup;
//
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//
//import androidx.annotation.NonNull;
//
//import com.jason.microstream.core.im.tup.data.SendNode;
//import com.jason.microstream.tool.log.LogTool;
//
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//public class SenderQueueTimer extends Thread{
//    private final String TAG = getClass().getSimpleName();
//
//    public static final int TIMEOUT_TIME = 1000;
//    public static final int BEAT_GAP = 10*1000;
//
//    private Map<Long, SendNode> sendingNodeMap;
//    private Map<Long, SendNode> sendingNodeMapBk;
//    private Handler handler;
//
//    private Dispatcher dispatcher;
//    private Sender sender;
//
//    public SenderQueueTimer(Sender sender) {
//        sendingNodeMap = new HashMap<>();
//        sendingNodeMapBk = new LinkedHashMap<Long, SendNode>(16){
//            @Override
//            protected boolean removeEldestEntry(Entry<Long, SendNode> eldest) {
//                return true;
//            }
//        };
//        this.sender = sender;
//        start();
//    }
//
//    public void putCandidateNode(SendNode node) {
//        Message message = new Message();
//        message.what = (int) (node.stubId & 0x0000_0000_7FFF_FFFFL);
//        message.obj = node.stubId;
//        LogTool.e(TAG, "putCandidateNode__node.stubId:" + node.stubId
//                + "-node.seqId:" + node.seqId
//        );
//        synchronized (this){
//            sendingNodeMap.put(node.stubId, node);
//        }
//        handler.sendMessageDelayed(message,TIMEOUT_TIME);
//    }
//
//    public void putFailNode(SendNode node) {
//        if (node.acked == 1) { //msg msg send fail,just drop it
//            return;
//        }
//        Message message = new Message();
//        message.what = (int) (node.stubId & 0x0000_0000_7FFF_FFFFL);
//        message.obj = node.stubId;
//        LogTool.e(TAG, "putFailNode__node.stubId:" + node.stubId
//                + "-node.seqId:" + node.seqId
//        );
//        synchronized (this){
//            sendingNodeMap.put(node.stubId, node);
//        }
//        handler.sendMessage(message);
//    }
//
//    public byte[] ackedCandidateNode(Coder.CoderData coderData) {
//        LogTool.e(TAG, "ackedCandidateNode__coderData.msgAction:" + coderData.msgAction
//                + "-coderData.ackFlag:" + coderData.ackFlag
//                + "-coderData.seqId:" + coderData.seqId
//                + "-coderData.stubId:" + coderData.stubId
//        );
//        handler.removeMessages((int) (coderData.stubId & 0x0000_0000_7FFF_FFFFL));
//        SendNode sendNode = null;
//        synchronized (this){
//            sendNode = sendingNodeMap.get(coderData.stubId);
//        }
//        if (sendNode == null) {
//            sendNode = sendingNodeMapBk.get(coderData.stubId);
//        }
//        if (sendNode == null) {
//            return null;
//        }
//        byte[] dataRet = new byte[sendNode.data.length - 4];
//        System.arraycopy(sendNode.data, 4, dataRet, 0, dataRet.length);
//        //发送 成功，收到了某条 发送完的 消息的ack，如何通知业务层？？？？？？？
//        sendNode.seqId = coderData.seqId;
//        dispatcher.dataSendSuccess(sendNode);
//        return dataRet;
//    }
//
//    public void activeBeat() {
//        Message message = Message.obtain(handler, -1);
//        handler.sendMessageDelayed(message,BEAT_GAP);
//    }
//
//    public void resetBeat() {
//        handler.removeMessages(-1);
//        Message message = Message.obtain(handler, -1);
//        handler.sendMessageDelayed(message,BEAT_GAP);
//    }
//
//    public void terminalBeat() {
//        handler.removeMessages(-1);
//    }
//
//    @Override
//    public void run() {
//        super.run();
//        Looper.prepare();
//        handler = new Handler(Looper.myLooper()) {
//            @Override
//            public void handleMessage(@NonNull Message msg) {
//                LogTool.e(TAG, "handler__handleMessage" + msg.what);
//                if (msg.what == -1) {
//                    // sender.sendBeat();
//                } else {
//                    handleUnackedMsg(msg);
//                }
//            }
//        };
//        Looper.loop();
//    }
//
//    private void handleUnackedMsg(Message msg) {
//        SendNode sendNode = null;
//        synchronized (this){
//            sendNode = sendingNodeMap.get((Long)msg.obj);
//        }
//        if (sendNode != null) {
//            SendNode temp = sendingNodeMap.remove((Long)msg.obj);
//            sendingNodeMapBk.put((Long) msg.obj, temp);
//            int code = 0;
//            if (temp.retryCount >= 3) {
//                code = -90001;
//            } else {
//                code = -1;
//            }
//            dispatcher.dataSendFail(code, sendNode);
//        }
//
//    }
//
//
//
//    public void setDispatcher(Dispatcher dispatcher) {
//        this.dispatcher = dispatcher;
//    }
//
//    public void reset() {
//        //先处理 在等待中的队列数据
////        handler.removeMessages();
//    }
//}
