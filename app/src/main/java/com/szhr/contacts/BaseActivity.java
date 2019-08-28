package com.szhr.contacts;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private ViewStub viewStub;
    private View contentView;
    private TextView leftTv;
    private ImageView rightIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_base);

        viewStub = findViewById(R.id.viewStub);
        leftTv = findViewById(R.id.leftTv);
        rightIv = findViewById(R.id.rightIv);

        setupBottom(leftTv, rightIv);

        viewStub.setLayoutResource(getLayoutResource());
        contentView = viewStub.inflate();

        setup(contentView);
    }

    protected void setupBottom(TextView leftTv, ImageView rightIv) {}

    protected abstract int getLayoutResource();

    protected abstract void setup(View contentView);
}
