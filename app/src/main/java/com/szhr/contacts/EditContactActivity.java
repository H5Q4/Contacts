package com.szhr.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class EditContactActivity extends BaseActivity {

    public static final String TYPE_SIM = "sim";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("编辑内容");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_edit_contact;
    }

    @Override
    protected void setup(View contentView) {

    }
}
