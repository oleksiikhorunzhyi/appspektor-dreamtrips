package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.service.LikesInteractor;
import com.worldventures.dreamtrips.modules.feed.service.storage.PendingLikesStorage;

import javax.inject.Inject;

import io.techery.janet.CancelException;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ChangeFeedEntityLikedStatusCommand extends CommandWithError<FeedEntity> implements InjectableAction {

   @Inject LikesInteractor likesInteractor;
   @Inject PendingLikesStorage pendingLikesStorage;

   private FeedEntity feedEntity;

   public ChangeFeedEntityLikedStatusCommand(FeedEntity feedEntity) {
      this.feedEntity = feedEntity;
   }

   @Override
   protected void run(CommandCallback<FeedEntity> callback) throws Throwable {
      if (pendingLikesStorage.contains(feedEntity.getUid())) {
         callback.onFail(new CancelException());
         return;
      }
      pendingLikesStorage.add(feedEntity.getUid());

      if (!feedEntity.isLiked()) {
         like(callback);
      } else {
         unlike(callback);
      }
   }

   private void like(CommandCallback<FeedEntity> callback) {
      likesInteractor.likePipe().createObservableResult(new LikeEntityCommand(feedEntity))
            .subscribe(likeEntityCommand -> {
               pendingLikesStorage.remove(likeEntityCommand.getResult().getUid());
               callback.onSuccess(likeEntityCommand.getResult());
            }, callback::onFail);
   }

   private void unlike(CommandCallback<FeedEntity> callback) {
      likesInteractor.unlikePipe().createObservableResult(new UnlikeEntityCommand(feedEntity))
            .subscribe(unlikeEntityCommand -> {
               pendingLikesStorage.remove(unlikeEntityCommand.getResult().getUid());
               callback.onSuccess(unlikeEntityCommand.getResult());
            }, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return !feedEntity.isLiked() ?  R.string.error_fail_to_like_item : R.string.error_fail_to_unlike_item;
   }
}
