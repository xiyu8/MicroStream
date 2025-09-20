package com.jason.microstream.tool;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import com.jason.microstream.tool.log.LogTool;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.MediaType;

/**
 * @author: Limp
 * Date: 2020/8/19 17:12
 */
public class NetUtil {

    // 没有连接
    public static final int NETWORN_NONE = 0;

    // wifi连接
    public static final int NETWORN_WIFI = 1;

    // 手机网络数据连接
    public static final int NETWORN_2G = 2;

    public static final int NETWORN_3G = 3;

    public static final int NETWORN_4G = 4;

    public static final int NETWORN_MOBILE = 5;

    public static final int NETWORK_TYPE_NR = 20;

    public static final int SDK_VERSION_Q = 29;

    private static NetUtil netUtil;

    public static int currentNetType = 0;

    private Context context;

    private String ip = "";

    private boolean isConnected = false;

    private NetUtil(Context context) {
        this.context = context;
    }

    public static NetUtil getInstance(Context context) {
        if (netUtil == null) {
            synchronized (NetUtil.class) {
                if (netUtil == null) {
                    netUtil = new NetUtil(context);
                }
            }
        }
        return netUtil;
    }

    /**
     * 判断是否是流量
     */
    public static boolean is4G() {
        return checkNetworkConnection() == 2;
    }

    /**
     * 判断网络类型
     * 0 :不可用
     * 1 :wifi
     * 2 :移动流量
     */
    private static int checkNetworkConnection() {
        // Whether there is a Wi-Fi connection.
        boolean wifiConnected = false;
        // Whether there is a mobile connection.
        boolean mobileConnected = false;
        // BEGIN_INCLUDE(connect)
        ConnectivityManager connMgr =
                (ConnectivityManager) LocContext.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected) {
                return 1;
            } else if (mobileConnected) {
                return 2;
            }
        }
        return 0;
    }



    /**
     * 网络状态改变记录网络信息
     */
    public void recordNetworkStateChanges() {
//      int networkState = getNetworkState();
//      LogTool.i("网络变化", "上次: " + getNetWorkType(currentNetType) +
//              " 当前: " + getNetWorkType(networkState));
//      if (currentNetType != networkState && (networkState == NETWORN_4G || networkState == NETWORN_MOBILE) && (currentNetType == NETWORN_NONE || currentNetType == NETWORN_WIFI)) {
////        CallMgr.getInstance().endCall(CallMgr.getInstance().getCallId());
//      }
//      currentNetType = networkState;
//      if (networkState == NETWORN_NONE
//              && CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getCallId())!=null)
//        ToastUtil.show(LocContext.getContext(), "网络异常");
    }

    private int getNetworkState() {
        if(context==null) return NETWORN_NONE;
        ConnectivityManager connManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connManager == null) {
            return NETWORN_NONE;
        }

        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();

        // Wifi网络Info
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // 手机网络Info
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        // 记录Ip地址
        getCurrentIp(wifiInfo, networkInfo);

        // 记录网络连接状态变化
        if (recordNetworkConnecte(activeNetInfo) == NETWORN_NONE) {
            return NETWORN_NONE;
        }

        // 记录wifi网络信息
        if (isWifiConnect(wifiInfo)) {
            return NETWORN_WIFI;
        }

        // 记录移动网络信息
        int mobileNetworkState = getMobileNetworkState(networkInfo, activeNetInfo);
        if (mobileNetworkState != NETWORN_NONE) {
            return mobileNetworkState;
        }
        return NETWORN_NONE;
    }

    private String getNetWorkType(int netWorkState) {
        switch (netWorkState) {
            case NETWORN_NONE:
                return "无网络";
            case NETWORN_WIFI:
                return "wifi连接";
            case NETWORN_2G:
                return "2g网";
            case NETWORN_3G:
                return "3g网";
            case NETWORN_4G:
                return "4g网";
            case NETWORN_MOBILE:
                return "5g网";
            default:
                return "";
        }
    }
  private int recordNetworkConnecte(NetworkInfo activeNetInfo) {
    if (activeNetInfo == null || !activeNetInfo.isAvailable() || !activeNetInfo.isConnected()) {
      boolean lastConnected = isConnected;
      isConnected = false;
      LogTool.i("网络连接状态", "lastConnected: " + lastConnected
              + " currentConnected: " + isConnected);
      return NETWORN_NONE;
    }

    boolean lastConnected = isConnected;
    isConnected = true;
    LogTool.i("网络连接状态", "lastConnected: " + lastConnected
            + " currentConnected: " + isConnected);
    return NETWORN_MOBILE;
  }
    private String getLocalIpAddress() {
        try {
            ArrayList<NetworkInterface> nilist = Collections.list(
                    NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : nilist) {
                ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address : ialist) {
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }

            }
        } catch (SocketException ex) {
        }
        return "";
    }

    private String intToIp(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    private boolean isWifiConnect(NetworkInfo wifiInfo) {
        if (wifiInfo != null) {
            NetworkInfo.State state = wifiInfo.getState();
            if (state != null)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    LogTool.i("WIFI", "网络详情"+
                            "wifiInfo.getState(): " + wifiInfo.getState()+
                            " wifiInfo.getSubtype(): " + wifiInfo.getSubtype()+
                            " wifiInfo.getSubtypeName(): " + wifiInfo.getSubtypeName()+
                            " wifiInfo.getType(): " + wifiInfo.getType()+
                            " wifiInfo.getTypeName(): " + wifiInfo.getTypeName());
                    return true;
                }
        }
        return false;
    }


    private int getMobileNetworkState(NetworkInfo networkInfo, NetworkInfo activeNetInfo) {
        if (networkInfo != null) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (state != null) {
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    LogTool.i("移动网", "网络详情"+
                            "networkInfo.getState(): " + networkInfo.getState()+
                            " networkInfo.getSubtype(): " + networkInfo.getSubtype()+
                            " networkInfo.getSubtypeName(): " + networkInfo.getSubtypeName()+
                            " networkInfo.getType(): " + networkInfo.getType()+
                            " networkInfo.getTypeName(): " + networkInfo.getTypeName());
                    switch (getNetWorkType()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORN_2G;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORN_3G;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORN_4G;
                        case TelephonyManager.NETWORK_TYPE_NR:
                            return NETWORN_MOBILE;
                        default:
                            // 有机型返回16,17
                            //中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") ||
                                    strSubTypeName.equalsIgnoreCase("WCDMA") ||
                                    strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return NETWORN_3G;
                            } else {
                                return NETWORN_MOBILE;
                            }
                    }
                }
            }
        }
        return NETWORN_NONE;
    }

    private void getCurrentIp(NetworkInfo wifiInfo, NetworkInfo networkInfo) {
        String lastIpAddress = ip;
        if (networkInfo != null && networkInfo.isConnected()) {
            ip = getLocalIpAddress();
        } else if (wifiInfo != null && wifiInfo.isConnected()) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifi = wifiManager.getConnectionInfo();
            int ipAddress = wifi.getIpAddress();
            ip = intToIp(ipAddress);
        } else {
            ip = "";
        }
//        LogTool.i("IP变化", "lastIpAddress: (" + LogTool.commonDisplay(lastIpAddress) +
//                ") currentIpAddress: (" + LogTool.commonDisplay(ip) + ")");
    }

    /**
     * 获取当前移动网络类型
     *
     * @return
     */
    private int getNetWorkType() {
        int networkType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            int defaultDataSubId = getSubId();
            if (defaultDataSubId == -1) {
                networkType = tm.getNetworkType();
            } else {
                try {
                    Method dataNetworkType = TelephonyManager.class
                            .getDeclaredMethod("getDataNetworkType", new Class[]{int.class});
                    dataNetworkType.setAccessible(true);
                    networkType = (int) dataNetworkType.invoke(tm, defaultDataSubId);
                } catch (Throwable t) {
                    networkType = tm.getNetworkType();
                }
            }
        } catch (Throwable t) {
            // do nothing
        }
        if (networkType == TelephonyManager.NETWORK_TYPE_LTE) {
            networkType = adjustNetworkType(networkType);
        }
        return networkType;
    }

    private int adjustNetworkType(int networkTypeFromSys) {
        int networkType = networkTypeFromSys;
        if (Build.VERSION.SDK_INT >= SDK_VERSION_Q
                && context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                ServiceState ss;
                int defaultDataSubId = getSubId();
                if (defaultDataSubId == -1) {
                    ss = tm.getServiceState();
                } else {
                    try {
                        Class<TelephonyManager> infTm = TelephonyManager.class;
                        Method method = infTm
                                .getDeclaredMethod("getServiceStateForSubscriber",
                                        new Class[]{int.class});
                        method.setAccessible(true);
                        ss = (ServiceState) method.invoke(tm, defaultDataSubId);
                    } catch (Throwable t) {
                        ss = tm.getServiceState();
                    }
                }
                if (ss != null && isServiceStateFiveGAvailable(ss.toString())) {
                    networkType = NETWORK_TYPE_NR;
                }
            } catch (Exception e) {
                // do nothing
            }
        }
        return networkType;
    }

    private int getSubId() {
        int defaultDataSubId = -1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            defaultDataSubId = SubscriptionManager.getDefaultDataSubscriptionId();
        }
        return defaultDataSubId;
    }

    private boolean isServiceStateFiveGAvailable(String ss) {
        boolean available = false;
        if (!TextUtils.isEmpty(ss)
                && (ss.contains("nrState=NOT_RESTRICTED")
                || ss.contains("nrState=CONNECTED"))) {
            available = true;
        }
        return available;
    }



    public static boolean mediaTypeIsText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
            )
                return true;
        }
        return false;
    }

}
