package com.jason.microstream;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.gyf.immersionbar.ImmersionBar;
import com.jason.microstream.account.AccountManager;
import com.jason.microstream.core.im.imconpenent.ImService;
import com.jason.microstream.model.User;
import com.jason.microstream.tool.CommonSharePrefsManager;
import com.jason.microstream.tool.TextUtil;
import com.jason.microstream.ui.base.BasicActivity;
import com.jason.microstream.ui.base.BasicPresenter;
import com.jason.microstream.ui.main.MainActivity;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.List;
//import com.tbruyelle.rxpermissions3.RxPermissions;

public class SplashActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStatusBarInflater().setVisibility(View.GONE);
        getToolBarArea().setVisibility(View.GONE);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                .init();

//        PermissionX.init(this)
//                .permissions(Manifest.permission.CAMERA)
//                .request((allGranted, grantedList, deniedList) -> {
//
//                });


        User savedUser = AccountManager.get().getSavedInitUser();
        Bundle anmiBundle = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, findViewById(R.id.splash_logo), getString(R.string.logo_transaction)).toBundle();
        if (savedUser != null) {
            /**
             * for test video activity
             */
//            startActivity(new Intent(this, MainActivity1_.class));
//            finish();

            ImService.getIm().auth(AccountManager.get().getToken(), AccountManager.get().getUid());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent, anmiBundle);
                    isTrans = true;
                }
            }, 100);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent, anmiBundle);
                    isTrans = true;

                }
            }, 100);
        }
    }

    private boolean isTrans = false;  //for the trans animation
    @Override
    public void onStop() {
        if (isTrans) {
            finish();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        finish();
//        finishAfterTransition();
    }

    @Override
    protected BasicPresenter onCreatePresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }
}