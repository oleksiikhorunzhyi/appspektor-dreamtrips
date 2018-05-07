package com.worldventures.dreamtrips.modules.dtl.service.action.http.error;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.CommentReview;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Errors;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.InnerErrors;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.review.PostReviewError;

import java.util.List;

public class PostReviewErrorAdapter {

   private final CommentReview commentReview;

   public PostReviewErrorAdapter(CommentReview commentReview) {
      this.commentReview = commentReview;
   }

   public boolean isHaveError() {
      return commentReview.errors() != null;
   }

   public PostReviewError errorReason() {
      return getPostingErrorFrom(commentReview.errors());
   }

   private PostReviewError getPostingErrorFrom(@Nullable List<Errors> errors) {
      String errorCode = parseErrorCode(errors);
      if (errorCode == null) {
         errorCode = parseErrorCodeFallback(errors);
      }
      return PostReviewError.of(errorCode);
   }

    @Nullable
    private String parseErrorCode(List<Errors> errors) {
      if (errors != null && !errors.isEmpty()) {
         Errors error = errors.get(0);
         if (error.innerError() != null && !error.innerError().isEmpty()) {
            InnerErrors innerErrors = error.innerError().get(0);
            return innerErrors.code();
         }
      }
      return null;
   }

   @Nullable
   private String parseErrorCodeFallback(List<Errors> errors) {
      try {
         return errors.get(0).innerError().get(0).formErrors().fieldErrors().reviewText().code();
      } catch (Exception e) {
         return null;
      }
   }
}
