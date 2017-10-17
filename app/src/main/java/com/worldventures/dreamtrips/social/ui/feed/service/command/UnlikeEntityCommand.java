package com.worldventures.dreamtrips.social.ui.feed.service.command;

import com.worldventures.core.service.command.api_action.ApiActionCommand;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.likes.DislikeHttpAction;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class UnlikeEntityCommand extends ApiActionCommand<DislikeHttpAction, FeedEntity> {

   private final FeedEntity feedEntity;

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
