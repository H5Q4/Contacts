package com.szhr.contacts;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.szhr.contacts.base.BaseActivity;

public class SendMmsActivity extends BaseActivity {

    private EditText contentEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mms);

        setTitle("短信息");

        contentEt = findViewById(R.id.contentEt);
    }

    @Override
    protected void onClickDpadCenter() {
        String content = contentEt.getText().toString();

        if (TextUtils.isEmpty(content)) {
            toast("空信息");
            return;
        }



        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("phoneNo", null, "sms message", null, null);
    }
}
