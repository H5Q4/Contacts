package com.szhr.contacts;

import android.os.Bundle;

import com.szhr.contacts.base.ConfirmActivity;
import com.szhr.contacts.model.Contact;
import com.szhr.contacts.util.ContactOperations;

public class DeleteConfirmActivity extends ConfirmActivity {

    public static final String FOR_ALL = "for_all";
    private boolean forAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        forAll = getIntent().getBooleanExtra(FOR_ALL, false);

        if (forAll) {
            setConfirmText("全部删除？");
        }
    }

    @Override
    protected void onConfirm() {
        boolean result;

        if (!forAll) {
            Contact contact = (Contact) getIntent().getSerializableExtra(ContactOptionsActivity.KEY_CONTACT);
            result = contact.isFromSim() ?
                    ContactOperations.deleteSimContact(getContentResolver(), contact.getDisplayName(), contact.getPhoneNumber()) :
                    ContactOperations.deletePhoneContact(getContentResolver(), contact.getDisplayName());
        } else {
            boolean forSim = getIntent().getBooleanExtra(SelectSimOrPhoneActivity.TYPE_SIM, false);
            result = forSim ?
                    ContactOperations.deleteAllSimContacts(getContentResolver()) :
                    ContactOperations.deleteAllPhoneContacts(getContentResolver());
        }

        toastThenFinish(result ? "删除成功" : "删除失败");

    }

}
