package com.jason.microstream.tool;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import com.jason.microstream.BuildConfig;

public class OSTool {


  public static void enterWhiteListSetting(Context context) {
    try {
      context.startActivity(getSettingIntent());
    } catch (Exception e) {
      context.startActivity(new Intent(Settings.ACTION_SETTINGS));
    }
  }

  private static Intent getSettingIntent() {

    ComponentName componentName = null;

    String brand = Build.BRAND;

    switch (brand.toLowerCase()) {
      case "samsung":
        componentName = new ComponentName("com.samsung.android.sm",
                "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity");
        break;
      case "honor":
      case "huawei":
        componentName = new ComponentName("com.huawei.systemmanager",
                "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
        break;
      case "redmi":
      case "xiaomi":
        componentName = new ComponentName("com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity");
        break;
      case "vivo":
        componentName = new ComponentName("com.iqoo.secure",
                "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");
        break;
      case "oppo":
        componentName = new ComponentName("com.coloros.oppoguardelf",
                "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");
        break;
      case "360":
        componentName = new ComponentName("com.yulong.android.coolsafe",
                "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity");
        break;
      case "meizu":
        componentName = new ComponentName("com.meizu.safe",
                "com.meizu.safe.permission.SmartBGActivity");
        break;
      case "oneplus":
        componentName = new ComponentName("com.oneplus.security",
                "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity");
        break;
      default:
        break;
    }

    Intent intent = new Intent();
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    if (componentName != null) {
      intent.setComponent(componentName);
    } else {
      intent.setAction(Settings.ACTION_SETTINGS);
    }
    return intent;
  }


  public static boolean isIgnoringBatteryOptimizations(Context context) {
    boolean isIgnoring = false;
    PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    if (powerManager != null) {
      isIgnoring = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
    }
    return isIgnoring;
  }


  public static void requestIgnoreBatteryOptimizations(Context context) {
    try {
      Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
      intent.setData(Uri.parse("package:" + context.getPackageName()));
      context.startActivity(intent);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


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

//  public static void toPermissionSetting(Context context) throws NoSuchFieldException, IllegalAccessException {
//    if (Build.VERSION.SDK_INT < 23) {
//      if (Build.BRAND.toLowerCase().equals("xiaomi")) {
//        gotoMiuiPermission(context);
//      } else if (Build.BRAND.toLowerCase().equals("meizu")) {
//        MeizuUtils.applyPermission(context);
//      } else if (Build.BRAND.toLowerCase().equals("huawei")||Build.BRAND.toLowerCase().equals("honor")) {
//        HuaweiUtils.applyPermission(context);
//      } else if (Build.BRAND.toLowerCase().equals("360")) {
//        QikuUtils.applyPermission(context);
//      } else if (Build.BRAND.toLowerCase().equals("oppo")) {
//        OppoUtils.applyOppoPermission(context);
//      }
//    }else {
////      startNotificationSetting(context);
//      if (Build.BRAND.toLowerCase().equals("xiaomi")||Build.BRAND.toLowerCase().equals("redmi")) {
//        MiuiUtils.applyMiuiPermission(context);
//      } else if (Build.BRAND.toLowerCase().equals("meizu")) {
//        gotoMeizuPermission(context);
//      } else if (Build.BRAND.toLowerCase().equals("huawei")||Build.BRAND.toLowerCase().equals("honor")) {
//        gotoHuaweiPermission(context);
//      } else if (Build.BRAND.toLowerCase().equals("vivo")) {
//        Intent localIntent;
//        if (((Build.MODEL.contains("Y85")) && (!Build.MODEL.contains("Y85A"))) || (Build.MODEL.contains("vivo Y53L"))) {
//          localIntent = new Intent();
//          localIntent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.PurviewTabActivity");
//          localIntent.putExtra("packagename", context.getPackageName());
//          localIntent.putExtra("tabId", "1");
//          context.startActivity(localIntent);
//        } else {
//          localIntent = new Intent();
//          localIntent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity");
//          localIntent.setAction("secure.intent.action.softPermissionDetail");
//          localIntent.putExtra("packagename", context.getPackageName());
//          context.startActivity(localIntent);
//        }
//        return;
//      } else if (Build.BRAND.toLowerCase().equals("360")) {
//        QikuUtils.applyPermission(context);
//      } else if (Build.BRAND.toLowerCase().equals("oppo")) {
//        OppoUtils.applyOppoPermission(context);
//      } else if (Build.BRAND.toLowerCase().equals("vivo")) {
//        Intent localIntent = new Intent();
//        localIntent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity");
//        localIntent.setAction("secure.intent.action.softPermissionDetail");
//        localIntent.putExtra("packagename", context.getPackageName());
//        context.startActivity(localIntent);
//
//      }
//
//    }
////    if (RomUtils.checkIsMeizuRom()) {
////      MeizuUtils.applyPermission(context);
////    } else {
////      if (Build.VERSION.SDK_INT >= 23) {
////        FloatWindowManager.commonROMPermissionApplyInternal(context);
////      }
////    }
//  }

  public static void gotoAppDetailSettingIntent(Context context) {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    intent.setData(Uri.parse("package:" + context.getPackageName()));
    context.startActivity(intent);
  }


  /**
   * 打开设置通知权限页面
   * @param context
   */
  public static void startNotificationSetting(Context context) {
    try {
      Intent intent = new Intent();
      intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
//这种方案适用于 API 26, 即8.0(含8.0)以上可以用
      intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
      intent.putExtra(Notification.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
//这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
      intent.putExtra("app_package", context.getPackageName());
      intent.putExtra("app_uid", context.getApplicationInfo().uid);
      context.startActivity(intent);
    } catch (Exception e) {
      try {
// 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
      } catch (Exception ex) {
      }
    }
  }
  /**
   * 跳转到miui的权限管理页面
   */
  private static void gotoMiuiPermission(Context context) {
    try { // MIUI 8
      Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
      localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
      localIntent.putExtra("extra_pkgname", context.getPackageName());
      context.startActivity(localIntent);
    } catch (Exception e) {
      try { // MIUI 5/6/7
        Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        localIntent.putExtra("extra_pkgname", context.getPackageName());
        context.startActivity(localIntent);
      } catch (Exception e1) { // 否则跳转到应用详情
        context.startActivity(getAppDetailSettingIntent(context));
      }
    }
  }

  /**
   * 跳转到魅族的权限管理系统
   */
  private static void gotoMeizuPermission(Context context) {
    try {
      Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
      intent.addCategory(Intent.CATEGORY_DEFAULT);
      intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
      context.startActivity(intent);
    } catch (Exception e) {
      e.printStackTrace();
      context.startActivity(getAppDetailSettingIntent(context));
    }
  }

  /**
   * 华为的权限管理页面
   */
  private static void gotoHuaweiPermission(Context context) {
    try {
      Intent intent = new Intent();
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainOldActivity");//华为权限管理
      intent.setComponent(comp);
      context.startActivity(intent);
    } catch (Exception e) {
      e.printStackTrace();
      context.startActivity(getAppDetailSettingIntent(context));
    }

  }

  /**
   * 获取应用详情页面intent（如果找不到要跳转的界面，也可以先把用户引导到系统设置页面）
   *
   * @return
   */
  private static Intent getAppDetailSettingIntent(Context context) {
    Intent localIntent = new Intent();
    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    if (Build.VERSION.SDK_INT >= 9) {
      localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
      localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
    } else if (Build.VERSION.SDK_INT <= 8) {
      localIntent.setAction(Intent.ACTION_VIEW);
      localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
      localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
    }
    return localIntent;
  }
  /**
   * 判断vivo锁屏显示 1未开启 0开启
   * @param context
   * @return
   */
  public static int getVivoLockStatus(Context context) {
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
          return currentmode;
        } else {
          cursor.close();
          return 1;
        }
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
    return 1;
  }



}
