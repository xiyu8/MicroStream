package com.jason.microstream.core.im.reqresp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jason.microstream.core.im.reqresp.data.BaseReqBean;
import com.jason.microstream.core.im.reqresp.data.BaseRespBean;

public class MsRequester<RP> implements DataParser {
    //todo:修改，ReqWrapper不该出现在此处
    private ReqWrapper reqWrapper;
    private String apiChar;
    private RP resp;
    private Gson gson;
    private ReqCallback<RP> callBack;
    private Class<RP> clazz;
    public MsRequester(BaseReqBean req,String apiChar) {
        if (req == null) {
            throw new RuntimeException("null of request!!!");
        }
        gson = new Gson();
        reqWrapper = new ReqWrapper();
        reqWrapper.apiChar = apiChar;
        reqWrapper.original = req;

        reqWrapper.requestContent = gson.toJson(req);
    }


    public void request(Class<RP> clazz, ReqCallback<RP> callBack) {
        this.callBack = callBack;
        this.clazz = clazz;
        requestImp();
    }
    private void requestImp() {
        if (reqWrapper == null || reqWrapper.requestContent == null) {
            if (callBack != null)
                callBack.onFail(new RuntimeException("ms request with null!"), null);
            return;
        }
        if (callBack == null) {
            callBack = new CallbackAdapter();
        }

        RequestCore.get().send(reqWrapper, this);
    }

    @Override
    public void parseData(RespWrapper respWrapper) {
        String apiChar = respWrapper.apiChar;
        String responseContent = respWrapper.respContent;
        if (responseContent != null) {
            resp = gson.fromJson(responseContent, clazz);
            callBack.onSuccess(resp);
        } else {
            onFail(new RuntimeException("null of response data!"));
        }
    }

    @Override
    public void onFail(Exception e) {
        callBack.onFail(e, reqWrapper);
    }


    //default callback
    private final class CallbackAdapter implements ReqCallback<RP>{

        @Override
        public void onSuccess(RP rp) {

        }

        @Override
        public void onFail(Exception exception, ReqWrapper req) {

        }
    }

}
