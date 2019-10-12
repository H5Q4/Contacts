package com.szhr.contacts.sms;

import android.os.Bundle;
import android.view.View;

import com.szhr.contacts.R;
import com.szhr.contacts.base.BaseListActivity;

public class SendSmsOptionsActivity extends BaseListActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.write_sms));

        String[] menus = {
                getString(R.string.sms_send_only),
                getString(R.string.sms_save_only),
                getString(R.string.sms_send_and_save)
        };

        setListData(menus);

        setIndicatorType(INDICATOR_TYPE_INDEX);
    }

    @Override
    protected void onClickListItem(View view, int position) {
        String number = getIntent().getStringExtra(SendSmsActivity.EXTRA_SMS_RECEIVER);
        String body = getIntent().getStringExtra(SendSmsActivity.EXTRA_SMS_BODY);

        switch (position) {
            case 0:
                SmsOperations.sendMessage(this, number,body, false);
                break;
            case 1:
                Sms sms = new Sms();
                sms.content = body;
                sms.receiver = number;
                SmsOperations.saveDraft(this, sms);
                break;
            case 2:
                SmsOperations.sendMessage(this, number, body, true);
                break;
        }

        finish();
    }
}
