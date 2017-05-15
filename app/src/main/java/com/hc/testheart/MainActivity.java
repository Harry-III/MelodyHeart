package com.hc.testheart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {
    HeartView heartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        heartView = (HeartView) findViewById(R.id.surfaceView);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        heartView.reDraw();
        return super.onTouchEvent(event);
    }

}
