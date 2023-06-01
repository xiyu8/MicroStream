package com.jason.microstream;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jason.microstream.localbroadcast.Events;
import com.jason.microstream.localbroadcast.LocBroadcast;
import com.jason.microstream.localbroadcast.LocBroadcastReceiver;
import com.jason.microstream.model.msg.DisplayMsg;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * 基于AndroidWebRTC，将 采集的本地摄像头画面，传输到另一个客户端videotunneltwo  显示
 * 信令传输使用java nio
 *
 */
public class MainActivity1_ extends AppCompatActivity implements NioPeriodChronicService.View, LocBroadcastReceiver {
    public final String TAG = "video_tunnel";
    public final String TAGT = "VideoStep";
    public final String  VIDEO_TRACK_ID = "ARDAMSv0";
    public final String  AUDIO_TRACK_ID = "ARDAMSa0";
    public final String[]  events = {Events.ACTION_ON_MSG_RECEIVE,Events.ACTION_ON_LOGIN,Events.ACTION_ON_LOGOUT};
    public static final Map<String, String> userMap = new HashMap<String, String>(){{
        put("11111111111111111111111111111112", "user1");
        put("11111111111111111111111111111113", "user2");
    }};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        gson = new Gson();
//        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},
//                111);

        LocBroadcast.getInstance().registerBroadcast(this, events);

        nioConnect();
        initView();
        initData();
    }

    private void nioConnect() {
//        startService(new Intent(this, NioPeriodChronicService.class));
        bindService(new Intent(this, NioPeriodChronicService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                nioBinder = (NioPeriodChronicService.NioBinder) service;
//                String host = "192.168.137.36";
//                String port = "8887";
//                String uid = "user1";
//                String token = "user1";
//                nioBinder.registerNIoSelector(MainActivity1_.this);
//                nioBinder.initWriteThread();
//                nioBinder.nioConnect(host,port,uid,token);

                if (nioBinder.isConnected()) {
                    connection_status.setText("connected");
                } else {
                    connection_status.setText("unconnected");
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {}
        }, Context.BIND_AUTO_CREATE);
    }

    Gson gson;

    int resumeCount = 0;
    NioPeriodChronicService.NioBinder nioBinder;
    @Override
    protected void onResume() {
        super.onResume();
        resumeCount++;
//        if (resumeCount == 2) {
//            bindService(new Intent(this, NioPeriodChronicService.class), new ServiceConnection() {
//                @Override
//                public void onServiceConnected(ComponentName name, IBinder service) {
//                    nioBinder = (NioPeriodChronicService.NioBinder) service;
//                    nioBinder.setView(MainActivity1_.this);
//                }
//
//                @Override
//                public void onServiceDisconnected(ComponentName name) {
//
//                }
//            }, Context.BIND_AUTO_CREATE);
//        }
    }

    SurfaceView mSurfaceView;
    SurfaceHolder surfaceHolder;
    CameraManager mCameraManager;
    Handler cameraHandler;
    TextView receive_msg_content;
    EditText send_user, send_msg_content;
    Button send_msg;
    TextView connection_status;
    private void initView() {
        mSurfaceView =findViewById(R.id.local_video);
        mLocalSurfaceView =findViewById(R.id.LocalSurfaceView);
        mRemoteSurfaceView =findViewById(R.id.RemoteSurfaceView);
        receive_msg_content =findViewById(R.id.receive_msg_content);
        connection_status =findViewById(R.id.connection_status);
        send_user =findViewById(R.id.send_user);
        send_msg =findViewById(R.id.send_msg);
        send_msg_content =findViewById(R.id.send_msg_content);

//        surfaceHolder = mSurfaceView.getHolder();
//        surfaceHolder.setKeepScreenOn(true);
//        surfaceHolder.addCallback(this);

    }

    PeerConnectionFactory mPeerConnectionFactory;
    VideoTrack mLocalVideoTrack;
    VideoTrack mVideoTrackRemote;
    AudioTrack mAudioTrack;
    SurfaceTextureHelper mSurfaceTextureHelper;
    VideoCapturer mVideoCapturer;
    SurfaceViewRenderer mLocalSurfaceView,mRemoteSurfaceView;
    EglBase.Context eglBaseContext;
    MediaStream localMediaStream;
    PeerConnection remotePeerConnection;
    private void initData() {
        eglBaseContext = EglBase.create().getEglBaseContext();
        // step 1 创建PeerConnectionFactory
        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(this)
                        .setEnableInternalTracer(true)
                        .createInitializationOptions()
        );

        //创建编解码对象
        DefaultVideoEncoderFactory videoEncode = new DefaultVideoEncoderFactory(eglBaseContext, true, true);
        DefaultVideoDecoderFactory videoDecode = new DefaultVideoDecoderFactory(eglBaseContext);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        mPeerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoDecoderFactory(videoDecode)
                .setVideoEncoderFactory(videoEncode)
                .createPeerConnectionFactory();
        AudioSource audioSource = mPeerConnectionFactory.createAudioSource(new MediaConstraints());
        mPeerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);

        mLocalSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                VideoCapturer videoCapturer = createVideoCapturer();
                VideoSource videoSource = mPeerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
                //拿到 surface工具类，用来表示camera 初始化的线程
                mSurfaceTextureHelper = SurfaceTextureHelper.create("caputerTHread", eglBaseContext);
                //用来表示当前初始化 camera 的线程，和 application context，当调用 startCapture 才会回调。
                videoCapturer.initialize(mSurfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());
                //开始采集
                videoCapturer.startCapture(
                        mLocalSurfaceView.getWidth(),
                        mLocalSurfaceView.getHeight(),
                        30
                );
                // 初始化 SurfaceViewRender ，这个方法非常重要，不初始化黑屏
                mLocalSurfaceView.init(eglBaseContext, null);
//                mLocalSurfaceView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
//                mLocalSurfaceView.setMirror(true);
//                mLocalSurfaceView.setEnableHardwareScaler(false /* enabled */);
                //添加视频轨道
                mLocalVideoTrack = mPeerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
                // 添加渲染接收端器到轨道中，画面开始呈现
                mLocalVideoTrack.addSink(mLocalSurfaceView);
                // 创建 mediastream
                localMediaStream = mPeerConnectionFactory.createLocalMediaStream("MediaStream");
                // 将视频轨添加到 mediastram 中，等待连接时传输
                localMediaStream.addTrack(mLocalVideoTrack);

//                mVideoTrack.setEnabled(true);

            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            }
        });

////////////////////////////////////////////////////////////////////////////////////////////////////
        mRemoteSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                //等待接收数据，这里只要初始化即可
                if(!surfaceIsInit){
                    mRemoteSurfaceView.init(eglBaseContext, null);
                    mRemoteSurfaceView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                    mRemoteSurfaceView.setEnableHardwareScaler(false);
                    surfaceIsInit = true;
                }
            }
            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {}
            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {}
        });
    }

    boolean surfaceIsInit = false;

    int commandCount = 0;
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                if (commandCount == 0) {
                    B0(); //将 localPeerConnection 提供给 其它端连接
                    remotePeerConnection.addStream(localMediaStream);
                }
                if (commandCount == 1) {
                    B1();
                }
                commandCount++;
                break;
            case R.id.send_msg:
                sendMsg();
                break;
        }
    }


    public void onClickk(View view) {
        startActivity(new Intent(this,NioPeriodChronicActivity.class));
    }

    private void B0() {
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
//        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        remotePeerConnection = mPeerConnectionFactory.createPeerConnection(iceServers, new PeerObserver() {
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAGT, "mediaStream.videoTracks.get(0).addSink(mRemoteSurfaceView)");
                        mediaStream.videoTracks.get(0).addSink(mRemoteSurfaceView);
                    }
                });
            }

            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                Log.d(TAGT, "remotePeerConnection->onIceCandidate");
                remotePeerConnection.addIceCandidate(iceCandidate);
                Log.d(TAGT, "localPeerConnection->onIceCandidate->nioBinder.nioWriteString");
                nioBinder.nioWriteString("remotePeerConnection->onIceCandidate"+gson.toJson(iceCandidate));

            }
        });

    }


    private void B1() {

        remotePeerConnection.createOffer(new SdpObserver("创建local offer") {
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAGT, "remotePeerConnection.createOffer->onCreateSuccess");
                remotePeerConnection.setLocalDescription(new SdpObserver("local 设置本地 sdp"){
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        super.onCreateSuccess(sessionDescription);
                    }
                }, sessionDescription);

                Log.d(TAGT, "remotePeerConnection.createOffer->onCreateSuccess->nioBinder.nioWriteString"+gson.toJson(sessionDescription));
                nioBinder.nioWriteString("remotePeerConnection.createOffer->onCreateSuccess"+gson.toJson(sessionDescription));;
            }
        }, new MediaConstraints());
    }


    @Override
    public void showData(String ss) {
        if (ss.startsWith("localPeerConnection.createOffer->onCreateSuccess")) {
            SessionDescription sessionDescription = gson.fromJson(
                    ss.substring("localPeerConnection.createOffer->onCreateSuccess".length()), SessionDescription.class);
            Log.d(TAGT, "localPeerConnection.createOffer->onCreateSuccess:remotePeerConnection.setRemoteDescription");
            remotePeerConnection.setRemoteDescription(new SdpObserver("把 sdp 给到 remote"), sessionDescription);
            Log.d(TAGT, "localPeerConnection.createOffer->onCreateSuccess:remotePeerConnection.createAnswer");
            remotePeerConnection.createAnswer(new SdpObserver("创建 remote answer") {
                public void onCreateSuccess(SessionDescription sessionDescription1) {
                    Log.d(TAGT, "remotePeerConnection.createAnswer->onCreateSuccess");
                    remotePeerConnection.setLocalDescription(new SdpObserver("remote 设置本地 sdp"), sessionDescription1);
                    nioBinder.nioWriteString("remotePeerConnection.createAnswer->onCreateSuccess"+gson.toJson(sessionDescription1));



                }
            }, new MediaConstraints());
        } else if (ss.startsWith("localPeerConnection.createAnswer->onCreateSuccess")) {
            SessionDescription sessionDescription = gson.fromJson(
                    ss.substring("localPeerConnection.createAnswer->onCreateSuccess".length()), SessionDescription.class);
            Log.d(TAGT, "localPeerConnection.createAnswer->onCreateSuccess:remotePeerConnection.setRemoteDescription");
            remotePeerConnection.setRemoteDescription(new SdpObserver("把 sdp 给到 remote"), sessionDescription);
        } else if (ss.startsWith("localPeerConnection->onIceCandidate")) {
            IceCandidate iceCandidate = gson.fromJson(
                    ss.substring("localPeerConnection->onIceCandidate".length()), IceCandidate.class);
            Log.d(TAGT, "localPeerConnection->onIceCandidate:remotePeerConnection.addIceCandidate");
            remotePeerConnection.addIceCandidate(iceCandidate);
        } else {
            Log.d(TAGT, "----------"+ss);
        }
    }

    @Override
    public void showConnection(String ip, String port, String uid, String token) {

    }


    private VideoCapturer createVideoCapturer() {
        if (Camera2Enumerator.isSupported(this)) {
            return createCameraCapturer(new Camera2Enumerator(this));
        } else {
            return createCameraCapturer(new Camera1Enumerator(true));
        }
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Log.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Log.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Log.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Log.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    public void showConnection(String ip, String port, String user) {

    }
    @Override
    public void showError(String ss) {

    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onReceive(String broadcastName, Object obj) {
        runOnUiThread(() -> {
            switch (broadcastName) {
                case Events.ACTION_ON_MSG_RECEIVE:
                    String displayMsg = (String) obj;
                    receive_msg_content.append(displayMsg + "\r");
                    break;
                case Events.ACTION_ON_LOGIN:
                    connection_status.setText("connected");
                    break;
                case Events.ACTION_ON_LOGOUT:
                    connection_status.setText("unconnected");
                    break;
            }
        });

    }

    private void sendMsg() {
        String user = send_user.getText().toString();
        String sendMsg = send_msg_content.getText().toString();

        if (user == null || sendMsg == null || user.equals("") || sendMsg.equals("")) {
            return;
        }

        nioBinder.sendNormalMsg(userMap.get(user),sendMsg);

    }
}