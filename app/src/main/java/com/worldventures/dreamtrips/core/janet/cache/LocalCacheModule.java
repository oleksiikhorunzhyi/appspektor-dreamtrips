package com.worldventures.dreamtrips.core.janet.cache;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.utils.FilePathProvider;
import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class LocalCacheModule {

   @Singleton
   @Provides
   FilePathProvider provideFilePathProvider(@ForApplication Context context) {
      return new FilePathProvider(context);
   }

   @Singleton
   @Provides
   CachedModelHelper provideCachedModelHelper(FilePathProvider filePathProvider) {
      return new CachedModelHelper(filePathProvider);
   }

}
