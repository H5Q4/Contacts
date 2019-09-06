package com.szhr.contacts;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.szhr.contacts.base.BaseActivity;
import com.szhr.contacts.model.Contact;

import java.util.ArrayList;

public class EditContactActivity extends BaseActivity {

    private static final String TAG = EditContactActivity.class.getSimpleName();
    public static final String TYPE_SIM = "sim";
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

        boolean forSim = getIntent().getBooleanExtra(TYPE_SIM, false);

        if (!forSim) {
            if (!forUpdate) {
                insertPhoneContact(name, number);
            } else {
                updatePhoneContact(contact.getDisplayName(), name, number);
            }
        } else {
            if (!forUpdate) {
                insertSimContact(name, number);
            }else {
                updateSimContact(contact.getDisplayName(), contact.getPhoneNumber(), name, number);
            }
        }

        startActivity(new Intent(EditContactActivity.this, ContactsActivity.class));
        finish();

    }

    private void insertPhoneContact(String name, String number) {

        ContentValues values = new ContentValues();
        Uri contactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        if (contactUri == null) {
            Toast.makeText(this, "号码添加失败", Toast.LENGTH_SHORT).show();
            return;
        }
        long contactId = ContentUris.parseId(contactUri);

        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        if (!TextUtils.isEmpty(name)) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .build());
        }

        if (!TextUtils.isEmpty(number)) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
                    .build());
        }

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(this, "号码添加成功", Toast.LENGTH_SHORT).show();
        } catch (OperationApplicationException | RemoteException e) {
            e.printStackTrace();
            Toast.makeText(this, "号码添加失败", Toast.LENGTH_SHORT).show();
        }


    }

    private void insertSimContact(String name, String phoneNumber) {
        Uri simUri = Uri.parse("content://icc/adn");
        ContentValues values = new ContentValues();
        values.put("tag", name);
        values.put("number", phoneNumber);
        Uri insertInfo = getContentResolver().insert(simUri, values);
        if (insertInfo == null) {
            Toast.makeText(this, "号码添加失败", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "号码添加成功", Toast.LENGTH_SHORT).show();
        Log.d("SimContact", insertInfo.toString());
    }

    public void updateSimContact(String oldName, String oldPhone, String newName,
                                 String newPhone) {
        Uri simUri = Uri.parse("content://icc/adn");
        ContentValues values = new ContentValues();
        values.put("tag", oldName);
        values.put("number", oldPhone);
        values.put("newTag", newName);
        values.put("newNumber", newPhone);
        int update = getContentResolver().update(simUri, values, null, null);
        Log.d("SimContact", "update =" + update);
    }


    /* Update phone number with raw contact id and phone type.*/
    private void updatePhoneContact(String oldName, String name, String number) {

        String contactId = getRawContactId(oldName) + "";

        String where = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";

        String[] nameParams = new String[]{contactId,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
        String[] numberParams = new String[]{contactId,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};

        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        if (!TextUtils.isEmpty(name)) {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, nameParams)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build());
        }
        if (!TextUtils.isEmpty(number)) {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, numberParams)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .build());
        }
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

            Toast.makeText(this, "号码更新成功", Toast.LENGTH_SHORT).show();
        } catch (OperationApplicationException | RemoteException e) {
            e.printStackTrace();
            Toast.makeText(this, "号码更新失败", Toast.LENGTH_SHORT).show();
        }
    }

    /* Get raw contact id by contact given name and family name.
     *  Return raw contact id.
     * */
    private long getRawContactId(String displayName) {
        ContentResolver contentResolver = getContentResolver();

        // Query raw_contacts table by display name field ( given_name family_name ) to get raw contact id.

        // Create query column array.
        String[] queryColumnArr = {ContactsContract.RawContacts._ID};

        // Create where condition clause.
        String whereClause = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " = '" + displayName
                + "'";

        // Query raw contact id through RawContacts uri.
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

        // Return the query cursor.
        Cursor cursor = contentResolver.query(rawContactUri, queryColumnArr, whereClause, null, null);

        long rawContactId = -1;

        if (cursor != null) {
            // Get contact count that has same display name, generally it should be one.
            int queryResultCount = cursor.getCount();
            // This check is used to avoid cursor index out of bounds exception. android.database.CursorIndexOutOfBoundsException
            if (queryResultCount > 0) {
                // Move to the first row in the result cursor.
                cursor.moveToFirst();
                // Get raw_contact_id.
                rawContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
            }
            cursor.close();
        }

        return rawContactId;
    }
}
