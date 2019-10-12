package com.szhr.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.szhr.contacts.base.BaseActivity;
import com.szhr.contacts.sms.InputPhoneNoActivity;

import java.io.Serializable;

public class SearchActivity extends BaseActivity {

    private EditText nameEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setTitle(getString(R.string.menu_search));

        nameEt = findViewById(R.id.nameEt);
    }



    @Override
    protected void onClickBottomLeft() {
        String name = nameEt.getText().toString().trim();

        Intent intent = new Intent(SearchActivity.this, ContactsActivity.class);
        Serializable contact = getIntent().getSerializableExtra(ContactOptionsActivity.KEY_CONTACT);
        intent.putExtra(ContactOptionsActivity.KEY_CONTACT, contact);
        intent.putExtra(InputPhoneNoActivity.EXTRA_SEND_VCARD,
                getIntent().getBooleanExtra(InputPhoneNoActivity.EXTRA_SEND_VCARD, false));
        intent.putExtra(ContactsActivity.KEY_QUERY_PARAM, name);
        startActivity(intent);
        finish();
    }
}
