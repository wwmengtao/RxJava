package com.rabtman;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rabtman.backpressure.BackpressureExample;
import com.rabtman.example2.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
  public static final String TAG = "MainActivity";
  private Unbinder mUnbinder;
  BackpressureExample backpressureExample;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mUnbinder = ButterKnife.bind(this);
    backpressureExample = new BackpressureExample();
  }

  @Override
  protected void onStop(){
    super.onStop();
    backpressureExample.unsubscribe();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mUnbinder.unbind();
  }

  @OnClick(R.id.tv) void unsubscribe() {
    backpressureExample.unsubscribe();
  }

  @OnClick(R.id.button10) void ObservableBackpressureTest() {
    backpressureExample.ObservableBackpressureTest();
  }
  @OnClick(R.id.button11) void BackpressureStrategyMissing() {
    backpressureExample.BackpressureStrategyMissing();
  }

  @OnClick(R.id.button12) void BackpressureStrategyError() {
    backpressureExample.BackpressureStrategyError();
  }

  @OnClick(R.id.button13)  void showOnBackpressureBuffer() {
    backpressureExample.BackpressureStrategyBuffer();
  }
  @OnClick(R.id.button14)  void showOnBackpressureDrop() {
    backpressureExample.BackpressureStrategyDrop();
  }
  @OnClick(R.id.button15)  void showOnBackpressureLatest() {
    backpressureExample.BackpressureStrategyLatest();
  }
  @OnClick(R.id.button16)  void pullViaBackpressureStrategy() {
    backpressureExample.pullViaBackpressureStrategy();
  }
}
