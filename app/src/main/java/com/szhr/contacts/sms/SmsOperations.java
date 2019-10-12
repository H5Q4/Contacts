package com.szhr.contacts.sms;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

import java.util.List;

public class SmsOperations {


    public static void sendMessage(Context context, String phone, String message, boolean persist) {
        SmsManager sms = SmsManager.getDefault();
        // if message length is too long messages are divided
        List<String> messages = sms.divideMessage(message);
        for (String msg : messages) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0,
                    new Intent("SMS_SENT"), 0);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(context, 0,
                    new Intent("SMS_DELIVERED"), 0);

            if (persist) {
                sms.sendTextMessage(phone, null, msg, sentIntent, deliveredIntent);
            } else {
                // TODO Uncomment this
//            sms.sendTextMessageWithoutPersisting(phone, null, msg, sentIntent, deliveredIntent);
            }
        }
    }

    public static void saveDraft(Context context, Sms sms) {
        //Store the message in the draft folder so that it shows in Messaging apps.
        ContentValues values = new ContentValues();
        // Message address.
        values.put("address", sms.sender);
        // Message body.
        values.put("body", sms.content);
        // Date of the draft message.
        values.put("date", String.valueOf(System.currentTimeMillis()));
        values.put("type", "3");
        // Put the actual thread id here. 0 if there is no thread yet.
        values.put("thread_id", "0");
        context.getContentResolver().insert(Uri.parse("content://sms/draft"), values);
    }


}
