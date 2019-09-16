package com.szhr.contacts;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.szhr.contacts.base.BaseActivity;
import com.szhr.contacts.model.Contact;

public class ShowContactDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact_detail);

        setTitle(getString(R.string.view_content));
        leftTv.setText("");

        Contact contact = (Contact) getIntent().getSerializableExtra(ContactOptionsActivity.KEY_CONTACT);

        TextView nameTv = findViewById(R.id.nameTv);
        TextView numberTv = findViewById(R.id.numberTv);

        if (contact != null) {
            nameTv.setText(contact.getDisplayName());
            numberTv.setText(contact.getPhoneNumber());
        }
    }

}
