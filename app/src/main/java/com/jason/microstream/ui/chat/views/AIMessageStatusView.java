package com.jason.microstream.ui.chat.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jason.microstream.R;


public class AIMessageStatusView extends FrameLayout {
    public static class Status{
        public static final int GEN_SUCCESS = 0;
        public static final int GEN_ING = 1;
        public static final int GEN_FAIL = 2;

    }

    public AIMessageStatusView(@NonNull Context context) {
        super(context);
        init();
    }

    public AIMessageStatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AIMessageStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AIMessageStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private int status = Status.GEN_SUCCESS;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        showStatusView();
    }

    private void showStatusView() {
        if (status == Status.GEN_SUCCESS) {

        } else if (status == Status.GEN_ING) {
        } else if (status == Status.GEN_FAIL) {
        }

    }

    private void init() {

        LayoutInflater.from(getContext()).inflate(R.layout.part_ai_message_status, this, true);
        showStatusView();

    }



}
