package com.jason.microstream.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jason.microstream.R;
import com.jason.microstream.ui.base.BasicActivity;
import com.jason.microstream.ui.base.BasicPresenter;

public class TestSettingActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected BasicPresenter onCreatePresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_setting;
    }
}