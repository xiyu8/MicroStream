package com.jason.microstream.tool;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 */
public class ToastUtils {
    static Toast toast;

    public static void toastShow(Context context, String str) {
        toast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        toast.setText(str);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
