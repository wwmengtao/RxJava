package com.rxjava2.android.samples.ui.operators;

import android.os.Bundle;
import android.widget.TextView;

import com.rxjava2.android.samples.R;
import com.rxjava2.android.samples.model.User;
import com.rxjava2.android.samples.utils.DpObserverInfo;
import com.rxjava2.android.samples.utils.ObsFetcher;

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
import io.reactivex.schedulers.Schedulers;

/**
 * Created by amitshekhar on 27/08/16.
 */
public class IntervalExampleActivity extends BaseActivity {

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
        disposables.add(ObsFetcher.getIntervalMapObs()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(DpObserverInfo.getObLong())
        );
    }

    @OnClick(R.id.btn2)
    public void doSomeWork2() {
        textView.setText("");
        disposables.add(getObservable2()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(DpObserverInfo.getObListUser())
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

}