package com.worldventures.dreamtrips.modules.background_uploading;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationMutator;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CancelCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.PauseCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.PhotoAttachmentUploadingCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.PostProcessingCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.ResumeCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.ScheduleCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.StartNextCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.util.UploadTimeEstimator;

import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.schedulers.Schedulers;

@Module(
      injects = {
            PostProcessingCommand.class,
            PhotoAttachmentUploadingCommand.class,
            ScheduleCompoundOperationCommand.class,
            CancelCompoundOperationCommand.class,
            StartNextCompoundOperationCommand.class,
            PauseCompoundOperationCommand.class,
            ResumeCompoundOperationCommand.class,
      },
      library = true, complete = false)
public class BackgroundUploadingModule {

   @Provides
   @Singleton
   BackgroundUploadingInteractor provideBackgroundUploadingInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new BackgroundUploadingInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   CompoundOperationsInteractor provideCompoundOperationsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new CompoundOperationsInteractor(sessionActionPipeCreator, Schedulers.from(Executors.newSingleThreadExecutor()));
   }

   @Provides
   PostCompoundOperationMutator providePostCompoundOperationMutator(SessionHolder<UserSession> sessionHolder) {
      return new PostCompoundOperationMutator(sessionHolder);
   }

   @Provides
   UploadTimeEstimator provideUploadingTimeEstimator() {
      return new UploadTimeEstimator();
   }
}
