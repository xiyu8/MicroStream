//package com.jason.microstream.core.im.tup;
//
//import com.google.gson.Gson;
//import com.jason.microstream.Tool;
//import com.jason.microstream.core.im.imconpenent.ImService;
//
//import org.webrtc.SessionDescription;
//
//import java.io.UnsupportedEncodingException;
//
//public class CallCmd {
//    ImService imService;
//
//    public CallCmd(ImService imService) {
//        this.imService = imService;
//    }
//
//    private void sendCalloutToUser(String uid) {
//        if (imService.mSocketChannel == null) {
//            showError("当前未连接");
//            return;
//        }
//        String msgData = uid + new Gson().toJson(sessionDescription);
//        byte[] dataBytes = new byte[0];
//        try {
//            dataBytes = msgData.getBytes(("UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        byte[] targetDataBytes = new byte[MSG_TYPE_SIZE + dataBytes.length];
//        System.arraycopy(Tool.intToByte4(MSG_TYPE_SWAP_SDP),0,targetDataBytes,0,MSG_TYPE_SIZE);
//        System.arraycopy(dataBytes,0,targetDataBytes,MSG_TYPE_SIZE,dataBytes.length);
//        int action = 0;
//        byte[] batchData = imService.getBatchData(action, targetDataBytes);
//        byte[] capsuleData = imService.pkgServiceData(batchData);
//        imService.asyncSendImp(capsuleData);
//    }
//    private long getMsgId() {
//        long id = System.currentTimeMillis();
////        while (mCacheMap.containsKey(id)) {
////            id++;
////        }
//        return id;
//    }
//
//}
