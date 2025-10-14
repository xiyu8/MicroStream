package com.jason.microstream.ui.chat.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class TwinkleTextView extends AppCompatTextView {


    boolean twinkle;

    public TwinkleTextView(@NonNull Context context) {
        super(context);
    }

    public TwinkleTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TwinkleTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTwinkle(boolean twinkle) {
        boolean isDiff = true;
        if (this.twinkle == twinkle) {
            isDiff = false;
        }
        this.twinkle = twinkle;
        _isShow = twinkle;
        if (twinkle) {
            String tempScrString = getText().toString();
            if (tempScrString == null) {
                tempScrString = "";
            }
            if (isDiff) {
                if (_isShow) {
                    setText(tempScrString + cursorString);
                } else {
                    setText(tempScrString + cursorStringBlank);
                }
            }
            if(getHandler()!=null) getHandler().post(runnable);
        } else {
            if(getHandler()!=null)  getHandler().removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setTwinkle(twinkle);
    }

    @Override
    protected void onDetachedFromWindow() {
        twinkle = false;
        if(getHandler()!=null) getHandler().removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    public boolean _isShow = false;
    private final String cursorString = "_";
    private final String cursorStringBlank = " ";
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(!twinkle) return;
            String tempScrString = getText().toString();
            if (tempScrString == null) {
                tempScrString = "";
            }
            if (!_isShow) {
                if (tempScrString.equals("")) {
                    setText(cursorString);
                } else {
                    setText(tempScrString.subSequence(0, tempScrString.length() - cursorString.length()) + cursorStringBlank);
                }
                _isShow = true;
            } else {
                if (tempScrString.equals("")) {
                    setText(cursorStringBlank);
                } else {
                    setText(tempScrString.subSequence(0, tempScrString.length() - cursorStringBlank.length()) + cursorString);
                }
                _isShow = false;
            }
            if(getHandler()!=null)  getHandler().postDelayed(this, 410);
        }
    };

}
