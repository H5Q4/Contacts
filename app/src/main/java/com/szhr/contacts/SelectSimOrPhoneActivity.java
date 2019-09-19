package com.szhr.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.szhr.contacts.base.BaseListActivity;

public class SelectSimOrPhoneActivity extends BaseListActivity {

    public static final String TYPE_SIM = "sim";
    public static final String FOR_DELETE = "delete";
    public static final String FOR_COPY = "copy";
    public static final String FOR_EXTRA_COPY = "extra_copy";
    private boolean forDelete;
    private boolean forCopy;
    private boolean forExtraCopy;
    private CopyAsyncTask copyTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.save_to));

        forDelete = getIntent().getBooleanExtra(FOR_DELETE, false);
        forCopy = getIntent().getBooleanExtra(FOR_COPY, false);
        forExtraCopy = getIntent().getBooleanExtra(FOR_EXTRA_COPY, false);

        if (forDelete) {
            setTitle(getString(R.string.delete));
        }

        final String[] data = {getString(R.string.sim_card), getString(R.string.mobile_phone)};

        if (forCopy || forExtraCopy) {
            setTitle(getString(R.string.copy));
            data[0] = getString(R.string.sim_to_phone);
            data[1] = getString(R.string.phone_to_sim);
        }
        setListData(data);

        setIndicatorType(INDICATOR_TYPE_INDEX);
    }

    @Override
    protected void onClickListItem(View view, int position) {

        if (forCopy) {
            copyTask = new CopyAsyncTask(this);
            copyTask.execute(position);
            return;
        }


        Intent intent = new Intent();
        intent.putExtra(TYPE_SIM, position == 0);

        if (forExtraCopy) {
            intent.setClass(SelectSimOrPhoneActivity.this, ExtraCopyActivity.class);
        } else if (!forDelete) {
            intent.setClass(SelectSimOrPhoneActivity.this, EditContactActivity.class);
        } else {
            intent.putExtra(DeleteConfirmActivity.FOR_ALL, true);
            intent.setClass(SelectSimOrPhoneActivity.this, DeleteConfirmActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (copyTask != null && isFinishing()) {
            copyTask.cancel(false);
        }
    }
}
