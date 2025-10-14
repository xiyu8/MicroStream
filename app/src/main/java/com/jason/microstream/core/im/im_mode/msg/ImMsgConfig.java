package com.jason.microstream.core.im.im_mode.msg;

public class ImMsgConfig {

    public static class ImMsgType{
        public static final int TYPE_DEFAULT = 0;
        public static final int TYPE_TEXT = 1;
    }


    public static class SendState{
        public static final int SENDING = 0;
        public static final int SEND_FAIL= 1;
        public static final int SEND_SUCCESS= 2;
    }


}
