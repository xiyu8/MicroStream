package com.jason.microstream.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//import com.gyf.barlibrary.ImmersionBar;
import com.gyf.immersionbar.ImmersionBar;
import com.jason.microstream.R;
import com.jason.microstream.customer.LoadingDialog;
import com.jason.microstream.tool.log.LogTool;
import com.jason.microstream.ui.base.error.DefaultErrorViewFactory;
//import com.noober.background.BackgroundLibrary;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.rxjava3.disposables.CompositeDisposable;


public abstract class BasicActivity<P extends BasicPresenter> extends AppCompatActivity 
                                                            implements View.OnClickListener {
    protected String TAG = BasicActivity.class.getSimpleName();

    //the container of this activity layout and sub-activity layout
    public LinearLayout parentLinearLayout;
    private TextView toolbarTitle;
    private TextView toolbarRight;
    private Unbinder mUnbinder;
    private LinearLayout toolBarArea;
    private LoadingDialog loadingDialog;
    private P mPresenter;
    protected CompositeDisposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        BackgroundLibrary.inject(this);
        super.onCreate(savedInstanceState);
        // ActivityCollector.addActivity(this);
        TAG = this.getClass().getSimpleName();
        LogTool.i(TAG, "Activity Name : " + getClass().getName());

        initContentView(R.layout.activity_basic);  //把toolbar加到contentView
        setContentView(getLayoutId());      //把contentView的其它加入
        initBasicToolbar();

        loadingDialog = new LoadingDialog(this);
        mUnbinder = ButterKnife.bind(this);
        // setStatusBar();

        disposable = new CompositeDisposable();
    }

    public P getPresenter() {
        if (mPresenter == null)
            mPresenter = onCreatePresenter();
        return mPresenter;
    }

    protected abstract P onCreatePresenter();

    protected abstract int getLayoutId();

    private void initBasicToolbar() {
//        ImmersionBar.with(this).statusBarDarkFont(true, 0.5f).init();
        ImmersionBar.with(this)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                .init();
        this.setSupportActionBar(getToolbar());
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle = (TextView) findViewById(R.id.tv_title);
        toolbarRight = (TextView) findViewById(R.id.tv_right);
        if (null != getToolbar() && isShowBacking()) {
            getToolbar().setNavigationIcon(R.drawable.back);
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    public ViewGroup viewGroup;

    private void initContentView(@LayoutRes int layoutResID) {
        viewGroup = (ViewGroup) findViewById(android.R.id.content);
        viewGroup.removeAllViews();
        parentLinearLayout = new LinearLayout(this);
        parentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        viewGroup.addView(parentLinearLayout);
        toolBarArea = (LinearLayout) LayoutInflater.from(this).inflate(layoutResID, parentLinearLayout, false);
        parentLinearLayout.addView(toolBarArea);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (!isShowToolbar())
            super.setContentView(layoutResID);
        else
            LayoutInflater.from(this).inflate(layoutResID, parentLinearLayout, true);
    }

    protected LinearLayout getToolBarArea() {
        return toolBarArea;
    }

    protected Toolbar getToolbar() {
        return findViewById(R.id.toolbar);
    }

    protected View getStatusBarInflater() {
        return findViewById(R.id.status_bar_inflater);
    }

    protected TextView getToolbarSubTitle() {
        return toolbarRight;
    }

    protected View getToolbarBoundary() {
        return findViewById(R.id.toolbar_boundary);
    }

    protected TextView getToolbarTitle() {
        return toolbarTitle;
    }

    protected boolean isShowBacking() {
        return true;
    }

    public Boolean isShowToolbar() {
        return true;
    }

    public void setToolbarBg(String colorString) {
        findViewById(R.id.toolbarArea).setBackgroundColor(Color.parseColor(colorString));
    }

    public void setStatusBarInflaterColor(String colorString) {
        findViewById(R.id.status_bar_inflater).setBackgroundColor(Color.parseColor(colorString));
    }

    public void setToolbarBoundary(String colorString) {
        findViewById(R.id.toolbar_boundary).setBackgroundColor(Color.parseColor(colorString));
    }

    public void setToolBarTitle(CharSequence title) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        } else {
            this.getToolbar().setTitle(title);
        }
    }

    protected <T extends Activity> void readyGo(Class<T> clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
    }

    protected <T extends Activity> void readyGo(Class<T> clz, String key, String value) {
        Intent intent = new Intent(this, clz);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    protected <T extends Activity> void readyGo(Class<T> clz) {
        readyGo(clz, null);
    }

    protected <T extends Activity> void readyGoForResult(Class<T> clz, Bundle bundle, int code) {
        Intent intent = new Intent(this, clz);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivityForResult(intent, code);
    }

    protected <T extends Activity> void readyGoForResult(Class<T> clz, int code) {
        readyGoForResult(clz, null, code);
    }


    public LoadingDialog getLoadingDialog() {
        return loadingDialog;
    }

    View errorView;

    public void handleError(Error error) {
        boolean handled = false;
//    if(error.getMessage().contains("权限拒绝")){
//      ToastUtil.show(this,"未获取权限");
//      handled = true;
//    } else if (true/*vc未登录  等*/) {
//      ToastUtil.show(this,"未知错误："+error.getMessage());
//    }
        if (handled) return;
        errorView = new DefaultErrorViewFactory(this).getErrorView("网络错误", "点击重试", 0);
        viewGroup.addView(errorView);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.refresh_reload) {
            removeErrorView();
        }
    }

    protected void removeErrorView() {
        if (errorView != null) viewGroup.removeView(errorView);
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        loadingDialog.onDestroy();
//    ActivityCollector.removeActivity(this);
        if (mPresenter != null) mPresenter.onDestroy();
//        ImmersionBar.with(this).destroy();

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();
    }
    @Override
    public void onStop() {
        if (getPresenter() != null) {
            mPresenter.onStop();
        }
        super.onStop();
    }

}
