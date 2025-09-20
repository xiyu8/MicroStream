package com.jason.microstream.core.im.imconpenent;

import com.google.gson.Gson;
import com.jason.microstream.core.im.tup.joint.MsgNotifier;
import com.jason.microstream.localbroadcast.Events;
import com.jason.microstream.localbroadcast.LocBroadcast;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

public class MsgHandler implements MsgNotifier {


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
