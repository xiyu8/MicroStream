package com.jason.microstream.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;
import com.jason.microstream.R;
import com.jason.microstream.tool.log.LogTag;
import com.jason.microstream.tool.log.LogTool;
import com.jason.microstream.ui.base.BasicFragment;
import com.jason.microstream.ui.base.BasicPresenter;


public abstract class MainFragment<P extends BasicPresenter<? extends BasicPresenter.View>> extends BasicFragment<P> {

    private String mTitle;

    TextView tab_title;
    ImageView tab_icon;

    boolean selected;

    ViewGroup tab_view;

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showTabData();
    }

    boolean isLoad = false;
    @Override
    public void onResume() {
        super.onResume();
        if (!isLoad) {
            lazyInit();
            isLoad = true;
        }
    }

    @Override
    public void onDestroy() {
        isLoad = false;
        super.onDestroy();
    }

    public void lazyInit() {

    }

    @Override
    protected void setStatusBarView() {
        super.setStatusBarView();
        ImmersionBar.with(this)
                .titleBarMarginTop(getView().findViewById(R.id.conversation_list_top))
                .init();
    }

    public void setTabView(Context context, @LayoutRes int layout) {
        tab_view = (ViewGroup) LayoutInflater.from(context).inflate(layout, null);
        tab_title = tab_view.findViewById(R.id.tab_title);
        tab_icon = tab_view.findViewById(R.id.tab_icon);
        if (tab_view == null || tab_title == null || tab_icon == null) {
            throw new RuntimeException("error tab layout !");
        }
    }



    public ViewGroup getTabView() {
        if(getContext()==null) return tab_view;
        showTabData();
        return tab_view;
    }

    private void showTabData() {
        if (tab_title != null && getContext() != null) {
            tab_title.setTextColor(getContext().getColor(selected ? R.color.ms_theme : R.color.sh_text));
            tab_title.setText(mTitle);
        }
        if(tab_icon!=null){
            tab_icon.setImageResource(selected ? getSelectedIcon() : getUnselectedIcon());
        }
    }

    protected abstract @DrawableRes int getSelectedIcon();
    protected abstract @DrawableRes int getUnselectedIcon();

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        showTabData();

    }


}
