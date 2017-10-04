package com.worldventures.dreamtrips.social.ui.feed.service.command;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.service.command.api_action.ApiActionCommand;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.post.DeletePostHttpAction;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeletePostCommand extends ApiActionCommand<DeletePostHttpAction, FeedEntity> implements InjectableAction {

   private FeedEntity feedEntity;

   public DeletePostCommand(FeedEntity feedEntity) {
      this.feedEntity = feedEntity;
   }

   @Override
   protected DeletePostHttpAction getHttpAction() {
      return new DeletePostHttpAction(feedEntity.getUid());
   }

   @Override
   protected FeedEntity mapHttpActionResult(DeletePostHttpAction httpAction) {
      return feedEntity;
   }

   @Override
   protected Class<DeletePostHttpAction> getHttpActionClass() {
      return DeletePostHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_delete_post;
   }
}
