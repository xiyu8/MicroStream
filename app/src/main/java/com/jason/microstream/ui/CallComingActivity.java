//package com.jason.microstream.ui;
//
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.provider.Settings;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//
//import com.gyf.barlibrary.ImmersionBar;
//import com.jason.microstream.BuildConfig;
//import com.jason.microstream.R;
//import com.jason.microstream.config.EquipConfig;
//import com.jason.microstream.service.CallService;
//import com.jason.microstream.service.InCallService;
//import com.jason.microstream.tackle.LocContext;
//import com.jason.microstream.tackle.ToastUtil;
//import com.jason.microstream.ui.base.BasicActivity;
//
//import butterknife.BindView;
//
//public class CallComingActivity extends BasicActivity<CallComingPresenter> implements CallComingPresenter.View {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//        initView();
//        initData();
//
//    }
//
//
//    @Override
//    protected CallComingPresenter onCreatePresenter() {
//        return new CallComingPresenter(this);
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_video_coming;
//    }
//
//    @BindView(R.id.opposite_avatar)
//    ImageView opposite_avatar;
//    @BindView(R.id.opposite_name)
//    TextView opposite_name;
//    @BindView(R.id.video_mute)
//    ImageView video_mute;
//    @BindView(R.id.video_mute_tip)
//    TextView video_mute_tip;
//    @BindView(R.id.video_mute_area)
//    ViewGroup video_mute_area;
//    CallInfo callInfo;
//    boolean isPresetOpenAudio, isPresetOpenVideo, isPresetLoudspeaker, defaultCameraState = false;
//
//    private void initView() {
//        getToolBarArea().setVisibility(View.GONE);
//        ImmersionBar.with(this).init();
//
//    }
//
//    private void initData() {
//
//        defaultCameraState = EquipConfig.callDefaultCamera;
//        isPresetOpenAudio = EquipConfig.callDefaultMic;
//        isPresetOpenVideo = EquipConfig.callDefaultCamera;
//        isPresetLoudspeaker = EquipConfig.callDefaultLoudspeaker;
//
//        callInfo = (CallInfo) getIntent().getSerializableExtra("callInfo");
//        if (!callInfo.isVideoCall()) {
//            video_mute_area.setVisibility(View.GONE);
//        }
//
//        if (!callInfo.isVideoCall()) {
//            ((TextView) findViewById(R.id.invite_call_type_tip)).setText("邀请您进行语音通话");
//        } else {
//            ((TextView) findViewById(R.id.invite_call_type_tip)).setText("邀请您进行视频通话");
//        }
//
////    opposite_avatar.setAvatar(mainUser.getName(), mainUser.getUid());
////    opposite_name.setText();
//        if (callInfo == null) {
//            finish();
//            return;
//        }
//
//
//        if (callInfo == null || callInfo.getPeerNumber() == null || callInfo.getPeerNumber().equals("")) {
//            // ToastUtil.show(this, "呼入信息出错");
//            finish();
//            return;
//        }
//        // getPresenter().presetDeviceState(isPresetOpenAudio,isPresetOpenVideo,isPresetLoudspeaker);
//
//        getLoadingDialog().showDialog();
//        // getPresenter().initComingCall(callInfo.getPeerNumber(), callInfo.getCallId(), callInfo.isVideoCall());
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        super.onClick(v);
//        int viewId = v.getId();
//        if (viewId == R.id.mini_fi) {
//            //TODO:小窗视频
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                intent.setData(Uri.parse("package:" + getPackageName()));
//                if (getPresenter() != null){
//                    showInOverLay();
//                    getPresenter().needRecoverCamera = false;
//                }
//                startActivityForResult(intent, 1011);
//                return;
//            }
//            if (getPresenter() != null){
//                showInOverLay();
//                getPresenter().needRecoverCamera = true;
//            }
//            finish();
//        } else if (viewId == R.id.hang_up_area) {
//            getPresenter().endCall(callInfo.getCallId(), callInfo.getPeerNumber());
//        } else if (viewId == R.id.answer_call_area) {
//            getPresenter().answerCall(this, callInfo.getCallId(), callInfo.isVideoCall());
//        } else if (viewId == R.id.video_mute) {
//            getPresenter().presetCameraState(!defaultCameraState);
//        }
//    }
//
//    public void showInOverLay() {
//
//        //有悬浮窗权限 调起悬浮窗，没有 发通知
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(LocContext.getContext())) {
//            //发通知
//            LocContext.getContext().bindService(new Intent(LocContext.getContext(), InCallService.class),
//                    new ServiceConnection() {
//
//                        @Override
//                        public void onServiceConnected(ComponentName name, IBinder service) {
//                            ((InCallService.InCallBinder) service).postInConfNotice();
//
//                        }
//
//                        @Override
//                        public void onServiceDisconnected(ComponentName name) {
//
//                        }
//                    }, BIND_AUTO_CREATE);
//        } else {
//            LocContext.getContext().bindService(new Intent(LocContext.getContext(), CallService.class),
//                    new ServiceConnection() {
//
//                        @Override
//                        public void onServiceConnected(ComponentName name, IBinder service) {
//                            ((CallService.CallBinder) service).createCallFloatWindow();
//                            if(getView()!=null) getView().justFinishActivity();
//                        }
//
//                        @Override
//                        public void onServiceDisconnected(ComponentName name) {
//
//                        }
//                    }, BIND_AUTO_CREATE);
//        }
//
//    }
//
//    public void showPresetCameraState(boolean isOpen) {
//        defaultCameraState = isOpen;
//        if (defaultCameraState) {
////        video_mute
//            video_mute_tip.setText("摄像头已开");
//        } else {
//            //        video_mute
//            video_mute_tip.setText("摄像头已关");
//        }
//    }
//
//    @Override
//    public void showUserInfo(CallUser callUser) {
//        getLoadingDialog().dismissDialog();
////        opposite_avatar.setAvatar(callUser.getName(), callUser.getUid());
//        opposite_name.setText(callUser.getName());
//    }
//
//    @Override
//    public void showMemberInfo() {
//        getLoadingDialog().dismissDialog();
//        // opposite_avatar.setAvatar(callInfo.getPeerDisplayName(), callInfo.getPeerNumber());
//        opposite_name.setText(callInfo.getPeerDisplayName());
//    }
//
//    @Override
//    public void showUserInfoError(Error error) {
//        getLoadingDialog().dismissDialog();
//        String errorMsg = "";
//        if (BuildConfig.DEBUG) {
//            errorMsg = "获取用户信息出错" + error.getMessage();
//        } else {
//            errorMsg = "获取用户信息出错";
//        }
//        ToastUtil.show(this, "获取用户信息出错");
//    }
//
//    @Override
//    public void showAnsweredCall() {
//        if (BuildConfig.DEBUG) {
//            ToastUtil.show(this, "接听成功");
//        }
//    }
//
//    @Override
//    public void showAnsweredCallError(Error error) {
//        ToastUtil.show(this, "接听失败");
//    }
//
//    @Override
//    public void showEndCall() {
//        finish();
//    }
//
//    @Override
//    public void showEndCallError(Error error) {
//        ToastUtil.show(this, "挂断失败");
//    }
//
//    @Override
//    public void showCallConnected() {
//        finish();
//    }
//
//    @Override
//    public void onStop() {
//        if (getPresenter() != null) {
//            getPresenter().onStop();
//        }
//        super.onStop();
//    }
//
//
//    public static class CallInfo{
//        public boolean isVideoCall;
//        public String peerNumber;
//        public String callId;
//        public String peerDisplayName;
//
//        public String getCallId() {
//            return callId;
//        }
//
//        public String getPeerDisplayName() {
//            return peerDisplayName;
//        }
//
//
//        public String getPeerNumber() {
//            return peerNumber;
//        }
//
//        public boolean isVideoCall() {
//            return isVideoCall;
//        }
//    }
//    public static class CallUser{
//        public String uid;
//        public String name;
//
//        public String getUid() {
//            return uid;
//        }
//
//        public String getName() {
//            return name;
//        }
//    }
//
//
//}