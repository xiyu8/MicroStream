package com.jason.microstream.core.im.tup.joint;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

public interface MsgNotifier {

    int notify(String fromId,String msg);

    int notifyIce(String formId, IceCandidate fromJson);

    int notifySwapSdp(String formId, SessionDescription fromJson);

    int notifyOfferSdp(String formId, SessionDescription fromJson);
}
