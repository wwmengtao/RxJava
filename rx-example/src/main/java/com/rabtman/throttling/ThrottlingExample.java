package com.rabtman.throttling;

import android.os.SystemClock;

import com.rabtman.ALog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Rabtman 关于节流（Throttling）
 */

public class ThrottlingExample {

    public ThrottlingExample(){

    }
  //sample
  public void showSample() {
    Observable.interval(1, TimeUnit.MILLISECONDS)
        .sample(1000, TimeUnit.MILLISECONDS)
        .observeOn(Schedulers.newThread())
        .subscribe(new Action1<Long>() {
          @Override
          public void call(Long aLong) {
            SystemClock.sleep(1000);
            ALog.Log("showSample#"+"<--------" + aLong + "--------->");
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            throwable.printStackTrace();
          }
        });
  }

  //throttleFirst
  public void showtTrottleFirst() {
    Observable.interval(1, TimeUnit.MILLISECONDS)
        .throttleFirst(1000, TimeUnit.MILLISECONDS)
        .observeOn(Schedulers.newThread())
        .subscribe(new Action1<Long>() {
          @Override
          public void call(Long aLong) {
            SystemClock.sleep(1000);
            ALog.Log("showtTrottleFirst#"+"<--------" + aLong + "--------->");
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            throwable.printStackTrace();
          }
        });
  }

  //debounce
  public void showDebounce() {
    //每600ms发送一个事件
    Observable.interval(600, TimeUnit.MILLISECONDS)
        .filter(new Func1<Long, Boolean>() {
          @Override
          public Boolean call(Long aLong) {
            return aLong % 2 == 0;   //为偶数时继续
          }
        })
        .debounce(1000, TimeUnit.MILLISECONDS)  //如果超过1000ms内没有收到事件，则发送出超时前的最后一个事件
        .observeOn(Schedulers.newThread())
        .subscribe(new Action1<Long>() {
          @Override
          public void call(Long aLong) {
            ALog.Log("showDebounce#"+"<--------" + aLong + "--------->");
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            throwable.printStackTrace();
          }
        });
  }

  //buffer
  public void showtBuffer() {
    Observable.interval(10, TimeUnit.MILLISECONDS)
        .buffer(1000, TimeUnit.MILLISECONDS)
        .observeOn(Schedulers.newThread())
        .subscribe(new Action1<List<Long>>() {
          @Override
          public void call(List<Long> aLong) {
            SystemClock.sleep(1000);
            ALog.Log("showtBuffer#"+"<--------" + aLong.toString() + "--------->");
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            throwable.printStackTrace();
          }
        });
  }

    //window
    public void showtWindow() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("hello i:" + i);
        }
        Observable.from(list).window(4).subscribe(new Subscriber<Observable<String>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Observable<String> stringObservable) {
                stringObservable.subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        ALog.Log("showtWindow#onNext: "+s);
                    }
                });
                ALog.Log("showtWindow#onNext: "+"\n next group");
            }
        });
    }
}
