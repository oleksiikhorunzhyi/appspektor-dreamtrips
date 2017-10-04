package com.worldventures.core.modules.video;



import android.content.Context;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.video.service.MemberVideosInteractor;
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand;
import com.worldventures.core.modules.video.service.command.GetVideoLocalesCommand;
import com.worldventures.core.modules.video.service.command.ResetCachedModelsInProgressCommand;
import com.worldventures.core.modules.video.service.command.UpdateStatusCachedEntityCommand;
import com.worldventures.core.modules.video.service.storage.MediaModelStorage;
import com.worldventures.core.modules.video.service.storage.MediaModelStorageImpl;
import com.worldventures.core.modules.video.utils.CachedModelHelper;
import com.worldventures.core.repository.DefaultSnappyOpenHelper;
import com.worldventures.core.service.FilePathProvider;
import com.worldventures.core.service.command.MigrateFromCachedEntityCommand;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            GetMemberVideosCommand.class,
            GetVideoLocalesCommand.class,
            MigrateFromCachedEntityCommand.class,
            ResetCachedModelsInProgressCommand.class,
            UpdateStatusCachedEntityCommand.class,
            UpdateStatusCachedEntityCommand.class,
      },
      complete = false,
      library = true)
public class MediaModule {

   @Provides
   @Singleton
   MemberVideosInteractor provideMemberVideosInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new MemberVideosInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   MediaModelStorage provideMediaModelStorage(@ForApplication Context appContext, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      return new MediaModelStorageImpl(appContext, defaultSnappyOpenHelper);
   }

   @Provides
   @Singleton
   CachedModelHelper provideCachedModelHelper(FilePathProvider filePathProvider) {
      return new CachedModelHelper(filePathProvider);
   }
}
