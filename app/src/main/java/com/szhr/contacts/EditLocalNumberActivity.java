package com.szhr.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.szhr.contacts.util.Constants;
import com.szhr.contacts.util.SharedPrefsUtils;

public class EditLocalNumberActivity extends BaseActivity {

    private EditText numberEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("本机号码");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_edit_local_number;
    }

    @Override
    protected void setup(View contentView) {
        numberEt = contentView.findViewById(R.id.numberEt);
        String number = SharedPrefsUtils.getStringPreference(this, Constants.KEY_LOCAL_NUMBER);
        if (!TextUtils.isEmpty(number)) {
            numberEt.setText(number);
        }
    }

    @Override
    protected void onClickBottomLeft() {
        numberEt.clearFocus();
        String number = numberEt.getText().toString().trim();
        if (!TextUtils.isEmpty(number)) {
            SharedPrefsUtils.setStringPreference(this, Constants.KEY_LOCAL_NUMBER, number);
        }
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
