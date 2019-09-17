package com.szhr.contacts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.szhr.contacts.base.BaseListActivity;
import com.szhr.contacts.model.Contact;
import com.szhr.contacts.util.ContactOperations;

import java.lang.ref.WeakReference;
import java.util.List;

public class SelectSimOrPhoneActivity extends BaseListActivity {

    public static final String TYPE_SIM = "sim";
    public static final String FOR_DELETE = "delete";
    public static final String FOR_COPY = "copy";
    private boolean forDelete;
    private boolean forCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.save_to));

        forDelete = getIntent().getBooleanExtra(FOR_DELETE, false);
        forCopy = getIntent().getBooleanExtra(FOR_COPY, false);

        if (forDelete) {
            setTitle(getString(R.string.delete));
        }

        final String[] data = {getString(R.string.sim_card), getString(R.string.mobile_phone)};

        if (forCopy) {
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
            new CopyAllAsyncTask(this).execute(position == 0);
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(TYPE_SIM, position == 0);

        if (!forDelete) {
            intent.setClass(SelectSimOrPhoneActivity.this, EditContactActivity.class);
        } else {
            intent.putExtra(DeleteConfirmActivity.FOR_ALL, true);
            intent.setClass(SelectSimOrPhoneActivity.this, DeleteConfirmActivity.class);
        }
        startActivity(intent);
        finish();
    }

    static class CopyAllAsyncTask extends AsyncTask<Boolean, Integer, Void> {
        private final WeakReference<Context> ctxRef;
        private ProgressDialog progressDialog;

        public CopyAllAsyncTask(Context context) {
            this.ctxRef = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            if (ctxRef.get() != null) {
                progressDialog = new ProgressDialog(ctxRef.get());
                progressDialog.setTitle(ctxRef.get().getString(R.string.copying));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setIndeterminate(false);
                progressDialog.setProgress(0);
                progressDialog.setMax(100);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Boolean... booleans) {
            boolean simToPhone = booleans[0];
            if (ctxRef.get() != null) {
                List<Contact> contacts = simToPhone ?
                        ContactOperations.querySimContacts(ctxRef.get().getContentResolver()) :
                        ContactOperations.queryPhoneContacts(ctxRef.get().getContentResolver(), null);
                if (contacts == null) return null;

                int count = 0;
                for (Contact contact : contacts) {
                    if (ctxRef.get() != null) {
                        boolean b = simToPhone ?
                                ContactOperations.insertPhoneContact(ctxRef.get().getContentResolver(),
                                        contact.getDisplayName(), contact.getPhoneNumber()) :
                                ContactOperations.insertPhoneContact(ctxRef.get().getContentResolver(),
                                        contact.getDisplayName(), contact.getPhoneNumber());
                        if (b) {
                            publishProgress(count + 1, contacts.size());
                        }

                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0] / values[1] * 100);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

}
