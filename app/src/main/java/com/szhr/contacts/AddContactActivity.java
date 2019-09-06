package com.szhr.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import com.szhr.contacts.base.BaseActivity;
import com.szhr.contacts.base.BaseListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddContactActivity extends BaseListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("存储到");

        final String[] data = {"SIM卡电话簿", "手机电话簿"};
        setListData(data);

        setIndicatorType(INDICATOR_TYPE_INDEX);
    }

    @Override
    protected void onClickListItem(View view, int position) {
        Intent intent = new Intent(AddContactActivity.this, EditContactActivity.class);
        intent.putExtra(EditContactActivity.TYPE_SIM, position == 0);
        startActivity(intent);
        finish();
    }

}
