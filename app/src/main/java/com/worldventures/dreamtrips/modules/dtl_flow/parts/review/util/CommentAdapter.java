package com.worldventures.dreamtrips.modules.dtl_flow.parts.review.util;


import android.widget.EditText;

import com.worldventures.dreamtrips.util.Action;

import rx.functions.Action0;

public class CommentAdapter {

   private final CommentParams commentParams;
   private final Action<Integer> commentChangeSizeAction;
   private final Action0 outOfLimitAction;
   private final CommentTextWatcher commentTextWatcher;

   public CommentAdapter(CommentParams commentParams, EditText editText,
         Action<Integer> commentChangeSizeAction, Action0 outOfLimitAction) {
      this.commentParams = commentParams;
      this.commentChangeSizeAction = commentChangeSizeAction;
      this.outOfLimitAction = outOfLimitAction;
      this.commentTextWatcher = new CommentTextWatcher(editText, this::handleCommentText);
   }

   private void handleCommentText(String text) {
      final int commentSize = commentSize();

      if (commentSize >= commentParams.maxSize()) {
         outOfLimitAction.call();
      }
      commentChangeSizeAction.action(commentSize);
   }

   public boolean isCommentValid() {
      return commentSize() >= commentParams.minSize() && commentSize() <= commentParams.maxSize();
   }

   public boolean isCommentExist() {
      return !commentText().isEmpty();
   }

   public String commentText() {
      return commentTextWatcher.getText();
   }

   private int commentSize() {
      return cleanText(commentTextWatcher.getText()).length();
   }

   private String cleanText(String comment) {
      return comment.replaceAll("\n", " ").trim();
   }
}
