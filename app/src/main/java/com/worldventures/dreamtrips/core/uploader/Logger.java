package com.worldventures.dreamtrips.core.uploader;

import android.util.Log;

import com.path.android.jobqueue.log.CustomLogger;

public class Logger implements CustomLogger {
    private static final String TAG = "UploadingService";

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void d(String text, Object... args) {
        Log.d(TAG, String.format(text, args));
    }

    @Override
    public void e(Throwable t, String text, Object... args) {
        Log.e(TAG, String.format(text, args), t);
    }

    @Override
    public void e(String text, Object... args) {
        Log.e(TAG, String.format(text, args));
    }
}
