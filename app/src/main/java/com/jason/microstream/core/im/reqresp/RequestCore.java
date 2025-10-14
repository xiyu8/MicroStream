package com.jason.microstream.core.im.reqresp;

import com.google.gson.Gson;
import com.jason.microstream.core.im.tup.Core;
import com.jason.microstream.core.im.tup.data.SendNode;
import com.jason.microstream.tool.log.LogTool;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RequestCore implements RequestTimeoutI{
    private final static String TAG = RequestCore.class.getSimpleName();
    private static volatile RequestCore requestCore;
    private Gson gson;
    private RequestMonitor requestMonitor;

    private RequestCore() {
        gson = new Gson();
        requestMonitor = new RequestMonitor(this);
    }

    public static RequestCore get() {
        if (requestCore == null) {
            synchronized (RequestCore.class) {
                if (requestCore == null) {
                    requestCore = new RequestCore();
                }
            }
        }
        return requestCore;
    }


    public void send(ReqWrapper reqWrapper, DataParser parser) {
        reqWrapper.reqId = genReqId();

        RequestMonitor.Task task = new RequestMonitor.Task(reqWrapper, parser, System.currentTimeMillis() + requestMonitor.TIME_OUT);
        requestMonitor.waitingRespMap.put(reqWrapper.reqId, task);
        requestMonitor.addTask(task);

        Core.getCore().sendRequest(reqWrapper, new SendNode.SendCallback() {
            @Override
            public void onSendSuccess(SendNode node) {

            }

            @Override
            public void onSendFailed(IOException e, SendNode node) {
                handleFail(reqWrapper, e);
            }
        });
    }

    private Set<Long> idsSet = new HashSet<>();
    private long genReqId() {
        long id = System.currentTimeMillis();
        while (idsSet.contains(id)) {
            id++;
        }
        idsSet.add(id);
        return id;
    }

    public boolean handleResponse(String msgContent) {
        RespWrapper respWrapper = gson.fromJson(msgContent, RespWrapper.class);

        RequestMonitor.Task task = requestMonitor.waitingRespMap.get(respWrapper.reqId);
        if (task != null && requestMonitor.removeTask(task))  {
            requestMonitor.waitingRespMap.remove(respWrapper.reqId);
            task.parser.parseData(respWrapper);
        }else {
            LogTool.e(TAG, "handleResponse-!!!:"
                    + "-task.req.reqId:" + (task == null ? null : task.reqWrapper.reqId)
            );
        }
        return true;
    }

    public boolean handleFail(ReqWrapper reqWrapper, Exception exception) {
        RequestMonitor.Task failTask = requestMonitor.waitingRespMap.get(reqWrapper.reqId);
        if (failTask != null && requestMonitor.removeTask(failTask)) {
            requestMonitor.waitingRespMap.remove(reqWrapper.reqId);
            LogTool.e(TAG, "handleFail:"
                    + "-exception:" + exception.getMessage()
                    + "-task.req.reqId:" + failTask.reqWrapper.reqId
            );
            failTask.parser.onFail(exception);
        } else {
            LogTool.e(TAG, "handleFail:"
                    + "-task.req.reqId:" + (failTask == null ? null : failTask.reqWrapper.reqId)
            );
        }
        return true;
    }

    @Override
    public void handleTimeout(RequestMonitor.Task task) {
            task.parser.onFail(new IOException("send wait ack timeout!"));
    }


}
