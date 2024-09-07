package com.alibaba.android.arouter.demo.module1;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.arouter.module.java.R;

@Route(path = "/module_java/2")
public class TestModule2Activity extends Activity {

    @Autowired
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_module2);
        ARouter.getInstance().inject(this);
        TextView tv = findViewById(R.id.tv);
        tv.setText("name11111111:" + name);
    }
}
