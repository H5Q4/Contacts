package com.szhr.contacts;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.szhr.contacts.base.BaseActivity;

public class ExtraCopyActivity extends BaseActivity {

    private CopyAsyncTask copyTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_copy);
    }

    @Override
    protected void onClickBottomLeft() {
        EditText startIndexEt = findViewById(R.id.startIndexEt);
        EditText endIndexEt = findViewById(R.id.endIndexEt);

        String start = startIndexEt.getText().toString();
        String end = endIndexEt.getText().toString();

        if (!TextUtils.isEmpty(start) &&
                !TextUtils.isEmpty(end) &&
                TextUtils.isDigitsOnly(start) &&
        TextUtils.isDigitsOnly(end) &&
        Integer.valueOf(end) - Integer.valueOf(start) > 0) {
            boolean b = getIntent().getBooleanExtra(SelectSimOrPhoneActivity.TYPE_SIM, false);
            copyTask = new CopyAsyncTask(this);
            copyTask.execute(b ? 0 : 1, Integer.valueOf(start), Integer.valueOf(end));
        } else {
            toast(getString(R.string.no_content));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (copyTask != null && isFinishing()) {
            copyTask.cancel(false);
        }
    }
}
