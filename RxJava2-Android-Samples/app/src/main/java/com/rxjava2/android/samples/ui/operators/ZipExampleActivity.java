package com.rxjava2.android.samples.ui.operators;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rxjava2.android.samples.ALog;
import com.rxjava2.android.samples.R;
import com.rxjava2.android.samples.model.User;
import com.rxjava2.android.samples.utils.AppConstant;
import com.rxjava2.android.samples.utils.Utils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by amitshekhar on 27/08/16.
 */
public class ZipExampleActivity extends AppCompatActivity {

    private static final String TAG = ZipExampleActivity.class.getSimpleName();
    Button btn;
    TextView textView;
    private long preTime, nowTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        btn = (Button) findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.textView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSomeWork();
            }
        });
    }

    /*
    * Here we are getting two user list
    * One, the list of cricket fans
    * Another one, the list of football fans
    * Then we are finding the list of users who loves both
    */
    private void doSomeWork() {
        /**
         * zip适用场景：有多个不同来源的数据需要组合产生新的数据。
         * getCricketFansObservable和getFootballFansObservable执行耗时操作的话，如果两者都不指定新线程，那么将在同一个
         * 线程中运行，耗时为两个任务的时间之和；如果都指定新线程的话，耗时为两者中时间最长的那个。
         */

        Observable.zip(getCricketFansObservable(), getFootballFansObservable(),
                new BiFunction<List<User>, List<User>, List<User>>() {
                    @Override
                    public List<User> apply(List<User> cricketFans, List<User> footballFans) throws Exception {
                        return Utils.filterUserWhoLovesBoth(cricketFans, footballFans);
                    }
                })
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver());
    }

    private Observable<List<User>> getCricketFansObservable() {
        ALog.Log(TAG+ " getCricketFansObservable");
        return Observable.create(new ObservableOnSubscribe<List<User>>() {
            @Override
            public void subscribe(ObservableEmitter<List<User>> e) throws Exception {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!e.isDisposed()) {
                            ALog.sleep(3000);
                            e.onNext(Utils.getUserListWhoLovesCricket());
                            e.onComplete();
                        }
                    }
                }).start();
            }
        });
    }

    private Observable<List<User>> getFootballFansObservable() {
        ALog.Log(TAG+ " getFootballFansObservable");
        return Observable.create(new ObservableOnSubscribe<List<User>>() {
            @Override
            public void subscribe(ObservableEmitter<List<User>> e) throws Exception {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!e.isDisposed()) {
                            ALog.sleep(2000);
                            e.onNext(Utils.getUserListWhoLovesFootball());
                            e.onComplete();
                        }
                    }
                }).start();
            }
        });
    }

    private Observer<List<User>> getObserver() {
        return new Observer<List<User>>() {

            @Override
            public void onSubscribe(Disposable d) {
                preTime = System.currentTimeMillis();
                ALog.Log(TAG+ " onSubscribe : " + d.isDisposed());
            }

            @Override
            public void onNext(List<User> userList) {
                nowTime = System.currentTimeMillis();
                textView.append(" onNext");
                textView.append(AppConstant.LINE_SEPARATOR);
                for (User user : userList) {
                    textView.append(" firstname : " + user.firstname);
                    textView.append(AppConstant.LINE_SEPARATOR);
                }
                //以下打印出此次观察者从申请观察到得到数据的耗时
                textView.append("Zip operator cost " + (nowTime - preTime)/1000 + " seconds!\n");
                ALog.Log(TAG+ " onNext : " + userList.size());
            }

            @Override
            public void onError(Throwable e) {
                textView.append(" onError : " + e.getMessage());
                textView.append(AppConstant.LINE_SEPARATOR);
                ALog.Log(TAG+ " onError : " + e.getMessage());
            }

            @Override
            public void onComplete() {
                textView.append(" onComplete");
                textView.append(AppConstant.LINE_SEPARATOR);
                ALog.Log(TAG+ " onComplete");
            }
        };
    }


}