//package com.jason.microstream.service;
//
//import android.app.NotificationManager;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Rect;
//import android.media.AudioManager;
//import android.os.Binder;
//import android.os.Environment;
//import android.os.IBinder;
//import android.util.DisplayMetrics;
//import android.view.Display;
//import android.view.GestureDetector;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.TextView;
//
//
//import com.jason.microstream.localbroadcast.LocBroadcast;
//import com.jason.microstream.localbroadcast.LocBroadcastReceiver;
//import com.jason.microstream.manager.call.CallMgr;
//import com.jason.microstream.tackle.LocContext;
//import com.jason.microstream.tackle.ToastUtil;
//import com.jason.microstream.tackle.UIConstants;
//
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//import org.greenrobot.greendao.annotation.NotNull;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
//import io.reactivex.rxjava3.core.Observable;
//import io.reactivex.rxjava3.core.ObservableEmitter;
//import io.reactivex.rxjava3.core.ObservableOnSubscribe;
//import io.reactivex.rxjava3.core.ObservableSource;
//import io.reactivex.rxjava3.schedulers.Schedulers;
//
//
//public class CallService extends Service implements LocBroadcastReceiver, View.OnTouchListener {
//    public CallService() {
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return callBinder;
//    }
//
//
//    String[] actions = new String[]{CustomBroadcastConstants.CALL_COMING, CustomBroadcastConstants.ACTION_CALL_CONNECTED
//            , CustomBroadcastConstants.ACTION_CALL_END, CustomBroadcastConstants.ADD_LOCAL_VIEW
//            , CustomBroadcastConstants.HEADSET_PLUG, CustomBroadcastConstants.BLUETOOTH_CONNECTION_STATE
//            , CustomBroadcastConstants.BLUETOOTH_STATE, CustomBroadcastConstants.CALL_ROUTE_CHANGED};
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        if (!isRegister) {
//            LocBroadcast.getInstance().registerBroadcast(this, actions);
//            EventBus.getDefault().register(this);
//            isRegister = true;
//        }
//    }
//
//    CallBinder callBinder = new CallBinder();
//
//    WindowManager windowManager;
//    View windowView;
//    View small_video_none_area;
//    GestureDetector gestureDetector;
//    WindowManager.LayoutParams layoutParams;
//    int screenWidth, screenHeight;
//    int callId;
//    boolean isVideoCall = false;
//    Session.CallState callState;
//    ViewGroup local_video_area;
//    ViewGroup remote_video_area;
//    ViewGroup hide_video_view;
//    boolean haveFloatWindow = false;
//    boolean isRegister = false;
//    Map<String, UserState> userStateMap = new HashMap<>();
//
//    private void createFloatWindow() {
//        this.callId = CallMgr.getInstance().getCallId();
//        if (callId == 0 || CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getCallId()) == null) {
//            return;
//        }
////    isSvc = CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getCallId()).getTsdkCall().getCallInfo().getIsSvcCall() == 1;
//        isVideoCall = CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getCallId()).getTsdkCall().getCallInfo().getIsVideoCall() == 1;
//        callState = CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getCallId()).getCallState();
//        if (callState != Session.CallState.CONNECTED) {
//            isConnected = false;
//        } else {
//            isConnected = true;
//        }
//        userStateMap = CallMgrb.getCallMgr().userStateMap;
//
//        windowManager = (WindowManager) LocContext.getContext().getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics dm = new DisplayMetrics();
//        Display display = windowManager.getDefaultDisplay();
//        if (display != null) {
//            display.getMetrics(dm);
//            screenWidth = dm.widthPixels;
//            screenHeight = dm.heightPixels;
//        }
//        layoutParams = new WindowManager.LayoutParams();
//        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//
//        setWindowView();
//        gestureDetector = new GestureDetector(LocContext.getContext(), new GestureListener());
//
//        haveFloatWindow = true;
//
//        if (!isRegister) {
//            LocBroadcast.getInstance().registerBroadcast(this, actions);
//            EventBus.getDefault().register(this);
//            isRegister = true;
//        }
//    }
//
//
//    private void setWindowView() {
//        if (callState == Session.CallState.CONNECTED && isVideoCall) {
//            if (windowView != null && windowView.findViewById(R.id.remote_video_area) != null) {
//            } else {
//                if (windowView != null) windowManager.removeView(windowView);
//                windowView = LayoutInflater.from(this).inflate(R.layout.part_float_window_call, null);
//                VideoMgr.getInstance().getRemoteVideoView().setVisibility(View.VISIBLE);
//                local_video_area = windowView.findViewById(R.id.local_video_area);
//                remote_video_area = windowView.findViewById(R.id.remote_video_area);
//                hide_video_view = windowView.findViewById(R.id.hide_video_view);
//                small_video_none_area = windowView.findViewById(R.id.small_video_none_area);
//                ((AvatarImageView) small_video_none_area.findViewById(R.id.small_member_avatar))
//                        .setRadius(10);
//
//                windowManager.addView(windowView, getWindowViewPara(106, 179));
//                windowView.setOnTouchListener(this);
//            }
//            setVideoView();
//        } else {
//            if (windowView != null && windowView.findViewById(R.id.call_tip) != null) {
//            } else {
//                windowView = LayoutInflater.from(this).inflate(R.layout.part_float_window_call_none_video, null);
//                windowManager.addView(windowView, getWindowViewPara(61, 61));
//                windowView.setOnTouchListener(this);
//            }
//            if (callState == Session.CallState.CONNECTED) {
//                ((TextView) (windowView.findViewById(R.id.call_tip))).setText("正在通话");
//            } else {
//                ((TextView) (windowView.findViewById(R.id.call_tip))).setText("正在呼叫");
//            }
//        }
//    }
//
//    private WindowManager.LayoutParams getWindowViewPara(int windowWith, int windowHeight) {
//        layoutParams.width = dip2px(this, windowWith);
//        layoutParams.height = dip2px(this, windowHeight);
//        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        layoutParams.x = screenWidth - layoutParams.width;
//        layoutParams.y = screenHeight / 2 - layoutParams.height / 2;
//        return layoutParams;
//    }
//
//    private void setVideoView() {
//        if (isExternalConf) {
//            small_video_none_area.setVisibility(View.GONE);
//            if ((local_video_area.getChildAt(0) == null
//                    || (local_video_area.getChildAt(0) != null && local_video_area.getChildAt(0) != VideoMgr.getInstance().getLocalVideoView()))) {
//                addSurfaceView(local_video_area, VideoMgr.getInstance().getLocalVideoView());
//                VideoMgr.getInstance().getLocalVideoView().setZOrderOnTop(true);
//            }
//            if ((remote_video_area.getChildAt(0) == null
//                    || (remote_video_area.getChildAt(0) != null && remote_video_area.getChildAt(0) != VideoMgr.getInstance().getRemoteVideoView()))) {
//                addSurfaceView(remote_video_area, VideoMgr.getInstance().getRemoteVideoView());
//                VideoMgr.getInstance().getLocalVideoView().setZOrderOnTop(false);
//            }
//            return;
//        }
//
//        addSurfaceView(hide_video_view, VideoMgr.getInstance().getLocalHideView());
//        VideoMgr.getInstance().getLocalHideView().setZOrderOnTop(true);
//        addSurfaceView(local_video_area, VideoMgr.getInstance().getLocalVideoView());
//        VideoMgr.getInstance().getLocalVideoView().setZOrderOnTop(true);
//        boolean bothIsClose = true;
//        String oppositeUserId = null;
//        for (String key : CallMgrb.getCallMgr().userStateMap.keySet()) {
//            if (!key.equals(AccountUtils.getInstance().getUserId())) {
//                oppositeUserId = key;
//                if (CallMgrb.getCallMgr().userStateMap.get(key).videoIsOpen != 1) {
//                    if ((remote_video_area.getChildAt(0) == null
//                            || (remote_video_area.getChildAt(0) != null && remote_video_area.getChildAt(0) != VideoMgr.getInstance().getLocalVideoView()))) {
//                        remote_video_area.removeAllViews();
//                        addSurfaceView(remote_video_area, VideoMgr.getInstance().getLocalVideoView());
//                        VideoMgr.getInstance().getLocalVideoView().setZOrderOnTop(false);
//                    }
//                } else {
//                    if ((local_video_area.getChildAt(0) == null
//                            || (local_video_area.getChildAt(0) != null && local_video_area.getChildAt(0) != VideoMgr.getInstance().getLocalVideoView()))) {
//                        addSurfaceView(local_video_area, VideoMgr.getInstance().getLocalVideoView());
//                        VideoMgr.getInstance().getLocalVideoView().setZOrderOnTop(true);
//                    }
//                    if ((remote_video_area.getChildAt(0) == null
//                            || (remote_video_area.getChildAt(0) != null && remote_video_area.getChildAt(0) != VideoMgr.getInstance().getRemoteVideoView()))) {
//                        addSurfaceView(remote_video_area, VideoMgr.getInstance().getRemoteVideoView());
//                        VideoMgr.getInstance().getLocalVideoView().setZOrderOnTop(false);
//                    }
//                }
//            }
//            if (CallMgrb.getCallMgr().userStateMap.get(key).videoIsOpen == 1) {
//                bothIsClose = false;
//            }
//        }
//        if (bothIsClose && oppositeUserId != null && small_video_none_area != null) {
//            CallUser callUser = CallMgrb.getCallMgr().userMap.get(oppositeUserId);
//            ((AvatarImageView) small_video_none_area.findViewById(R.id.small_member_avatar))
//                    .setAvatar(callUser.name, callUser.id);
//            small_video_none_area.setVisibility(View.VISIBLE);
//        } else {
//            if (small_video_none_area != null)
//                small_video_none_area.setVisibility(View.GONE);
//        }
//    }
//
//    private void destroyFloatWindow() {
//        if (!haveFloatWindow) return;
//        if (windowView != null) {
//            try {
//                windowManager.removeView(windowView);
//            } catch (Exception e) {
//            }
//        }
//        if (isRegister) {
//            LocBroadcast.getInstance().unRegisterBroadcast(this, actions);
//            EventBus.getDefault().unregister(this);
//            isRegister = false;
//        }
//        windowView = null;
//        isConnected = false;
//        isVideoCall = false;
//        callId = 0;
//        haveFloatWindow = false;
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(EventFromPush event) {
//        switch (event.type) {
//            case EventFromPush.TYPE_CALL_CANCEL_MUTE_SELF_AUDIO:
//                break;
//            case EventFromPush.TYPE_CALL_CANCEL_MUTE_SELF_VIDEO:
//                setWindowView();
//                break;
//            case EventFromPush.TYPE_CALL_MUTE_SELF_AUDIO:
//                break;
//            case EventFromPush.TYPE_CALL_MUTE_SELF_VIDEO:
//                setWindowView();
//                break;
//        }
//    }
//
//    boolean isConnected = false;
//
//    @Override
//    public void onReceive(String broadcastName, Object obj) {
//        switch (broadcastName) {
//            case CustomBroadcastConstants.CALL_COMING:
//
//                break;
//            case CustomBroadcastConstants.ACTION_CALL_CONNECTED:
//                if (obj instanceof CallInfo) {
//                    CallInfo callInfo = (CallInfo) obj;
//                    initInCallStatus(this, callInfo);
//                }
//                Observable.just(0)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Consumer<Integer>() {
//                            @Override
//                            public void accept(@NonNull Integer integer) throws Exception {
//                                callState = Session.CallState.CONNECTED;
//                                setWindowView();
//                            }
//                        });
//                break;
//            case CustomBroadcastConstants.ACTION_CALL_END:
//                isConnected = true;
//                endCall_(isConnected, CallMgr.getInstance().getCallId());
//                if (obj instanceof CallInfo) {
//                    CallInfo callInfo = (CallInfo) obj;
//                    if (callInfo.getReasonCode() == 50331750) {  //对方不在线
//                        ToastUtil.show(LocContext.getContext(), "对方不在线");
//                    } else if (callInfo.getReasonCode() == 50331781) {   //对方拒绝接听
////            ToastUtil.show(LocContext.getContext(),"对方拒绝接听");
//                    } else if (callInfo.getReasonCode() == 0) {  //自己正常挂断
//                    }
//                }
//                /**TODO:
//                 * 50331750  对方不在线
//                 * 50331745  收到呼叫后对方结束了进程
//                 *
//                 */
//                break;
//            case CustomBroadcastConstants.ADD_LOCAL_VIEW:
//                Observable.just(0)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Consumer<Integer>() {
//                            @Override
//                            public void accept(@NonNull Integer integer) throws Exception {
//                                setWindowView();
//                            }
//                        });
//                break;
//            case CustomBroadcastConstants.HEADSET_PLUG:
//                int audioRoute = CallMgr.getInstance().getCurrentAudioRoute();
//                boolean isHeadset = audioRoute == TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_HEADSET.getIndex();
//                if ((boolean) obj/*&&!isHeadset*/) { //插入耳机，并且当前链接不是耳机
////            CallMgr.getInstance().setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_HEADSET);
//                    CallMgr.getInstance().setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT);
//                }
//                if (!(boolean) obj/*&&isHeadset*/) {
////            if(VideoMgr.getInstance().haveBluetooth) {
//////              CallMgr.getInstance().setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_BLUETOOTH);
////              CallMgr.getInstance().setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT);
////              view.showLoudSpeaker(false);
////            } else {
//                    CallMgr.getInstance().setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_LOUDSPEAKER);
////            }
//                }
//
//                break;
//            case CustomBroadcastConstants.BLUETOOTH_CONNECTION_STATE:
//                int audioRoute1 = CallMgr.getInstance().getCurrentAudioRoute();
//                boolean isBluetooth1 = audioRoute1 == TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_BLUETOOTH.getIndex();
////          TsdkManager.getInstance().getCallManager().g()
//                if (!(boolean) obj /*&& isBluetooth1*/) {  //当前是蓝牙链接，并且是 蓝牙链接断开
//                    if (VideoMgr.getInstance().haveHeadset) {
//////              CallMgr.getInstance().setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_HEADSET);
//                        CallMgr.getInstance().setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT);
//                    } else {
//                        CallMgr.getInstance().setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_LOUDSPEAKER);
//                    }
//                }
//                if ((boolean) obj) {
////            CallMgr.getInstance().setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_BLUETOOTH);
//                    boolean ret = CallMgr.getInstance().setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT);
//                }
//
//                break;
//            case CustomBroadcastConstants.BLUETOOTH_STATE:
//                int audioRoute2 = CallMgr.getInstance().getCurrentAudioRoute();
//                boolean isBluetooth2 = audioRoute2 == TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_BLUETOOTH.getIndex();
////          TsdkManager.getInstance().getCallManager().g()
//                if (!(boolean) obj/* && isBluetooth2*/) {  //当前是蓝牙，并且是 蓝牙断开
//                    if (VideoMgr.getInstance().haveHeadset) {
////              CallMgr.getInstance().setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_HEADSET);
//                        CallMgr.getInstance().setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT);
//                    } else {
//                        CallMgr.getInstance().setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_LOUDSPEAKER);
//                    }
//                }
//
//                break;
//            case CustomBroadcastConstants.CALL_ROUTE_CHANGED:
////        int route = (int) obj;
////        if(getView()!=null) {
////          Observable.just(0).observeOn(AndroidSchedulers.mainThread())
////                  .subscribe(new Consumer<Integer>() {
////                    @Override
////                    public void accept(@NonNull Integer integer) throws Exception {
////                      if(getView()!=null) getView().showOpenLoudSpeaker(route == 1);
////                    }
////                  });
////        }
//                break;
//
//        }
//    }
//
//    Throwable tempError;
//    boolean isPlatFormCall;
//
//    public void initInCallStatus(Context context, CallInfo callInfo) {
//        ((NotificationManager) (context.getSystemService(Context.NOTIFICATION_SERVICE))).cancel(1001);
//        if (CallMgr.getInstance().getCallSessionByCallID(callInfo.getCallID()) == null) {
//            return;
//        }
//        isConnected = true;
//        boolean isPresetOpenAudio;
//        boolean isPresetOpenVideo;
//        boolean isPresetLoudspeaker;
//
//        AudioManager localAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        boolean haveHeadset = (localAudioManager.isWiredHeadsetOn() || VideoMgr.getInstance().haveBluetooth);
//
//        isPresetOpenAudio = CallMgrb.getCallMgr().isPresetOpenAudio;
//        isPresetOpenVideo = CallMgrb.getCallMgr().isPresetOpenVideo;
//        isPresetLoudspeaker = haveHeadset ? false : CallMgrb.getCallMgr().isPresetLoudspeaker;
//        Observable<Long> observable = null;
//        if (isExternalConf) {
//            observable = Observable.just(0L);
//        } else if (CallMgrb.getCallMgr().vocationalCallId == null || CallMgrb.getCallMgr().vocationalCallId == 0) {
//            observable = TempAccount.toVoca(callInfo.getPeerNumber())
//                    .retryWhen(new Function<Observable<Throwable>, ObservableSource<Long>>() {
//                        @NonNull
//                        @Override
//                        public ObservableSource<Long> apply(@NonNull Observable<Throwable> throwableObservable) throws Exception {
//                            return throwableObservable
//                                    .zipWith(Observable.range(1, 3/*立即重试次数n-1*/), new BiFunction<Throwable, Integer, Integer>() {
//                                        @Override
//                                        public Integer apply(Throwable throwable, Integer integer) throws Exception {
//                                            tempError = throwable;
//                                            if (throwable.getMessage().equals("非平台用户")) {
//                                                isPlatFormCall = false;
//                                                return -1;
//                                            }
//                                            return integer;
//                                        }
//                                    })
//                                    .flatMap(new Function<Integer, ObservableSource<Long>>() {
//                                        @Override
//                                        public ObservableSource<Long> apply(Integer retryCount) throws Exception {
//                                            if (retryCount == -1)
//                                                return Observable.error(tempError);
//                                            if (retryCount == 3) return Observable.error(tempError);
//                                            return Observable.timer(0/*立即重试的时间间隔*/, TimeUnit.SECONDS);
//                                        }
//                                    });
//                        }
//                    })
//                    .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends String>>() {
//                        @NonNull
//                        @Override
//                        public ObservableSource<? extends String> apply(@NonNull Throwable throwable) throws Exception {
//                            if (throwable.getMessage().equals("非平台用户")) {
//                                isPlatFormCall = false;
//                                return Observable.just("-1");
//                            }
//                            return Observable.error(throwable);
//                        }
//                    })
//                    .flatMap(new Function<String, ObservableSource<Long>>() {
//                        @NonNull
//                        @Override
//                        public ObservableSource<Long> apply(@NonNull String userId) throws Exception {
//                            if (userId.equals("-1"))
//                                return Observable.just(0l);
//                            isPlatFormCall = true;
//                            return CallMgrb.getCallMgr()
//                                    .getVocationalCallIdByUserId(userId, callInfo.isVideoCall())
//                                    .flatMap(new Function<Long, ObservableSource<Long>>() {
//                                        @NonNull
//                                        @Override
//                                        public ObservableSource<Long> apply(@NonNull Long vocationalCallId) throws Exception {
//                                            return CallMgrb.getCallMgr().actCall(vocationalCallId, true)
//                                                    .map(new Function<Integer, Long>() {
//                                                        @NonNull
//                                                        @Override
//                                                        public Long apply(@NonNull Integer integer) throws Exception {
//                                                            return Long.parseLong(integer + "");
//                                                        }
//                                                    });
//                                        }
//                                    })
//                                    ;
//                        }
//                    });
//
//        } else if (CallMgrb.getCallMgr().vocationalCallId != null && CallMgrb.getCallMgr().vocationalCallId != 0) {
//            observable = CallMgrb.getCallMgr().actCall(CallMgrb.getCallMgr().vocationalCallId, true)
//                    .map(new Function<Integer, Long>() {
//                        @NonNull
//                        @Override
//                        public Long apply(@NonNull Integer integer) throws Exception {
//                            return Long.parseLong(integer + "");
//                        }
//                    });
//
//
//        }
//        observable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(new Function<Long, ObservableSource<Integer>>() {
//                    @NonNull
//                    @Override
//                    public ObservableSource<Integer> apply(Long integer) throws Exception {
//                        return Observable.create(new ObservableOnSubscribe<Integer>() {
//                            @Override
//                            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                                openAudio(isPresetOpenAudio);
//                                openVideo(isPresetOpenVideo, 1);
//                                openLoudspeaker(isPresetLoudspeaker);
//                                e.onNext(0);
//                                e.onComplete();
//                            }
//                        });
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BriefDisposableObserver<Integer>() {
//                               @Override
//                               public void onNext(Integer integer) {
//                               }
//
//                               @Override
//                               public void onError(Throwable e) {
//                               }
//                           }
//                );
//    }
//
//
//    public void openVideo(boolean isOpen, int cameraIndex) {
//        if (CallMgr.getInstance().getCallId() == 0) {
//            return;
//        }
//        if (isExternalConf) {
//            CallMgr.getInstance().operateCameraWithIndex(isOpen, CallMgr.getInstance().getCallId(), cameraIndex);
//            return;
//        }
//        CallMgrb.getCallMgr().operateState(CallMgrb.getCallMgr().vocationalCallId, false, isOpen)
//                .onErrorReturn(new Function<Throwable, Integer>() {
//                    @NonNull
//                    @Override
//                    public Integer apply(@NonNull Throwable throwable) throws Exception {
//                        return 0;
//                    }
//                })
//                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
//                    @NonNull
//                    @Override
//                    public ObservableSource<Integer> apply(@NonNull Integer integer) throws Exception {
//                        return Observable.create(new ObservableOnSubscribe<Integer>() {
//                            @Override
//                            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                                CallMgr.getInstance().operateCameraWithIndex(isOpen, CallMgr.getInstance().getCallId(), cameraIndex);
//                                e.onNext(0);
//                                e.onComplete();
//                            }
//                        });
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BriefDisposableObserver<Integer>() {
//                    @Override
//                    public void onNext(Integer integer) {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//                })
//        ;
//    }
//
//    public void openAudio(boolean isOpen) {
//        if (CallMgr.getInstance().getCallId() == 0) {
//            return;
//        }
//        if (isExternalConf) {
//            CallMgr.getInstance().muteMic(CallMgr.getInstance().getCallId(), !isOpen);
//            return;
//        }
//        CallMgrb.getCallMgr().operateState(CallMgrb.getCallMgr().vocationalCallId, true, isOpen)
//                .onErrorReturn(new Function<Throwable, Integer>() {
//                    @NonNull
//                    @Override
//                    public Integer apply(@NonNull Throwable throwable) throws Exception {
//                        return 0;
//                    }
//                })
//                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
//                    @NonNull
//                    @Override
//                    public ObservableSource<Integer> apply(@NonNull Integer integer) throws Exception {
//                        return Observable.create(new ObservableOnSubscribe<Integer>() {
//                            @Override
//                            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                                CallMgr.getInstance().muteMic(CallMgr.getInstance().getCallId(), !isOpen);
//                                e.onNext(0);
//                                e.onComplete();
//                            }
//                        });
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BriefDisposableObserver<Integer>() {
//                    @Override
//                    public void onNext(Integer integer) {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//                })
//        ;
//    }
//
//    public void openLoudspeaker(boolean isOpen) {
//        CallMgr.getInstance().setAudioRoute(isOpen ? TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_LOUDSPEAKER : TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT);
//    }
//
//    private void endCall_(boolean isConnected, int callId) {
//        Observable<Integer> observable;
//        if (CallMgrb.getCallMgr().vocationalCallId == null) {
//            observable = Observable.just(0);
//        } else if (isConnected) {
//            observable = CallMgrb.getCallMgr().actCall(CallMgrb.getCallMgr().vocationalCallId, false);
//        } else {
//            observable = CallMgrb.getCallMgr().cancelCall(CallMgrb.getCallMgr().vocationalCallId);
//        }
//        observable
//                .onErrorReturn(new Function<Throwable, Integer>() {
//                    @NonNull
//                    @Override
//                    public Integer apply(@NonNull Throwable throwable) throws Exception {
//                        return 0;
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BriefDisposableObserver<Integer>() {
//                    @Override
//                    public void onNext(Integer integer) {
//                        destroyFloatWindow();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
////                if (getView() != null) getView().showCallEnd();
////        if (getView() != null) getView().showCallEndError();
//                    }
//                })
//        ;
//    }
//
//
//    @Override
//    public void onDestroy() {
//        destroyFloatWindow();
//
//        if (isRegister) {
//            LocBroadcast.getInstance().unRegisterBroadcast(this, actions);
//            EventBus.getDefault().unregister(this);
//            isRegister = false;
//        }
//        super.onDestroy();
//    }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        return gestureDetector.onTouchEvent(event);
//    }
//
//    public class CallBinder extends Binder {
//        public void createCallFloatWindow() {
//            createFloatWindow();
//        }
//
//        public void destroyCallFloatWindow() {
//            destroyFloatWindow();
//        }
//
//    }
//
//
//    private void addSurfaceView(ViewGroup container, SurfaceView child) {
//        if (child == null) {
//            return;
//        }
//        if (child.getParent() != null) {
//            ViewGroup vGroup = (ViewGroup) child.getParent();
//            vGroup.removeAllViews();
//        }
//        container.addView(child);
//    }
//
//
//    int lastTouchX, lastTouchY;
//
//    public class GestureListener implements GestureDetector.OnGestureListener {
//        private Rect touchFrame;
//
//        public boolean onDown(@NotNull MotionEvent e) {
//            lastTouchX = 0;
//            lastTouchY = 0;
//            return false;
//        }
//
//        public void onShowPress(@NotNull MotionEvent e) {
//        }
//
//        public boolean onSingleTapUp(@NotNull MotionEvent e) {
//            if (CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getCallId()) == null) {
//                destroyFloatWindow();
//                return true;
//            }
//
//            Session.CallState callState = CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getCallId()).getCallState();
//
//            Intent intent = null;
//            if (callState == Session.CallState.CONNECTED)
//                intent = new Intent(LocContext.getContext(), isVideoCall ? CallOutActivity.class : CallOutAudioActivity.class);
//            else if (callState == Session.CallState.COMING) {
//                intent = new Intent(LocContext.getContext(), isVideoCall ? VideoMeetCallComingActivity.class : VideoMeetCallComingActivity.class);
//            } else {
//                intent = new Intent(LocContext.getContext(), isVideoCall ? CallOutActivity.class : CallOutAudioActivity.class);
//            }
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            destroyFloatWindow();
////        Intent intent = new Intent(FloatWindowService.this, VideoMeetControlActivityV2.class);
//
//            int callID = CallMgr.getInstance().getCallId();
//            if (callID != 0) {
//                TsdkCall tsdkCall = TsdkManager.getInstance().getCallManager().getCallByCallId(callID);
//                String picturePath = Environment.getExternalStorageDirectory() + File.separator + BMP_FILE;
//                tsdkCall.setCameraPicture(picturePath);
//            }
//
////      Intent intent = null;
////      boolean isPad = DeviceConfig.isPad(LocContext.getContext());
////      if (/*isExternalConf*/false && !isPad) {
//////        intent = new Intent(CallService.this, SimpleMeetingContainerActivity.class);
////      } else if (!/*isExternalConf*/false && !isPad) {
//////        intent = new Intent(CallService.this, MingleMeetingContainerActivity.class);
//////        intent = new Intent(CallService.this, CallOutActivity.class);
////      } else if (/*isExternalConf*/false && isPad) {
////        //TODO: 大屏 三方呼入
////      } else if (!/*isExternalConf*/false && isPad) {
//////        intent = new Intent(CallService.this, PadMeetingContainerActivity.class);
////      } else {
//////        intent = new Intent(CallService.this, SimpleMeetingContainerActivity.class);
////      }
//
//            TsdkCallInfo call = CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getCallId()).getTsdkCall().getCallInfo();
//            String peerNumber = call.getPeerNumber();
//            String peerDisplayName = call.getPeerDisplayName();
//            boolean isFocus = false;
//            boolean isVideoCall = false;
//            boolean isCaller = call.getIsCaller() == 1 ? true : false;
//            if (call.getIsFocus() == 1) isFocus = true;
//            if (call.getIsVideoCall() == 1) isVideoCall = true;
//            CallInfo callInfo = new CallInfo.Builder()
//                    .setCallID(call.getCallId())
//                    .setConfID(call.getConfId())
//                    .setIsSvcCall(call.getIsSvcCall())
//                    .setPeerNumber(peerNumber)
//                    .setPeerDisplayName(peerDisplayName)
//                    .setVideoCall(isVideoCall)
//                    .setFocus(isFocus)
//                    .setCaller(isCaller)
//                    .setReasonCode(call.getReasonCode())
//                    .build();
//            intent.putExtra(UIConstants.CALL_INFO, callInfo);
//            intent.putExtra("callInfo", callInfo);
//            intent.putExtra("isVideoCall", callInfo.isVideoCall());
//            intent.putExtra("isInCall", callState == Session.CallState.CONNECTED ? true : false);
//            intent.putExtra("isResume", true);
//            LocContext.getContext().startActivity(intent);
//
//            return true;
//        }
//
//        public boolean onScroll(@NotNull MotionEvent e1, @NotNull MotionEvent event, float distanceX, float distanceY) {
//            int currentX = (int) event.getRawX();
//            int currentY = (int) event.getRawY();
//
//            if (lastTouchX == 0) {
//                lastTouchX = currentX;
//                lastTouchY = currentY;
//                return true;
//            }
//
//            int dx = currentX - lastTouchX;
//            int dy = currentY - lastTouchY;
//
//            layoutParams = (WindowManager.LayoutParams) windowView.getLayoutParams();
////      layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
//            int tempX = layoutParams.x += dx;
//            int tempY = layoutParams.y += dy;
//
//
//            int rightMost = screenWidth - layoutParams.width;
//            int leftMost = 0;
//            int topMost = 0/*getNavigationBarHeight(FloatWindowService.this)*/;
//            int bottomMost = screenHeight - layoutParams.height;
//            //'将浮窗移动区域限制在屏幕内'
//            if (tempX < leftMost) tempX = leftMost;
//            if (tempX > rightMost) tempX = rightMost;
//            if (tempY < topMost) tempY = topMost;
//            if (tempY > bottomMost) tempY = bottomMost;
//
//            layoutParams.x = tempX;
//            layoutParams.y = tempY;
//            windowManager.updateViewLayout(windowView, layoutParams);
//            lastTouchX = currentX;
//            lastTouchY = currentY;
//
//            return true;
//        }
//
//        public void onLongPress(@NotNull MotionEvent e) {
//        }
//
//        public boolean onFling(@NotNull MotionEvent e1, @NotNull MotionEvent event, float velocityX, float velocityY) {
//            //TODO:添加惯性滚动
////      //'获取当前手指坐标'
////      int currentX = (int) event.getRawX();
////      int currentY = (int) event.getRawY();
////      //'获取手指移动增量'
////      int dx = currentX - lastTouchX;
////      int dy = currentY - lastTouchY;
////      //'将移动增量应用到窗口布局参数上'
////      layoutParams.x += dx;
////      layoutParams.y += dy;
////      int rightMost = screenWidth - layoutParams.width;
////      int leftMost = 0;
////      int topMost = 0;
////      int bottomMost = screenHeight - layoutParams.height - getNavigationBarHeight(FloatWindowService.this);
////      //'将浮窗移动区域限制在屏幕内'
////      if (layoutParams.x < leftMost) {
////        layoutParams.x = leftMost;
////      }
////      if (layoutParams.x > rightMost) {
////        layoutParams.x = rightMost;
////      }
////      if (layoutParams.y < topMost) {
////        layoutParams.y = topMost;
////      }
////      if (layoutParams.y > bottomMost) {
////        layoutParams.y = bottomMost;
////      }
////      //'更新浮窗位置'
////      windowManager.updateViewLayout(windowView, layoutParams);
////      lastTouchX = currentX;
////      lastTouchY = currentY;
//
//            return true;
//        }
//    }
//
//}