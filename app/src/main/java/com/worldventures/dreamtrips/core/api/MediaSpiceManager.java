package com.worldventures.dreamtrips.core.api;

import android.util.Log;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.SpiceService;
import com.worldventures.dreamtrips.BuildConfig;

import roboguice.util.temp.Ln;

public class MediaSpiceManager extends SpiceManager {

    public MediaSpiceManager(Class<? extends SpiceService> spiceServiceClass) {
        super(spiceServiceClass);
        Ln.getConfig().setLoggingLevel(BuildConfig.DEBUG ? Log.DEBUG : Log.ERROR);
    }
}
