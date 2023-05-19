package com.jason.microstream.tackle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.lang.reflect.Method;

public class SystemRomTool {

  //显示在其它应用上层检测
  public static boolean checkDrawOverlays(Activity activity){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return Settings.canDrawOverlays(activity);
    }
    return true;
  }


  /**
   * 锁屏显示权限检测
   */
  public static boolean canShowLockView(Context context) {
    if (Build.BRAND.toLowerCase().equals("xiaomi") || Build.BRAND.toLowerCase().equals("redmi")) {
      AppOpsManager ops = null;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        ops = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
      }
      try {
        int op = 10020; // >= 23
        // ops.checkOpNoThrow(op, uid, packageName)
        Method method = ops.getClass().getMethod("checkOpNoThrow", new Class[]
                {int.class, int.class, String.class}
        );
        Integer result = (Integer) method.invoke(ops, op,  android.os.Process.myUid(), context.getPackageName());

        return result == AppOpsManager.MODE_ALLOWED;

      } catch (Exception e) {
        e.printStackTrace();
      }
      return false;
    } else if (Build.BRAND.toLowerCase().equals("vivo")) {
      String packageName = context.getPackageName();
      Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/control_locked_screen_action");
      String selection = "pkgname = ?";
      String[] selectionArgs = new String[]{packageName};
      try {
        Cursor cursor = context
                .getContentResolver()
                .query(uri2, null, selection, selectionArgs, null);
        if (cursor != null) {
          if (cursor.moveToFirst()) {
            int currentmode = cursor.getInt(cursor.getColumnIndex("currentstate"));
            cursor.close();
            return currentmode==0;
          } else {
            cursor.close();
            return false;
          }
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
      return false;
    } /*else if () {
    }*/

    return true;
  }

  /**
   * 后台弹出界面权限检测  :只有 小米 vivo
   */
  public static boolean isAllowedBackPopPermission(Context context) {
    if(Build.BRAND.toLowerCase().equals("xiaomi")||Build.BRAND.toLowerCase().equals("redmi")) {
      AppOpsManager ops = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
      try {
        int op = 10021;
        Method method = ops.getClass().getMethod("checkOpNoThrow", new Class[]{int.class, int.class, String.class});
        Integer result = (Integer) method.invoke(ops, op, android.os.Process.myUid(), context.getPackageName());
        return result == AppOpsManager.MODE_ALLOWED;

      } catch (Exception e) {
      }
      return false;
    }else if(Build.BRAND.toLowerCase().equals("vivo")){
      String packageName = context.getPackageName();
      Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/start_bg_activity");
      String selection = "pkgname = ?";
      String[] selectionArgs = new String[]{packageName};
      try {
        Cursor cursor = context
                .getContentResolver()
                .query(uri2, null, selection, selectionArgs, null);
        if (cursor != null) {
          if (cursor.moveToFirst()) {
            int currentmode = cursor.getInt(cursor.getColumnIndex("currentstate"));
            cursor.close();
            return currentmode==0;
          } else {
            cursor.close();
            return false;
          }
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
      return false;
    }/* else if () {
    }*/

    return true;
  }







  @TargetApi(Build.VERSION_CODES.M)
  private void jumpOverlayPermissionSetting(Activity context) {
    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
    intent.setData(Uri.parse("package:" + context.getPackageName()));
    context.startActivityForResult(intent, 0);
  }


  //跳转到自启动设置
  public static void jumpAutoLaunchSetting(Context context) {
    ComponentName componentName = null;
    int sdkVersion = Build.VERSION.SDK_INT;
    if (Build.BRAND.toLowerCase().equals("huawei")||Build.BRAND.toLowerCase().equals("honor")){
      if (sdkVersion >= 28){//9:已测试
        componentName = ComponentName.unflattenFromString("com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");//跳自启动管理
      }else if (sdkVersion >= 26){//8：已测试
        componentName = ComponentName.unflattenFromString("com.huawei.systemmanager/.appcontrol.activity.StartupAppControlActivity");
      }else if (sdkVersion >= 23){//7.6：已测试
        componentName = ComponentName.unflattenFromString("com.huawei.systemmanager/.startupmgr.ui.StartupNormalAppListActivity");
      }else if (sdkVersion >= 21){//5
        componentName = ComponentName.unflattenFromString("com.huawei.systemmanager/.optimize.bootstart.BootStartActivity");
      }
    } else if (Build.BRAND.toLowerCase().equals("xiaomi")||Build.BRAND.toLowerCase().equals("redmi")) {
      if (sdkVersion >= 23) {//8.7.6：已测试
        componentName = ComponentName.unflattenFromString("com.miui.securitycenter/com.miui.permcenter.autostart.AutoStartManagementActivity");
      }
    }else if (Build.BRAND.toLowerCase().equals("vivo")) {
      if (sdkVersion >= 23) {//8.7.6：已测试
        componentName = ComponentName.unflattenFromString("com.vivo.permissionmanager/.activity.BgStartUpManagerActivity");
      }
    }else if (Build.BRAND.toLowerCase().equals("iqoo")) {
      componentName = ComponentName.unflattenFromString("com.iqoo.secure/.ui.phoneoptimize.SoftwareManagerActivity");
    }else if (Build.BRAND.toLowerCase().equals("oppo")) {
      if (sdkVersion >= 23) {//8、7、6
        componentName = ComponentName.unflattenFromString("com.coloros.safecenter/.startupapp.StartupAppListActivity");
      } else if (sdkVersion >= 21) {
        componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");
      }
    }else if (Build.BRAND.toLowerCase().equals("samsung")) {
      if (sdkVersion >= 25) {//8、7
        componentName = ComponentName.unflattenFromString("com.samsung.android.sm_cn/com.samsung.android.sm.ui.ram.AutoRunActivity");
      }
    }else if (Build.BRAND.toLowerCase().equals("meizu")) {

    }

    try {
      Intent intent = new Intent();
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.setComponent(componentName);
      context.startActivity(intent);
    }catch (Exception e){
      //跳转失败
    }
  }
}
