package com.worldventures.dreamtrips.modules.dtl_flow.parts.common;

import android.content.Context;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.util.Action;

public final class SweetDialogParams<T> {

   private final String title;
   private final String content;
   private final String positiveText;
   private final boolean cancelable;
   private final @Nullable String negativeText;
   private final @Nullable Action<T> positiveAction;

   public SweetDialogParams(String title, String content, String positiveText) {
      this(title, content, positiveText, null, false, null);
   }

   public SweetDialogParams(String title, String content, String positiveText, @Nullable String negativeText, boolean cancelable, @Nullable Action<T> positiveAction) {
      this.content = content;
      this.positiveText = positiveText;
      this.negativeText = negativeText;
      this.title = title;
      this.cancelable = cancelable;
      this.positiveAction = positiveAction;
   }

   public String title() {
      return title;
   }

   public String content() {
      return content;
   }

   public String positiveText() {
      return positiveText;
   }

   public boolean isCancelable() {
      return cancelable;
   }

   @Nullable
   public String negativeText() {
      return negativeText;
   }

   @Nullable
   public Action<T> positiveAction() {
      return positiveAction;
   }

   // TODO Create factory for custom exceptions
   public static <T> SweetDialogParams<T> forProfanityErrorView(Context context) {
      return new SweetDialogParams<>(
            context.getString(R.string.app_name),
            context.getString(R.string.comment_review_profanity_text),
            context.getString(R.string.comment_review_confirm_text)
      );
   }

   public static <T> SweetDialogParams<T> forLimitReachedErrorView(Context context) {
      return new SweetDialogParams<>(
            context.getString(R.string.app_name),
            context.getString(R.string.comment_review_error_limited_reached_content),
            context.getString(R.string.comment_review_error_limited_reached_confirm)
      );
   }

   public static <T> SweetDialogParams<T> forReviewDuplicatedErrorView(Context context) {
      return new SweetDialogParams<>(
            context.getString(R.string.app_name),
            context.getString(R.string.comment_review_duplicated_review),
            context.getString(R.string.comment_review_confirm_text)
      );
   }

   public static <T> SweetDialogParams<T> forUnrecognizedErrorView(Context context) {
      return new SweetDialogParams<>(
            context.getString(R.string.app_name),
            context.getString(R.string.comment_review_unrecognized_error_content),
            context.getString(R.string.comment_review_unrecognized_error_confirm)
      );
   }

   public static <T> SweetDialogParams<T> forUnknownErrorView(Context context, Action<T> action) {
      return new SweetDialogParams<>(
            context.getString(R.string.comment_review_error_unknown_title),
            context.getString(R.string.comment_review_error_unknown_text),
            context.getString(R.string.comment_review_error_unknown_confirm),
            context.getString(R.string.comment_review_error_unknown_cancel),
            true,
            action);
   }

   public static <T> SweetDialogParams<T> forNetworkUnAvailableErrorView(Context context, String message, Action<T> action) {
      return new SweetDialogParams<>(
            context.getString(R.string.comment_review_title_sorry),
            message,
            context.getString(R.string.comment_review_no_internet_confirm_text),
            context.getString(R.string.comment_review_no_internet_cancel_text),
            true,
            action);
   }
}
