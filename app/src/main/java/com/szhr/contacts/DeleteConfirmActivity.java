package com.szhr.contacts;

import android.os.Bundle;

import com.szhr.contacts.base.ConfirmActivity;
import com.szhr.contacts.model.Contact;
import com.szhr.contacts.util.ContactOperations;

public class DeleteConfirmActivity extends ConfirmActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onConfirm() {
        Contact contact = (Contact) getIntent().getSerializableExtra(ContactOptionsActivity.KEY_CONTACT);

        boolean result = contact.isFromSim() ?
                ContactOperations.deleteSimContact(getContentResolver(), contact.getDisplayName(), contact.getPhoneNumber()) :
                ContactOperations.deletePhoneContact(getContentResolver(), contact.getDisplayName());
    }


}
