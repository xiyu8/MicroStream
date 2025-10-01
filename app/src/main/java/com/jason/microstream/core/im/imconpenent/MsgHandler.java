package com.jason.microstream.core.im.imconpenent;

import static com.jason.microstream.core.im.tup.MsgDistributor.MSG_TYPE_OFFER_SDP;
import static com.jason.microstream.core.im.tup.MsgDistributor.MSG_TYPE_SIZE;
import static com.jason.microstream.core.im.tup.MsgDistributor.MSG_TYPE_SWAP_ICE;
import static com.jason.microstream.core.im.tup.MsgDistributor.MSG_TYPE_SWAP_SDP;
import static com.jason.microstream.core.im.tup.MsgDistributor.MSG_TYPE_TEST;

import android.util.Log;

import com.google.gson.Gson;
import com.jason.microstream.Tool;
import com.jason.microstream.core.im.tup.MsgDistributor;
import com.jason.microstream.core.im.tup.data.msg.TestMsg;
import com.jason.microstream.core.im.tup.data.msg.VideoCmd;
import com.jason.microstream.core.im.tup.joint.MsgNotifier;
import com.jason.microstream.localbroadcast.Events;
import com.jason.microstream.localbroadcast.LocBroadcast;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.io.UnsupportedEncodingException;

public class MsgHandler implements MsgNotifier {
    private static final String TAG = MsgHandler.class.getSimpleName();
    Gson gson;

    @Override
    public void handleData(byte[] msgModeData, int action, int msgType) throws UnsupportedEncodingException { //data
        int sendType = Tool.byte4ToInt(msgModeData, 0);
        if (sendType == MSG_TYPE_TEST) {
            byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
            System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
            String msgContent = new String(msgData, "UTF-8");
            TestMsg testMsg = gson.fromJson(msgContent, TestMsg.class);
            // msgCache.add(msgContent);
            Log.e(TAG, "receive cmd MSG_TYPE_TEST:" + msgContent);
            notify(testMsg.fromId, testMsg.content);
        } else if (sendType == MSG_TYPE_SWAP_ICE) {
            byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
            System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
            String msgContent = new String(msgData, "UTF-8");
            VideoCmd videoCmd = gson.fromJson(msgContent, VideoCmd.class);
            // msgCache.add(msgContent);
            Log.e(TAG, "receive cmd MSG_TYPE_SWAP_ICE:" + msgContent);
            notifyIce(videoCmd.fromId, gson.fromJson(videoCmd.cmdContent, IceCandidate.class));
        } else if (sendType == MSG_TYPE_SWAP_SDP) {
            byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
            System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
            String msgContent = new String(msgData, "UTF-8");
            VideoCmd videoCmd = gson.fromJson(msgContent, VideoCmd.class);
            // msgCache.add(msgContent);
            Log.e(TAG, "receive cmd MSG_TYPE_SWAP_SDP:" + msgContent);
            notifySwapSdp(videoCmd.fromId, gson.fromJson(videoCmd.cmdContent, SessionDescription.class));
        } else if (sendType == MSG_TYPE_OFFER_SDP) {
            byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
            System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
            String msgContent = new String(msgData, "UTF-8");
            VideoCmd videoCmd = gson.fromJson(msgContent, VideoCmd.class);
            // msgCache.add(msgContent);
            Log.e(TAG, "receive cmd MSG_TYPE_OFFER_SDP:" + msgContent);
            notifyOfferSdp(videoCmd.fromId, gson.fromJson(videoCmd.cmdContent, SessionDescription.class));
        }
    }

    @Override
    public int notify(String fromId, String msgContent) {
        LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_MSG_RECEIVE, fromId + msgContent);
        return 0;
    }

    @Override
    public int notifyIce(String formId, IceCandidate iceCandidate) {
        LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_MSG_RECEIVE, iceCandidate);
        return 0;
    }

    @Override
    public int notifySwapSdp(String formId, SessionDescription sdp) {
        LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_MSG_RECEIVE, sdp);
        return 0;
    }

    @Override
    public int notifyOfferSdp(String formId, SessionDescription sdp) {
        LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_SDP_OFFER_RECEIVE, sdp);
        return 0;
    }



}
