package com.worldventures.dreamtrips.modules.background_uploading.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.analytics.CancelVideoUploadAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CancelCompoundOperationCommand extends Command implements InjectableAction {

   private PostCompoundOperationModel compoundOperationModel;

   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public CancelCompoundOperationCommand(PostCompoundOperationModel compoundOperationModel) {
      this.compoundOperationModel = compoundOperationModel;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      if (compoundOperationModel.state() == CompoundOperationState.STARTED) {
         backgroundUploadingInteractor.postProcessingPipe().cancelLatest();
      }
      sendAnalytics();
      compoundOperationsInteractor.compoundOperationsPipe()
            .createObservable(CompoundOperationsCommand.compoundCommandRemoved(compoundOperationModel))
            .doOnNext(command -> startNext())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void startNext() {
      backgroundUploadingInteractor.startNextCompoundPipe().send(new StartNextCompoundOperationCommand());
   }

   private void sendAnalytics() {
      if (compoundOperationModel.type() == PostBody.Type.VIDEO) {
         PostWithVideoAttachmentBody body = (PostWithVideoAttachmentBody) compoundOperationModel.body();
         analyticsInteractor.analyticsActionPipe().send(CancelVideoUploadAction.createAction(body));
      }
   }
}
