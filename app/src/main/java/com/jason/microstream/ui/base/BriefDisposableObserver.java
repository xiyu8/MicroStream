package com.jason.microstream.ui.base;

import io.reactivex.rxjava3.observers.DisposableObserver;

public abstract class BriefDisposableObserver<D> extends DisposableObserver<D> {

  @Override
  public void onComplete() {

  }
}
