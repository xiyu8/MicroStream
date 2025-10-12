package com.jason.microstream.ui.contact;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.jason.microstream.R;
import com.jason.microstream.ui.base.BasicActivity;
import com.jason.microstream.ui.base.BasicPresenter;
import com.jason.microstream.ui.chat.MsChatActivity;

public class MyContactActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_contact;
    }

    @Override
    protected BasicPresenter onCreatePresenter() {
        return null;
    }

    private void initView() {
        getToolBarArea().setVisibility(View.VISIBLE);
        ImmersionBar.with(this)
                .titleBarMarginTop(parentLinearLayout)
                .init();
        setToolbarBg("#ffffff");
        setToolBarTitle("");
        setToolbarBoundary("#cccccc");

        findViewById(R.id.user_send_area).setOnClickListener(this);

    }

    String userName;
    String userId;
    private void initData() {
        Intent comeIntent = getIntent();
        userName = comeIntent.getStringExtra("userName");
        userId = comeIntent.getStringExtra("userId");

        TextView user_name = findViewById(R.id.user_name);
        TextView user_id = findViewById(R.id.user_id);
        user_name.setText(userName);
        user_id.setText("UID:"+userId);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.user_send_area:
                Intent intent = new Intent(this, MsChatActivity.class);
                intent.putExtra("userName", userName);
                intent.putExtra("userId", userId);
                startActivity(intent);
                break;
        }
    }
}