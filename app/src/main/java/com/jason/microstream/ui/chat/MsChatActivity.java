package com.jason.microstream.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gyf.immersionbar.ImmersionBar;
import com.jason.microstream.R;
import com.jason.microstream.ui.base.BasicActivity;
import com.jason.microstream.ui.base.BasicPresenter;

public class MsChatActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        getToolBarArea().setVisibility(View.VISIBLE);
        ImmersionBar.with(this)
                .titleBarMarginTop(parentLinearLayout)
                .init();
        setToolbarBg("#ffffff");
        setToolBarTitle("");
        setToolbarBoundary("#cccccc");


    }

    String userName;
    String userId;
    private void initData() {
        Intent comeIntent = getIntent();
        userName = comeIntent.getStringExtra("userName");
        userId = comeIntent.getStringExtra("userId");

        setToolBarTitle(userName);


    }

    @Override
    protected BasicPresenter onCreatePresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ms_chat;
    }
}