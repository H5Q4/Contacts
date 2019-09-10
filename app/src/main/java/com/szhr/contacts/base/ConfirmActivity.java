package com.szhr.contacts.base;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szhr.contacts.R;
import com.szhr.contacts.base.BaseActivity;

public abstract class ConfirmActivity extends BaseActivity {

    private TextView contentTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        contentTv = findViewById(R.id.contentTv);
    }

    protected void setConfirmText(String content) {
        contentTv.setText(content);
    }

    protected abstract void onConfirm();

    protected void onCancel() {
        finish();
    }

    @Override
    protected void onClickDpadCenter() {
        onConfirm();
//        finish();

    }
}
