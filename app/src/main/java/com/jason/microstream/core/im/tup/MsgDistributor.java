package com.jason.microstream.core.im.tup;

import static com.jason.microstream.MainActivity1_.TAGT;
import static com.jason.microstream.core.im.tup.Coder.MSG_TYPE_REQUEST;
import static com.jason.microstream.core.im.tup.Coder.MSG_TYPE_SIZE;

import com.google.gson.Gson;
import com.jason.microstream.Tool;
import com.jason.microstream.core.im.imconpenent.ImService;
import com.jason.microstream.core.im.reqresp.RequestCore;
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
//                msgNotifier.handleData(msgData, action, msgType);
                handleData(msgData, action, msgType);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        return ret;
    }


    public void handleData(byte[] msgModeData, int action, int msgType) throws UnsupportedEncodingException { //data
        int sendType = Tool.byte4ToInt(msgModeData, 0);

        if (sendType == MSG_TYPE_REQUEST) {
            byte[] msgData = new byte[msgModeData.length - MSG_TYPE_SIZE];
            System.arraycopy(msgModeData, MSG_TYPE_SIZE, msgData, 0, msgData.length);
            String msgContent = new String(msgData, "UTF-8");
            RequestCore.get().handleResponse(msgContent);
        } else {
            msgNotifier.handleData(msgModeData, action, msgType);
        }
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


}
