package com.szhr.contacts.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.szhr.contacts.R;

import java.lang.ref.WeakReference;

@SuppressLint("Registered")
public class BaseActivity extends Activity {

    private FrameLayout contentLayout;
    protected TextView leftTv;
    protected ImageView rightIv;
    protected TextView titleTv;
    protected TextView contentTv;

    protected static Handler handler;

    public AlertDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.setContentView(R.layout.activity_base);

        contentLayout = findViewById(R.id.contentLayout);
        titleTv = findViewById(R.id.titleTv);
        leftTv = findViewById(R.id.leftTv);
        rightIv = findViewById(R.id.rightIv);
        contentTv = findViewById(R.id.centerTv);

        if (handler == null) {
            handler = new BaseHandler(this);
        }

        setTitle(getString(R.string.app_name));
        leftTv.setText(R.string.accept);

    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, contentLayout);
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    protected void setTitle(String title) {
        titleTv.setText(title);
    }


    protected void onClickBottomLeft() {
        // 模拟 OK 键
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Log.d("Key_Down", "dpad_center");
                onClickDpadCenter();
                break;
            case KeyEvent.KEYCODE_MENU: // TODO replace with actual key
                onClickBottomLeft();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onClickDpadCenter() {
        // empty implementation
    }

    public void showDialog(String message, boolean alwaysShow, DialogInterface.OnDismissListener dismissListener){
        View view = LayoutInflater.from(this).inflate(R.layout.base_dialog, null);

        TextView textView = view.findViewById(R.id.text1);
        textView.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(false); //返回键dismiss

        dialog = builder.create();
        dialog.setOnDismissListener(dismissListener);
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//去掉圆角背景背后的棱角
        dialog.setCanceledOnTouchOutside(false);   //失去焦点dismiss
        dialog.show();

        if(!alwaysShow) {
           handler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   dismissDialog();
               }
           }, 2000);
        }
    }

    public void changeDialogText(String text) {
        Message message = Message.obtain();
        message.what = BaseHandler.CHANGE_DIALOG_TEXT;
        message.obj = text;
        handler.sendMessage(message);
    }

    public void toastThenFinish(String msg) {
        showDialog(msg, false, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
    }

    public void toast(String msg) {
        showDialog(msg, false, null);
    }

    public void dismissDialog(){
        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    private static class BaseHandler extends Handler {

        public static final int CHANGE_DIALOG_TEXT = 1111;

        private WeakReference<BaseActivity> ctxRef;

        BaseHandler(BaseActivity context) {
            ctxRef = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == CHANGE_DIALOG_TEXT) {	//update
                if (ctxRef.get() != null && ctxRef.get().dialog != null) {
                    TextView tv = (ctxRef.get().dialog).findViewById(R.id.text1);
                    tv.setText((String) msg.obj);
                }

            }
        }
    }

}
