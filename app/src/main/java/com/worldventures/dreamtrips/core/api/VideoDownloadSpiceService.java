package com.worldventures.dreamtrips.core.api;

import android.app.Application;

import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.binary.InFileBigInputStreamObjectPersister;
import com.octo.android.robospice.persistence.exception.CacheCreationException;

public class VideoDownloadSpiceService extends DreamSpiceService {

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
