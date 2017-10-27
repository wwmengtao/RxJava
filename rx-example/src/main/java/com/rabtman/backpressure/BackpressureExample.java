package com.rabtman.backpressure;

import com.rabtman.ALog;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.internal.util.RxRingBuffer;
import rx.schedulers.Schedulers;

/**
 * 一、流量控制方法：反压(BackPressure)、打包、节流(Throttling)、阻塞。反压从生产者入手，通过ReactivePull方式以及反压操作符使得生产者
 * 看起来“降低”了生产率；其余方法都是从消费者入手，对生产者发送的数据采用某种形式的处理以匹配消费者消费数据的速度。
 * 二、RxJava1.X中处理Hot Observables和cold Observables存在的问题
 * Cold Observables：指的是那些在订阅之后才开始发送事件的Observable（每个Subscriber都能接收到完整的事件）。
 * Hot Observables:指的是那些在创建了Observable之后，（不管是否订阅）就开始发送事件的Observable
 * Hot Observable这一类是不支持反压的，而是Cold Observable这一类中也有一部分并不支持反压（比如interval，timer等操作符创建的Observable）。
 * 都是Observable，结果有的支持反压，有的不支持，这就是RxJava1.X的一个问题。RxJava2.X已经将被观察者分为支持/不支持反压两种方式。
 * 三、反压实现的两种方式：ReactivePull方式以及反压操作符，消费者执行request(n)。对不支持ReactivePull操作的Observable通过类似onBackpressurebuffer、onBackpressureDrop
 * 操作使得其看上去支持ReactivePull方式，总体上看，生产者“降低”了生产速率。
 * 四、对不支持反压的Observable执行onBackpressurebuffer，onBackpressureDrop等操作使其支持反压。
 * @author mengtao1 关于背压
 */

public class BackpressureExample {
    Subscription mSubscription = null;
    public void unsubscribe(){
        if(null != mSubscription && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }

    public void schedulersDemo(){
//        Observable.just(1, 2, 3, 4)
//                .subscribeOn(Schedulers.io()) // IO 线程，由 subscribeOn() 指定
//                .observeOn(Schedulers.newThread())
//                .map(mapOperator) // 新线程，由 observeOn() 指定
//                .observeOn(Schedulers.io())
//                .map(mapOperator2) // IO 线程，由 observeOn() 指定
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber) // Android 主线程，由 observeOn() 指定
//                ·Schedulers.immediate()//直接在当前线程运行，相当于不指定线程。这是默认的Scheduler。
//                ·Schedulers.newThread();//总是启用新线程，并在新线程执行操作。
    }

    private void callExecute(String str, Long aLong, long millis){
        ALog.Log(str + "# " + aLong + " " + Thread.currentThread().toString());
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void DefaultObservableBuffer(){
        /*Observable.observeOn注释如下：
         * Modifies an Observable to perform its emissions and notifications on a specified {@link Scheduler},
         * asynchronously with a bounded buffer of {@link rx.internal.util.RxRingBuffer#SIZE} slots.
         * 上述说明Observable.observeOn异步情况下会创建一个有界buffer，大小为rx.internal.util.RxRingBuffer#SIZE，超过
         * 这个数值就会报MissingBackpressureException。
         */
        final int BUFFERSIZE = RxRingBuffer.SIZE;//此时数值为16
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0;i< BUFFERSIZE; i++) {//超过这个数值就会报MissingBackpressureException
                    subscriber.onNext(i);
                }
            }
        })
        .subscribeOn(Schedulers.io())//subscribeOn:指定Observable发射数据的线程
        .observeOn(AndroidSchedulers.mainThread())//observeOn:指定观察者运行的线程
        .subscribe(new Action1<Integer>() {//subscribe：AndroidSchedulers.mainThread()线程，由 observeOn() 指定
            @Override
            public void call(Integer integer) {
                callExecute("DefaultObservableBuffer", integer.longValue(), 100);
            }
        });
    }

    //背压异常
    public void MissingBackpressureException() {
        unsubscribe();
        Observable<Long> mObservable = Observable.interval(1, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())//subscribeOn:指定Observable发射数据的线程
        .observeOn(Schedulers.newThread());//observeOn:指定观察者运行的线程
        mSubscription = mObservable.subscribe(new Action1<Long>() {//subscribe：Schedulers.newThread()线程，由 observeOn() 指定
            @Override
            public void call(Long aLong) {
                callExecute("MissingBackpressureException", aLong, 1000);
            }
        });
    }

  //onBackpressureBuffer：对于不支持反压的Observable使用onBackpressureBuffer操作使其支持反压
  public void onBackpressureBuffer() {
      unsubscribe();
      Observable<Long> mObservable = Observable.interval(1, TimeUnit.MILLISECONDS)
      .onBackpressureBuffer()
      .subscribeOn(Schedulers.io())//subscribeOn:指定Observable发射数据的线程
      .observeOn(Schedulers.newThread());//observeOn:指定观察者运行的线程
      mSubscription = mObservable.subscribe(new Action1<Long>() {
          @Override
          public void call(Long aLong) {
              callExecute("onBackpressureBuffer", aLong, 1000);
          }
        });
  }

  //onBackpressureLatest：对于不支持反压的Observable使用onBackpressureLatest操作使其支持反压
  public void onBackpressureLatest() {
      unsubscribe();
      Observable<Long> mObservable = Observable.interval(1, TimeUnit.MILLISECONDS)
      .onBackpressureLatest()
      .subscribeOn(Schedulers.io())//subscribeOn:指定Observable发射数据的线程
      .observeOn(Schedulers.newThread());//observeOn:指定观察者运行的线程
      mSubscription = mObservable.subscribe(new Action1<Long>() {
          @Override
          public void call(Long aLong) {
              callExecute("onBackpressureLatest", aLong, 100);
          }
        });
  }

    //onBackpressureDrop：对于不支持反压的Observable使用onBackpressureDrop操作使其支持反压
  public void onBackpressureDrop() {
      unsubscribe();
      Observable<Long> mObservable = Observable.interval(1, TimeUnit.MILLISECONDS)
      .onBackpressureDrop()
      .subscribeOn(Schedulers.io())//subscribeOn:指定Observable发射数据的线程
      .observeOn(Schedulers.newThread());//observeOn:指定观察者运行的线程
      mSubscription = mObservable.subscribe(new Action1<Long>() {
          @Override
          public void call(Long aLong) {
              callExecute("onBackpressureDrop", aLong, 100);
          }
        });
  }

  //ReactivePull：响应式拉取，此方法对于支持反压的Observable适用。
  public void ReactivePull() {
      unsubscribe();
      Observable<Integer> mObservable = Observable.range(0, 10000)
      .subscribeOn(Schedulers.io())//subscribeOn:指定Observable发射数据的线程
      .observeOn(Schedulers.newThread());//observeOn:指定观察者运行的线程
      mSubscription = mObservable.subscribe(new Subscriber<Integer>() {
          @Override
          public void onStart() {
            request(1);
          }
          @Override
          public void onCompleted() {
          }
          @Override
          public void onError(Throwable throwable) {
          }
          @Override
          public void onNext(Integer integer) {
            ALog.Log("ReactivePull#"+"<--------" + integer + "--------->");
            request(1);
          }
        });
  }

}
