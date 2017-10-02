package com.worldventures.dreamtrips.social.di;

import android.content.Context;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.repository.DefaultSnappyOpenHelper;
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository;
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class SocialSnappyModule {

   @Provides
   @Singleton
   SocialSnappyRepository snappyRepositoryImpl(@ForApplication Context appContext, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      return new SocialSnappyRepositoryImpl(appContext, defaultSnappyOpenHelper);
   }
}