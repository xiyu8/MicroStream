package com.jason.microstream;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.camera2.CameraManager;
import android.os.Build;
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
import com.jason.microstream.core.im.imconpenent.ImService;
import com.jason.microstream.core.im.tup.Coder;
import com.jason.microstream.core.im.tup.data.SendNode;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;


/**
 *
 * 基于AndroidWebRTC，将 采集的本地摄像头画面，传输到另一个客户端videotunneltwo  显示
 * 信令传输使用java nio
 *
 */
public class MainActivity1_ extends AppCompatActivity implements LocBroadcastReceiver {
    public final String TAG = "video_tunnel";
    public static final String TAGT = "VideoStep";
    public final String VIDEO_TRACK_ID = "ARDAMSv0";
    public final String AUDIO_TRACK_ID = "ARDAMSa0";
    public final String[] EVENTS = {Events.ACTION_ON_MSG_RECEIVE, Events.ACTION_ON_SDP_OFFER_RECEIVE, Events.ACTION_ON_LOGIN, Events.ACTION_ON_LOGOUT};
    public static final Map<String, String> userNameIdMap = new HashMap<String, String>() {{
        put("user1", "11111111111111111111111111111112");
        put("user2", "11111111111111111111111111111113");
        put("user3", "11111111111111111111111111111114");
        put("user4", "11111111111111111111111111111115");
        put("user5", "11111111111111111111111111111116");
        put("user6", "11111111111111111111111111111117");
        put("user7", "11111111111111111111111111111118");
    }};
    public static final Map<String, String> userIdNameMap = new HashMap<String, String>() {{
        put("11111111111111111111111111111112", "user1");
        put("11111111111111111111111111111113", "user2");
        put("11111111111111111111111111111114", "user3");
        put("11111111111111111111111111111115", "user4");
        put("11111111111111111111111111111116", "user5");
        put("11111111111111111111111111111117", "user6");
        put("11111111111111111111111111111118", "user7");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        gson = new Gson();
//        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},
//                111);

        LocBroadcast.getInstance().registerBroadcast(this, EVENTS);

//        nioConnect();
        initView();
        initData();
    }

//    private void nioConnect() {
////        startService(new Intent(this, NioPeriodChronicService.class));
//        bindService(new Intent(this, NioPeriodChronicService.class), new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                nioBinder = (NioPeriodChronicService.NioBinder) service;
////                String host = "192.168.137.36";
////                String port = "8887";
////                String uid = "user1";
////                String token = "user1";
////                nioBinder.registerNIoSelector(MainActivity1_.this);
////                nioBinder.initWriteThread();
////                nioBinder.nioConnect(host,port,uid,token);
//
//                if (nioBinder.isConnected()) {
//                    connection_status.setText("connected");
//                } else {
//                    connection_status.setText("unconnected");
//                }
//            }
//            @Override
//            public void onServiceDisconnected(ComponentName name) {}
//        }, Context.BIND_AUTO_CREATE);
//    }

    Gson gson;

    int resumeCount = 0;
//    NioPeriodChronicService.NioBinder nioBinder;
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

        if (Build.BRAND.toLowerCase().equals("samsung")
//                || Build.BRAND.toLowerCase().equals("redmi")
//                || Build.BRAND.toLowerCase().equals("vivo")
        ) {
            send_user.setText("user2");
        } else if (Build.BRAND.toLowerCase().equals("honor")
//                ||Build.BRAND.toLowerCase().equals("huawei")
                     ) {
            send_user.setText("user1");
        } else {
            send_user.setText("user3");
        }


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
                videoCapturer.startCapture(mLocalSurfaceView.getWidth(), mLocalSurfaceView.getHeight(), 30);
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
            case R.id.video_step1:
                if (commandCount == 0) {
                    B0(); // 下行流监听：将 localPeerConnection 提供给 其它端连接
                    remotePeerConnection.addStream(localMediaStream); //上行流添加1
                }
                if (commandCount == 1) {
                    B1();//上行流添加2 触发上传
                }
                commandCount++;
                break;
            case R.id.send_msg:
                sendMsg();
                break;
        }
    }


    public void onClickk(View view) {
//        startActivity(new Intent(this,NioPeriodChronicActivity.class));
    }

    int iceCount = 1;
    private void B0() {//下行流监听
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
//        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        remotePeerConnection = mPeerConnectionFactory.createPeerConnection(iceServers, new PeerObserver() {
            public void onAddStream(MediaStream mediaStream) { //下行流监听
                super.onAddStream(mediaStream);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAGT, "downstream receive and added");
                        mediaStream.videoTracks.get(0).addSink(mRemoteSurfaceView);
                    }
                });
            }

            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                Log.e(TAGT, "onIceCandidate:" + iceCount);
                iceCount++;
                remotePeerConnection.addIceCandidate(iceCandidate);
//                nioBinder.nioWriteString("remotePeerConnection->onIceCandidate"+gson.toJson(iceCandidate));
                Log.e(TAGT, "onIceCandidate send swap");
//                nioBinder.sendSwapIceCandidate(iceCandidate,peerId);
                ImService.getIm().sendVideoCmd(iceCandidate, peerId, Coder.MSG_TYPE_SWAP_ICE, new SendNode.SendCallback() {
                    @Override
                    public void onSendSuccess(SendNode node) {

                    }

                    @Override
                    public void onSendFailed(IOException e, SendNode node) {

                    }
                });

            }
        });

    }

//    private void testNy() {
//        //创建两个线程组 boosGroup、workerGroup
//        EventLoopGroup bossGroup = new NioEventLoopGroup();
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        try {
//            //创建服务端的启动对象，设置参数
//            ServerBootstrap bootstrap = new ServerBootstrap();
//            //设置两个线程组boosGroup和workerGroup
//            bootstrap.group(bossGroup, workerGroup)
//                    //设置服务端通道实现类型
//                    .channel(NioServerSocketChannel.class)
//                    //设置线程队列得到连接个数
//                    .option(ChannelOption.SO_BACKLOG, 128)
//                    //设置保持活动连接状态
//                    .childOption(ChannelOption.SO_KEEPALIVE, true)
//                    //使用匿名内部类的形式初始化通道对象
//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel socketChannel) throws Exception {
//                            //给pipeline管道设置处理器
//                            socketChannel.pipeline().addLast(new MyServerHandler());
//                        }
//                    });//给workerGroup的EventLoop对应的管道设置处理器
//            System.out.println("java技术爱好者的服务端已经准备就绪...");
//            //绑定端口号，启动服务端
//            ChannelFuture channelFuture = bootstrap.bind(6666).sync();
//            //对关闭通道进行监听
//            channelFuture.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
//    }
//    private void testNy2() {
//        DefaultPromise defaultPromise;
//        defaultPromise.sync();
//        ChannelInboundHandler;
//        ChannelOutboundHandler;
//        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
//        try {
//            //创建bootstrap对象，配置参数
//            Bootstrap bootstrap = new Bootstrap();
//            //设置线程组
//            bootstrap.group(eventExecutors)
//                    //设置客户端的通道实现类型
//                    .channel(NioSocketChannel.class)
//                    //使用匿名内部类初始化通道
//                    .handler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel ch) throws Exception {
//                            //添加客户端通道的处理器
//                            ch.pipeline().addLast(new MyClientHandler());
//                        }
//                    });
//            System.out.println("客户端准备就绪，随时可以起飞~");
//            //连接服务端
//            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6666).sync();
//            //对通道关闭进行监听
//            try {
//                channelFuture.channel().closeFuture().sync();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        } finally {
//            //关闭线程组
//            eventExecutors.shutdownGracefully();
//        }
//    }

    private void B1() { //上行流添加2 触发上传
        Log.e(TAGT, "upstream offered");
        remotePeerConnection.createOffer(new SdpObserver("创建local offer") {
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.e(TAGT, "upstream offered create success");
                remotePeerConnection.setLocalDescription(new SdpObserver("local 设置本地 sdp"){
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        super.onCreateSuccess(sessionDescription);
                    }
                }, sessionDescription);
//                nioBinder.nioWriteString("remotePeerConnection.createOffer->onCreateSuccess"+gson.toJson(sessionDescription));;
                Log.e(TAGT, "upstream offer sdp");
//                nioBinder.sendOfferSdp(sessionDescription,peerId);
                ImService.getIm().sendVideoCmd(sessionDescription, peerId, Coder.MSG_TYPE_OFFER_SDP, new SendNode.SendCallback() {
                    @Override
                    public void onSendSuccess(SendNode node) {

                    }

                    @Override
                    public void onSendFailed(IOException e, SendNode node) {

                    }
                });
            }
        }, new MediaConstraints());
    }


    public void showData(String ss) {
        if (ss.startsWith("localPeerConnection.createOffer->onCreateSuccess")) {
            SessionDescription sessionDescription = gson.fromJson(
                    ss.substring("localPeerConnection.createOffer->onCreateSuccess".length()), SessionDescription.class);
            Log.e(TAGT, "localPeerConnection.createOffer->onCreateSuccess:remotePeerConnection.setRemoteDescription");
            remotePeerConnection.setRemoteDescription(new SdpObserver("把 sdp 给到 remote"), sessionDescription);
            Log.e(TAGT, "localPeerConnection.createOffer->onCreateSuccess:remotePeerConnection.createAnswer");
            remotePeerConnection.createAnswer(new SdpObserver("创建 remote answer") {
                public void onCreateSuccess(SessionDescription sessionDescription1) {
                    Log.e(TAGT, "remotePeerConnection.createAnswer->onCreateSuccess");
                    remotePeerConnection.setLocalDescription(new SdpObserver("remote 设置本地 sdp"), sessionDescription1);
//                    nioBinder.nioWriteString("remotePeerConnection.createAnswer->onCreateSuccess"+gson.toJson(sessionDescription1));



                }
            }, new MediaConstraints());
        } else if (ss.startsWith("localPeerConnection.createAnswer->onCreateSuccess")) {
            SessionDescription sessionDescription = gson.fromJson(
                    ss.substring("localPeerConnection.createAnswer->onCreateSuccess".length()), SessionDescription.class);
            Log.e(TAGT, "localPeerConnection.createAnswer->onCreateSuccess:remotePeerConnection.setRemoteDescription");
            remotePeerConnection.setRemoteDescription(new SdpObserver("把 sdp 给到 remote"), sessionDescription);
        } else if (ss.startsWith("localPeerConnection->onIceCandidate")) {
            IceCandidate iceCandidate = gson.fromJson(
                    ss.substring("localPeerConnection->onIceCandidate".length()), IceCandidate.class);
            Log.e(TAGT, "localPeerConnection->onIceCandidate:remotePeerConnection.addIceCandidate");
            remotePeerConnection.addIceCandidate(iceCandidate);
        } else {
            Log.e(TAGT, "----------"+ss);
        }
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
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    String peerId;
    @Override
    public void onReceive(String broadcastName, Object obj) {
        runOnUiThread(() -> {
            switch (broadcastName) {
                case Events.ACTION_ON_MSG_RECEIVE:
                    if (obj instanceof IceCandidate) {
                        Log.e(TAGT, "receive IceCandidate swap");
                        IceCandidate iceCandidate = (IceCandidate) obj;
                        Log.e(TAGT, "set receive IceCandidate swap");
                        remotePeerConnection.addIceCandidate(iceCandidate);
                    } else if (obj instanceof SessionDescription) {
                        Log.e(TAGT, "receive sdp swap");
                        SessionDescription sessionDescription = (SessionDescription) obj;
                        Log.e(TAGT, "set receive sdp swap");
                        remotePeerConnection.setRemoteDescription(new SdpObserver("把 sdp 给到 remote"), sessionDescription);
                    }else {
                        String displayMsg = (String) obj;
                        peerId = displayMsg.substring(0, 32);
                        receive_msg_content.append(displayMsg + "\r");
                    }
                    break;
                case Events.ACTION_ON_SDP_OFFER_RECEIVE:
                    SessionDescription sessionDescription = (SessionDescription) obj;
                    Log.e(TAGT, "receive sdp offer");
                    Log.e(TAGT, "set receive sdp offer");
                    remotePeerConnection.setRemoteDescription(new SdpObserver("把 sdp 给到 remote"), sessionDescription);
                    Log.e(TAGT, "set answer sdp");
                    remotePeerConnection.createAnswer(new SdpObserver("创建 remote answer") {
                        public void onCreateSuccess(SessionDescription sessionDescription1) {
                            Log.e(TAGT, "set answer sdp success");
                            remotePeerConnection.setLocalDescription(new SdpObserver("remote 设置本地 sdp"), sessionDescription1);
//                            nioBinder.nioWriteString("remotePeerConnection.createAnswer->onCreateSuccess"+gson.toJson(sessionDescription1));
                            Log.e(TAGT, "send swap sdp");
//                            nioBinder.sendSwapSdp(sessionDescription1, peerId);
                            ImService.getIm().sendVideoCmd(sessionDescription1, peerId, Coder.MSG_TYPE_SWAP_SDP, new SendNode.SendCallback() {
                                @Override
                                public void onSendSuccess(SendNode node) {

                                }

                                @Override
                                public void onSendFailed(IOException e, SendNode node) {

                                }
                            });

                        }
                    }, new MediaConstraints());
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

        ImService.getIm().sendTest(userNameIdMap.get(user), sendMsg, new SendNode.SendCallback() {
            @Override
            public void onSendSuccess(SendNode node) {

            }

            @Override
            public void onSendFailed(IOException e, SendNode node) {

            }
        });
    }

}