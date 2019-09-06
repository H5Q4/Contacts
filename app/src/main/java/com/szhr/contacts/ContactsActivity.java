package com.szhr.contacts;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.szhr.contacts.base.BaseListActivity;
import com.szhr.contacts.model.Contact;

import java.util.ArrayList;
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

        contacts = queryPhoneContacts();

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


    /**
     * 查询手机联系人
     */
    private List<Contact> queryPhoneContacts() {
        String searchString = getIntent().getStringExtra(KEY_QUERY_PARAM);
        String selection = null;
        String[] selectionArgs = null;

        if (!TextUtils.isEmpty(searchString)) {
            selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
            selectionArgs = new String[]{"%" + searchString + "%"};
        }

        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        List<Contact> items = new ArrayList<>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, selectionArgs, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        if (cursor == null) {
            return items;
        }
        while (cursor.moveToNext()) {
            String displayName = cursor.getString(0);
            String phoneNumber = cursor.getString(1);
//            Log.d(TAG, cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE)));
            items.add(new Contact(displayName, phoneNumber));
        }

        cursor.close();
        return items;

    }

    private void querySimContacts() {

        String displayName;
        String phoneNumber;

        Uri simUri = Uri.parse("content://icc/adn");
        String[] projection = {"name", "number"};
        Cursor cursorSim = this.getContentResolver().query(simUri, projection, null, null, "name");

        if (cursorSim == null) return;

        Log.i("SimContact", "total: " + cursorSim.getCount());

        while (cursorSim.moveToNext()) {
            displayName = cursorSim.getString(cursorSim.getColumnIndex("name"));
            phoneNumber = cursorSim.getString(cursorSim.getColumnIndex("number"));
            phoneNumber = phoneNumber.replaceAll("\\D", "").replaceAll("&", "");
            displayName = displayName.replace("|", "");

            Log.i("SimContact", "name: " + displayName + " phone: " + phoneNumber);
        }
        cursorSim.close();

    }

    private void insertSimContact(String name, String phoneNumber) {
        Uri simUri = Uri.parse("content://icc/adn");
        ContentValues values = new ContentValues();
        values.put("tag", name);
        values.put("number", phoneNumber);
        Uri insertInfo = getContentResolver().insert(simUri, values);
        Log.d("SimContact", insertInfo != null ? insertInfo.toString() : "Insert Failed");
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

    public void deleteSimContact(String name, String phone) {
        // 这种方式删除数据时不行，查阅IccProvider源码发现，在provider中重写的delete方法并没有用到String[]
        // whereArgs这个参数
        // int delete = getContentResolver().delete(uri,
        // " tag = ? AND number = ? ",
        // new String[] { "jason", "1800121990" });
        Uri simUri = Uri.parse("content://icc/adn");
        String where = "tag='" + name + "'";

        where += " AND number='" + phone + "'";
        int delete = getContentResolver().delete(simUri, where, null);
        Log.d("SimContact", "delete =" + delete);

    }

}


