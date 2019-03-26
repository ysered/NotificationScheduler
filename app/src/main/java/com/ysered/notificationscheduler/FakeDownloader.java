package com.ysered.notificationscheduler;

import android.os.AsyncTask;
import android.util.Log;


class FakeDownloader extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = FakeDownloader.class.getSimpleName();

    private static final int FAKE_DOWNLOAD_DURATION_SECONDS = 30;

    @Override
    protected Boolean doInBackground(Void... voids) {
        for (int i = 0; i < FAKE_DOWNLOAD_DURATION_SECONDS; i ++) {
            try {
                if (isCancelled()) {
                    Log.i(TAG, "Download canceled!");
                    return false;
                }
                Thread.sleep(1000);
                Log.i(TAG, "Downloading...");
            } catch (InterruptedException e) {
                Log.e(TAG, "Execution was interrupted!");
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (isSuccess) {
            Log.i(TAG, "Download complete.");
        } else {
            Log.i(TAG, "Download failed!");
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.i(TAG, "Download canceled!");
    }
}
