package com.worldventures.dreamtrips.social.ui.feed.service.command;

import com.worldventures.core.service.command.api_action.ApiActionCommand;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.likes.LikeHttpAction;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LikeEntityCommand extends ApiActionCommand<LikeHttpAction, FeedEntity> {

   private final FeedEntity feedEntity;

   public LikeEntityCommand(FeedEntity feedEntity) {
      this.feedEntity = feedEntity;
   }

   @Override
   protected LikeHttpAction getHttpAction() {
      return new LikeHttpAction(feedEntity.getUid());
   }

   @Override
   protected Class<LikeHttpAction> getHttpActionClass() {
      return LikeHttpAction.class;
   }

   @Override
   protected FeedEntity mapCommandResult(FeedEntity httpCommandResult) {
      feedEntity.setLiked(true);
      feedEntity.setLikesCount(feedEntity.getLikesCount() + 1);
      return feedEntity;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_like_item;
   }
}
