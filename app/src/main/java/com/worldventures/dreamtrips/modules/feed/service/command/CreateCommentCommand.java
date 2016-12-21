package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.comment.CreateCommentHttpAction;
import com.worldventures.dreamtrips.core.api.action.MappableApiActionCommand;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CreateCommentCommand extends MappableApiActionCommand<CreateCommentHttpAction, Comment, Comment> {

   private FeedEntity feedEntity;
   private String text;

   public CreateCommentCommand(FeedEntity feedEntity, String text) {
      this.feedEntity = feedEntity;
      this.text = text;
   }

   @Override
   protected void onSuccess(CommandCallback<Comment> callback, Comment createdComment) {
      feedEntity.setCommentsCount(feedEntity.getCommentsCount() + 1);
      feedEntity.getComments().add(createdComment);
      callback.onSuccess(createdComment);
   }

   @Override
   protected Class<Comment> getMappingTargetClass() {
      return Comment.class;
   }

   @Override
   protected Object mapHttpActionResult(CreateCommentHttpAction action) {
      return action.response();
   }

   @Override
   protected CreateCommentHttpAction getHttpAction() {
      return new CreateCommentHttpAction(feedEntity.getUid(), text);
   }

   @Override
   protected Class<CreateCommentHttpAction> getHttpActionClass() {
      return CreateCommentHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_post_comment;
   }

   public FeedEntity getFeedEntity() {
      return feedEntity;
   }
}
