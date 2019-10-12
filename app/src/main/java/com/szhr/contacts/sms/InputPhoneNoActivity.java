package com.szhr.contacts.sms;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.szhr.contacts.ContactOptionsActivity;
import com.szhr.contacts.R;
import com.szhr.contacts.SearchActivity;
import com.szhr.contacts.base.BaseActivity;
import com.szhr.contacts.model.Contact;

public class InputPhoneNoActivity extends BaseActivity {

    public static final String EXTRA_SEND_VCARD = "send_vcard";


    private EditText numberEt;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_phone_no);

        centerTv.setVisibility(View.VISIBLE);
        centerTv.setText(R.string.find);

        numberEt = findViewById(R.id.numberEt);

        contact = (Contact) getIntent().getSerializableExtra(ContactOptionsActivity.KEY_CONTACT);

    }

    @Override
    protected void onClickDpadCenter() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(EXTRA_SEND_VCARD, true);
        intent.putExtra(ContactOptionsActivity.KEY_CONTACT, contact);
        startActivity(intent);

        finish();
    }


    @Override
    protected void onClickBottomLeft() {
        String number = numberEt.getText().toString();

        if (TextUtils.isEmpty(number) || !TextUtils.isDigitsOnly(number)) {
            toast(getString(R.string.invalid_input));
            return;
        }


        String body = contact.getDisplayName() + " : " + contact.getPhoneNumber();

        SmsOperations.sendMessage(this, number,body, false);


        finish();

    }
}
