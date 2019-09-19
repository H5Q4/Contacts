package com.szhr.contacts;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.szhr.contacts.base.BaseListActivity;
import com.szhr.contacts.model.Contact;
import com.szhr.contacts.util.ContactOperations;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsActivity extends BaseListActivity {

    private static final String TAG = ContactsActivity.class.getSimpleName();
    public static final String KEY_QUERY_PARAM = "query_param";

    private List<Contact> contacts;
    private QueryAsyncTask queryAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        leftTv.setText(getString(R.string.options));

    }

    @Override
    protected void onResume() {
        super.onResume();

        String searchString = getIntent().getStringExtra(KEY_QUERY_PARAM);
        queryAsyncTask = new QueryAsyncTask(this);
        queryAsyncTask.execute(searchString);


        setIndicatorType(INDICATOR_TYPE_CUSTOM);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (queryAsyncTask != null && isFinishing()) {
            queryAsyncTask.cancel(false);
        }
    }

    private void handleContacts(List<Contact> contacts) {
        items.clear();
        this.contacts = contacts;

        for (Contact contact : contacts) {
            Map<String, String> item = new HashMap<>();
            item.put(ITEM_NAME, contact.getDisplayName());
            item.put(ITEM_EXTRA, contact.getPhoneNumber());
            items.add(item);
        }

        notifyDataSetChanged();
    }

    @Override
    protected int getItemIndicatorDrawable(int i) {
        return contacts.get(i).isFromSim() ? R.drawable.ic_sim_card : R.drawable.ic_phone;
    }

    @Override
    protected void onClickListItem(View view, int position) {
        Intent intent = new Intent(ContactsActivity.this, ContactOptionsActivity.class);
        intent.putExtra(ContactOptionsActivity.KEY_CONTACT, contacts.get(position));
        startActivity(intent);
    }

    static class QueryAsyncTask extends AsyncTask<String, Void, List<Contact>> {
        private final WeakReference<ContactsActivity> ctxRef;

        public QueryAsyncTask(ContactsActivity ctx) {
            this.ctxRef = new WeakReference<>(ctx);
        }

        @Override
        protected void onPreExecute() {
            if (ctxRef.get() != null) {
//                ctxRef.get().showDialog(ctxRef.get().getString(R.string.loading), true, null);
            }
        }

        @Override
        protected List<Contact> doInBackground(String... strings) {
            List<Contact> contacts = new ArrayList<>();
            String queryString = strings[0];
            if (ctxRef.get() != null) {
                contacts = ContactOperations.queryPhoneContacts(ctxRef.get().getContentResolver(), queryString);
                contacts.addAll(ContactOperations.querySimContacts(ctxRef.get().getContentResolver(), queryString));
                Collections.sort(contacts, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact o1, Contact o2) {
                        return o1.getDisplayName().compareTo(o2.getDisplayName());
                    }
                });
            }
            return contacts;
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
            if (ctxRef.get() != null) {
                ctxRef.get().handleContacts(contacts);
                ctxRef.get().dismissDialog();
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

}


