package com.rabtman;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.rabtman.backpressure.BackpressureExample;
import com.rabtman.example.R;
import com.rabtman.throttling.ThrottlingExample;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
  @Bind(R.id.button11)  Button btn11;
  public static final String TAG = "MainActivity";
  BackpressureExample backpressureExample;//关于背压
  ThrottlingExample throttlingExample;//关于节流
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    backpressureExample = new BackpressureExample();
    throttlingExample = new ThrottlingExample();

  }

  @Override
  protected void onDestroy(){
    backpressureExample.unsubscribe();
    super.onDestroy();
  }

  @OnClick(R.id.tv) void backpressureExampleUnsubscribe() {
    backpressureExample.unsubscribe();
  }

  @OnClick(R.id.button10) void DefaultObservableBuffer() {
    backpressureExample.DefaultObservableBuffer();
  }

  @OnClick(R.id.button11) void MissingBackpressureException() {
    backpressureExample.DefaultObservableBuffer();
  }
  @OnClick(R.id.button12)  void showOnBackpressureBuffer() {
    backpressureExample.onBackpressureBuffer();
  }
  @OnClick(R.id.button13)  void showOnBackpressureDrop() {
    backpressureExample.onBackpressureDrop();
  }
  @OnClick(R.id.button14)  void showOnBackpressureLatest() {
    backpressureExample.onBackpressureLatest();
  }
  @OnClick(R.id.button15)  void showReactivePull() {
    backpressureExample.ReactivePull();
  }


  @OnClick(R.id.button21)  void showSample() {
    throttlingExample.showSample();
  }
  @OnClick(R.id.button22)  void showtTrottleFirst() {
    throttlingExample.showtTrottleFirst();
  }
  @OnClick(R.id.button23)  void showDebounce() {
    throttlingExample.showDebounce();
  }
  @OnClick(R.id.button24)  void showtBuffer() {
    throttlingExample.showtBuffer();
  }
  @OnClick(R.id.button25)  void showtshowtWindow() {
    throttlingExample.showtWindow();
  }
}
