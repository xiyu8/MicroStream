//package com.jason.microstream.service;
//
//import static android.app.Notification.PRIORITY_MAX;
//import static android.app.Notification.VISIBILITY_PUBLIC;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Binder;
//import android.os.Build;
//import android.os.IBinder;
//import android.support.annotation.RequiresApi;
//
//import com.jason.microstream.tackle.LocContext;
//import com.jason.microstream.tackle.UIConstants;
//
//
//public class InCallService extends Service {
//
//  private String TAG = InCallService.class.getSimpleName();
//
//  private NotificationManager notificationManager;
//  private String notificationId = "serviceid";
//  private String notificationName = "servicename";
//
//  public InCallService() {
//  }
//
//  @Override
//  public IBinder onBind(Intent intent) {
//    return new InCallBinder();
//  }
//
//  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//  @Override
//  public void onCreate() {
//    super.onCreate();
//  }
//
//  public class InCallBinder extends Binder {
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void postInConfNotice() {
//      int callID = CallMgr.getInstance().getCallId();
////      if (callID != 0) {
////        TsdkCall tsdkCall = TsdkManager.getInstance().getCallManager().getCallByCallId(MeetingMgr.getInstance().getCurrentConferenceCallID());
////        String picturePath = Environment.getExternalStorageDirectory() + File.separator + BMP_FILE;
////        tsdkCall.setCameraPicture(picturePath);
////      }
//      if (callID == 0 || CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getCallId()) == null) {
//        return;
//      }
//
//      TsdkCallInfo call= CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getCallId()).getTsdkCall().getCallInfo();
//      Session.CallState callState = CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getCallId()).getCallState();
//      boolean isVideoCall = CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getCallId()).getTsdkCall().getCallInfo().getIsVideoCall() == 1;
//
//
//      Intent intent =null;
//      if(callState==Session.CallState.CONNECTED)
//        intent= new Intent(LocContext.getContext(),isVideoCall? CallOutActivity.class: CallOutAudioActivity.class);
//      else if(callState==Session.CallState.COMING){
//        intent= new Intent(LocContext.getContext(),isVideoCall? VideoMeetCallComingActivity.class: VideoMeetCallComingActivity.class);
//      }else {
//        intent= new Intent(LocContext.getContext(),isVideoCall? CallOutActivity.class: CallOutAudioActivity.class);
//      }
//
//
//
//      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//      String peerNumber = call.getPeerNumber();
//      String peerDisplayName = call.getPeerDisplayName();
//      boolean isFocus = false;
//      boolean isCaller = call.getIsCaller() == 1 ? true : false;
//      if (call.getIsFocus() == 1) isFocus = true;
//      if (call.getIsVideoCall() == 1) isVideoCall = true;
//      CallInfo callInfo= new CallInfo.Builder()
//              .setCallID(call.getCallId())
//              .setConfID(call.getConfId())
//              .setIsSvcCall(call.getIsSvcCall())
//              .setPeerNumber(peerNumber)
//              .setPeerDisplayName(peerDisplayName)
//              .setVideoCall(isVideoCall)
//              .setFocus(isFocus)
//              .setCaller(isCaller)
//              .setReasonCode(call.getReasonCode())
//              .build();
//      intent.putExtra(UIConstants.CALL_INFO, callInfo);
//      intent.putExtra("isVideoCall", callInfo.isVideoCall());
//      intent.putExtra("isInCall", callState==Session.CallState.CONNECTED?true:false);
//      intent.putExtra("isResume", true);
//      intent.putExtra("callInfo", callInfo);
//      LocContext.getContext().startActivity(intent);
//
//      PendingIntent pendingIntent = PendingIntent.getActivity(InCallService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//      Notification.Builder builder = new Notification.Builder(InCallService.this)
//              .setSmallIcon(R.mipmap.logo)
//              .setContentTitle("您正在通话")
//              .setVisibility(VISIBILITY_PUBLIC)
//              .setPriority(PRIORITY_MAX)
//              .setContentText("请在设置中打开悬浮窗权限")
//              .setAutoCancel(true)
//              .setContentIntent(pendingIntent);
//
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        builder.setChannelId("inConfId");
//      }
//      Notification notification = builder.build();
//
//
//
//      NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//
//      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//        NotificationChannel channel = new NotificationChannel("inConfId", "inConf", NotificationManager.IMPORTANCE_HIGH);
//        notificationManager.createNotificationChannel(channel);
//      }
////    startForeground(1001,notification);
//      notificationManager.notify(1001,notification);
//    }
//
//    public void destroyInConfNotice() {
//
//    }
//
//    public void abnormalExitConfActivity() {
//
//    }
//
//
//    public void recoverInConf() {
//
//    }
//
//
//  }
//
//
//}