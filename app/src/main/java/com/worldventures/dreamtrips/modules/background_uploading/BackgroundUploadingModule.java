package com.worldventures.dreamtrips.modules.background_uploading;

import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationMutator;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.CancelCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.PauseCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.PhotoAttachmentUploadingCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.PostProcessingCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.ResumeCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.ScheduleCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.StartNextCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.util.UploadTimeEstimator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

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
   PostCompoundOperationMutator providePostCompoundOperationMutator(SessionHolder<UserSession> sessionHolder) {
      return new PostCompoundOperationMutator(sessionHolder);
   }

   @Provides
   UploadTimeEstimator provideUploadingTimeEstimator() {
      return new UploadTimeEstimator();
   }

}
