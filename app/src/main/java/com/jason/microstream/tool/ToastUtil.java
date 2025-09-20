/**
 *
 */
package com.jason.microstream.tool;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.jason.microstream.BuildConfig;


public class ToastUtil {


    private static boolean filter = false;

    //过滤toast，以免短时间同时弹出很多toast
    public static void show(final Context context, final String info, long time) {
        if (!filter) {
            show(context, info);
            filter = true;
        }
//        Handlers.postDelayed(() -> filter = false, time);
    }

    public static void show(final Context context, final String info) {
        if (context == null) {
            return;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Toast mToast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.show();

        } else {
            Looper.getMainLooper().getQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    Toast mToast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    mToast.show();
                    return false;
                }
            });
//            Handlers.postMain(() -> {
//                Toast mToast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
//                mToast.setGravity(Gravity.CENTER, 0, 0);
//                mToast.show();
//            });
        }
    }

  public static void showDev(final Context context, final String info) {
      if (BuildConfig.DEBUG)
        show(context, info);

  }

    public static void show(final Context context, final int info) {
        if (context == null) {
            return;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            realShow(context, info);
        } else {
//            Handlers.postMain(() -> realShow(context, info));
        }
    }

    private static void realShow(final Context context, final int info) {
        Toast toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    public static void showLayout(final Activity activity, final int layoutId) {
        if (activity == null) {
            return;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            showToastLayout(activity, layoutId);
        } else {
//            Handlers.postMain(() -> {
//                showToastLayout(activity, layoutId);
//            });
        }
    }

    private static void showToastLayout(Activity activity, int layoutId) {
        Toast mToast = new Toast(activity);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        View view = activity.getLayoutInflater().inflate(layoutId, null);
        mToast.setView(view);
        mToast.show();
    }

    public static void showToastLayout(Context context, int layoutId) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Toast mToast = new Toast(context);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            View view = View.inflate(context, layoutId, null);
            mToast.setView(view);
            mToast.show();
        } else {
//            Handlers.postMain(() -> {
//                Toast mToast = new Toast(context);
//                mToast.setGravity(Gravity.CENTER, 0, 0);
//                View view = View.inflate(context, layoutId, null);
//                mToast.setView(view);
//                mToast.show();
//            });
        }
    }
}
