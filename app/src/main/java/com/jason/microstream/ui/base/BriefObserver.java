package com.jason.microstream.ui.base;


import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BriefObserver<D> implements Observer<D> {

  CompositeDisposable compositeDisposable;
  public BriefObserver(CompositeDisposable compositeDisposable) {
    this.compositeDisposable = compositeDisposable;
  }

  @Override
  public void onSubscribe(Disposable disposable) {
    if(compositeDisposable!=null) {
        compositeDisposable.add(disposable);
    }
  }

  @Override
  public void onComplete() {

  }
}
