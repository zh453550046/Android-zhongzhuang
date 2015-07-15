package com.example.zhouwn.myapplication;

import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {

    ProgressScrollView progressScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressScrollView = (ProgressScrollView) findViewById(R.id.view);
        progressScrollView.setResource(R.mipmap.knob);
    }

}
