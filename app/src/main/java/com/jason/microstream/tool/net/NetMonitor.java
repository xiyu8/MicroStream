package com.jason.microstream.tool.net;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;

import com.jason.microstream.account.AccountManager;
import com.jason.microstream.core.im.imconpenent.ImService;
import com.jason.microstream.tool.DeviceManager;
import com.jason.microstream.tool.LocContext;
import com.jason.microstream.tool.NetUtil;

import java.util.regex.Pattern;

public class NetMonitor extends BroadcastReceiver {

    public static String TAG = "NetMonitor";

    public void init(Application application) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        application.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connMgr.getAllNetworks();
//        LogTool.i(TAG, "NetWorkStateReceiver:" + (networks.length != 0));
        NetUtil.getInstance(LocContext.getContext()).recordNetworkStateChanges();
        if (networks.length == 0) {
//            LogTool.i(TAG, "NetWorkStateReceiver:" + (networks.length == 0));
        } else {
//            LogTool.i(TAG, "NetWorkStateReceiver:" + true);
            String localIpAddress = DeviceManager.getLocalIpAddress(true);
        }
    }


    /**
     * 判断IP是否为IPv6格式
     * @param ipAddress
     * @return
     */
    public static boolean isIPv6(String ipAddress) {
        String ipv6 = "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}" +
                "(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))" +
                "|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)" +
                "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}" +
                "(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)" +
                "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})" +
                "|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))" +
                "|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)" +
                "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})" +
                "|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))" +
                "|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)" +
                "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$";
        boolean isIP = Pattern.matches(ipv6, ipAddress);
        return isIP;
    }
}
