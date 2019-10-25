package com.szhr.contacts;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.szhr.contacts.base.BaseActivity;
import com.szhr.contacts.model.Contact;
import com.szhr.contacts.util.ContactOperations;

import static com.szhr.contacts.SelectSimOrPhoneActivity.TYPE_SIM;

public class EditContactActivity extends BaseActivity {

    public static final String FOR_UPDATE = "update";
    public static final String KEY_NUMBER = "number";
    private static final String TAG = EditContactActivity.class.getSimpleName();
    private EditText nameEt;
    private EditText numberEt;
    private boolean forUpdate;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        setTitle(getString(R.string.edit_content));

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
        } else {
            String number = getIntent().getStringExtra(KEY_NUMBER);
            if (!TextUtils.isEmpty(number)) {
                numberEt.setText(number);
                nameEt.requestFocus();
            }
        }
    }


    @Override
    protected void onClickBottomLeft() {

        String name = nameEt.getText().toString().trim();
        String number = numberEt.getText().toString().trim();

        boolean forSim;
        boolean result;

        if (forUpdate) {
            forSim = contact.isFromSim();

            if (forSim) {
                result = ContactOperations.updateSimContact(getContentResolver(), contact.getDisplayName(), contact.getPhoneNumber(), name, number);
            } else {
                result = ContactOperations.updatePhoneContact(getContentResolver(), contact.getDisplayName(), name, number);
            }

            toastThenFinish(result ? "更新成功" : "更新失败");

        } else {
            forSim = getIntent().getBooleanExtra(TYPE_SIM, false);

            if (forSim) {
                result = ContactOperations.insertSimContact(getContentResolver(), name, number);
            } else {
                result = ContactOperations.insertPhoneContact(getContentResolver(), name, number);
            }

            toastThenFinish(result ? "添加成功" : "添加失败");
        }

    }

}
