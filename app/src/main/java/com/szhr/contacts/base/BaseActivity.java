package com.szhr.contacts.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.szhr.contacts.R;

@SuppressLint("Registered")
public class BaseActivity extends Activity {

    private FrameLayout contentLayout;
    protected TextView leftTv;
    protected ImageView rightIv;
    protected TextView titleTv;
    protected TextView contentTv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.setContentView(R.layout.activity_base);

        contentLayout = findViewById(R.id.contentLayout);
        titleTv = findViewById(R.id.titleTv);
        leftTv = findViewById(R.id.leftTv);
        rightIv = findViewById(R.id.rightIv);
        contentTv = findViewById(R.id.centerTv);

    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, contentLayout);
    }

    protected void setTitle(String title) {
        titleTv.setText(title);
    }


    protected void onClickBottomLeft() {
        // 模拟 OK 键
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Log.d("Key_Down", "dpad_center");
                onClickDpadCenter();
                break;
            case KeyEvent.KEYCODE_MENU: // TODO replace with actual key
                onClickBottomLeft();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onClickDpadCenter() {
        // empty implementation
    }


}
