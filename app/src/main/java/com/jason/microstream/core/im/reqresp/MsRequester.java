package com.jason.microstream.core.im.reqresp;

import com.google.gson.Gson;
import com.jason.microstream.core.im.reqresp.data.BaseReq;
import com.jason.microstream.core.im.reqresp.data.BaseResp;

public class MsRequester<RP> implements DataParser {
    //todo:修改，ReqWrapper不该出现在此处
    private ReqWrapper reqWrapper;
    private String apiChar;
    private RP resp;
    private Gson gson;
    private ReqCallback<RP> callBack;
    private Class clazz;
    public MsRequester(Object reqData, String apiChar) {
        if (reqData == null) {
            throw new RuntimeException("null of request!!!");
        }
        gson = new Gson();
        reqWrapper = new ReqWrapper();
        reqWrapper.apiChar = apiChar;
        reqWrapper.original = reqData;

        BaseReq baseReq = new BaseReq();
//        baseReq.uid = ;
//        baseReq.token = ;
//        baseReq.uuid =  ;
        if (reqData instanceof String) {
            baseReq.data = (String) reqData;
        } else {
            baseReq.data = gson.toJson(reqData);
        }
        reqWrapper.requestContent = gson.toJson(baseReq);
    }


    public void request(Class clazz, ReqCallback<RP> callBack) {
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


        BaseResp baseResp = gson.fromJson(responseContent, BaseResp.class);
        if (baseResp.errorCode == 0) {
            if (clazz == String.class) { //不解析，给业务层解析的情况
                callBack.onSuccess((RP) baseResp.data);
            } else {
                resp = (RP) gson.fromJson(baseResp.data, clazz);
                callBack.onSuccess(resp);
            }
        } else {
            callBack.onFail(new RuntimeException(((BaseResp) resp).errorCode + ":" + ((BaseResp) resp).errorCode)
                    ,reqWrapper);

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
