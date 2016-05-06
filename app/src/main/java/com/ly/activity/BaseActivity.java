package com.ly.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ly.happyreader.R;
import com.ly.utils.ActivityController;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ActivityController.addActivity(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }
}
