package com.jason.microstream.customer;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jason.microstream.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;

public class LoadingDialog extends Dialog {

    private static int delayTime = 1000;

    LinearLayout dialog_loading_view;
    ProgressBar loading_progress;
    LinearLayout dialog_succeed_view;
    LinearLayout dialog_fail_view;
    TextView dialog_succeed_hint_tv;
    TextView dialog_fail_hint_tv;

    public interface DisMissCallBack {
        void onDialogDisMiss();
    }

    private DisMissCallBack disMissCallBack_;

    public enum State {
        Fail,
        Succeed,
    }

    private Handler handler_ = new Handler();

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.my_dialog);
        setContentView(R.layout.dialog_loading_view);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        getWindow().setDimAmount(0);

        dialog_loading_view = findViewById(R.id.dialog_loading_view);
        loading_progress = findViewById(R.id.loading_progress);
        dialog_succeed_view = findViewById(R.id.dialog_succeed_view);
        dialog_fail_view = findViewById(R.id.dialog_fail_view);
        dialog_succeed_hint_tv = findViewById(R.id.dialog_succeed_hint_tv);
        dialog_fail_hint_tv = findViewById(R.id.dialog_fail_hint_tv);
    }

    public void dismissDialog() {
        if (!isShowing()) {
            return;
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            hide();
        } else {
            dismiss();

        }
    }


    public void showDialog() {
        if (isShowing()) {
            return;
        }
        dialog_loading_view.setVisibility(View.INVISIBLE);
        dialog_fail_view.setVisibility(View.GONE);
        dialog_succeed_view.setVisibility(View.GONE);
        show();
        handler_.postDelayed(() -> {
            if (isShowing()) {
                dialog_loading_view.setVisibility(View.VISIBLE);
            }
        }, delayTime);
    }


    public void dismissDialogDelay() {
        Observable.just(1)
                .delay(3, TimeUnit.SECONDS)
                .subscribe(integer -> dismissDialog());
    }
    public void dismissDialogWithState(State state) {
        if (disMissCallBack_ == null) {
            disMissCallBack_ = new DisMissCallBack() {
                @Override
                public void onDialogDisMiss() {

                }
            };
        }
        dialog_loading_view.setVisibility(View.GONE);
        dialog_fail_view.setVisibility(state == State.Fail ? View.VISIBLE : View.GONE);
        dialog_succeed_view.setVisibility(state == State.Succeed ? View.VISIBLE : View.GONE);
        handler_.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissDialog();
                disMissCallBack_.onDialogDisMiss();
            }
        }, 800);
    }

    public void dismissDialogWithCallBack(State state, DisMissCallBack callBack) {
        disMissCallBack_ = callBack;
        dismissDialogWithState(state);
    }

    public void dismissDialogWithStateAndHint(State state, String hint) {
        if (state == State.Fail) {
            dialog_fail_hint_tv.setText(hint);
        }
        if (state == State.Succeed) {
            dialog_succeed_hint_tv.setText(hint);
        }
        dismissDialogWithState(state);
    }

    public void dismissDialogWithHintAndCallBack(State state, String hint, DisMissCallBack callBack) {
        disMissCallBack_ = callBack;
        dismissDialogWithStateAndHint(state, hint);
    }


    public void onDestroy() {
        handler_.removeCallbacksAndMessages(null);
    }
}
