package com.worldventures.dreamtrips.modules.dtl_flow.di;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.service.UploadReceiptInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class DtlAppModule {

   @Singleton
   @Provides
   UploadReceiptInteractor provideUploadReceiptInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new UploadReceiptInteractor(sessionActionPipeCreator);
   }
}
