package com.szhr.contacts;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.szhr.contacts.model.Contact;
import com.szhr.contacts.util.Constants;
import com.szhr.contacts.util.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CONTACT = 200;
    private static final int REQUEST_CODE_LOCAL_NUMBER = 300;

    private View lastSelectedView;

    private ViewAdapter adapter;
    private List<Map<String, String>> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askForContactPermission();
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

        final String[] data = {"查找联系人", "添加联系人", "显示全部", "存储器状态", "本机号码"};
        final String[] extras = new String[data.length];

        String localNumber = SharedPrefsUtils.getStringPreference(this, Constants.KEY_LOCAL_NUMBER);
        if (TextUtils.isEmpty(localNumber)) {
            extras[extras.length - 1] = "无";
        } else {
            extras[extras.length - 1] = localNumber;
        }


        items = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("ordinal", i + 1 + "");
            item.put("name", data[i]);
            item.put("extra", extras[i]);
            items.add(item);
        }

        adapter = new ViewAdapter(items, this);
        listView.setAdapter(adapter);
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                currentSelectedPosition = position;
                if (lastSelectedView != null) {
                    lastSelectedView.setVisibility(View.GONE);
                }
                View extraTv = view.findViewById(R.id.extraTv);
                lastSelectedView = extraTv;
                if (extraTv != null && extras[currentSelectedPosition] != null) {
                    extraTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0 : break;
                    case 1 : break;
                    case 2 : {
                        startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                        break;
                    }
                    case 3 : break;
                    case 4 : {
                        startActivityForResult(new Intent(MainActivity.this, EditLocalNumberActivity.class),
                                REQUEST_CODE_LOCAL_NUMBER);
                    }
                    default: break;
                }
            }
        });
        listView.setSelection(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_LOCAL_NUMBER && resultCode == RESULT_OK) {
            String localNumber = SharedPrefsUtils.getStringPreference(this, Constants.KEY_LOCAL_NUMBER);
            if (TextUtils.isEmpty(localNumber)) {
                localNumber = "无";
            }
            items.get(items.size() - 1).put("extra", localNumber);
            adapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS,
                                                    Manifest.permission.WRITE_CONTACTS}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.WRITE_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    static class ViewAdapter extends BaseAdapter {

        private  List<Map<String, String>> items;
        private LayoutInflater inflater;
        private Context context;

        public ViewAdapter( List<Map<String, String>> items, Context context) {
            this.items = items;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_main_menu, null);
                holder.indicatorTv = convertView.findViewById(R.id.indicatorTv);
                holder.nameTv = convertView.findViewById(R.id.nameTv);
                holder.extraTv = convertView.findViewById(R.id.extraTv);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Map<String, String> item = items.get(i);
            holder.indicatorTv.setText(item.get("ordinal"));
            holder.nameTv.setText(item.get("name"));
            holder.extraTv.setText(item.get("extra"));

            return convertView;
        }

        static class ViewHolder {
            TextView indicatorTv;
            TextView nameTv;
            TextView extraTv;
        }
    }


}

