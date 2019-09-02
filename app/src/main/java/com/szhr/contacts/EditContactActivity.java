package com.szhr.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.util.Objects;

public class EditContactActivity extends BaseActivity {

    private static final String TAG = EditContactActivity.class.getSimpleName();
    public static final String TYPE_SIM = "sim";
    private TextInputLayout nameInputLayout;
    private TextInputLayout numberInputLayout;
    private TextInputEditText nameEt;
    private TextInputEditText numberEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("编辑内容");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_edit_contact;
    }

    @Override
    protected void setup(View contentView) {
        nameInputLayout = contentView.findViewById(R.id.nameInputLayout);
        numberInputLayout = contentView.findViewById(R.id.numberInputLayout);
        nameEt = contentView.findViewById(R.id.nameEt);
        numberEt = contentView.findViewById(R.id.numberEt);



    }

    @Override
    protected void onClickBottomLeft() {
        String name = nameEt.getText().toString().trim();
        String number = numberEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameInputLayout.setError("名字不能为空");
            return;
        }
        if (TextUtils.isEmpty(number)) {
            numberInputLayout.setError("电话号码不能为空");
            return;
        }

        boolean forSim = getIntent().getBooleanExtra(TYPE_SIM, false);

        if (!forSim) {
            insertPhoneContact(name, number);
        } else {
            insertSimContact(name, number);
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
        long contact_id = ContentUris.parseId(contactUri);
        values.put(ContactsContract.Data.RAW_CONTACT_ID, contact_id);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, contact_id);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        Toast.makeText(this, "号码添加成功", Toast.LENGTH_SHORT).show();


    }

    private void updatePhoneContact(String name, String number) {

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
}
