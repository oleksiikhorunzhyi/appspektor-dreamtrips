package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.post.DeletePostHttpAction;
import com.worldventures.dreamtrips.core.api.action.ApiActionCommand;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeletePostCommand extends ApiActionCommand<DeletePostHttpAction, Object> {

   private String uid;

   public DeletePostCommand(String uid) {
      this.uid = uid;
   }

   @Override
   protected DeletePostHttpAction getHttpAction() {
      return new DeletePostHttpAction(uid);
   }

   @Override
   protected Class<DeletePostHttpAction> getHttpActionClass() {
      return DeletePostHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_delete_post;
   }

   public String getUid() {
      return uid;
   }
}
