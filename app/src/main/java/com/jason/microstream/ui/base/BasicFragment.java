package com.jason.microstream.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gyf.immersionbar.ImmersionBar;
import com.jason.microstream.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BasicFragment<P extends BasicPresenter<? extends BasicPresenter.View>> extends Fragment implements BasicPresenter.View{

    private Unbinder mUnbinder;
    private P mPresenter;

    View status_bar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = null;
        ViewGroup viewContainer = (ViewGroup) inflater.inflate(R.layout.fragment_basic, container, false);
        status_bar = viewContainer.findViewById(R.id.status_bar);
        if (onCreateLayout() != -1) {
            inflater.inflate(onCreateLayout(), viewContainer.findViewById(R.id.fragment_content), true);
            mUnbinder = ButterKnife.bind(this, viewContainer);
        }
        mPresenter = onCreatePresenter();

        if (viewContainer == null) {
            viewContainer = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        }
        // initTopBar(viewContainer);
        return viewContainer;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setStatusBarView();
    }

    protected void setStatusBarView() {
//        status_bar.getLayoutParams().height = ImmersionBar.getStatusBarHeight(this);
//        status_bar.setVisibility(View.VISIBLE);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                .init();
    }

    public P getPresenter() {
        return mPresenter;
    }

    protected abstract int onCreateLayout();

    protected abstract P onCreatePresenter();

    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }


}