package com.szhr.contacts.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.szhr.contacts.R;


public class AsyncDialog {
    private AlertDialog dialog;
    private final Activity mActivity;
    private final Handler mHandler;
    private static final int DELAY_TIME_SHOW = 500;
    private int mDelayTime = DELAY_TIME_SHOW;

    public AsyncDialog(Activity activity) {
        mActivity = activity;
        mHandler = new Handler();
    }

    public void setDelayTime(int time) {
        mDelayTime = time;
    }

    /**
     * Asynchronously executes a task while blocking the UI with a progress spinner.
     *
     * Must be invoked by the UI thread.  No exceptions!
     *
     * @param backgroundTask the work to be done in the background wrapped in a Runnable
     * @param postExecuteTask an optional runnable to run on the UI thread when the background
     * runnable is finished
     * @param dialogStringId the id of the string to be shown in the dialog
     */
    public void runAsync(final Runnable backgroundTask,
                         final Runnable postExecuteTask, final int dialogStringId) {
        new ModalDialogAsyncTask(dialogStringId, postExecuteTask)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, backgroundTask);
    }

    // Shows the activity's progress spinner. Should be canceled if exiting the activity.
    private Runnable mShowProgressDialogRunnable = new Runnable() {
        @Override
        public void run() {
            if (dialog != null) {
                dialog.show();
            }
        }
    };

    public void clearPendingProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        // remove any callback to display a progress spinner
        mHandler.removeCallbacks(mShowProgressDialogRunnable);
        // clear the dialog so any pending dialog.dismiss() call can be avoided
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
    }

    /**
     * Asynchronously performs tasks specified by Runnables.
     * Displays a progress spinner while the tasks are running.  The progress spinner
     * will only show if tasks have not finished after a certain amount of time.
     *
     * This AsyncTask must be instantiated and invoked on the UI thread.
     *
     * Need to implement a way for the background thread to pass a result to
     * the onPostExecute thread. AsyncTask already provides this functionality.
     */
    private class ModalDialogAsyncTask extends AsyncTask<Runnable, Void, Void> {
        final Runnable mPostExecuteTask;

        /**
         * Creates the Task with the specified string id to be shown in the dialog
         */
        public ModalDialogAsyncTask(int dialogStringId,
                                    final Runnable postExecuteTask) {
            mPostExecuteTask = postExecuteTask;
            // lazy initialization of progress dialog for loading attachments
            if (dialog == null) {
                dialog = createProgressDialog(dialogStringId);
            }
        }

        /**
         * Initializes the progress dialog with its intended settings.
         */
        private AlertDialog createProgressDialog(int stringId) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.base_dialog, null);

            TextView textView = (TextView) view.findViewById(R.id.text1);
            textView.setText(mActivity.getString(stringId));
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setView(view);
            builder.setCancelable(false); //返回键dismiss

            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);   //失去焦点dismiss
            return dialog;
        }

        /**
         * Activates a progress spinner on the UI.  This assumes the UI has invoked this Task.
         */
        @Override
        protected void onPreExecute() {
            // activate spinner after half a second
            mHandler.postDelayed(mShowProgressDialogRunnable, mDelayTime);
        }

        /**
         * Perform the specified Runnable tasks on a background thread
         */
        @Override
        protected Void doInBackground(Runnable... params) {
            if (params != null) {
                try {
                    for (int i = 0; i < params.length; i++) {
                        params[i].run();
                    }

                    // Test code. Uncomment this block to test the progress dialog popping up.
//                    try {
//                        Thread.sleep(2000);
//                    } catch (Exception e) {
//                    }
                } finally {
                    // Cancel pending display of the progress bar if the background task has
                    // finished before the progress bar has popped up.
                    mHandler.removeCallbacks(mShowProgressDialogRunnable);
                }
            }
            return null;
        }

        /**
         * Deactivates the progress spinner on the UI. This assumes the UI has invoked this Task.
         */
        @Override
        protected void onPostExecute(Void result) {
            if (mActivity.isFinishing()) {
                return;
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (mPostExecuteTask != null) {
                mPostExecuteTask.run();
            }
        }
    }
}