package com.szhr.contacts;

import android.os.Bundle;
import android.widget.EditText;

import com.szhr.contacts.base.BaseActivity;
import com.szhr.contacts.model.Contact;
import com.szhr.contacts.util.ContactOperations;

import static com.szhr.contacts.SelectSimOrPhoneActivity.TYPE_SIM;

public class EditContactActivity extends BaseActivity {

    private static final String TAG = EditContactActivity.class.getSimpleName();
    public static final String FOR_UPDATE = "update";

    private EditText nameEt;
    private EditText numberEt;
    private boolean forUpdate;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        setTitle("编辑内容");

        nameEt = findViewById(R.id.nameEt);
        numberEt = findViewById(R.id.numberEt);

        forUpdate = getIntent().getBooleanExtra(FOR_UPDATE, false);

        if (forUpdate) {
            contact = (Contact) getIntent().getSerializableExtra(ContactOptionsActivity.KEY_CONTACT);
            if (contact != null) {
                nameEt.setText(contact.getDisplayName());
                numberEt.setText(contact.getPhoneNumber());

                nameEt.setSelection(contact.getDisplayName().length());
            }
        }
    }


    @Override
    protected void onClickBottomLeft() {

        String name = nameEt.getText().toString().trim();
        String number = numberEt.getText().toString().trim();

        boolean forSim;

        if (forUpdate) {
            forSim = contact.isFromSim();

            if (forSim) {
                ContactOperations.updateSimContact(getContentResolver(), contact.getDisplayName(), contact.getPhoneNumber(), name, number);
            } else {
                ContactOperations.updatePhoneContact(getContentResolver(), contact.getDisplayName(), name, number);
            }
        } else {
            forSim = getIntent().getBooleanExtra(TYPE_SIM, false);

            if (forSim) {
                ContactOperations.insertSimContact(getContentResolver(), name, number);
            } else {
                ContactOperations.insertPhoneContact(getContentResolver(), name, number);
            }
        }

//        startActivity(new Intent(EditContactActivity.this, ContactsActivity.class));
        finish();

    }

}
