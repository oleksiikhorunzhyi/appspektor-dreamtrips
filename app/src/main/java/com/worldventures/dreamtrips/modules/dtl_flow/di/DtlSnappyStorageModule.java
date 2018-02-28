package com.worldventures.dreamtrips.modules.dtl_flow.di;

import android.content.Context;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.repository.DefaultSnappyOpenHelper;
import com.worldventures.dreamtrips.modules.dtl.domain.storage.snappy.DtlSnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.domain.storage.snappy.DtlSnappyRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
class DtlSnappyStorageModule {

   @Provides
   @Singleton
   DtlSnappyRepository snappyRepositoryImpl(@ForApplication Context appContext, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      return new DtlSnappyRepositoryImpl(appContext, defaultSnappyOpenHelper);
   }
}
