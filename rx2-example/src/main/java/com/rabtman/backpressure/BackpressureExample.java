package com.rabtman.backpressure;

import com.rabtman.ALog;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Rabtman 关于背压
 */

public class BackpressureExample {
    Disposable mDisposable = null;
    AtomicBoolean shouldSkipFor = new AtomicBoolean(false);
    public void unsubscribe(){
        if(null != mDisposable && !mDisposable.isDisposed()){
            acceptExecute("unsubscribe", new Long(0), 0);
            shouldSkipFor.set(true);
            mDisposable.dispose();
        }
    }

    private void acceptExecute(String str, Long aLong, long millis){
        ALog.Log(str + "# " + aLong + " " + Thread.currentThread().toString());
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ObservableBackpressureTest(){
        /*Observable.observeOn注释如下：
          * Modifies an ObservableSource to perform its emissions and notifications on a specified {@link Scheduler},
         * asynchronously with an unbounded buffer with {@link Flowable#bufferSize()} "island size".
         * 上述说明Observable.observeOn异步情况下会创建一个无界buffer，大小为Flowable#bufferSize()，超过
         * 这个数值不会报MissingBackpressureException。说明RX2中，Observable不支持反压。
         */
        mDisposable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {   //无限循环发事件，不会报MissingBackpressureException。
                    if(shouldSkipFor.get()){
                        shouldSkipFor.set(false);
                        break;
                    }
                    emitter.onNext(i);
                }
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.newThread())
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                acceptExecute("BackpressureException", integer.longValue(), 100);
            }
        });
    }

    /**
     * BackpressureStrategyMissing:如果流的速度无法保持同步，可能会抛出MissingBackpressureException或IllegalStateException。
     */
    public void BackpressureStrategyMissing() {
        unsubscribe();
        Flowable<Integer> mFlowable = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {
                    if(shouldSkipFor.get()){
                        shouldSkipFor.set(false);
                        break;
                    }
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.MISSING)//如果流的速度无法保持同步，可能会抛出MissingBackpressureException或IllegalStateException。
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread());
        mDisposable = mFlowable.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                acceptExecute("BackpressureStrategyMissing", integer.longValue(), 10);
            }
        });
    }

    /**
     * BackpressureStrategyError:会在下游跟不上速度时抛出MissingBackpressureException。
     */
    public void BackpressureStrategyError() {
        unsubscribe();
        Flowable<Integer> mFlowable = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {
                    if(shouldSkipFor.get()){
                        shouldSkipFor.set(false);
                        break;
                    }
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.ERROR)//会在下游跟不上速度时抛出MissingBackpressureException。
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread());
        mDisposable = mFlowable.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                acceptExecute("BackpressureStrategyError", integer.longValue(), 10);
            }
        });
    }

  //BackpressureStrategyBuffer
  public void BackpressureStrategyBuffer() {
    unsubscribe();
    Flowable<Integer> mFlowable = Flowable.create(new FlowableOnSubscribe<Integer>() {
      @Override
      public void subscribe(@NonNull FlowableEmitter<Integer> emitter) throws Exception {
        for (int i = 0; ; i++) {
            if(shouldSkipFor.get()){
                shouldSkipFor.set(false);
                break;
            }
            emitter.onNext(i);
        }
      }
    }, BackpressureStrategy.BUFFER)
    .subscribeOn(Schedulers.io())
    .observeOn(Schedulers.newThread());
    mDisposable = mFlowable.subscribe(new Consumer<Integer>() {
        @Override
        public void accept(@NonNull Integer integer) throws Exception {
          acceptExecute("BackpressureStrategyBuffer", integer.longValue(), 10);
        }
    });
  }

  //BackpressureStrategyLatest
  public void BackpressureStrategyLatest() {
    unsubscribe();
    Flowable<Integer> mFlowable = Flowable.create(new FlowableOnSubscribe<Integer>() {
      @Override
      public void subscribe(@NonNull FlowableEmitter<Integer> emitter) throws Exception {
        for (int i = 0; ; i++) {
            if(shouldSkipFor.get()){
                shouldSkipFor.set(false);
                break;
            }
            emitter.onNext(i);
        }
      }
    }, BackpressureStrategy.LATEST)
    .subscribeOn(Schedulers.io())
    .observeOn(Schedulers.newThread());
    mDisposable = mFlowable.subscribe(new Consumer<Integer>() {
        @Override
        public void accept(@NonNull Integer integer) throws Exception {
          acceptExecute("BackpressureStrategyLatest", integer.longValue(), 10);
        }
    });
  }

  //BackpressureStrategyDrop
  public void BackpressureStrategyDrop() {
    unsubscribe();
    Flowable<Integer> mFlowable = Flowable.create(new FlowableOnSubscribe<Integer>() {
      @Override
      public void subscribe(@NonNull FlowableEmitter<Integer> emitter) throws Exception {
        for (int i = 0; ; i++) {
            if(shouldSkipFor.get()){
                shouldSkipFor.set(false);
                break;
            }
            emitter.onNext(i);
        }
      }
    }, BackpressureStrategy.DROP)
    .subscribeOn(Schedulers.io())
    .observeOn(Schedulers.newThread());
    mDisposable = mFlowable.subscribe(new Consumer<Integer>() {//Consumer接收但一数值
        @Override
        public void accept(@NonNull Integer integer) throws Exception {
          acceptExecute("BackpressureStrategyDrop", integer.longValue(), 10);
        }
    });
  }

    /**
     * 在下列mSubscription.request(1)的情况以及发送数据量为10*FlowableBufferSize的情况下：
     * 1、BackpressureStrategy.MISSING：会提示"io.reactivex.exceptions.MissingBackpressureException: Queue is full?!"，
     * 流程终止于onError(Throwable t)。
     * 2、BackpressureStrategy.ERROR：会提示"io.reactivex.exceptions.MissingBackpressureException:
     * create: could not emit value due to lack of requests"，流程终止于onError(Throwable t)。
     * 3、BackpressureStrategy.DROP等其他策略正常走完流程，执行到onComplete()。
     */
  public void pullViaBackpressureStrategy(){
      final int FlowableBufferSize = Flowable.bufferSize();//默认的Followable缓存个数
      Flowable.create(new FlowableOnSubscribe<Integer>() {
          @Override
          public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
              ALog.Log("pullViaError#subscribe");
              for(int i = 0; i< 10*FlowableBufferSize; i++){//发送的数据量为FlowableBufferSize+1
                  emitter.onNext(i);
              }
              emitter.onComplete();
          }
      }, BackpressureStrategy.ERROR)//尝试不同的策略来处理此种情况
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())//observeOn：默认的Followable缓存个数为Flowable.bufferSize()
      .subscribe(new Subscriber<Integer>() {
          Subscription mSubscription;
          @Override
          public void onSubscribe(Subscription s) {
              mSubscription = s;
//              mSubscription.request(Long.MAX_VALUE);  //下游处理事件能力值，不写request的话，下游收不到上游发送的数值
              mSubscription.request(1);
          }
          @Override
          public void onNext(Integer s) {
              ALog.Log("pullViaBackpressureStrategy#onNext: "+s.intValue());
              mSubscription.request(1);
          }

          @Override
          public void onError(Throwable t) {
              ALog.Log("pullViaBackpressureStrategy#onError:\n"+t.fillInStackTrace().toString());
          }

          @Override
          public void onComplete() {
              ALog.Log("pullViaBackpressureStrategy#onComplete");
          }
      });
  }
}
