package com.rabtman;

import android.app.Activity;
import android.os.Bundle;
import com.rabtman.backpressure.BackpressureExample;
import com.rabtman.example.R;
import com.rabtman.throttling.ThrottlingExample;

public class MainActivity extends Activity {

  public static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //关于背压
    BackpressureExample backpressureExample = new BackpressureExample();
    backpressureExample.showRxBackpressureException();
    backpressureExample.showOnBackpressureBuffer();
    backpressureExample.showOnBackpressureDrop();
    backpressureExample.showOnBackpressureLatest();
    backpressureExample.showReactivePull();

    //关于节流
    ThrottlingExample throttlingExample = new ThrottlingExample();
    //throttlingExample.showSample();
    //throttlingExample.showtTrottleFirst();
    //throttlingExample.showDebounce();
    //throttlingExample.showtBuffer();
  }

}
