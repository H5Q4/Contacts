package com.szhr.contacts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.szhr.contacts.base.BaseActivity;
import com.szhr.contacts.model.Contact;
import com.szhr.contacts.util.ContactOperations;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

public class CopyAsyncTask extends AsyncTask<Integer, Integer, Void> {
    private final WeakReference<BaseActivity> ctxRef;
    private ProgressDialog progressDialog;

    public CopyAsyncTask(BaseActivity context) {
        this.ctxRef = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        if (ctxRef.get() != null) {
           ctxRef.get().showDialog(ctxRef.get().getString(R.string.copying), true, null);
        }
    }

    @Override
    protected Void doInBackground(Integer... params) {
        boolean simToPhone = params[0] == 0;
        int startIndex = params[1];
        int endIndex = params[2];
        if (ctxRef.get() != null) {
            List<Contact> contacts = simToPhone ?
                    ContactOperations.querySimContacts(ctxRef.get().getContentResolver(), null) :
                    ContactOperations.queryPhoneContacts(ctxRef.get().getContentResolver(), null);
            if (contacts == null) return null;

            int count = 0;
            for (int i = startIndex; i < endIndex;  i++) {
                Contact contact = contacts.get(i);
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
        if (ctxRef.get() != null) {
            ctxRef.get().changeDialogText(String.format(Locale.CHINA,
                    ctxRef.get().getString(R.string.placeholder_copying), values[0], values[1]));
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (ctxRef.get() != null) {
            ctxRef.get().toastThenFinish(ctxRef.get().getString(R.string.copy_finished));
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (ctxRef.get() != null) {
            ctxRef.get().dismissDialog();
        }
    }
}
