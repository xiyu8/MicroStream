package com.jason.microstream.core.im.tup;

import static com.jason.microstream.MainActivity1_.TAGT;

import com.google.gson.Gson;
import com.jason.microstream.core.im.imconpenent.ImService;
import com.jason.microstream.core.im.tup.channelcontext.ChannelContext;
import com.jason.microstream.core.im.tup.data.msg.LoginRet;
import com.jason.microstream.core.im.tup.joint.MsgNotifier;
import com.jason.microstream.tool.log.LogTool;

import java.io.UnsupportedEncodingException;

public class MsgDistributor {

    public static final String CMD_TAG = TAGT+"VIDEO CMD";
    public static final String TAG = MsgDistributor.class.getSimpleName();

    MsgNotifier msgNotifier;
    Gson gson;

    Demultiplexer demultiplexer;
    public MsgDistributor(Demultiplexer demultiplexer) {
        this.gson = new Gson();
        this.demultiplexer = demultiplexer;
        this.msgNotifier = Core.getCore().getMsgNotifier();
    }

    public int distribute(ChannelContext channelContext, boolean isAck, byte[] msgData, int action, int msgType) {
        if (isAck) {
            return -1;
        }
        int ret = -1;

        try {
            ret = distributeImp(channelContext, msgData, action, msgType);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    public int distributeImp(ChannelContext channelContext, byte[] msgData, int action, int msgType) throws UnsupportedEncodingException {
        int ret = -1;
        if (action == Core.ACTION_AUTH) { // auth
            handleAuth(channelContext, msgData);
            return 0;
        } else if (action == Core.ACTION_BEAT) { //heart
            System.out.println("heart beat");
        } else if (action == Core.ACTION_DISCONNECT) { //disconnect ?????业务层的未处理
            demultiplexer.handleCloseConnect(channelContext,channelContext.getSocket());
        } else if (action == Core.ACTION_DATA) { //data
            try {
                msgNotifier.handleData(msgData, action, msgType);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        return ret;
    }

    private void handleAuth(ChannelContext channelContext, byte[] msgData) { // auth
        String authRetData;
        LogTool.e(TAG, "distributeImp__Sender.ACTION_AUTH");
        try {
            authRetData = new String(msgData, 4, msgData.length - 4, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            ImService.getIm().setAuthed(false, null, null);
            return;
        }
        LoginRet loginRet = gson.fromJson(authRetData, LoginRet.class);
        if(loginRet != null) LogTool.e(TAG, "distributeImp__loginRet:"
                + "-loginRet.authRet:" + loginRet.authRet
                + "-loginRet.uid:" + loginRet.uid
                + "-loginRet.token:" + loginRet.token
        );
        if (loginRet != null && loginRet.authRet != null && loginRet.authRet.equals("success")) {
            ImService.getIm().setAuthed(true, loginRet.uid, loginRet.token);
        } else {
            ImService.getIm().setAuthed(false, loginRet == null ? null : loginRet.uid, loginRet == null ? null : loginRet.token);

        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int MSG_TYPE_SIZE = 4;
    public static final int MSG_TYPE_TEST = 1;
    public static final int MSG_TYPE_REGISTER = 1;
    public static final int MSG_TYPE_SWAP_ICE = 102;
    public static final int MSG_TYPE_SWAP_SDP = 103;
    public static final int MSG_TYPE_OFFER_SDP = 104;
    private static final int MSG_TYPE_ID_SIZE = 32;


}
