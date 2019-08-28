package com.szhr.contacts;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private ListView listView;
    private int currentSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void setup(View contentView) {
        listView = contentView.findViewById(R.id.listView);
        listView.setDivider(null);
        listView.setItemsCanFocus(true);

        //ListView 要顯示的內容
        String[] data = {"查找联系人", "添加联系人", "显示全部", "存储器状态", "本机号码"};
        final String[] extras = new String[data.length];
        extras[extras.length - 1] = "无";
        //將資料轉換成<key,value>的型態
        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("ordinal", i + 1);
            item.put("name", data[i]);
            item.put("extra", extras[i]);
            items.add(item);
        }

        //帶入對應資料
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                items,
                R.layout.simple_list_item,
                new String[]{"ordinal", "name", "extra"},
                new int[]{R.id.ordinalTv, R.id.nameTv, R.id.extraTv}
        );
        listView.setAdapter(adapter);
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                currentSelectedPosition = position;
                if (extras[currentSelectedPosition] != null) {
                    view.findViewById(R.id.extraTv).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (currentSelectedPosition == 0) {
                    listView.setSelection(listView.getCount() - 1);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
               if (currentSelectedPosition == listView.getCount() - 1) {
                   listView.setSelection(0);
               }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}