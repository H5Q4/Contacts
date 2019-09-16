package com.szhr.contacts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;


import com.szhr.contacts.base.BaseListActivity;
import com.szhr.contacts.util.Constants;
import com.szhr.contacts.util.SharedPrefsUtils;

public class MainActivity extends BaseListActivity {

    private static final int PERMISSION_REQUEST_CONTACT = 200;
    private static final int REQUEST_CODE_LOCAL_NUMBER = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askForContactPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();

        final String[] data = {getString(R.string.menu_search), getString(R.string.menu_add_entry),
                getString(R.string.menu_display_all), getString(R.string.menu_storage_status)};
        setListData(data);

        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String localNumber = null;
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            localNumber = tMgr.getLine1Number();
        }
        String extra;
        if (TextUtils.isEmpty(localNumber)) {
            extra = getString(R.string.none);
        } else {
            extra = localNumber;
        }

        addListItem(getString(R.string.menu_owner_number), extra);

        setIndicatorType(INDICATOR_TYPE_INDEX);
    }

    @Override
    protected void onClickListItem(View view, int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;
            case 1:
                startActivity(new Intent(MainActivity.this, SelectSimOrPhoneActivity.class));
                break;
            case 2: {
                startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                break;
            }
            case 3:
                startActivity(new Intent(MainActivity.this, StorageStateActivity.class));
                break;
            case 4: {
//                startActivityForResult(new Intent(MainActivity.this, EditLocalNumberActivity.class),
//                        REQUEST_CODE_LOCAL_NUMBER);
            }
            default:
                break;
        }
    }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            if (requestCode == REQUEST_CODE_LOCAL_NUMBER && resultCode == RESULT_OK) {
                String localNumber = SharedPrefsUtils.getStringPreference(this, Constants.KEY_LOCAL_NUMBER);
                if (TextUtils.isEmpty(localNumber)) {
                    localNumber = "无";
                }
                items.get(items.size() - 1).put("extra", localNumber);
                notifyDataSetChanged();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

        public void askForContactPermission () {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
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
                                                        Manifest.permission.WRITE_CONTACTS,
                                                Manifest.permission.READ_PHONE_STATE}
                                        , PERMISSION_REQUEST_CONTACT);
                            }
                        });
                        builder.show();
                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        requestPermissions(
                                new String[]
                                        {Manifest.permission.READ_CONTACTS,
                                                Manifest.permission.WRITE_CONTACTS,
                                                Manifest.permission.READ_PHONE_STATE}
                                , PERMISSION_REQUEST_CONTACT);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
            }
        }

        @Override
        public void onRequestPermissionsResult ( int requestCode,
        String permissions[], int[] grantResults){
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




}

