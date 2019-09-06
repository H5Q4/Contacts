package com.szhr.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.szhr.contacts.base.BaseActivity;

public class SearchActivity extends BaseActivity {

    private EditText nameEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setTitle("查找联系人");

        nameEt = findViewById(R.id.nameEt);
    }



    @Override
    protected void onClickBottomLeft() {
        String name = nameEt.getText().toString().trim();

        Intent intent = new Intent(SearchActivity.this, ContactsActivity.class);
        intent.putExtra(ContactsActivity.KEY_QUERY_PARAM, name);
        startActivity(intent);
        finish();
    }
}
