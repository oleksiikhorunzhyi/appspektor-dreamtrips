package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.comment.DeleteCommentHttpAction;
import com.worldventures.dreamtrips.core.api.action.ApiActionCommand;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeleteCommentCommand extends ApiActionCommand<DeleteCommentHttpAction, Comment> implements InjectableAction {

   private FeedEntity feedEntity;
   private Comment comment;

   public DeleteCommentCommand(FeedEntity feedEntity, Comment comment) {
      this.feedEntity = feedEntity;
      this.comment = comment;
   }

   @Override
   protected Comment mapCommandResult(Comment httpCommandResult) {
      return comment;
   }

   @Override
   protected void onSuccess(CommandCallback<Comment> callback, Comment comment) {
      feedEntity.setCommentsCount(feedEntity.getCommentsCount() - 1);
      feedEntity.getComments().remove(comment);
      callback.onSuccess(comment);
   }

   @Override
   protected DeleteCommentHttpAction getHttpAction() {
      return new DeleteCommentHttpAction(comment.getUid());
   }

   @Override
   protected Class<DeleteCommentHttpAction> getHttpActionClass() {
      return DeleteCommentHttpAction.class;
   }

   public FeedEntity getFeedEntity() {
      return feedEntity;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_delete_comment;
   }
}