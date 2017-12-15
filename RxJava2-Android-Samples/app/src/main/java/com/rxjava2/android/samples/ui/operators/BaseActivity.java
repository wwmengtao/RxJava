package com.rxjava2.android.samples.ui.operators;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rxjava2.android.samples.utils.ObsFetcher;

/**
 * Created by mengtao1 on 2017/12/15.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObsFetcher.reset();
    }

}
