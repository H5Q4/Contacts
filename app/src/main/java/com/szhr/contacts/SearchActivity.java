package com.szhr.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class SearchActivity extends BaseActivity {

    private EditText nameEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("查找联系人");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search;
    }

    @Override
    protected void setup(View contentView) {
        nameEt = contentView.findViewById(R.id.nameEt);

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
