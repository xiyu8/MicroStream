package com.jason.microstream.core.im.im_mode.msg;

import java.io.IOException;

public interface ImSendCallback {

    void onSendSuccess(TextMsg textMsg);

    void onSendFail(TextMsg textMsg, IOException e);

}
