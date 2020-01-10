package com.xinhuo.ocrdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import yanzhikai.textpath.AsyncTextPathView;

/**
 * created by ThinkPad on 2020/1/2
 * Describe
 */

public class SplashActivity extends Activity {

    AsyncTextPathView asyncTextPathView;

    Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        asyncTextPathView = findViewById(R.id.enjoy);

        initData();
    }

    public void initData() {
        //从无到显示
        asyncTextPathView.startAnimation(0,1);

        // 延时下获取必要的信息
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.gotoMain(SplashActivity.this);
                finish();
            }
        }, 2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
    }
}
