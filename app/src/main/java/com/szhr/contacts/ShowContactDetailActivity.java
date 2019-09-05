package com.szhr.contacts;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.szhr.contacts.model.Contact;

public class ShowContactDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("查看内容");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_show_contact_detail;
    }

    @Override
    protected void setup(View contentView) {
        Contact contact = (Contact) getIntent().getSerializableExtra(ContactOptionsActivity.KEY_CONTACT);

        TextView nameTv = contentView.findViewById(R.id.nameTv);
        TextView numberTv = contentView.findViewById(R.id.numberTv);

        if (contact != null) {
            nameTv.setText(contact.getDisplayName());
            numberTv.setText(contact.getPhoneNumber());
        }

    }


    @Override
    protected void setupBottom(TextView leftTv, ImageView rightIv) {
        leftTv.setText("");
    }
}
