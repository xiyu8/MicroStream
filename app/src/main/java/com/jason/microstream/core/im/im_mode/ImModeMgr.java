package com.jason.microstream.core.im.im_mode;

import com.jason.microstream.core.im.im_mode.msg.ImMsgConfig;
import com.jason.microstream.core.im.im_mode.msg.ImSendCallback;
import com.jason.microstream.core.im.im_mode.msg.TextMsg;
import com.jason.microstream.core.im.tup.Coder;
import com.jason.microstream.core.im.tup.Core;
import com.jason.microstream.core.im.tup.data.SendNode;

import java.io.IOException;

public class ImModeMgr {

    private static ImModeMgr imManager;

    private ImModeMgr() {
    }

    public static ImModeMgr getImManager() {
        if (imManager == null) {
            synchronized (ImModeMgr.class) {
                if (imManager == null) {
                    imManager = new ImModeMgr();
                }
            }
        }
        return imManager;
    }

    public void sendTextMsg(TextMsg textMsg, ImSendCallback callback) {
        Core.getCore().sendTo(textMsg, Coder.MSG_TYPE_IM, new SendNode.SendCallback() {
            @Override
            public void onSendSuccess(SendNode node) {
                textMsg.state = ImMsgConfig.SendState.SEND_SUCCESS;
                callback.onSendSuccess(textMsg);
            }

            @Override
            public void onSendFailed(IOException e, SendNode node) {
                textMsg.state = ImMsgConfig.SendState.SEND_FAIL;
                callback.onSendFail(textMsg,e);
            }
        });

    }



}
