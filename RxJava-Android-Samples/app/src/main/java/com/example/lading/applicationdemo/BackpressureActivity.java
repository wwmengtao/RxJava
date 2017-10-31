package com.example.lading.applicationdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class BackpressureActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mText;
    private Button mBtn;
    private Button mBtn2;
    private TextView mEdit;
    private Subscription mSubscription=null;
    private Integer [] integer={1,2,3,4,5,6};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout2);
        initView();
    }

    private void initView() {
        mText= (TextView) findViewById(R.id.text1);mText.setVisibility(View.INVISIBLE);
        mEdit= (TextView) findViewById(R.id.edit1);mEdit.setVisibility(View.INVISIBLE);
        mBtn2= (Button) findViewById(R.id.button_cancal);
        mBtn= (Button) findViewById(R.id.button);
        mBtn.setText("Backpressure");
        mBtn.setOnClickListener(this);
        mBtn2.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button:
                MissingBackpressureException();
                break;
            case R.id.button_cancal:
                Backpressure();
                break;
        }
    }

    private void MissingBackpressureException() {
        //被观察者在主线程中，每1ms发送一个事件
        Observable.interval(1, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.newThread())
        //将观察者的工作放在新线程环境中
                .observeOn(Schedulers.newThread())
        //观察者处理每1000ms才处理一个事件
                .subscribe(new Action1() {
                    @Override
                    public void call(Object aLong) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.w("TAG","---->"+aLong);
                    }
                });
    }

    private void Backpressure() {
        //被观察者将产生100000个事件
        Observable observable=Observable.range(1,100000);
        class MySubscriber extends Subscriber<Integer> {
            @Override
            public void onStart() {
                //一定要在onStart中通知被观察者先发送一个事件
                request(1);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer n) {
            ALog.Log("onNext");
                //处理完毕之后，在通知被观察者发送下一个事件
                request(1);
            }
        }

        observable.observeOn(Schedulers.newThread())
                .subscribe(new MySubscriber());
    }

}
