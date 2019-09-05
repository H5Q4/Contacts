package com.szhr.contacts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.szhr.contacts.model.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactOptionsActivity extends BaseActivity {

    public static final String KEY_CONTACT = "contact";

    private Contact contact;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_contact_options;
    }

    @Override
    protected void setup(View contentView) {
        contact = (Contact) getIntent().getSerializableExtra(KEY_CONTACT);

        listView = contentView.findViewById(R.id.listView);
        listView.setDivider(null);
        listView.setItemsCanFocus(true);

        final String[] data = {"拨号", "IP拨号", "写短信", "查看内容", "编辑", "删除当前记录", "删除全部记录", "复制当前记录"
                , "复制全部记录", "高级复制", "发送名片"};
        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", data[i]);
            items.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                items,
                R.layout.item_contact_option,
                new String[]{"name"},
                new int[]{R.id.nameTv}
        );
        listView.setAdapter(adapter);
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                currentSelectedPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                switch (i) {
                    case 3:
                        intent = new Intent(ContactOptionsActivity.this, ShowContactDetailActivity.class);
                        intent.putExtra(KEY_CONTACT, contact);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(ContactOptionsActivity.this, EditContactActivity.class);
                        intent.putExtra(EditContactActivity.FOR_UPDATE, true);
                        intent.putExtra(KEY_CONTACT, contact);
                        startActivity(intent);
                    default:
                        break;
                }
            }
        });
        listView.setSelection(0);


    }
}
