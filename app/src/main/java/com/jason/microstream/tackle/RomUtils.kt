//package com.shinemo.cloudvc.tackle
//
//import android.app.AppOpsManager
//import android.content.Context
//import android.net.Uri
//import android.os.Build
//import android.provider.Settings
//import java.lang.reflect.Method
//
///**
// * Author: panjiejun
// * Date: 6/8/21 11:41 AM
// * Description:
// */
//object RomUtils {
//    private val TAG = RomUtils::class.java.simpleName
//
//    fun isXiaoMi(): Boolean {
//        return checkManufacturer("xiaomi")
//    }
//
//    fun isOppo(): Boolean {
//        return checkManufacturer("oppo")
//    }
//
//    fun isVivo(): Boolean {
//        return checkManufacturer("vivo")
//    }
//
//    private fun checkManufacturer(manufacturer: String): Boolean {
//        return manufacturer.equals(Build.MANUFACTURER, true)
//    }
//
//    fun isBackgroundStartAllowed(context: Context): Boolean {
//        if (isXiaoMi()) {
//            return isXiaomiBgStartPermissionAllowed(context)
//        }
//
//        if (isVivo()) {
//            return isVivoBgStartPermissionAllowed(context)
//        }
//
//        if (isOppo() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            return Settings.canDrawOverlays(context)
//        }
//        return true
//    }
//
//
//    private fun isXiaomiBgStartPermissionAllowed(context: Context): Boolean {
//        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
//        try {
//            val op = 10021
//            val method: Method = ops.javaClass.getMethod("checkOpNoThrow", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, String::class.java)
//            val result = method.invoke(ops, op, android.os.Process.myUid(), context.packageName) as Int
//            return result == AppOpsManager.MODE_ALLOWED
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return false
//    }
//
//    private fun isVivoBgStartPermissionAllowed(context: Context): Boolean {
//        return getVivoBgStartPermissionStatus(context) == 0
//    }
//
//    /**
//     * 判断Vivo后台弹出界面状态， 1无权限，0有权限
//     * @param context context
//     */
//    private fun getVivoBgStartPermissionStatus(context: Context): Int {
//        val uri: Uri = Uri.parse("content://com.vivo.permissionmanager.provider.permission/start_bg_activity")
//        val selection = "pkgname = ?"
//        val selectionArgs = arrayOf(context.packageName)
//        var state = 1
//        try {
//            context.contentResolver.query(uri, null, selection, selectionArgs, null)?.use {
//                if (it.moveToFirst()) {
//                    state = it.getInt(it.getColumnIndex("currentstate"))
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return state
//    }
//
//
//    public  fun canShowLockView(context :Context) :Boolean{
//        val ops = null
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
//        }
//        if (Build.BRAND.toLowerCase().equals("redmi") || Build.BRAND.toLowerCase().equals("xiaomi") || Build.BRAND.toLowerCase().equals("vivo"))
//            try {
//                if (Build.BRAND.toLowerCase().equals("vivo")) {
//                    val state=OSTool.getVivoLockStatus(context)
//                    return state == 0
//                }
//
//                val op = 10020
//                val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
//                val method: Method = ops.javaClass.getMethod("checkOpNoThrow", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, String::class.java)
//                val result = method.invoke(ops, op, android.os.Process.myUid(), context.packageName) as Int
//                return result == AppOpsManager.MODE_ALLOWED
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        return true;
//    }
//}