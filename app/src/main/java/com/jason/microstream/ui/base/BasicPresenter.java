package com.jason.microstream.ui.base;


import org.greenrobot.eventbus.EventBus;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public abstract class BasicPresenter<V extends BasicPresenter.View> {

    private V mView;
    public CompositeDisposable disposable;

    public BasicPresenter(V mView) {
        this.mView = mView;
        disposable = new CompositeDisposable();
    }
    public V getView() {
        return mView;
    }

    public void attachView(V mView) {
        this.mView = mView;
    }

    protected void detachView() {
        mView = null;
    }

    protected void onDestroy() {
        mView = null;
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public interface View {

    }

    public void onStop() {

    }


}
