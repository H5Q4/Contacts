package com.szhr.contacts;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private ViewStub viewStub;
    private View contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_base);

        viewStub = findViewById(R.id.viewStub);
        viewStub.setLayoutResource(getLayoutResource());
        contentView = viewStub.inflate();
        setup(contentView);
    }

    protected abstract int getLayoutResource();

    protected abstract void setup(View contentView);
}
