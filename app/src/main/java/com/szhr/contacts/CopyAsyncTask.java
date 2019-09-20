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

public class CopyAsyncTask extends AsyncTask<Integer, Integer, Exception> {
    private final WeakReference<BaseActivity> ctxRef;

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
    protected Exception doInBackground(Integer... params) {
        boolean simToPhone = params[0] == 0;
        int startIndex = 0;
        int endIndex = 0;

        if (params.length > 2) {
            startIndex = params[1];
            endIndex = params[2];
        }

        if (ctxRef.get() != null) {
            try {
                List<Contact> contacts = simToPhone ?
                        ContactOperations.querySimContacts(ctxRef.get().getContentResolver(), null) :
                        ContactOperations.queryPhoneContacts(ctxRef.get().getContentResolver(), null);
                if (contacts == null) return null;

                int count = 0;
                if (endIndex > contacts.size() || endIndex == 0) {
                    endIndex = contacts.size();
                }
                for (int i = startIndex; i < endIndex;  i++) {
                    Contact contact = contacts.get(i);
                    if (ctxRef.get() != null) {
                        boolean b = simToPhone ?
                                ContactOperations.insertPhoneContact(ctxRef.get().getContentResolver(),
                                        contact.getDisplayName(), contact.getPhoneNumber()) :
                                ContactOperations.insertSimContact(ctxRef.get().getContentResolver(),
                                        contact.getDisplayName(), contact.getPhoneNumber());
                        if (b) {
                            publishProgress(count + 1, contacts.size());
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e;
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
    protected void onPostExecute(Exception e) {
        if (ctxRef.get() != null) {
            ctxRef.get().dismissDialog();
            ctxRef.get().toastThenFinish(e == null ?
                    ctxRef.get().getString(R.string.copy_finished) :
                    ctxRef.get().getString(R.string.copy_failed));
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
