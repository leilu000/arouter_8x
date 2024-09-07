package com.alibaba.android.arouter.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * @author: created by leilu
 * email: lu.lei@hsbc.com
 */
@Route(path = Const.TestActivity_TAG)
//@AndroidEntryPoint
public class TestActivity extends Activity {

    @Autowired
    String name;

    @Inject
    MyDependency myDependency;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);

        Button btn = new Button(this);
        btn.setText(name + "   myDependency:" + myDependency);
        setContentView(btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/module_java/2").withString("name", "sunny").navigation();
            }
        });
    }
}
