package com.worldventures.dreamtrips.core.api;

import android.app.Application;

import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;

public class PhotoUploadingSpiceService extends DreamSpiceService {

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        return super.createCacheManager(application);
    }

    @Override
    public int getThreadCount() {
        return 4;
    }
}
