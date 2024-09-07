package com.alibaba.android.arouter.demo.module1;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;

/**
 * @author: created by leilu
 * email: lu.lei@hsbc.com
 */
@Route(path = "/module_java/ModuleJavaActivity")
public class ModuleJavaActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button btn = new Button(this);
        btn.setText("这是-----------ModuleJavaActivity");
        setContentView(btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
