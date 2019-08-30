package com.szhr.contacts;

import android.app.Instrumentation;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private ViewStub viewStub;
    private View contentView;
    private TextView leftTv;
    private ImageView rightIv;
    private TextView titleTv;

    protected ListView listView;
    protected int currentSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_base);

        viewStub = findViewById(R.id.viewStub);
        titleTv = findViewById(R.id.titleTv);
        leftTv = findViewById(R.id.leftTv);
        rightIv = findViewById(R.id.rightIv);

        setupBottom(leftTv, rightIv);

        viewStub.setLayoutResource(getLayoutResource());
        contentView = viewStub.inflate();

        setup(contentView);
    }

    protected void setupBottom(TextView leftTv, ImageView rightIv) {}

    protected void setTitle(String title) {
        titleTv.setText(title);
    }

    protected abstract int getLayoutResource();

    protected abstract void setup(View contentView);

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
        Log.d("Key_Down", keyCode + "");
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (currentSelectedPosition == 0 && listView != null) {
                    listView.setSelection(listView.getCount() - 1);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (currentSelectedPosition == listView.getCount() - 1 && listView != null) {
                    listView.setSelection(0);
                }
                break;
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
