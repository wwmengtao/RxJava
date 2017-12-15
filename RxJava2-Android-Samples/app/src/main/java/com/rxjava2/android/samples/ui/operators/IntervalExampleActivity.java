package com.rxjava2.android.samples.ui.operators;

import android.os.Bundle;

import com.rxjava2.android.samples.R;
import com.rxjava2.android.samples.utils.DpObserverInfo;
import com.rxjava2.android.samples.utils.ObsFetcher;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by amitshekhar on 27/08/16.
 */
public class IntervalExampleActivity extends BaseActivity {

    private static final String TAG = IntervalExampleActivity.class.getSimpleName();
    private Unbinder mUnbinder = null;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        disposables.clear(); // clearing it : do not emit after destroy
        super.onDestroy();
    }

    /*
     * simple example using interval to run task at an interval of 2 sec
     * which start immediately
     */
    @OnClick(R.id.btn)
    public void doSomeWork() {
        disposables.add(ObsFetcher.getIntervalMapObs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(DpObserverInfo.getObLong())
        );
    }

    @OnClick(R.id.btn2)
    public void doSomeWork2() {
        disposables.add(ObsFetcher.getIntervalFlatMapObs()//注意与getIntervalMapFlatMapObs的区别
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(DpObserverInfo.getObListUser())
        );
    }

}