//package com.jason.microstream.manager.call;
//
//import android.os.Handler;
//import android.os.Looper;
//import android.text.TextUtils;
//import android.view.View;
//
//import com.jason.microstream.tackle.DeviceManager;
//import com.jason.microstream.tackle.LogTool;
//
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.Map;
//import java.util.Queue;
//
//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
//import io.reactivex.rxjava3.annotations.NonNull;
//import io.reactivex.rxjava3.core.Observable;
//import io.reactivex.rxjava3.functions.Consumer;
//
//public class CallMgr {
//    private final String TAG = CallMgr.class.getSimpleName();
//
//    private static final CallMgr mInstance = new CallMgr();
//
//    private Map<Integer, CallSession> callSessionMap = new HashMap<Integer, CallSession>();
//
//    private ICallNotification mCallNotification;
//
//    /**
//     * Call Bell Sound handle
//     * 呼叫铃音句柄
//     */
//    private int ringingToneHandle = -1;
//
//    /**
//     * Ring back tone handle
//     * 回铃音句柄
//     */
//    private int ringBackToneHandle = -1;
//
//    /**
//     * 是否恢复转会议通话
//     */
//    private boolean resumeHold = false;
//
//    /**
//     * 普通通话呼叫ID，用于通话转会议失败之后，恢复原通话
//     */
//    private int originalCallId = 0;
//
//    private int mCallId = 0;
//    public boolean isCallOut = false;
//    public boolean isJustCall = false;
//
//    private CallMgr() {
//        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
//            handler = new Handler();
//            audioHandler = new Handler();
//        } else {
//            Observable.just(0).observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<Integer>() {
//                        @Override
//                        public void accept(@NonNull Integer integer) throws Exception {
//                            handler = new Handler();
//                            audioHandler = new Handler();
//                        }
//                    });
//        }
//    }
//
//    public int getCallId() {
//        return mCallId;
//    }
//
//    public static CallMgr getInstance() {
//        return mInstance;
//    }
//
//    public boolean isResumeHold() {
//        return resumeHold;
//    }
//
//    public void setResumeHold(boolean resumeHold) {
//        this.resumeHold = resumeHold;
//    }
//
//    public int getOriginal_CallId() {
//        return originalCallId;
//    }
//
//    public void setOriginal_CallId(int original_CallId) {
//        this.originalCallId = original_CallId;
//    }
//
//    /**
//     * This method is used to store call session
//     *
//     * @param session 会话信息
//     */
//    public void putCallSessionToMap(CallSession session) {
//        callSessionMap.put(session.getCallId(), session);
//    }
//
//    /**
//     * This method is used to remove call information
//     *
//     * @param session 会话信息
//     */
//    public void removeCallSessionFromMap(CallSession session) {
//        callSessionMap.remove(session.getCallId());
//    }
//
//    /**
//     * This method is used to get call information by ID
//     *
//     * @param callID 呼叫id
//     * @return CallSession          会话信息
//     */
//    public CallSession getCallSessionByCallID(int callID) {
//        return callSessionMap.get(callID);
//    }
//
//    /**
//     * This method is used to Video Destroy.
//     * 释放视频资源
//     */
//    public void videoDestroy() {
//        VideoMgr.getInstance().clearCallVideo();
//    }
//
//    /**
//     * This method is used to gets video device.
//     * 获取视频设备
//     *
//     * @return the video device
//     */
//    public VideoMgr getVideoDevice() {
//        return VideoMgr.getInstance();
//    }
//
//    @Override
//    public void regCallServiceNotification(ICallNotification callNotification) {
//        this.mCallNotification = callNotification;
//    }
//
//    /**
//     * This method is used to set the default audio output device
//     * 设置默认的音视频路由
//     *
//     * @param isVideoCall
//     */
//    public void setDefaultAudioRoute(boolean isVideoCall) {
//        //获取移动音频路由设备
//        TsdkMobileAuidoRoute currentAudioRoute = TsdkManager.getInstance().getCallManager().getMobileAudioRoute();
//
//        if (isVideoCall) {
//            //如果当前是听筒，则切换默认设备为杨声器
//            if (currentAudioRoute == TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_EARPIECE) {
//                //This method is used to set mobile audio route
//                //设置移动音频路由设备
//                TsdkManager.getInstance().getCallManager().setMobileAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_LOUDSPEAKER);
//            }
//        } else {
//            //This method is used to set mobile audio route
//            //设置移动音频路由设备
//            TsdkManager.getInstance().getCallManager().setMobileAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT);
//        }
//    }
//
//    public TsdkCallStreamInfo getCallQuality(int callid) {
//        TsdkCallStreamInfo mediaQosInfos = TsdkManager.getInstance().getCallManager().getCallStreamInfo(callid);
//        return mediaQosInfos;
//    }
//
//    /**
//     * This method is used to configure Call Parameters
//     */
//    @Override
//    public void configCallServiceParam() {
//        //Optional
//    }
//
//    /**
//     * This method is used to switching audio routing devices
//     * 切换音频路由设备
//     *
//     * @return
//     */
//    @Override
//    public int switchAudioRoute() {
//        //获取移动音频路由设备
//        int audioRoute = getCurrentAudioRoute();
//        LogTool.i(TAG, "audioRoute is" + audioRoute);
//
//        if (audioRoute == TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_LOUDSPEAKER.getIndex()) {
//            setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT);
//            LogTool.i(TAG, "set telReceiver Success");
//            return TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT.getIndex();
//        } else {
//            //设置移动音频路由设备
//            //set up a mobile audio routing device
//            setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_LOUDSPEAKER);
//
//            //设置扬声器输出音量大小
//            //set speaker output Volume size
//            int setMediaSpeakVolumeResult = TsdkManager.getInstance().getCallManager().setSpeakVolume(60);
//            LogTool.i(TAG, "setMediaSpeakVolumeResult" + setMediaSpeakVolumeResult);
//            return TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_LOUDSPEAKER.getIndex();
//        }
//    }
//
//    /**
//     * This method is used to get mobile audio route
//     * <p>
//     * 获取移动音频路由设备
//     *
//     * @return the audio route
//     */
//    @Override
//    public int getCurrentAudioRoute() {
//        if (TsdkManager.getInstance() == null)
//            return -1;
//        if (TsdkManager.getInstance().getCallManager() == null)
//            return -1;
//
//        if (null == TsdkManager.getInstance().getCallManager().getMobileAudioRoute()) {
//            LogTool.e(TAG, "getMobileAudioRoute is null");
//            return -1;
//        }
//        return TsdkManager.getInstance().getCallManager().getMobileAudioRoute().getIndex();
//    }
//
//    /**
//     * This method is used to get speak volume of media.
//     * 获取扬声器输出音量大小
//     *
//     * @return the media speak volume
//     */
//    private int getMediaSpeakVolume() {
//        int ret = TsdkManager.getInstance().getCallManager().getSpeakVolume();
//        return ret;
//    }
//
//    /**
//     * This method is used to get call status
//     * 获取呼叫状态
//     *
//     * @param callID 呼叫id
//     * @return
//     */
//    @Override
//    public CallConstant.CallStatus getCallStatus(int callID) {
//        CallSession callSession = getCallSessionByCallID(callID);
//        if (callSession == null) {
//            return CallConstant.CallStatus.UNKNOWN;
//        }
//
//        return callSession.getCallStatus();
//    }
//
//
//    /**
//     * This method is used to make call or make video call
//     * 创建一个音频或者视频呼叫
//     *
//     * @param toNumber    呼叫号码
//     * @param isVideoCall 是否是视频
//     * @return int 0 success
//     */
//    @Override
//    public synchronized int startCall(String toNumber, boolean isVideoCall) {
//        if (TextUtils.isEmpty(toNumber)) {
//            LogTool.e(TAG, "call number is null!");
//            return 0;
//        }
//        String displayName = "";
//
//        //创建一路呼叫
//        TsdkCall call = TsdkManager.getInstance().getCallManager().startCall(toNumber, isVideoCall, displayName);
//        if (call != null) {
//            CallSession newSession = new CallSession(call);
//            putCallSessionToMap(newSession);
//
//            setDefaultAudioRoute(isVideoCall);
//            if (isVideoCall) {
//                newSession.initVideoWindow();
//            }
//
//            LogTool.i(TAG, "make call is success.");
//
//            isJustCall = (call.getCallInfo().getConfId() == null || call.getCallInfo().getConfId().equals(""))
//                    && (!call.getCallInfo().getPeerNumber().startsWith(VCConfig.confAccessCodePrefix));
//            return call.getCallInfo().getCallId();
//        }
//
//        LogTool.e(TAG, "make call is failed.");
//        return 0;
//    }
//
//
//    /**
//     * This method is used to answer incoming call
//     * 接听一路呼叫
//     *
//     * @param callID  呼叫id
//     * @param isVideo 是否是视频
//     * @return true:success, false:failed
//     */
//    @Override
//    public boolean answerCall(int callID, boolean isVideo) {
//        CallSession callSession = getCallSessionByCallID(callID);
//        if (callSession == null) {
//            return false;
//        }
//
//        isJustCall = (callSession.getTsdkCall().getCallInfo().getConfId() == null || callSession.getTsdkCall().getCallInfo().getConfId().equals(""))
//                && (!callSession.getTsdkCall().getCallInfo().getPeerNumber().startsWith(VCConfig.confAccessCodePrefix));
//        return callSession.answerCall(isVideo);
//    }
//
//    /**
//     * This method is used to reject or hangup call
//     * 结束呼叫
//     *
//     * @param callID 呼叫id
//     * @return true:success, false:failed
//     */
//    @Override
//    public boolean endCall(int callID) {
//        if (TsdkManager.getInstance().getCallManager() == null) return false;
//        TsdkCall tsdkCall = TsdkManager.getInstance().getCallManager().getCallByCallId(callID);
//        if (null == tsdkCall) {
//            mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_ENDED_FAILED, null, 0);
//            return false;
//        }
//
//        int result = tsdkCall.endCall();
//        if (result != 0) {
//            LogTool.e(TAG, "endCall return failed, result = " + result);
//            return false;
//        }
//
//        return true;
//    }
//
//    /**
//     * This method is used to hold the video Call
//     * 保持一路视频呼叫
//     *
//     * @param callID 呼叫id
//     * @return
//     */
//    @Override
//    public boolean holdVideoCall(int callID) {
//        CallSession callSession = getCallSessionByCallID(callID);
//        if (callSession == null) {
//            return false;
//        }
//
//        //视频保持先移除视频，待视频移除成功后，再保持
//        boolean result = callSession.delVideo();
//        if (result) {
//            callSession.setVideoHold(true);
//        }
//
//        return result;
//    }
//
//    /**
//     * This method is used to send DTMF tone
//     * 二次拨号
//     *
//     * @param callID 呼叫id55
//     * @param code   （0到9，*为10,#为11）
//     * @return true:success, false:failed
//     */
//    @Override
//    public boolean reDial(int callID, int code) {
//        CallSession callSession = getCallSessionByCallID(callID);
//        if (callSession == null) {
//            return false;
//        }
//
//        return callSession.reDial(code);
//    }
//
//    /**
//     * This method is used to request change from an audio call to a video call
//     * 音频转视频
//     *
//     * @param callID 呼叫id
//     * @return true:success, false:failed
//     */
//    @Override
//    public boolean addVideo(int callID) {
//        CallSession callSession = getCallSessionByCallID(callID);
//        if (callSession == null) {
//            return false;
//        }
//
//        return callSession.addVideo();
//    }
//
//    /**
//     * This method is used to request a change from a video call to an audio call
//     * 视频转音频
//     *
//     * @param callID 呼叫id
//     * @return true:success, false:failed
//     */
//    @Override
//    public boolean delVideo(int callID) {
//        CallSession callSession = getCallSessionByCallID(callID);
//        if (callSession == null) {
//            return false;
//        }
//
//        return callSession.delVideo();
//    }
//
//    /**
//     * This method is used to reject change from an audio call to a video call
//     * 拒绝音频转视频请求
//     *
//     * @param callID 呼叫id
//     * @return true:success, false:failed
//     */
//    @Override
//    public boolean rejectAddVideo(int callID) {
//        CallSession callSession = getCallSessionByCallID(callID);
//        if (callSession == null) {
//            return false;
//        }
//
//        return callSession.rejectAddVideo();
//    }
//
//    /**
//     * This method is used to accept change from an audio call to a video call
//     * 接受音频转视频请求
//     *
//     * @param callID 呼叫id
//     * @return true:success, false:failed
//     */
//    @Override
//    public boolean acceptAddVideo(int callID) {
//        CallSession callSession = getCallSessionByCallID(callID);
//        if (callSession == null) {
//            return false;
//        }
//
//        boolean result = callSession.acceptAddVideo();
//        if (result) {
//            setDefaultAudioRoute(true);
//            callSession.setCallStatus(CallConstant.CallStatus.VIDEO_CALLING);
//
//            CallInfo callInfo = getCallInfo(callSession.getTsdkCall());
//            mCallNotification.onCallEventNotify(CallConstant.CallEvent.OPEN_VIDEO, callInfo, 0);
//        }
//
//        return result;
//    }
//
//
//    public boolean isMuteMic() {
//        return isMuteMic;
//    }
//
//    /**
//     * This method is used to set whether mute the microphone
//     * 设置麦克风静音
//     *
//     * @param callID 呼叫id
//     * @param mute   是否静音
//     * @return true:success, false:failed
//     */
//    boolean isMuteMic = false;
//
//    @Override
//    public boolean muteMic(int callID, boolean mute) {
//        CallSession callSession = getCallSessionByCallID(callID);
//        if (callSession == null) {
//            return false;
//        }
//        boolean ret = callSession.muteMic(mute);
//        if (ret) {
//            isMuteMic = mute;
//        }
//        return ret;
//    }
//
//    /**
//     * This method is used to set whether mute the speaker
//     * 设置扬声器静音
//     *
//     * @param callID 呼叫id
//     * @param mute   是否静音
//     * @return true:success, false:failed
//     */
//    @Override
//    public boolean muteSpeak(int callID, boolean mute) {
//        CallSession callSession = getCallSessionByCallID(callID);
//        if (callSession == null) {
//            return false;
//        }
//
//        return callSession.muteSpeak(mute);
//    }
//
//    /**
//     * This method is used to Local preview
//     * 本地预览
//     *
//     * @param callID
//     * @param visible
//     */
//    @Override
//    public void switchLocalView(int callID, boolean visible) {
//
//    }
//
//    /**
//     * This method is used to switch camera
//     * 切换摄像头
//     *
//     * @param callID      呼叫ID
//     * @param cameraIndex 摄像头下标
//     */
//    @Override
//    public void switchCamera(int callID, int cameraIndex) {
//        TsdkCall call = TsdkManager.getInstance().getCallManager().getCallByCallId(callID);
//        VideoMgr.getInstance().switchCamera(call, cameraIndex);
//
//    }
//
//
//    /**
//     * This method is used to open camera
//     * 打开摄像头
//     *
//     * @param callID 呼叫id
//     */
//    public void operateCamera(boolean isOpen, int callID) {
//        operateCameraWithIndex(isOpen, callID, CallConstant.FRONT_CAMERA);
//    }
//
//    Queue<CameraOperation> cameraOperationQueue = new LinkedList<>();
//    boolean isRun = false;
//
//    public void operateCameraWithIndex(boolean isOpen, int callID, int cameraIndex) {
//        cameraOperationQueue.offer(new CameraOperation(isOpen, callID, cameraIndex));
//        L.e("operateCameraWithIndex+" + isOpen + callID + cameraIndex);
////
////    if (!isRun) {
//        CameraOperation cameraOperation = cameraOperationQueue.poll();
////      if (cameraOperation == null) return;
////      TsdkCall call = TsdkManager.getInstance().getCallManager().getCallByCallId(callID);
//        operateCameraWithIndexImp(cameraOperation);
////
////      isRun = true;
////      if(handler!=null)
////        handler.postDelayed(task, 1000);
////      else
////        isRun = false;
////    }
//    }
//
//    Runnable task = new Runnable() {
//        @Override
//        public void run() {
//            CameraOperation cameraOperation = cameraOperationQueue.poll();
//            if (cameraOperation == null) {
//                isRun = false;
//                return;
//            }
//            operateCameraWithIndexImp(cameraOperation);
//            if (handler != null)
//                handler.postDelayed(task, 1000);
//            else
//                isRun = false;
//        }
//    };
//
//    private Handler handler;
//
//    public void operateCameraWithIndexImp(CameraOperation cameraOperation) {
//        TsdkCall call = TsdkManager.getInstance().getCallManager().getCallByCallId(cameraOperation.callId);
//        if (cameraOperation.isOpen) {
//            VideoMgr.getInstance().openCameraWithIndex(call, cameraOperation.cameraIndex);
//            VideoMgr.getInstance().getLocalVideoView().setVisibility(View.VISIBLE);
//        } else VideoMgr.getInstance().closeCamera(call);
//    }
//
//    /**
//     * This method is used to play ringing tone
//     * 播放铃音
//     *
//     * @param ringingFile 音频文件路径
//     */
//    @Override
//    public void startPlayRingingTone(String ringingFile) {
//        int result;
////        TupCallManager callManager = TupMgr.getInstance().getCallManagerIns();
//
//        //处理可能的异常
//        if (ringingToneHandle != -1) {
//            result = TsdkManager.getInstance().getCallManager().stopPlayMedia(ringingToneHandle);
//            if (result != 0) {
//                LogTool.e(TAG, "mediaStopplay is return failed, result = " + result);
//            }
//        }
//
//        //振铃默认使用扬声器播放
//        //Ringing by default using speaker playback
//        TsdkManager.getInstance().getCallManager().setMobileAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_LOUDSPEAKER);
//
//        //播放指定振铃
//        //Play the specified ringing
//        ringingToneHandle = TsdkManager.getInstance().getCallManager().startPlayMedia(0, ringingFile);
//        if (ringingToneHandle == -1) {
//            LogTool.e(TAG, "mediaStartplay is return failed.");
//        }
//    }
//
//    /**
//     * This method is used to stop play ringing tone
//     * 停止播放铃音
//     */
//    @Override
//    public void stopPlayRingingTone() {
//        if (ringingToneHandle != -1) {
//            int result = TsdkManager.getInstance().getCallManager().stopPlayMedia(ringingToneHandle);
//            if (result != 0) {
//                LogTool.e(TAG, "mediaStopPlay is return failed, result = " + result);
//            }
//            ringingToneHandle = -1;
//        }
//    }
//
//    /**
//     * This method is used to play ring back tone
//     * 播放回铃音
//     *
//     * @param ringingFile 音频文件路径
//     */
//    @Override
//    public void startPlayRingBackTone(String ringingFile) {
//        int result;
//
//        //处理可能的异常
//        if (ringBackToneHandle != -1) {
//            result = TsdkManager.getInstance().getCallManager().stopPlayMedia(ringBackToneHandle);
//            if (result != 0) {
//                LogTool.e(TAG, "mediaStopPlay is return failed, result = " + result);
//            }
//        }
//
//        //回铃音使用默认设备播放
//        //Ring tone Use default device playback
//        TsdkManager.getInstance().getCallManager().setMobileAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT);
//
//        //播放指定回铃音
//        //Play the specified ring tone
//        ringBackToneHandle = TsdkManager.getInstance().getCallManager().startPlayMedia(0, ringingFile);
//        if (ringBackToneHandle == -1) {
//            LogTool.e(TAG, "mediaStartPlay is return failed.");
//        }
//    }
//
//    /**
//     * This method is used to stop play ring back tone
//     * 停止播放回铃音
//     */
//    @Override
//    public void stopPlayRingBackTone() {
//        if (ringBackToneHandle != -1) {
//            int result = TsdkManager.getInstance().getCallManager().stopPlayMedia(ringBackToneHandle);
//            if (result != 0) {
//                LogTool.e(TAG, "mediaStopPlay is return failed, result = " + result);
//            }
//            ringBackToneHandle = -1;
//        }
//    }
//
//    /**
//     * |
//     * This method is used to  request anonymous call
//     * 发起匿名呼叫
//     *
//     * @param anonymousnumber [en] register anonymous  number
//     *                        [cn]注册匿名号码
//     * @param calleeNumber    [en] call number
//     *                        [cn]呼叫码号
//     * @param isVideo         [en] is video call
//     *                        [cn] 是否是视频呼叫
//     * @param primaryIp       [en] server primary ip
//     *                        [cn] 主服务器IP地址
//     * @param primaryport     [en] server primary port
//     *                        [cn]主服务器端口
//     * @param backupIp1       [en] server backup ip1
//     *                        [cn] 服务器备份ip1
//     * @param backupport1     [en] server backup port1
//     *                        [cn] 服务器备份端口port1
//     * @param backupIp2       [en] server backup ip2
//     *                        [cn]服务器备份ip2
//     * @param backupport2     [en]server backup port2
//     *                        [cn]服务器备份端口port2
//     * @param backupIp3       [en]server backup ip3
//     *                        [cn]服务器备份ip3
//     * @param backupport3     [en]server backup port3
//     *                        [cn]服务器备份端口port3
//     * @param sipport         [en] sip port
//     *                        [cn] sip端口
//     * @param isVPN           [en] is vpn
//     *                        [cn] 是否是vpn方式
//     * @return
//     */
//    @Override
//    public int startAnonymousCall(String anonymousnumber, String calleeNumber, boolean isVideo, String primaryIp, int primaryport,
//                                  String backupIp1, int backupport1,
//                                  String backupIp2, int backupport2,
//                                  String backupIp3, int backupport3,
//                                  int sipport, boolean isVPN) {
//        if (TextUtils.isEmpty(calleeNumber)) {
//            LogTool.e(TAG, "call number is null!");
//            return 0;
//        }
//        //设置本端IP
//        String localIpAddress = DeviceManager.getLocalIpAddress(isVPN);
//        TsdkLocalAddress localAddress = new TsdkLocalAddress(localIpAddress);
//        TsdkManager.getInstance().setConfigParam(localAddress);
//
//        //设置ipcallswitch
//        TsdkManager.getInstance().setConfigParam(1);
//
//        //设置sip端口号
//        TsdkManager.getInstance().setConfigParam(new TsdkSipParam(sipport));
//        //设置服务器地址、端口和备份地址、端口
//        TsdkServerRegAddressParam.ServerRegPrimary serverRegPrimary = new TsdkServerRegAddressParam.ServerRegPrimary(primaryIp, primaryport);
//        TsdkServerRegAddressParam.ServerRegPrimary backup1 = new TsdkServerRegAddressParam.ServerRegPrimary(backupIp1, backupport1);
//        TsdkServerRegAddressParam.ServerRegPrimary backup2 = new TsdkServerRegAddressParam.ServerRegPrimary(backupIp2, backupport2);
//        TsdkServerRegAddressParam.ServerRegPrimary backup3 = new TsdkServerRegAddressParam.ServerRegPrimary(backupIp3, backupport3);
//        TsdkManager.getInstance().setConfigParam(new TsdkServerRegAddressParam(serverRegPrimary, backup1, backup2, backup3));
//        //设置匿名号码
//        TsdkManager.getInstance().setConfigParam(new TsdkAnonymousCallParam(anonymousnumber));
//        //创建匿名呼叫
//        TsdkCall call = TsdkManager.getInstance().getCallManager().startAnonymousCall(calleeNumber, isVideo);
//        if (call != null) {
//            CallSession newSession = new CallSession(call);
//            putCallSessionToMap(newSession);
//
//            setDefaultAudioRoute(isVideo);
//            if (isVideo) {
//                newSession.initVideoWindow();
//            }
//
//            LogTool.i(TAG, "make anonymous call is success.");
//            return call.getCallInfo().getCallId();
//        }
//        LogTool.e(TAG, "make anonymous call is failed.");
//        return 0;
//    }
//
//
//    /**
//     * This method is used to get call information
//     * 获取呼叫信息
//     *
//     * @param call
//     * @return
//     */
//    private CallInfo getCallInfo(TsdkCall call) {
//        String peerNumber = call.getCallInfo().getPeerNumber();
//        String peerDisplayName = call.getCallInfo().getPeerDisplayName();
//        boolean isFocus = false;
//        boolean isVideoCall = false;
//        boolean isCaller = call.getCallInfo().getIsCaller() == 1 ? true : false;
//
//        if (call.getCallInfo().getIsFocus() == 1) {
//            isFocus = true;
//        }
//
//        if (call.getCallInfo().getIsVideoCall() == 1) {
//            isVideoCall = true;
//        }
//
//        return new CallInfo.Builder()
//                .setCallID(call.getCallInfo().getCallId())
//                .setConfID(call.getCallInfo().getConfId())
//                .setIsSvcCall(call.getCallInfo().getIsSvcCall())
//                .setPeerNumber(peerNumber)
//                .setPeerDisplayName(peerDisplayName)
//                .setVideoCall(isVideoCall)
//                .setFocus(isFocus)
//                .setCaller(isCaller)
//                .setReasonCode(call.getCallInfo().getReasonCode())
//                .build();
//    }
//
//
//    /**
//     * This method is used to sets audio route.
//     * 设置音频路由
//     *
//     * @param audioSwitch the audio switch
//     * @return the audio route
//     */
//    boolean isFirstRun = true;
//    TsdkMobileAuidoRoute callAudioRoute;
//
//    public boolean setAudioRoute(TsdkMobileAuidoRoute switchAudioRoute) {
////    isFirstRun = false;
////
////    auidoRouteQueue.offer(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT);
////    auidoRouteQueue.offer(switchAudioRoute);
////
////    if (!isAudioRouteRun) {
////      if (/*isFirstRun*/false) {
////        isAudioRouteRun = true;
////        audioHandler.postDelayed(audioRouteRunTask, 5000);
////      }else {
////
////      isAudioRouteRun = true;
////      TsdkMobileAuidoRoute audioRoute = auidoRouteQueue.poll();
////      new Thread(new Runnable() {
////        @Override
////        public void run() {
////          if (audioRoute == null) return ;
////
//        int ret = -1;
//        if (TsdkManager.getInstance().getCallManager() != null) {
//            callAudioRoute = switchAudioRoute;
//            ret = TsdkManager.getInstance().getCallManager().setMobileAudioRoute(switchAudioRoute);
//        }
//
//
//        //        }
////      }).start();
////
////
////      audioHandler.postDelayed(audioRouteRunTask, 1000);
////      }
////    }
//
//        return true;
//
//
//    }
//
//    Queue<TsdkMobileAuidoRoute> auidoRouteQueue = new LinkedList<>();
//    boolean isAudioRouteRun = false;
//    Runnable audioRouteRunTask = new Runnable() {
//        @Override
//        public void run() {
//            TsdkMobileAuidoRoute audioRoute = auidoRouteQueue.poll();
//            if (audioRoute == null) {
//                isAudioRouteRun = false;
//                return;
//            }
//
//            int ret = TsdkManager.getInstance().getCallManager().setMobileAudioRoute(audioRoute);
//
//            isAudioRouteRun = true;
//            if (audioHandler != null)
//                audioHandler.postDelayed(audioRouteRunTask, 1000);
//            else
//                isAudioRouteRun = false;
//        }
//    };
//
//    private Handler audioHandler;
//
//
//    /**
//     * This method is used to support video.
//     * 是否支持视频功能
//     *
//     * @return the boolean
//     */
//    private boolean isSupportVideo() {
//        return VideoMgr.getInstance().isSupportVideo();
//    }
//
//
//    /**************************************************************目前先保留 回调转换完删除 为了好找回调***************************************************************************/
//
//    /**
//     * [en]This method is used to handle the call incoming.
//     * [cn]处理来电事件
//     *
//     * @param call           [en]Indicates call info
//     *                       [cn]呼叫信息
//     * @param maybeVideoCall [en]Indicates maybe video call
//     *                       [cn]是否是视频
//     */
//    public void handleCallComing(TsdkCall call, Boolean maybeVideoCall) {
//        LogTool.i(TAG, "onCallComing");
//        if (null == call) {
//            LogTool.e(TAG, "onCallComing call is null");
//            return;
//        }
//        CallSession newSession = new CallSession(call);
//        newSession.setCallState(CallSession.CallState.COMING);
//        putCallSessionToMap(newSession);
//
//        CallInfo callInfo = getCallInfo(call);
//        this.mCallId = callInfo.getCallId();
//        callInfo.setMaybeVideoCall(maybeVideoCall);
//
//        isJustCall = (call.getCallInfo().getConfId() == null || call.getCallInfo().getConfId().equals(""))
//                && (!call.getCallInfo().getPeerNumber().startsWith(VCConfig.confAccessCodePrefix));
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_COMING, callInfo, 0);
//        MeetingMgr.getInstance().isJoining = true;
//    }
//
//    /**
//     * [en]This method is used to handle the call out going.
//     * [cn]处理呼出事件
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleCallGoing(TsdkCall call) {
//        LogTool.i(TAG, "onCallGoing");
//        if (null == call) {
//            LogTool.e(TAG, "tupCall obj is null");
//            return;
//        }
//        CallInfo callInfo = getCallInfo(call);
//        this.mCallId = callInfo.getCallId();
//        callInfo.setMaybeVideoCall(true);
//
//        CallSession newSession = new CallSession(call);
//        newSession.setCallState(CallSession.CallState.OUT_GOING);
//        putCallSessionToMap(newSession);
//        isCallOut = true;
//        isJustCall = (call.getCallInfo().getConfId() == null || call.getCallInfo().getConfId().equals(""))
//                && (!call.getCallInfo().getPeerNumber().startsWith(VCConfig.confAccessCodePrefix));
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_GOING, callInfo, 0);
//    }
//
//    /**
//     * [en]This method is used to handle call connected
//     * [cn]处理通话建立事件
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleCallConnected(TsdkCall call, int isRejoinConf) {
//        if (isRejoinConf != 1)
//            TsdkManager.getInstance().getCallManager().setMobileAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT);
//        LogTool.i(TAG, "onCallConnected");
//        if (null == call) {
//            LogTool.e(TAG, "call obj is null");
//            return;
//        }
//
//        CallInfo callInfo = getCallInfo(call);
//        CallSession callSession = getCallSessionByCallID(call.getCallInfo().getCallId());
//        if (callSession == null) {
//            LogTool.e(TAG, "call session obj is null");
//            callSession = new CallSession(call);
//            CallMgr.getInstance().putCallSessionToMap(callSession);
//        }
//        callSession.setCallState(CallSession.CallState.CONNECTED);
//
//        if (callInfo.isVideoCall()) {
//            if (isRejoinConf != 1) {
//                callSession.setCallStatus(CallConstant.CallStatus.VIDEO_CALLING);
//                VideoMgr.getInstance().closeCamera(call);
//            }
//        } else {
//            if (isRejoinConf != 1)
//                callSession.setCallStatus(CallConstant.CallStatus.AUDIO_CALLING);
//        }
//        this.mCallId = callInfo.getCallId();
//        if (isRejoinConf != 1) {
//            isJustCall = (call.getCallInfo().getConfId() == null || call.getCallInfo().getConfId().equals(""))
//                    && (!call.getCallInfo().getPeerNumber().startsWith(VCConfig.confAccessCodePrefix));
//            mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_CONNECTED, callInfo, isRejoinConf);
//            MeetingMgr.getInstance().isJoining = true;
//        }
//
//
//        if (isRejoinConf == 1) {
//            if (isJustCall) {
//                setAudioRoute(callAudioRoute);
//                muteMic(mCallId, isMuteMic);
//            } else {
//                setAudioRoute(callAudioRoute);
////              MeetingMgr.getInstance().
//                muteMic(mCallId, MeetingMgr.getInstance().selfIsMute);
////              MeetingMgr.getInstance().}
//            }
//            int cameraIndex = VideoMgr.getInstance().getCurrentCameraIndex();
//            if (cameraIndex == CallConstant.CAMERA_NON) {
//                VideoMgr.getInstance().closeCamera(call);
//            } else {
//                VideoMgr.getInstance().openCameraWithIndex(call, cameraIndex);
//            }
//        }
//    }
//
//    /**
//     * [en]This method is used to handle call ring back
//     * [cn]处理响铃音事件
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleCallRingback(TsdkCall call) {
//        LogTool.i(TAG, "onCallRingBack");
//        if (null == call) {
//            LogTool.e(TAG, "onCallRingBack call is null");
//            return;
//        }
//        if (null != mCallNotification) {
//            mCallNotification.onCallEventNotify(CallConstant.CallEvent.PLAY_RING_BACK_TONE, null, 0);
//        }
//    }
//
//    /**
//     * [en]This method is used to handle call end
//     * [cn]处理通话结束
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleCallEnded(TsdkCall call) {
//        LogTool.i(TAG, "onCallEnded");
//        if (null == call) {
//            LogTool.e(TAG, "onCallEnded call is null");
//            return;
//        }
//        CallInfo callInfo = getCallInfo(call);
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_ENDED, callInfo, 0);
//        MeetingMgr.getInstance().isJoining = false;
//        isJustCall = false;
//        isCallOut = false;
//    }
//
//    public void handleCallConnectedFail(TsdkCall call) {
//        LogTool.i(TAG, "onCallEnded");
//        if (null == call) {
//            LogTool.e(TAG, "onCallEnded call is null");
//            return;
//        }
//        CallInfo callInfo = getCallInfo(call);
//        isCallOut = false;
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_CONNECTED_FAIL, callInfo, 0);
//        MeetingMgr.getInstance().isJoining = false;
//        isJustCall = false;
//        isCallOut = false;
//    }
//
//    /**
//     * [en]This method is used to handle call end destroy
//     * [cn]处理呼叫销毁事件
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleCallDestroy(TsdkCall call) {
//        LogTool.i(TAG, "onCallDestroy");
//        if (null == call) {
//            LogTool.e(TAG, "call obj is null");
//            return;
//        }
////        CallInfo callInfo = getCallInfo(call);
////        mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_ENDED, callInfo);
//
//        CallSession callSession = getCallSessionByCallID(call.getCallInfo().getCallId());
//        if (callSession == null) {
//            LogTool.e(TAG, "call session obj is null");
//            return;
//        }
//
//        //从会话列表中移除一路会话
//        removeCallSessionFromMap(callSession);
//        MeetingMgr.getInstance().isJoining = false;
//        isJustCall = false;
//        isCallOut = false;
//    }
//
//    /**
//     * [en]This method is used to handle call rtp created.
//     * [cn]处理RTP创建事件
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleCallRtpCreated(TsdkCall call) {
//        LogTool.i(TAG, "onCallRTPCreated");
//        if (null == call) {
//            LogTool.e(TAG, "tupCall obj is null");
//            return;
//        }
//
//        CallInfo callInfo = getCallInfo(call);
//
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.RTP_CREATED, callInfo, 0);
//    }
//
//    /**
//     * [en]This method is used to handle call audio to video request.
//     * [cn]处理音频转视频请求
//     *
//     * @param call       [en]Indicates call info
//     *                   [cn]呼叫信息
//     * @param orientType [en]Indicates orient type
//     *                   [cn]视频显示方向类型
//     */
//    public void handleOpenVideoReq(TsdkCall call, TsdkVideoOrientation orientType) {
//        LogTool.i(TAG, "onCallAddVideo");
//        if (null == call) {
//            LogTool.e(TAG, "onCallAddVideo tupCall is null");
//            return;
//        }
//
//        //音频转视频
//        CallSession callSession = getCallSessionByCallID(call.getCallInfo().getCallId());
//        if (callSession == null) {
//            LogTool.e(TAG, "call session obj is null");
//            return;
//        }
//
//        CallConstant.CallStatus callStatus = callSession.getCallStatus();
//        boolean isSupportVideo = isSupportVideo();
//
//        if ((!isSupportVideo) || (CallConstant.CallStatus.AUDIO_CALLING != callStatus)) {
//            callSession.rejectAddVideo();
//            return;
//        }
//
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.RECEIVED_REMOTE_ADD_VIDEO_REQUEST, null, 0);
//
//    }
//
//    /**
//     * [en]This method is used to handle call audio to video request result.
//     * [cn]处理音频转视频结果
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleOpenVideoInd(TsdkCall call) {
//        int isVideo = call.getCallInfo().getIsVideoCall(); // 1:video, 0: audio
//        int callId = call.getCallInfo().getCallId();
//        LogTool.i(TAG, "isVideo: " + isVideo + "callId: " + callId);
//
//        CallSession callSession = getCallSessionByCallID(callId);
//        if (callSession == null) {
//            return;
//        }
//        CallInfo callInfo = getCallInfo(call);//audio --> video success
//        LogTool.i(TAG, "Upgrade To Video Call");
//        VideoMgr.getInstance().setVideoOrient(callId, CallConstant.FRONT_CAMERA);
//
//        callSession.setCallStatus(CallConstant.CallStatus.VIDEO_CALLING);
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.OPEN_VIDEO, callInfo, 0);
//    }
//
//    /**
//     * [en]This method is used to handle call video to audio request result
//     * [cn]处理视频转音频结果
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleCloseVideoInd(TsdkCall call) {
//        if (null == call) {
//            LogTool.e(TAG, "onCallDelVideo tupCall is null");
//            return;
//        }
//
//        CallSession callSession = getCallSessionByCallID(call.getCallInfo().getCallId());
//        if (callSession == null) {
//            LogTool.e(TAG, "call session obj is null");
//            return;
//        }
//
//        callSession.setCallStatus(CallConstant.CallStatus.AUDIO_CALLING);
//
//        //Clear video data
//        VideoMgr.getInstance().clearCallVideo();
//
//        if (null != mCallNotification) {
//            CallInfo callInfo = getCallInfo(call);
//            mCallNotification.onCallEventNotify(CallConstant.CallEvent.CLOSE_VIDEO, callInfo, 0);
//        }
//
////    if (callSession.isVideoHold()) {
////      callSession.holdCall();
////    }
//    }
//
//    /**
//     * [en]This method is used to handle call window refresh
//     * [cn]刷新窗口信息
//     *
//     * @param call        [en]Indicates call info
//     *                    [cn]呼叫信息
//     * @param refreshInfo [en]Indicates refresh Info
//     *                    [cn]刷新信息
//     */
//    public void handleRefreshViewInd(TsdkCall call, TsdkVideoViewRefresh refreshInfo) {
//        LogTool.i(TAG, "refreshLocalView");
//        TsdkVideoViewType mediaType = TsdkVideoViewType.enumOf(refreshInfo.getViewType());
//        TsdkVideoViewRefreshEvent eventType = TsdkVideoViewRefreshEvent.enumOf(refreshInfo.getEvent());
//        int callId = call.getCallInfo().getCallId();
//
//        switch (mediaType) {
//            case TSDK_E_VIEW_LOCAL_PREVIEW: //local video preview
//            case TSDK_E_VIEW_VIDEO_VIEW: //general video
//                if (eventType == TsdkVideoViewRefreshEvent.TSDK_E_VIDEO_LOCAL_VIEW_ADD) //add local view
//                {
//                    //VideoDeviceManager.getInstance().refreshLocalVideo(true, callId);
//                    mCallNotification.onCallEventNotify(CallConstant.CallEvent.ADD_LOCAL_VIEW, callId, 0);
//                } else //remove local view
//                {
//                    //VideoDeviceManager.getInstance().refreshLocalVideo(false, callId);
//                    mCallNotification.onCallEventNotify(CallConstant.CallEvent.DEL_LOCAL_VIEW, callId, 0);
//                }
//                break;
//
//            case TSDK_E_VIEW_AUX_DATA_VIEW: //auxiliary data
//                break;
//
//            default:
//                break;
//        }
//
//    }
//
//    /**
//     * [en]This method is used to handle call hold success
//     * [cn]处理呼叫保持成功事件
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleHoldSuccess(TsdkCall call) {
//        LogTool.i(TAG, "handleHoldSuccess");
//        CallInfo callInfo = getCallInfo(call);
//        CallSession callSession = getCallSessionByCallID(callInfo.getCallId());
//        if (callSession.isVideoHold()) {
//            mCallNotification.onCallEventNotify(CallConstant.CallEvent.VIDEO_HOLD_SUCCESS, callInfo, 0);
//        } else {
//            mCallNotification.onCallEventNotify(CallConstant.CallEvent.AUDIO_HOLD_SUCCESS, callInfo, 0);
//        }
//    }
//
//    /**
//     * [en]This method is used to handle call hold failed
//     * [cn]处理呼叫保持失败事件
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleHoldFailed(TsdkCall call) {
//        LogTool.i(TAG, "handleHoldFailed");
//        CallInfo callInfo = getCallInfo(call);
//        CallSession callSession = getCallSessionByCallID(callInfo.getCallId());
//        if (callSession.isVideoHold()) {
//            callSession.setVideoHold(false);
//            //保持失败，只直接通知UI失败，不自动动恢复视频
//            mCallNotification.onCallEventNotify(CallConstant.CallEvent.VIDEO_HOLD_FAILED, callInfo, 0);
//        } else {
//            mCallNotification.onCallEventNotify(CallConstant.CallEvent.AUDIO_HOLD_FAILED, callInfo, 0);
//        }
//    }
//
//    /**
//     * [en]This method is used to handle call unhold success
//     * [cn]处理取消呼叫保持成功事件
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleUnholdSuccess(TsdkCall call) {
//        LogTool.i(TAG, "handleUnholdSuccess");
//        int callId = call.getCallInfo().getCallId();
//        CallSession callSession = getCallSessionByCallID(callId);
//        if (callSession == null) {
//            LogTool.e(TAG, "call session obj is null");
//            return;
//        }
//
//        //如果此保持发起时是“视频保持”，则再在“保持恢复”后，请求远端“增加视频”
//        if (callSession.isVideoHold()) {
//            addVideo(callId);
//            callSession.setVideoHold(false);
//        }
//
//        //调试音频
//        CallInfo callInfo = getCallInfo(call);
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.UN_HOLD_SUCCESS, callInfo, 0);
//    }
//
//    /**
//     * [en]This method is used to handle call unhold failed
//     * [cn]处理取消呼叫保持失败事件
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleUnholdFailed(TsdkCall call) {
//        LogTool.i(TAG, "handleUnholdFailed");
//
//        CallInfo callInfo = getCallInfo(call);
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.UN_HOLD_FAILED, callInfo, 0);
//    }
//
//    /**
//     * [en]This method is used to handle call divert failed
//     * [cn]处理偏转失败事件
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleDivertFailed(TsdkCall call) {
//        LogTool.i(TAG, "handleDivertFailed");
//
//        CallInfo callInfo = getCallInfo(call);
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.DIVERT_FAILED, callInfo, 0);
//    }
//
//    /**
//     * [en]This method is used to handle  blind transfer success
//     * [cn]处理盲转成功事件
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleBldTransferSuccess(TsdkCall call) {
//        LogTool.i(TAG, "handleBldTransferSuccess");
//
//        CallInfo callInfo = getCallInfo(call);
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.BLD_TRANSFER_SUCCESS, callInfo, 0);
//    }
//
//    /**
//     * [en]This method is used to handle  blind transfer success failed
//     * [cn]处理盲转失败事件
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleBldTransferFailed(TsdkCall call) {
//        LogTool.i(TAG, "handleBldTransferFailed");
//
//        CallInfo callInfo = getCallInfo(call);
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.BLD_TRANSFER_FAILED, callInfo, 0);
//    }
//
//    /**
//     * [en]This method is used to handle  remote reject audio to video
//     * [cn]远端拒绝音频转视频
//     *
//     * @param call [en]Indicates call info
//     *             [cn]呼叫信息
//     */
//    public void handleRefuseOpenVideoInd(TsdkCall call) {
//
//        VideoMgr.getInstance().clearCallVideo();
//
//        CallSession callSession = getCallSessionByCallID(call.getCallInfo().getCallId());
//        callSession.setCallStatus(CallConstant.CallStatus.AUDIO_CALLING);
//
//        CallInfo callInfo = getCallInfo(call);
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.REMOTE_REFUSE_ADD_VIDEO_SREQUEST, callInfo, 0);
//
//    }
//
//    /**
//     * refresh route ui
//     *
//     * @param call
//     * @param route
//     */
//    public void handleRefreshoute(TsdkCall call, int route) {
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.REFRESH_ROUTE, route, 0);
//    }
//
//    /**
//     * audio device status changed
//     *
//     * @param route
//     */
//    public void handleAudioDeviceChanged(int route) {
//        mCallNotification.onCallEventNotify(CallConstant.CallEvent.DEVICE_CHANGED, route, 0);
//    }
//
//}
