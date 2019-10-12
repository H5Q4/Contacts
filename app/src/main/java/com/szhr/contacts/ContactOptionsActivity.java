package com.szhr.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.szhr.contacts.base.BaseListActivity;
import com.szhr.contacts.model.Contact;
import com.szhr.contacts.sms.SendSmsActivity;
import com.szhr.contacts.util.ContactOperations;

public class ContactOptionsActivity extends BaseListActivity {

    public static final String KEY_CONTACT = "contact";

    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contact = (Contact) getIntent().getSerializableExtra(KEY_CONTACT);

        final String[] data = {getString(R.string.option_dial), getString(R.string.option_dial_with_ip),
                getString(R.string.option_write_sms), getString(R.string.option_view),
                getString(R.string.option_edit), getString(R.string.option_delete_this_item),
                getString(R.string.option_delete_all), getString(R.string.option_copy_this_item),
                getString(R.string.option_copy_all_items), getString(R.string.option_extra_copy),
                getString(R.string.option_send_vcard)};
        setListData(data);

        setIndicatorType(INDICATOR_TYPE_CYCLE);

    }

    @Override
    protected void onClickListItem(View view, int position) {
        Intent intent = new Intent();
        intent.putExtra(KEY_CONTACT, contact);

        switch (position) {
            case 2:
                intent.setClass(this, SendSmsActivity.class);
                intent.putExtra(SendSmsActivity.EXTRA_SMS_RECEIVER, contact.getPhoneNumber());
                startActivity(intent);
                break;
            case 3:
                intent.setClass(this, ShowContactDetailActivity.class);
                startActivity(intent);
                break;
            case 4:
                intent.setClass(ContactOptionsActivity.this, EditContactActivity.class);
                intent.putExtra(EditContactActivity.FOR_UPDATE, true);
                startActivity(intent);
                break;
            case 5:
                intent.setClass(this, DeleteConfirmActivity.class);
                startActivity(intent);
                break;
            case 6:
                intent.setClass(this, SelectSimOrPhoneActivity.class);
                intent.putExtra(SelectSimOrPhoneActivity.FOR_DELETE, true);
                startActivity(intent);
                break;
            case 7:
                duplicateContact();
                break;
            case 8:
                intent.setClass(this, SelectSimOrPhoneActivity.class);
                intent.putExtra(SelectSimOrPhoneActivity.FOR_COPY, true);
                startActivity(intent);
                break;
            case 9:
                intent.setClass(this, SelectSimOrPhoneActivity.class);
                intent.putExtra(SelectSimOrPhoneActivity.FOR_EXTRA_COPY, true);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void duplicateContact() {
        boolean result = contact.isFromSim() ?
                ContactOperations.insertPhoneContact(getContentResolver(),
                        contact.getDisplayName(), contact.getPhoneNumber())
                : ContactOperations.insertSimContact(getContentResolver(),
                contact.getDisplayName(), contact.getPhoneNumber());
        toastThenFinish(result ? getString(R.string.copied_successfully) : getString(R.string.copy_failed));
    }
}
