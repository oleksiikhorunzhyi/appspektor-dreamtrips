package com.worldventures.dreamtrips.modules.video.api;

import com.octo.android.robospice.request.simple.BigBinaryRequest;

import java.io.File;
import java.util.Calendar;

public class DownloadVideoRequest extends BigBinaryRequest {

    private long timeFromLastSync = 0;

    public static final long DELTA = 1000L;


    public DownloadVideoRequest(String url, File cacheFile) {
        super(url, cacheFile);
    }

    @Override
    protected void publishProgress() {
        if (Calendar.getInstance().getTimeInMillis() - timeFromLastSync > DELTA) {
            timeFromLastSync = Calendar.getInstance().getTimeInMillis();
            super.publishProgress();
        }
    }
}
