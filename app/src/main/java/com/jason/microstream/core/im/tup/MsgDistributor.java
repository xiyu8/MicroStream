package com.jason.microstream.core.im.tup;

import static com.jason.microstream.MainActivity1_.TAGT;

import android.util.Log;

import com.google.gson.Gson;
import com.jason.microstream.MsApplication;
import com.jason.microstream.Tool;
import com.jason.microstream.core.im.tup.data.msg.LoginRet;
import com.jason.microstream.core.im.tup.data.msg.TestMsg;
import com.jason.microstream.core.im.tup.data.msg.VideoCmd;
import com.jason.microstream.core.im.tup.joint.Account;
import com.jason.microstream.core.im.tup.joint.MsgNotifier;
import com.jason.microstream.tool.ToastUtil;
import com.jason.microstream.tool.log.L;
import com.jason.microstream.tool.log.LogTool;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.io.UnsupportedEncodingException;

public class MsgDistributor {

    public static final String CMD_TAG = TAGT+"VIDEO CMD";
    public static final String TAG = MsgDistributor.class.getSimpleName();

    MsgNotifier msgNotifier;
    Dispatcher dispatcher;
    ChannelHolder channelHolder;
    Gson gson;

    public MsgDistributor() {
        this.gson = new Gson();
    }

    public int distribute(byte[] msgData,int action) {
        int ret = -1;

        try {
            ret = distributeImp(msgData, action);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }



    public static final int MSG_TYPE_SIZE = 4;
    public static final int MSG_TYPE_TEST = 1;
    public static final int MSG_TYPE_REGISTER = 1;
    public static final int MSG_TYPE_SWAP_ICE = 102;
    public static final int MSG_TYPE_SWAP_SDP = 103;
    public static final int MSG_TYPE_OFFER_SDP = 104;
    private static final int MSG_TYPE_ID_SIZE = 32;
//    public static final int FROM_UID_SIZE = 32;
    private int distributeImp(byte[] msgModeData,int action) throws UnsupportedEncodingException {
        int ret = -1;
        if (action == Sender.ACTION_AUTH) { // auth
            String authRetData;
            LogTool.e(TAG, "distributeImp__Sender.ACTION_AUTH");
            try {
                authRetData = new String(msgModeData, 4, msgModeData.length - 4, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                ret = -1;
//                nioDisconnect();
                throw new RuntimeException(e);
            }
            LoginRet loginRet = gson.fromJson(authRetData, LoginRet.class);
            LogTool.e(TAG, "distributeImp__loginRet:"
                    + "-loginRet.authRet:" + loginRet.authRet
                    + "-loginRet.uid:" + loginRet.uid
                    + "-loginRet.token:" + loginRet.token
            );
            if (loginRet != null && loginRet.authRet != null && loginRet.authRet.equals("success")) {
                Account.get().authed();
//                channelHolder.nioDisconnect();
            } else {
                //TODO:IM服务重置，并根据错误原因 决定是否通知业务层的 登录
//                showError("校验用户失败：" +authRet);
                dispatcher.forceLogout();
//                Account.get().forceLogout();
                channelHolder.nioDisconnect();
            }
            ret = 0;
        } else if (action == Integer.MAX_VALUE) { //heart
            System.out.println("heart beat");
        } else if (action == -1) { //disconnect

        } else if (action == Sender.ACTION_DATA) { //data
            //TODO:
            // msgCache.add(content);
            int sendType = Tool.byte4ToInt(msgModeData, 0);
            if (sendType == MSG_TYPE_TEST) {
                byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
                System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
                String msgContent = new String(msgData, "UTF-8");
                TestMsg testMsg = gson.fromJson(msgContent, TestMsg.class);
                // msgCache.add(msgContent);
                Log.e(CMD_TAG, "receive cmd MSG_TYPE_TEST:" + msgContent);
                ret = msgNotifier.notify(testMsg.fromId, testMsg.content);
            } else if (sendType == MSG_TYPE_SWAP_ICE) {
                byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
                System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
                String msgContent = new String(msgData, "UTF-8");
                VideoCmd videoCmd = gson.fromJson(msgContent, VideoCmd.class);
                // msgCache.add(msgContent);
                Log.e(CMD_TAG, "receive cmd MSG_TYPE_SWAP_ICE:" + msgContent);
                ret = msgNotifier.notifyIce(videoCmd.fromId, gson.fromJson(videoCmd.cmdContent, IceCandidate.class));
            } else if (sendType == MSG_TYPE_SWAP_SDP) {
                byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
                System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
                String msgContent = new String(msgData, "UTF-8");
                VideoCmd videoCmd = gson.fromJson(msgContent, VideoCmd.class);
                // msgCache.add(msgContent);
                Log.e(CMD_TAG, "receive cmd MSG_TYPE_SWAP_SDP:" + msgContent);
                ret = msgNotifier.notifySwapSdp(videoCmd.fromId, gson.fromJson(videoCmd.cmdContent, SessionDescription.class));
            } else if (sendType == MSG_TYPE_OFFER_SDP) {
                byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
                System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
                String msgContent = new String(msgData, "UTF-8");
                VideoCmd videoCmd = gson.fromJson(msgContent, VideoCmd.class);
                // msgCache.add(msgContent);
                Log.e(CMD_TAG, "receive cmd MSG_TYPE_OFFER_SDP:" + msgContent);
                ret = msgNotifier.notifyOfferSdp(videoCmd.fromId, gson.fromJson(videoCmd.cmdContent, SessionDescription.class));
            }
        }

        return ret;
    }

    public int msgFail(int code, byte[] msgData,int action) {
        int ret = -1;
        try {
            ret = msgFailImp(code, msgData, action);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    public int msgSendSuccess(byte[] msgData,int action) {
        int ret = -1;
        try {
            ret = msgSendSuccessImp(msgData, action);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }


    private int msgFailImp(int code, byte[] msgModeData,int action) throws UnsupportedEncodingException {
        int errorCode = code;
        if (action == Sender.ACTION_AUTH) { // auth
//            String authRetData;
//            try {
//                authRetData = new String(msgModeData, 4, msgModeData.length - 4, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                throw new RuntimeException(e);
//            }
            ToastUtil.show(MsApplication.getInstance(),code+","+"tokenAuth失败");
            LogTool.e(TAG, "tokenAuth失败");


        } else if (action == Integer.MAX_VALUE) { //heart
            System.out.println("heart beat");
        } else if (action == -1) { //disconnect

        } else if (action == Sender.ACTION_DATA) { //data
            //TODO:
            // msgCache.add(content);
            int sendType = Tool.byte4ToInt(msgModeData, 0);
            if (sendType == MSG_TYPE_TEST) {
                byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
                System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
                String msgContent = new String(msgData, "UTF-8");
                TestMsg testMsg = gson.fromJson(msgContent, TestMsg.class);
                // msgCache.add(msgContent);
                Log.e(CMD_TAG, "receive cmd MSG_TYPE_TEST:" + msgContent);
                ToastUtil.show(MsApplication.getInstance(),code+","+"test消息 失败");
            } else if (sendType == MSG_TYPE_SWAP_ICE) {
                byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
                System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
                String msgContent = new String(msgData, "UTF-8");
                VideoCmd videoCmd = gson.fromJson(msgContent, VideoCmd.class);
                // msgCache.add(msgContent);
                Log.e(CMD_TAG, "receive cmd MSG_TYPE_SWAP_ICE:" + msgContent);
                ToastUtil.show(MsApplication.getInstance(),code+","+"ice消息 失败");
            } else if (sendType == MSG_TYPE_SWAP_SDP) {
                byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
                System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
                String msgContent = new String(msgData, "UTF-8");
                VideoCmd videoCmd = gson.fromJson(msgContent, VideoCmd.class);
                // msgCache.add(msgContent);
                Log.e(CMD_TAG, "receive cmd MSG_TYPE_SWAP_SDP:" + msgContent);
                ToastUtil.show(MsApplication.getInstance(),code+","+"swap sdp消息 失败");
            } else if (sendType == MSG_TYPE_OFFER_SDP) {
                byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
                System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
                String msgContent = new String(msgData, "UTF-8");
                VideoCmd videoCmd = gson.fromJson(msgContent, VideoCmd.class);
                // msgCache.add(msgContent);
                Log.e(CMD_TAG, "receive cmd MSG_TYPE_OFFER_SDP:" + msgContent);
                ToastUtil.show(MsApplication.getInstance(),code+","+"offer sdp消息 失败");
            }
        }

        return errorCode;
    }


    private int msgSendSuccessImp(byte[] msgModeData,int action) throws UnsupportedEncodingException {
        if (action == Sender.ACTION_AUTH) { // auth
//            String authRetData;
//            try {
//                authRetData = new String(msgModeData, 4, msgModeData.length - 4, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                throw new RuntimeException(e);
//            }
            ToastUtil.show(MsApplication.getInstance(),"token验证消息发送成功，收到ack");
            LogTool.e(TAG, "msgSendSuccessImp_____acked:"
                    + "-action == Sender.ACTION_AUTH:"
            );


        } else if (action == Integer.MAX_VALUE) { //heart
            System.out.println("heart beat");
        } else if (action == -1) { //disconnect

        } else if (action == Sender.ACTION_DATA) { //data
            //TODO:
            // msgCache.add(content);
            int sendType = Tool.byte4ToInt(msgModeData, 0);
            if (sendType == MSG_TYPE_TEST) {
                byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
                System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
                String msgContent = new String(msgData, "UTF-8");
                TestMsg testMsg = gson.fromJson(msgContent, TestMsg.class);
                // msgCache.add(msgContent);
                LogTool.e(TAG+CMD_TAG, "msgSendSuccessImp_____acked:"
                        + "-sendType == MSG_TYPE_TEST::::"
                );
            } else if (sendType == MSG_TYPE_SWAP_ICE) {
                byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
                System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
                String msgContent = new String(msgData, "UTF-8");
                VideoCmd videoCmd = gson.fromJson(msgContent, VideoCmd.class);
                // msgCache.add(msgContent);
                LogTool.e(TAG+CMD_TAG, "msgSendSuccessImp_____acked:"
                        + "-sendType == MSG_TYPE_SWAP_ICE::::"
                );
            } else if (sendType == MSG_TYPE_SWAP_SDP) {
                byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
                System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
                String msgContent = new String(msgData, "UTF-8");
                VideoCmd videoCmd = gson.fromJson(msgContent, VideoCmd.class);
                // msgCache.add(msgContent);
                LogTool.e(TAG+CMD_TAG, "msgSendSuccessImp_____acked:"
                        + "-sendType == MSG_TYPE_SWAP_SDP::::"
                );
            } else if (sendType == MSG_TYPE_OFFER_SDP) {
                byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
                System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
                String msgContent = new String(msgData, "UTF-8");
                VideoCmd videoCmd = gson.fromJson(msgContent, VideoCmd.class);
                // msgCache.add(msgContent);
                LogTool.e(TAG+CMD_TAG, "msgSendSuccessImp_____acked:"
                        + "-sendType == MSG_TYPE_OFFER_SDP::::"
                );
            }
        }

        return 0;
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////


    public void setMsgNotifier(MsgNotifier msgNotifier) {
        this.msgNotifier = msgNotifier;
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    public void setChannelHolder(ChannelHolder channelHolder) {
        this.channelHolder = channelHolder;
    }
}
