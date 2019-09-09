package com.szhr.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.szhr.contacts.base.BaseListActivity;
import com.szhr.contacts.model.Contact;
import com.szhr.contacts.util.ContactOperations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsActivity extends BaseListActivity {

    private static final String TAG = ContactsActivity.class.getSimpleName();
    public static final String KEY_QUERY_PARAM = "query_param";

    private List<Contact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        leftTv.setText("选项");

    }

    @Override
    protected void onResume() {
        super.onResume();

        String searchString = getIntent().getStringExtra(KEY_QUERY_PARAM);

        contacts = ContactOperations.queryPhoneContacts(getContentResolver(), searchString);
        items.clear();

        for (Contact contact : contacts) {
            Map<String, String> item = new HashMap<>();
            item.put(ITEM_NAME, contact.getDisplayName());
            item.put(ITEM_EXTRA, contact.getPhoneNumber());
            items.add(item);
        }

        setIndicatorType(INDICATOR_TYPE_CUSTOM);

    }

    @Override
    protected int getItemIndicatorDrawable(int i) {
        return contacts.get(i).isFromSim() ? R.drawable.ic_sim_card : R.drawable.ic_phone;
    }

    @Override
    protected void onClickListItem(View view, int position) {
        Intent intent = new Intent(ContactsActivity.this, ContactOptionsActivity.class);
        intent.putExtra(ContactOptionsActivity.KEY_CONTACT, contacts.get(position));
        startActivity(intent);
    }

}


