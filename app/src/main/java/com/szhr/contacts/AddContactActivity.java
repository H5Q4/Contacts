package com.szhr.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddContactActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("存储到");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_contact;
    }

    @Override
    protected void setup(View contentView) {
        listView = contentView.findViewById(R.id.listView);
        listView.setDivider(null);
        listView.setItemsCanFocus(true);

        final String[] data = {"SIM卡电话簿", "手机电话簿"};
        List<Map<String, String>> items = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("ordinal", i + 1 + "");
            item.put("name", data[i]);
            items.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                items,
                R.layout.item_main_menu,
                new String[]{"ordinal", "name"},
                new int[]{R.id.indicatorTv, R.id.nameTv}
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
                Intent intent = new Intent(AddContactActivity.this, EditContactActivity.class);
                intent.putExtra(EditContactActivity.TYPE_SIM, i == 0);
                startActivity(intent);
            }
        });
        listView.setSelection(0);
    }
}
