package com.szhr.contacts.sms;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.szhr.contacts.R;
import com.szhr.contacts.base.BaseActivity;

public class SendSmsActivity extends BaseActivity {

    public static final String EXTRA_SMS_BODY = "sms_body";
    public static final String EXTRA_SMS_RECEIVER = "sms_receiver";

    private EditText contentEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mms);

        setTitle(getString(R.string.write_sms));

        contentEt = findViewById(R.id.contentEt);
    }

    @Override
    protected void onClickBottomLeft() {
        String content = contentEt.getText().toString();

        if (TextUtils.isEmpty(content)) {
            toast("空信息");
            return;
        }

        String number = getIntent().getStringExtra(EXTRA_SMS_RECEIVER);

        Intent intent = new Intent(this, SendSmsOptionsActivity.class);
        intent.putExtra(EXTRA_SMS_BODY, content);
        intent.putExtra(EXTRA_SMS_RECEIVER, number);
        startActivity(intent);

        finish();

    }
}
