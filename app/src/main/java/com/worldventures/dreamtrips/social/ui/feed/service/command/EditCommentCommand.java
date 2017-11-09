package com.worldventures.dreamtrips.social.ui.feed.service.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.service.command.api_action.MappableApiActionCommand;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.comment.UpdateCommentHttpAction;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.comment.Comment;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class EditCommentCommand extends MappableApiActionCommand<UpdateCommentHttpAction, Comment, Comment> implements InjectableAction {

   private FeedEntity feedEntity;
   private String commentUid;
   private String text;

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   public EditCommentCommand(FeedEntity feedEntity, String commentUid, String text) {
      this.feedEntity = feedEntity;
      this.commentUid = commentUid;
      this.text = text;
   }

   @Override
   protected void onSuccess(CommandCallback<Comment> callback, Comment updatedComment) {
      int location = feedEntity.getComments().indexOf(updatedComment);
      if (location != -1) {
         feedEntity.getComments().set(location, updatedComment);
      }
      callback.onSuccess(updatedComment);
   }

   @Override
   protected Class<Comment> getMappingTargetClass() {
      return Comment.class;
   }

   @Override
   protected Object mapHttpActionResult(UpdateCommentHttpAction updateCommentHttpAction) {
      return updateCommentHttpAction.response();
   }

   @Override
   protected UpdateCommentHttpAction getHttpAction() {
      return new UpdateCommentHttpAction(commentUid, text);
   }

   @Override
   protected Class<UpdateCommentHttpAction> getHttpActionClass() {
      return UpdateCommentHttpAction.class;
   }

   public FeedEntity getFeedEntity() {
      return feedEntity;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_edit_comment;
   }
}
