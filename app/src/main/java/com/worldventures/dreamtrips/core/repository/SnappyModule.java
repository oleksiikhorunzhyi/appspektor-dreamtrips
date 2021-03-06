package com.worldventures.dreamtrips.core.repository;

import android.content.Context;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.modules.auth.api.command.LogoutAction;
import com.worldventures.core.repository.DefaultSnappyOpenHelper;
import com.worldventures.dreamtrips.modules.dtl.domain.storage.snappy.DtlSnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.helper.howtopayvideo.HowToPayHintDelegate;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class SnappyModule {

   @Provides
   @Singleton
   SnappyRepository snappyRepositoryImpl(@ForApplication Context appContext, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      return new SnappyRepositoryImpl(appContext, defaultSnappyOpenHelper);
   }

   @Provides
   @Singleton
   DefaultSnappyOpenHelper defaultSnappyOpenHelper() {
      return new DefaultSnappyOpenHelper();
   }

   @Provides(type = Provides.Type.SET)
   LogoutAction provideSnappyRepositoryLogoutAction(SnappyRepository snappyRepository) {
      return snappyRepository::clearAll;
   }

   @Provides(type = Provides.Type.SET)
   LogoutAction provideDtlSnappyRepositoryLogoutAction(HowToPayHintDelegate howToPayHintDelegate) {
      return howToPayHintDelegate::reset;
   }
}
