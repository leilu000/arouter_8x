package com.alibaba.android.arouter.demo;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

import dagger.hilt.android.HiltAndroidApp;

/**
 * @author: created by leilu
 * email: lu.lei@hsbc.com
 */
@HiltAndroidApp
public class HiltApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.openLog();   // Print log
        ARouter.openDebug(); // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        ARouter.init(this);
    }
}
