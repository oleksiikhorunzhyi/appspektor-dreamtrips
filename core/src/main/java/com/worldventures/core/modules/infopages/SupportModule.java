package com.worldventures.core.modules.infopages;

import android.content.Context;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.infopages.service.DocumentsInteractor;
import com.worldventures.core.modules.infopages.service.FeedbackInteractor;
import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand;
import com.worldventures.core.modules.infopages.service.command.GetFeedbackCommand;
import com.worldventures.core.modules.infopages.service.command.SendFeedbackCommand;
import com.worldventures.core.modules.infopages.service.command.UploadFeedbackAttachmentCommand;
import com.worldventures.core.modules.infopages.service.storage.InfopagesStorage;
import com.worldventures.core.modules.infopages.service.storage.InfopagesStorageImpl;
import com.worldventures.core.repository.DefaultSnappyOpenHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            GetDocumentsCommand.class,
            SendFeedbackCommand.class,
            GetFeedbackCommand.class,
            UploadFeedbackAttachmentCommand.class,
      },
      library = true,
      complete = false)
public class SupportModule {

   @Provides
   StaticPageProvider provideStaticPageProvider(StaticPageProviderConfig config) {
      return new StaticPageProvider(config);
   }

   @Provides
   @Singleton
   InfopagesStorage infopagesStorage(Context context, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      return new InfopagesStorageImpl(context, defaultSnappyOpenHelper);
   }

   @Provides
   @Singleton
   DocumentsInteractor provideDocumentsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DocumentsInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   FeedbackInteractor provideFeedbackInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new FeedbackInteractor(sessionActionPipeCreator);
   }
}
