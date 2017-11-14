package com.worldventures.dreamtrips.social.ui.tripsimages.service.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CompoundOperationsCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.QueryCompoundOperationsCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.VideoMediaEntity;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class CheckVideoProcessingStatusCommand extends Command<Void> implements InjectableAction {

   @Inject CompoundOperationsInteractor compoundOperationsInteractor;

   private List<BaseMediaEntity> mediaEntities;

   public CheckVideoProcessingStatusCommand(List<BaseMediaEntity> feedItems) {
      this.mediaEntities = feedItems;
   }

   @Override
   protected void run(CommandCallback<Void> commandCallback) throws Throwable {
      Observable<List<String>> videoFromFeedItemsIds = Observable.from(mediaEntities)
            .filter(feedEntity -> feedEntity instanceof VideoMediaEntity)
            .map(feedEntity -> feedEntity.getItem().getUid())
            .toList();
      Observable<List<PostCompoundOperationModel>> processingVideoModels = compoundOperationsInteractor.compoundOperationsPipe()
            .createObservableResult(new QueryCompoundOperationsCommand())
            .flatMap(compoundOperationsCommand -> Observable.from(compoundOperationsCommand.getResult())
                  .filter(model -> model.state() == CompoundOperationState.PROCESSING)
                  .filter(postCompoundOperationModel -> postCompoundOperationModel.body() instanceof PostWithVideoAttachmentBody)
                  .toList());

      Observable.zip(videoFromFeedItemsIds, processingVideoModels,
            (videoFeedIds, processingModels) -> {
               for (PostCompoundOperationModel model : processingModels) {
                  PostWithVideoAttachmentBody body = (PostWithVideoAttachmentBody) model.body();
                  if (videoFeedIds.contains(body.videoUid())) {
                     compoundOperationsInteractor.compoundOperationsPipe()
                           .send(CompoundOperationsCommand.compoundCommandRemoved(model));
                  }
               }
               return processingModels;
            })
            .subscribe(models -> commandCallback.onSuccess(null), commandCallback::onFail);
   }
}
