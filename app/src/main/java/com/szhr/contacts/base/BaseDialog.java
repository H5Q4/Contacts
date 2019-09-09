package com.szhr.contacts.base;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.szhr.contacts.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by EngLee on 2019/9/4.
 */

public class BaseDialog {
    private static AlertDialog dialog;
    private static final Timer t = new Timer();

    public static void show(Context context, String message){
        show(context, message, false);
    }

    public static void show(Context context, String message, boolean alwaysShow){
        View view = LayoutInflater.from(context).inflate(R.layout.base_dialog, null);
        TextView textView = (TextView) view.findViewById(R.id.text1);
        textView.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false); //返回键dismiss

        dialog = builder.create();
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//去掉圆角背景背后的棱角
        dialog.setCanceledOnTouchOutside(false);   //失去焦点dismiss
        dialog.show();

        if(!alwaysShow) {
            t.schedule(new TimerTask() {
                public void run() {
                    if(dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                }
            }, 2000);
        }
    }

    public static void dismiss(){
        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
