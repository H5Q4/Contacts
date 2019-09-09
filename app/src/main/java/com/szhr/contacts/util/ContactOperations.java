package com.szhr.contacts.util;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.szhr.contacts.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactOperations {
    /**
     * 查询手机联系人
     */
    public static List<Contact> queryPhoneContacts(ContentResolver resolver, String searchString) {

        String selection = null;
        String[] selectionArgs = null;

        if (!TextUtils.isEmpty(searchString)) {
            selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
            selectionArgs = new String[]{"%" + searchString + "%"};
        }

        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        List<Contact> items = new ArrayList<>();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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

    public static Contact querySimContacts(ContentResolver resolver) {

        String displayName;
        String phoneNumber;
        Contact contact = null;

        Uri simUri = Uri.parse("content://icc/adn");
        String[] projection = {"name", "number"};
        Cursor cursorSim = resolver.query(simUri, projection, null, null, "name");

        if (cursorSim == null) return contact;

        Log.i("SimContact", "total: " + cursorSim.getCount());

        while (cursorSim.moveToNext()) {
            displayName = cursorSim.getString(cursorSim.getColumnIndex("name"));
            phoneNumber = cursorSim.getString(cursorSim.getColumnIndex("number"));
            phoneNumber = phoneNumber.replaceAll("\\D", "").replaceAll("&", "");
            displayName = displayName.replace("|", "");

            contact = new Contact(displayName, phoneNumber);

            Log.i("SimContact", "name: " + displayName + " phone: " + phoneNumber);
        }
        cursorSim.close();

        return contact;

    }

    public static boolean deleteAllPhoneContacts(ContentResolver resolver) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).build());

        // Delete raw_contacts table related data.
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI).build());
        try {
            resolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean deleteAllSimContacts() {
        return false;
    }

    public static boolean deletePhoneContact(ContentResolver resolver, String name) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        String[] args = new String[]{String.valueOf(getRawContactId(resolver, name))};
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());

        // Delete raw_contacts table related data.
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=?", args).build());
        try {
            resolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean deleteSimContact(ContentResolver resolver, String name, String phone) {
        // 这种方式删除数据时不行，查阅IccProvider源码发现，在provider中重写的delete方法并没有用到String[]
        // whereArgs这个参数
        // int delete = getContentResolver().delete(uri,
        // " tag = ? AND number = ? ",
        // new String[] { "jason", "1800121990" });
        Uri simUri = Uri.parse("content://icc/adn");
        String where = "tag='" + name + "'";

        where += " AND number='" + phone + "'";
        int delete = resolver.delete(simUri, where, null);
        Log.d("SimContact", "delete =" + delete);
        return delete > 0;

    }

    public static boolean insertPhoneContact(ContentResolver resolver, String name, String number) {

        ContentValues values = new ContentValues();
        Uri contactUri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);
        if (contactUri == null) {

            return false;
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
            resolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (OperationApplicationException | RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public static boolean insertSimContact(ContentResolver resolver, String name, String phoneNumber) {
        Uri simUri = Uri.parse("content://icc/adn");
        ContentValues values = new ContentValues();
        values.put("tag", name);
        values.put("number", phoneNumber);
        Uri insertInfo = resolver.insert(simUri, values);
        Log.d("SimContact", insertInfo != null ? insertInfo.toString() : "Insert Failed");
        return insertInfo != null;
    }

    /* Update phone number with raw contact id and phone type.*/
    public static boolean updatePhoneContact(ContentResolver resolver, String oldName, String name, String number) {

        long contactId = getRawContactId(resolver, oldName);

        String where = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";

        String[] nameParams = new String[]{String.valueOf(contactId),
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
        String[] numberParams = new String[]{String.valueOf(contactId),
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
            resolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (OperationApplicationException | RemoteException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateSimContact(ContentResolver resolver, String oldName, String oldPhone, String newName,
                                           String newPhone) {
        Uri simUri = Uri.parse("content://icc/adn");
        ContentValues values = new ContentValues();
        values.put("tag", oldName);
        values.put("number", oldPhone);
        values.put("newTag", newName);
        values.put("newNumber", newPhone);
        int update = resolver.update(simUri, values, null, null);
        Log.d("SimContact", "update =" + update);
        return update > 0;
    }

    /* Get raw contact id by contact given name and family name.
     *  Return raw contact id.
     * */
    private static long getRawContactId(ContentResolver contentResolver, String displayName) {

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
