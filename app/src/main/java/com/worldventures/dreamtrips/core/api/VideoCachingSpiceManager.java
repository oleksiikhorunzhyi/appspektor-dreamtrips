package com.worldventures.dreamtrips.core.api;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.SpiceService;

public class VideoCachingSpiceManager extends SpiceManager {

    public VideoCachingSpiceManager(Class<? extends SpiceService> spiceServiceClass) {
        super(spiceServiceClass);
    }
}
