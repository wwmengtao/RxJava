package com.rxjava2.android.samples.ui.operators;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.rxjava2.android.samples.ALog;
import com.rxjava2.android.samples.R;
import com.rxjava2.android.samples.model.User;
import com.rxjava2.android.samples.ui.networking.ObsFetcher;
import com.rxjava2.android.samples.utils.AppConstant;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by amitshekhar on 27/08/16.
 */
public class IntervalExampleActivity extends AppCompatActivity {

    private static final String TAG = IntervalExampleActivity.class.getSimpleName();
    private Unbinder mUnbinder = null;
    TextView textView;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        mUnbinder = ButterKnife.bind(this);
        textView = (TextView) findViewById(R.id.textView);
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        disposables.clear(); // clearing it : do not emit after destroy
        ObsFetcher.reset();
        super.onDestroy();
    }

    /*
     * simple example using interval to run task at an interval of 2 sec
     * which start immediately
     */
    @OnClick(R.id.btn)
    public void doSomeWork() {
        textView.setText("");
        disposables.add(getObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver()));
    }

    int sleepIndex = 1;
    long preTime = 0, nowTime = 0;
    private Observable<? extends Long> getObservable() {
        return Observable.interval(0, 2, TimeUnit.SECONDS)
                .map(new Function<Long,Long>() {
                    @Override
                    public Long apply(Long l) throws Exception {
                        ALog.sleep(sleepIndex++*1000);
                        nowTime = System.currentTimeMillis();
                        //下列log信息说明RX的interval配合map可以实现上一个任务执行完毕之后再间隔一定时间执行下一个任务
                        if(0!=preTime)ALog.Log("Observable.interval: "+(nowTime - preTime)/1000);
                        preTime = System.currentTimeMillis();
                        return l;
                    }
                });
    }

    private DisposableObserver<Long> getObserver() {
        return new DisposableObserver<Long>() {

            @Override
            public void onNext(Long value) {
                textView.append("getObserver onNext : value : " + value);
                textView.append(AppConstant.LINE_SEPARATOR);
                ALog.Log(TAG+ " onNext : value : " + value);
            }

            @Override
            public void onError(Throwable e) {
                textView.append("getObserver onError : " + e.getMessage());
                textView.append(AppConstant.LINE_SEPARATOR);
                ALog.Log(TAG+ "getObserver onError : " + e.getMessage());
            }

            @Override
            public void onComplete() {
                textView.append("getObserver onComplete");
                textView.append(AppConstant.LINE_SEPARATOR);
                ALog.Log(TAG+ "getObserver onComplete");
            }
        };
    }

    @OnClick(R.id.btn2)
    public void doSomeWork2() {
        textView.setText("");
        disposables.add(getObservable2()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver2())
        );
    }

    private Observable<List<User>> getObservable2() {
        return Observable.interval(0, 2, TimeUnit.SECONDS)
                .switchMap(new Function<Long, ObservableSource<List<User>>>() {
                    @Override
                    public ObservableSource<List<User>> apply(Long l) throws Exception {
                        return ObsFetcher.getZipFansObservable();
                    }
                });
    }

    private DisposableObserver<List<User>> getObserver2() {
        return new DisposableObserver<List<User>>() {

            @Override
            public void onNext(List<User> value) {
                textView.append("getObserver2 onNext : value : " + value.size());
                textView.append(AppConstant.LINE_SEPARATOR);
            }

            @Override
            public void onError(Throwable e) {
                textView.append("getObserver2 onError : " + e.getMessage());
                textView.append(AppConstant.LINE_SEPARATOR);
            }

            @Override
            public void onComplete() {
                textView.append("getObserver2 onComplete");
                textView.append(AppConstant.LINE_SEPARATOR);
            }
        };
    }

}