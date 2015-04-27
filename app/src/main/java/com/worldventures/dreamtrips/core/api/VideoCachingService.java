package com.worldventures.dreamtrips.core.api;

import android.app.Application;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.binary.InFileBigInputStreamObjectPersister;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.retrofit.GsonRetrofitObjectPersisterFactory;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;
import com.worldventures.dreamtrips.App;

import javax.inject.Inject;

import retrofit.converter.GsonConverter;

public class VideoCachingService extends SpiceService {

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        cacheManager.addPersister(new InFileBigInputStreamObjectPersister(application));
        return cacheManager;
    }

    @Override
    public int getThreadCount() {
        return 1;
    }
}
