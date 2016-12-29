package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.likes.DislikeHttpAction;
import com.worldventures.dreamtrips.api.likes.LikeHttpAction;
import com.worldventures.dreamtrips.core.api.action.ApiActionCommand;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class UnlikeEntityCommand extends ApiActionCommand<DislikeHttpAction, FeedEntity> {

   private FeedEntity feedEntity;

   public UnlikeEntityCommand(FeedEntity feedEntity) {
      this.feedEntity = feedEntity;
   }

   @Override
   protected DislikeHttpAction getHttpAction() {
      return new DislikeHttpAction(feedEntity.getUid());
   }

   @Override
   protected Class<DislikeHttpAction> getHttpActionClass() {
      return DislikeHttpAction.class;
   }

   @Override
   protected FeedEntity mapCommandResult(FeedEntity httpCommandResult) {
      feedEntity.setLiked(false);
      feedEntity.setLikesCount(feedEntity.getLikesCount() - 1);
      return feedEntity;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_unlike_item;
   }
}