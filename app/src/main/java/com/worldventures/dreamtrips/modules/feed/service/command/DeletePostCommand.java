package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.post.DeletePostHttpAction;
import com.worldventures.dreamtrips.core.api.action.ApiActionCommand;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeletePostCommand extends ApiActionCommand<DeletePostHttpAction, Object> {

   private String postId;

   public DeletePostCommand(String postId) {
      this.postId = postId;
   }

   @Override
   protected DeletePostHttpAction getHttpAction() {
      return new DeletePostHttpAction(postId);
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
