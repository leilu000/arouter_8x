package com.alibaba.android.arouter.demo;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.demo.service.model.TestObj;
import com.alibaba.android.arouter.demo.service.model.TestParcelable;
import com.alibaba.android.arouter.demo.service.model.TestSerializable;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.yourapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ARouter.openLog();
        ARouter.openDebug();
        ARouter.init(getApplication());

        // startActivity(new Intent(this, TestActivity.class));

        ARouter.getInstance().build(Const.TestActivity_TAG).withString("name", "sunny").navigation();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        // Build test data.
        TestSerializable testSerializable = new TestSerializable("Titanic", 555);
        TestParcelable testParcelable = new TestParcelable("jack", 666);
        TestObj testObj = new TestObj("Rose", 777);
        List<TestObj> objList = new ArrayList<>();
        objList.add(testObj);
        Map<String, List<TestObj>> map = new HashMap<>();
        map.put("testMap", objList);

        if (v == findViewById(R.id.openLog)) {
            ARouter.openLog();
        } else if (v == findViewById(R.id.openDebug)) {
            ARouter.openDebug();
        } else if (v == findViewById(R.id.init)) {
            // 调试模式不是必须开启，但是为了防止有用户开启了InstantRun，但是
            // 忘了开调试模式，导致无法使用Demo，如果使用了InstantRun，必须在
            // 初始化之前开启调试模式，但是上线前需要关闭，InstantRun仅用于开
            // 发阶段，线上开启调试模式有安全风险，可以使用BuildConfig.DEBUG
            // 来区分环境
            ARouter.openDebug();
            ARouter.init(getApplication());
        } else if (v == findViewById(R.id.normalNavigation)) {
            ARouter.getInstance().build(Const.TestActivity_TAG).withString("name", "sunny").navigation();
        } else if (v == findViewById(R.id.kotlinNavigation)) {
            ARouter.getInstance()
                    .build("/kotlin/test")
                    .withString("name", "老王")
                    .withInt("age", 23)
                    .navigation();
        } else if (v == findViewById(R.id.normalNavigationWithParams)) {
            Uri testUriMix = Uri.parse("arouter://m.aliyun.com/test/activity2");
            ARouter.getInstance().build(testUriMix)
                    .withString("key1", "value1")
                    .navigation();

        } else if (v == findViewById(R.id.oldVersionAnim)) {
            ARouter.getInstance()
                    .build("/test/activity2")
                    .withTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                    .navigation(this);

        } else if (v == findViewById(R.id.newVersionAnim)) {
            if (Build.VERSION.SDK_INT >= 16) {


                ARouter.getInstance()
                        .build("/test/activity2")
                        .navigation();
            } else {
                Toast.makeText(this, "API < 16,不支持新版本动画", Toast.LENGTH_SHORT).show();
            }

        } else if (v == findViewById(R.id.interceptor)) {
            ARouter.getInstance()
                    .build("/test/activity4")
                    .navigation(this, new NavCallback() {
                        @Override
                        public void onArrival(Postcard postcard) {

                        }

                        @Override
                        public void onInterrupt(Postcard postcard) {
                            Log.d("ARouter", "被拦截了");
                        }
                    });

        } else if (v == findViewById(R.id.navByUrl)) {
            ARouter.getInstance()
                    .build("/test/webview")
                    .withString("url", "file:///android_asset/scheme-test.html")
                    .navigation();

        } else if (v == findViewById(R.id.autoInject)) {
            ARouter.getInstance().build("/test/activity1")
                    .withString("name", "老王")
                    .withInt("age", 18)
                    .withBoolean("boy", true)
                    .withLong("high", 180)
                    .withString("url", "https://a.b.c")
                    .withSerializable("ser", testSerializable)
                    .withParcelable("pac", testParcelable)
                    .withObject("obj", testObj)
                    .withObject("objList", objList)
                    .withObject("map", map)
                    .navigation();

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 666:
                Log.e("activityResult", String.valueOf(resultCode));
                break;
            default:
                break;
        }
    }
}
