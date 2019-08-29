package com.szhr.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.szhr.contacts.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends BaseActivity {

    private View lastSelectedView;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_contacts;
    }

    @Override
    protected void setup(View contentView) {
        listView = contentView.findViewById(R.id.listView);
        listView.setDivider(null);
        listView.setItemsCanFocus(true);
        final List<Contact> contacts = queryPhoneContacts();
        listView.setAdapter(new ViewAdapter(queryPhoneContacts(), this));
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                currentSelectedPosition = position;
                if (lastSelectedView != null) {
                    lastSelectedView.setVisibility(View.GONE);
                }
                View extraTv = view.findViewById(R.id.extraTv);
                lastSelectedView = extraTv;
                if (extraTv != null) {
                    extraTv.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ContactsActivity.this, ContactOptionsActivity.class);
                intent.putExtra("contact", contacts.get(i));
                startActivity(intent);
            }
        });
        listView.setSelection(0);
    }

    @Override
    protected void setupBottom(TextView leftTv, ImageView rightIv) {
        leftTv.setText("选项");
    }

    /**
     * 查询手机联系人
     */
    private List<Contact> queryPhoneContacts() {
        List<Contact> items = new ArrayList<>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor == null) {
            return items;
        }
        while (cursor.moveToNext()) {
            String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            items.add(new Contact(displayName, phoneNumber));
        }

        cursor.close();
        return items;

    }

    private void querySimContacts() {

        String displayName;
        String phoneNumber;

        Uri simUri = Uri.parse("content://icc/adn");
        Cursor cursorSim = this.getContentResolver().query(simUri, null, null, null, null);

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

class ViewAdapter extends BaseAdapter {

    private List<Contact> contacts;
    private LayoutInflater inflater;
    private Context context;

    public ViewAdapter(List<Contact> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int i) {
        return contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_contact, null);
            holder.simOrPhone = convertView.findViewById(R.id.indicatorTv);
            holder.displayName = convertView.findViewById(R.id.nameTv);
            holder.phoneNumber = convertView.findViewById(R.id.extraTv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Contact contact = contacts.get(i);
        String simOrPhone = contact.isFromSim() ? "S" : "P";
        holder.simOrPhone.setText(simOrPhone);
        holder.displayName.setText(contact.getDisplayName());
        holder.phoneNumber.setText(contact.getPhoneNumber());

        return convertView;
    }

    static class ViewHolder {
        TextView simOrPhone;
        TextView displayName;
        TextView phoneNumber;
    }
}
