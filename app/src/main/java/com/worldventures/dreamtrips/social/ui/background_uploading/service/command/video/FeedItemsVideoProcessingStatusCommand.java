package com.worldventures.dreamtrips.social.ui.background_uploading.service.command.video;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CompoundOperationsCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.QueryCompoundOperationsCommand;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class FeedItemsVideoProcessingStatusCommand extends Command<Void> implements InjectableAction {

   @Inject CompoundOperationsInteractor compoundOperationsInteractor;

   private List<FeedItem> feedItems;

   public FeedItemsVideoProcessingStatusCommand(List<FeedItem> feedItems) {
      this.feedItems = feedItems;
   }

   @Override
   protected void run(CommandCallback<Void> commandCallback) throws Throwable {
      Observable<List<String>> videoFromFeedItemsIds = Observable.from(feedItems)
            .map(feedItem -> feedItem.getItem())
            .filter(feedEntity -> feedEntity instanceof TextualPost)
            .cast(TextualPost.class)
            .flatMap(textualPost -> Observable.from(textualPost.getAttachments()))
            .map(feedEntityHolder -> feedEntityHolder.getItem())
            .filter(feedEntity -> feedEntity instanceof Video)
            .map(feedEntity -> feedEntity.getUid())
            .toList();
      Observable<List<PostCompoundOperationModel>> processingVideoModels = compoundOperationsInteractor.compoundOperationsPipe()
            .createObservableResult(new QueryCompoundOperationsCommand())
            .flatMap(compoundOperationsCommand -> {
               return Observable.from(compoundOperationsCommand.getResult())
                     .filter(model -> model.state() == CompoundOperationState.PROCESSING)
                     .filter(postCompoundOperationModel -> postCompoundOperationModel.body() instanceof PostWithVideoAttachmentBody)
                     .toList();
            });
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
