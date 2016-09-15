package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

public class EditCommentCommand extends Command<Comment> {

   private Comment comment;

   public EditCommentCommand(Comment comment) {
      super(Comment.class);
      this.comment = comment;
   }

   @Override
   public Comment loadDataFromNetwork() throws Exception {
      return getService().editComment(comment.getUid(), comment.getMessage());
   }

   @Override
   public int getErrorMessage() {
      return R.string.error_fail_to_edit_comment;
   }
}
