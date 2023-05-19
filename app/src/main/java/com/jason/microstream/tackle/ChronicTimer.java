package com.jason.microstream.tackle;


import android.os.Handler;
import android.widget.TextView;


import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ChronicTimer {
  TextView textView;
  Disposable disposable;
  ObservableEmitter<String> timerEmitter;
  public ChronicTimer(TextView textView) {
    this.textView = textView;

    handler = new Handler();
    disposable = Observable
            .create(new ObservableOnSubscribe<String>() {
              @Override
              public void subscribe(ObservableEmitter<String> e) throws Exception {
                timerEmitter = e;
              }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
              @Override
              public void accept(String ss) throws Exception {
                textView.setText(ss+"");
              }
            });
  }

  private MyTimerTask myTimerTask;
  private Timer timer;
  private boolean timerIsStart = false;
  public void startTimer() {
    initTimer();
    timer.schedule(myTimerTask,200,1000);
    timerIsStart = true;
  }
  public boolean timerIsStart() {
    return timerIsStart;
  }


  public void stopTimer() {
    if (null != timer) {
      timer.cancel();
      timer = null;
    }
    if(disposable!=null)
      disposable.dispose();
  }

  private void initTimer() {
    timer = new Timer();
    myTimerTask = new MyTimerTask();
  }

  long confStartTime;
  long timeOffset=-1;
  public void setStartTime(Long confStartTime) {
    this.confStartTime = confStartTime/*-24000L*/;
  }

  Handler handler;
  class MyTimerTask extends TimerTask {

    @Override
    public void run() {
      handler.post(new Runnable() {
        @Override
        public void run() {
//          long sysZeroTime = System.currentTimeMillis()-8L * 60 * 60 * 1000;
//          if (timeOffset == -1) {
//            //本地9时区时间和服务器9时区时间差
//            timeOffset = AccountUtils.getInstance().getNowTime() - sysZeroTime;
//            if ((sysZeroTime-8L * 60 * 60 * 1000)+timeOffset-confStartTime < -8L * 60 * 60 * 1000) {
//              timeOffset = timeOffset - (((sysZeroTime-8L * 60 * 60 * 1000)+timeOffset-confStartTime)+8L * 60 * 60 * 1000);
//            }
//          }
//          textView.setText(DateUtils.getLongHM((sysZeroTime-8L * 60 * 60 * 1000)+timeOffset-confStartTime));

          long sysZeroTime = System.currentTimeMillis();
          if (timeOffset == -1) {
            //本地9时区时间和服务器9时区时间差
            timeOffset = sysZeroTime-System.currentTimeMillis();
          }
          textView.setText(DateUtils.getLongHM(((sysZeroTime-timeOffset-confStartTime)<0?0:(sysZeroTime-timeOffset-confStartTime))-8L * 60 * 60 * 1000));
        }
      });


    }
  }


}
